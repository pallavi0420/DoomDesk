package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.ChatMessage
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.remote.LocalFaqEngine
import com.dormdesk.app.data.remote.RetrofitClient
import com.dormdesk.app.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val messages: StateFlow<List<ChatMessage>> = chatRepository.messages

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _backendStatus = MutableStateFlow("Local FAQ Assistant")
    val backendStatus: StateFlow<String> = _backendStatus.asStateFlow()

    val suggestedQuestions: List<String> = LocalFaqEngine.suggestedQuestions

    init {
        checkBackendHealth()
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun checkBackendHealth() {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.apiService.checkHealth()
                if (resp.isSuccessful && resp.body() != null) {
                    val body = resp.body()!!
                    _backendStatus.value = if (body.groqConfigured) "Groq AI Active (${body.model})" else "Local FAQ Backend"
                } else {
                    _backendStatus.value = "Local Assistant"
                }
            } catch (e: Exception) {
                _backendStatus.value = "Local Assistant (Standalone)"
            }
        }
    }

    fun sendMessage(
        currentUser: User?,
        userComplaints: List<Complaint>,
        announcements: List<Announcement>,
        customMessage: String? = null
    ) {
        val messageToSend = customMessage ?: _inputText.value.trim()
        if (messageToSend.isBlank()) return

        if (customMessage == null) {
            _inputText.value = ""
        }

        viewModelScope.launch {
            _isTyping.value = true
            chatRepository.sendMessage(
                userMessageText = messageToSend,
                currentUser = currentUser,
                userComplaints = userComplaints,
                announcements = announcements
            )
            _isTyping.value = false
        }
    }

    fun clearHistory() {
        chatRepository.clearChat()
    }
}
