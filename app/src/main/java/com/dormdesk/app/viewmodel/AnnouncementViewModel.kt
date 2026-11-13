package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.AnnouncementCategory
import com.dormdesk.app.data.model.ComplaintPriority
import com.dormdesk.app.data.repository.AnnouncementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnnouncementViewModel(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    val allAnnouncements: StateFlow<List<Announcement>> = announcementRepository.announcements

    private val _selectedCategory = MutableStateFlow<AnnouncementCategory?>(null)
    val selectedCategory: StateFlow<AnnouncementCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun setCategory(category: AnnouncementCategory?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredAnnouncements(): List<Announcement> {
        val q = _searchQuery.value.trim().lowercase()
        val cat = _selectedCategory.value

        return allAnnouncements.value.filter { a ->
            val matchesSearch = q.isEmpty() ||
                    a.title.lowercase().contains(q) ||
                    a.description.lowercase().contains(q)
            val matchesCategory = cat == null || a.category == cat
            matchesSearch && matchesCategory
        }
    }

    fun createAnnouncement(
        title: String,
        description: String,
        category: AnnouncementCategory,
        priority: ComplaintPriority,
        isPinned: Boolean,
        author: String = "Hostel Warden Office"
    ) {
        if (title.isBlank() || description.isBlank()) {
            _errorMessage.value = "Title and description cannot be empty."
            return
        }
        viewModelScope.launch {
            val announcement = Announcement(
                title = title.trim(),
                description = description.trim(),
                category = category,
                priority = priority,
                isPinned = isPinned,
                createdBy = author
            )
            announcementRepository.createAnnouncement(announcement)
        }
    }

    fun updateAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            announcementRepository.updateAnnouncement(announcement)
        }
    }

    fun deleteAnnouncement(announcementId: String) {
        viewModelScope.launch {
            announcementRepository.deleteAnnouncement(announcementId)
        }
    }

    fun togglePin(announcementId: String) {
        viewModelScope.launch {
            announcementRepository.togglePin(announcementId)
        }
    }

    fun markAsRead(announcementId: String) {
        viewModelScope.launch {
            announcementRepository.markAsRead(announcementId)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
