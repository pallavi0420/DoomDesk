package com.dormdesk.app.data.model

enum class SenderType {
    USER,
    ASSISTANT,
    SYSTEM
}

data class ChatMessage(
    val messageId: String = "",
    val userId: String = "",
    val message: String = "",
    val senderType: SenderType = SenderType.ASSISTANT,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromRemoteAi: Boolean = false,
    val suggestedActions: List<String> = emptyList()
)
