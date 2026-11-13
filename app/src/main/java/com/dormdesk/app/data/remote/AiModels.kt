package com.dormdesk.app.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class HistoryMessage(
    val role: String, // "user" or "assistant"
    val content: String
)

@Serializable
data class ChatRequest(
    val message: String,
    val studentId: String? = null,
    val studentName: String? = null,
    val roomNumber: String? = null,
    val activeComplaintsSummary: String? = null,
    val conversationHistory: List<HistoryMessage> = emptyList()
)

@Serializable
data class ChatResponse(
    val reply: String,
    val source: String = "gemini",
    val isFallback: Boolean = false,
    val actionRoute: String? = null
)

@Serializable
data class HealthResponse(
    val status: String,
    val aiConfigured: Boolean,
    val provider: String = "Google Gemini",
    val model: String
)

@Serializable
data class FaqItem(
    val question: String,
    val answer: String,
    val category: String,
    val actionRoute: String? = null
)
