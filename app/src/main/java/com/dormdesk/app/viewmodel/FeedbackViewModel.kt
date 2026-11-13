package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.Feedback
import com.dormdesk.app.data.model.FeedbackCategory
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.repository.FeedbackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val feedbackRepository: FeedbackRepository
) : ViewModel() {

    val allFeedbacks: StateFlow<List<Feedback>> = feedbackRepository.feedbacks

    private val _selectedCategory = MutableStateFlow<FeedbackCategory?>(null)
    val selectedCategory: StateFlow<FeedbackCategory?> = _selectedCategory.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submissionSuccess = MutableStateFlow(false)
    val submissionSuccess: StateFlow<Boolean> = _submissionSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun setCategory(category: FeedbackCategory?) {
        _selectedCategory.value = category
    }

    fun getFilteredFeedbacks(forStudentId: String? = null): List<Feedback> {
        val base = if (forStudentId != null) {
            allFeedbacks.value.filter { it.studentId == forStudentId }
        } else {
            allFeedbacks.value
        }
        val cat = _selectedCategory.value
        return if (cat == null) base else base.filter { it.category == cat }
    }

    fun submitFeedback(
        currentUser: User,
        category: FeedbackCategory,
        rating: Int,
        description: String,
        isAnonymous: Boolean
    ) {
        if (description.isBlank()) {
            _errorMessage.value = "Please write a brief description of your feedback."
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            val feedback = Feedback(
                studentId = currentUser.uid,
                studentName = currentUser.fullName,
                category = category,
                description = description.trim(),
                rating = rating,
                isAnonymous = isAnonymous
            )

            val result = feedbackRepository.submitFeedback(feedback)
            _isSubmitting.value = false
            result.onSuccess {
                _submissionSuccess.value = true
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "Failed to submit feedback"
            }
        }
    }

    fun respondToFeedback(feedbackId: String, adminReply: String) {
        if (adminReply.isBlank()) {
            _errorMessage.value = "Please enter an administrative response."
            return
        }
        viewModelScope.launch {
            feedbackRepository.respondToFeedback(feedbackId, adminReply.trim())
        }
    }

    fun resetSubmission() {
        _submissionSuccess.value = false
        _errorMessage.value = null
    }
}
