package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.FeedbackCategory
import com.dormdesk.app.data.model.FeedbackItem
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.repository.DormDeskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FeedbackUiState(
    val selectedCategory: FeedbackCategory = FeedbackCategory.HOSTEL_FACILITIES,
    val rating: Int = 5,
    val description: String = "",
    val isAnonymous: Boolean = false,
    val isSubmitting: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class FeedbackViewModel(
    private val repository: DormDeskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<User?> = repository.currentUser

    val allFeedbacks: StateFlow<List<FeedbackItem>> = repository.feedbacks

    val studentFeedbacks: StateFlow<List<FeedbackItem>> = combine(
        repository.feedbacks,
        repository.currentUser
    ) { list, user ->
        if (user != null) {
            list.filter { it.studentId == user.uid }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategory(category: FeedbackCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setRating(rating: Int) {
        _uiState.update { it.copy(rating = rating.coerceIn(1, 5)) }
    }

    fun setDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun setAnonymous(isAnonymous: Boolean) {
        _uiState.update { it.copy(isAnonymous = isAnonymous) }
    }

    fun submitFeedback(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your feedback comments.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.submitFeedback(
                category = state.selectedCategory,
                description = state.description,
                rating = state.rating,
                isAnonymous = state.isAnonymous
            )
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        description = "",
                        rating = 5,
                        isAnonymous = false,
                        successMessage = "Feedback submitted successfully! Thank you for helping improve the hostel."
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { s -> s.copy(isSubmitting = false, errorMessage = err.message) }
            }
        }
    }

    fun wardenRespond(feedbackId: String, responseText: String, onSuccess: () -> Unit) {
        if (responseText.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a response.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val result = repository.respondToFeedback(feedbackId, responseText)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        successMessage = "Response sent to resident."
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { s -> s.copy(isSubmitting = false, errorMessage = err.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
