package com.dormdesk.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class ChatSender {
    USER,
    ASSISTANT
}

@Serializable
data class ChatMessage(
    val messageId: String = "",
    val userId: String = "",
    val message: String = "",
    val senderType: ChatSender = ChatSender.USER,
    val timestamp: Long = System.currentTimeMillis(),
    val isGroqAi: Boolean = false,
    val suggestedActionRoute: String? = null
)
