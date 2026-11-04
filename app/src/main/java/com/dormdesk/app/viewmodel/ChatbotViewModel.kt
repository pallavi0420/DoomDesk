package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.ChatMessage
import com.dormdesk.app.data.model.ComplaintStatus
import com.dormdesk.app.data.model.SenderType
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.remote.GroqChatApi
import com.dormdesk.app.data.repository.DormDeskRepository
import com.dormdesk.app.utils.FaqEngine
import com.dormdesk.app.utils.IdGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatbotUiState(
    val isTyping: Boolean = false,
    val isRemoteAiActive: Boolean = false,
    val errorMessage: String? = null
)

class ChatbotViewModel(
    private val repository: DormDeskRepository,
    private val groqChatApi: GroqChatApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatbotUiState())
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<User?> = repository.currentUser

    val messages: StateFlow<List<ChatMessage>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suggestedQuestions = listOf(
        "What is my complaint status?",
        "How do I raise a complaint?",
        "What are the latest hostel announcements?",
        "How can I contact the warden?",
        "How do I submit feedback?"
    )

    fun sendMessage(userText: String, onActionTarget: ((String) -> Unit)? = null) {
        val query = userText.trim()
        if (query.isBlank()) return

        val user = repository.currentUser.value
        val userId = user?.uid ?: "guest"
        val now = System.currentTimeMillis()

        val userMessage = ChatMessage(
            messageId = IdGenerator.generateMessageId(),
            userId = userId,
            message = query,
            senderType = SenderType.USER,
            timestamp = now
        )

        viewModelScope.launch {
            // Save user message immediately
            repository.addChatMessage(userMessage)
            _uiState.update { it.copy(isTyping = true) }

            // Gather context data for the student
            val userComplaints = repository.complaints.value.filter { it.studentId == userId }
            val pendingCount = userComplaints.count { it.status.isActive }
            val activeSummary = userComplaints.filter { it.status.isActive }
                .take(3)
                .joinToString("; ") { "${it.complaintId}: ${it.title} (${it.status.label})" }
            val announcements = repository.announcements.value
            val latestAnnouncementTitle = announcements.firstOrNull()?.title

            // Small natural delay for responsive typing feeling
            delay(400)

            // Attempt FastAPI Groq backend first
            val remoteResponse = groqChatApi.sendMessage(
                query = query,
                currentUser = user,
                pendingCount = pendingCount,
                activeSummary = activeSummary,
                latestAnnouncement = latestAnnouncementTitle,
                history = repository.chatMessages.value
            )

            if (remoteResponse != null) {
                // Successfully received from Groq/FastAPI backend
                val assistantMessage = ChatMessage(
                    messageId = IdGenerator.generateMessageId(),
                    userId = userId,
                    message = remoteResponse.response,
                    senderType = SenderType.ASSISTANT,
                    timestamp = System.currentTimeMillis(),
                    isFromRemoteAi = remoteResponse.source == "groq",
                    suggestedActions = remoteResponse.suggestedActions ?: emptyList()
                )
                repository.addChatMessage(assistantMessage)
                _uiState.update { it.copy(isTyping = false, isRemoteAiActive = remoteResponse.source == "groq") }

                remoteResponse.targetRoute?.let { target ->
                    onActionTarget?.invoke(target)
                }
            } else {
                // Use intelligent local FAQ engine
                val faqResult = FaqEngine.processQuery(
                    rawQuery = query,
                    currentUser = user,
                    userComplaints = userComplaints,
                    recentAnnouncements = announcements
                )

                val assistantMessage = ChatMessage(
                    messageId = IdGenerator.generateMessageId(),
                    userId = userId,
                    message = faqResult.answer,
                    senderType = SenderType.ASSISTANT,
                    timestamp = System.currentTimeMillis(),
                    isFromRemoteAi = false,
                    suggestedActions = faqResult.suggestedActions
                )
                repository.addChatMessage(assistantMessage)
                _uiState.update { it.copy(isTyping = false, isRemoteAiActive = false) }

                faqResult.targetRoute?.let { target ->
                    onActionTarget?.invoke(target)
                }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }
}
