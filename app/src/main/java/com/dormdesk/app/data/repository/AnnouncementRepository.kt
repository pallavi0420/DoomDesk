package com.dormdesk.app.data.repository

import com.dormdesk.app.data.model.Announcement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnnouncementRepository(
    private val notificationRepository: NotificationRepository? = null
) {
    private val _announcements = MutableStateFlow<List<Announcement>>(DemoDataProvider.getInitialAnnouncements())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    suspend fun createAnnouncement(announcement: Announcement): Result<Announcement> {
        val uniqueId = "ANN-${(200 + _announcements.value.size + 1)}"
        val newAnnouncement = announcement.copy(
            announcementId = uniqueId,
            createdAt = System.currentTimeMillis()
        )
        // Keep pinned notices prioritized at the top
        val currentList = _announcements.value.toMutableList()
        currentList.add(0, newAnnouncement)
        _announcements.value = currentList.sortedWith(compareByDescending<Announcement> { it.isPinned }.thenByDescending { it.createdAt })

        notificationRepository?.notifyGlobalAnnouncement(
            title = "New Notice: ${newAnnouncement.title}",
            message = newAnnouncement.description.take(120),
            referenceId = newAnnouncement.announcementId
        )

        return Result.success(newAnnouncement)
    }

    suspend fun updateAnnouncement(announcement: Announcement): Result<Announcement> {
        val currentList = _announcements.value.toMutableList()
        val index = currentList.indexOfFirst { it.announcementId == announcement.announcementId }
        if (index != -1) {
            currentList[index] = announcement
            _announcements.value = currentList.sortedWith(compareByDescending<Announcement> { it.isPinned }.thenByDescending { it.createdAt })
            return Result.success(announcement)
        }
        return Result.failure(Exception("Announcement not found"))
    }

    suspend fun deleteAnnouncement(announcementId: String): Result<Unit> {
        val currentList = _announcements.value.filter { it.announcementId != announcementId }
        _announcements.value = currentList
        return Result.success(Unit)
    }

    suspend fun togglePin(announcementId: String): Result<Announcement> {
        val currentList = _announcements.value.toMutableList()
        val index = currentList.indexOfFirst { it.announcementId == announcementId }
        if (index != -1) {
            val updated = currentList[index].copy(isPinned = !currentList[index].isPinned)
            currentList[index] = updated
            _announcements.value = currentList.sortedWith(compareByDescending<Announcement> { it.isPinned }.thenByDescending { it.createdAt })
            return Result.success(updated)
        }
        return Result.failure(Exception("Announcement not found"))
    }

    suspend fun markAsRead(announcementId: String) {
        val currentList = _announcements.value.toMutableList()
        val index = currentList.indexOfFirst { it.announcementId == announcementId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isRead = true)
            _announcements.value = currentList
        }
    }
}
