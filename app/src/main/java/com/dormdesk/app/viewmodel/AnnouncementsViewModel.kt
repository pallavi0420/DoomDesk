package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.AnnouncementCategory
import com.dormdesk.app.data.model.AnnouncementPriority
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.repository.DormDeskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AnnouncementsUiState(
    val selectedCategory: AnnouncementCategory? = null,
    val searchQuery: String = "",
    val isActionInProgress: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class AnnouncementsViewModel(
    private val repository: DormDeskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnouncementsUiState())
    val uiState: StateFlow<AnnouncementsUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<User?> = repository.currentUser

    val filteredAnnouncements: StateFlow<List<Announcement>> = combine(
        repository.announcements,
        _uiState
    ) { list, state ->
        list.filter { ann ->
            val matchesCategory = state.selectedCategory == null || ann.category == state.selectedCategory
            val matchesQuery = state.searchQuery.isBlank() ||
                    ann.title.contains(state.searchQuery, ignoreCase = true) ||
                    ann.description.contains(state.searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }.sortedWith(
            compareByDescending<Announcement> { it.isPinned }
                .thenByDescending { it.createdAt }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategory(category: AnnouncementCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun markAsRead(announcementId: String) {
        viewModelScope.launch {
            repository.markAnnouncementAsRead(announcementId)
        }
    }

    fun createAnnouncement(
        title: String,
        description: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        isPinned: Boolean,
        onSuccess: () -> Unit
    ) {
        if (title.isBlank() || description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter both title and description.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            val result = repository.createAnnouncement(title, description, category, priority, isPinned)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isActionInProgress = false,
                        successMessage = "Announcement published successfully!"
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { s -> s.copy(isActionInProgress = false, errorMessage = err.message) }
            }
        }
    }

    fun updateAnnouncement(
        announcementId: String,
        title: String,
        description: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        isPinned: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            val result = repository.updateAnnouncement(announcementId, title, description, category, priority, isPinned)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isActionInProgress = false,
                        successMessage = "Announcement updated."
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { s -> s.copy(isActionInProgress = false, errorMessage = err.message) }
            }
        }
    }

    fun deleteAnnouncement(announcementId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            repository.deleteAnnouncement(announcementId)
            _uiState.update {
                it.copy(
                    isActionInProgress = false,
                    successMessage = "Announcement deleted."
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
