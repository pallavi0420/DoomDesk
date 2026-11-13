package com.dormdesk.app.data.repository

import com.dormdesk.app.data.model.Feedback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedbackRepository(
    private val notificationRepository: NotificationRepository? = null
) {
    private val _feedbacks = MutableStateFlow<List<Feedback>>(DemoDataProvider.getInitialFeedbacks())
    val feedbacks: StateFlow<List<Feedback>> = _feedbacks.asStateFlow()

    fun getFeedbacksForStudent(studentId: String): List<Feedback> {
        return _feedbacks.value.filter { it.studentId == studentId }
    }

    suspend fun submitFeedback(feedback: Feedback): Result<Feedback> {
        val uniqueId = "FB-${(500 + _feedbacks.value.size + 1)}"
        val newFeedback = feedback.copy(
            feedbackId = uniqueId,
            createdAt = System.currentTimeMillis()
        )
        _feedbacks.value = listOf(newFeedback) + _feedbacks.value
        return Result.success(newFeedback)
    }

    suspend fun respondToFeedback(feedbackId: String, adminReply: String): Result<Feedback> {
        val currentList = _feedbacks.value.toMutableList()
        val index = currentList.indexOfFirst { it.feedbackId == feedbackId }
        if (index != -1) {
            val existing = currentList[index]
            val updated = existing.copy(
                adminResponse = adminReply,
                isReviewed = true
            )
            currentList[index] = updated
            _feedbacks.value = currentList

            if (!existing.isAnonymous && existing.studentId.isNotBlank()) {
                notificationRepository?.notifyFeedbackResponse(
                    userId = existing.studentId,
                    title = "Warden Responded to Your Feedback",
                    message = adminReply.take(120),
                    referenceId = existing.feedbackId
                )
            }
            return Result.success(updated)
        }
        return Result.failure(Exception("Feedback record not found"))
    }
}
