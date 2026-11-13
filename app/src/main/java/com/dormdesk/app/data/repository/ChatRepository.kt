package com.dormdesk.app.data.repository

import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.ChatMessage
import com.dormdesk.app.data.model.ChatSender
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.remote.ChatRequest
import com.dormdesk.app.data.remote.HistoryMessage
import com.dormdesk.app.data.remote.LocalFaqEngine
import com.dormdesk.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatRepository {

    private val initialWelcome = ChatMessage(
        messageId = "msg_init",
        userId = "system",
        message = "Hello! I am DormDesk AI, your hostel assistant. How can I assist you with your room, complaints, or hostel updates today?",
        senderType = ChatSender.ASSISTANT,
        timestamp = System.currentTimeMillis() - 60000,
        isGeminiAi = false
    )

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(initialWelcome))
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    suspend fun sendMessage(
        userMessageText: String,
        currentUser: User?,
        userComplaints: List<Complaint>,
        announcements: List<Announcement>
    ): ChatMessage {
        val userMsgId = "msg_${System.currentTimeMillis()}"
        val userMsg = ChatMessage(
            messageId = userMsgId,
            userId = currentUser?.uid ?: "guest",
            message = userMessageText,
            senderType = ChatSender.USER,
            timestamp = System.currentTimeMillis()
        )
        _messages.value = _messages.value + userMsg

        // Prepare active complaints summary for contextual AI understanding
        val activeSummary = userComplaints.filter { !it.isClosed }
            .take(5)
            .joinToString("; ") { "#${it.complaintId} (${it.category.displayName}): ${it.title} [${it.status.displayName}]" }

        val historyList = _messages.value.takeLast(6).map {
            HistoryMessage(
                role = if (it.senderType == ChatSender.USER) "user" else "assistant",
                content = it.message
            )
        }

        // Try FastAPI backend first
        var assistantReply: String
        var isGemini = false
        var actionRoute: String? = null

        try {
            val response = RetrofitClient.apiService.sendMessage(
                ChatRequest(
                    message = userMessageText,
                    studentId = currentUser?.studentId,
                    studentName = currentUser?.fullName,
                    roomNumber = currentUser?.roomNumber,
                    activeComplaintsSummary = activeSummary,
                    conversationHistory = historyList
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                assistantReply = body.reply
                isGemini = !body.isFallback
                actionRoute = body.actionRoute
            } else {
                // Fallback to local engine
                val localResponse = LocalFaqEngine.generateResponse(
                    userQuery = userMessageText,
                    currentUser = currentUser,
                    userComplaints = userComplaints,
                    announcements = announcements
                )
                assistantReply = localResponse.reply
                isGemini = false
                actionRoute = localResponse.actionRoute
            }
        } catch (e: Exception) {
            // Network failure or FastAPI server not running -> Use robust Local FAQ Assistant
            val localResponse = LocalFaqEngine.generateResponse(
                userQuery = userMessageText,
                currentUser = currentUser,
                userComplaints = userComplaints,
                announcements = announcements
            )
            assistantReply = localResponse.reply
            isGemini = false
            actionRoute = localResponse.actionRoute
        }

        val assistantMsg = ChatMessage(
            messageId = "msg_${System.currentTimeMillis()}",
            userId = currentUser?.uid ?: "guest",
            message = assistantReply,
            senderType = ChatSender.ASSISTANT,
            timestamp = System.currentTimeMillis(),
            isGeminiAi = isGemini,
            suggestedActionRoute = actionRoute
        )

        _messages.value = _messages.value + assistantMsg
        return assistantMsg
    }

    fun clearChat() {
        _messages.value = listOf(initialWelcome)
    }
}
