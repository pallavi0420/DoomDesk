package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.NotificationItem
import com.dormdesk.app.data.repository.NotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val allNotifications: StateFlow<List<NotificationItem>> = notificationRepository.notifications

    fun getNotificationsForUser(userId: String): List<NotificationItem> {
        return notificationRepository.getNotificationsForUser(userId)
    }

    fun getUnreadCount(userId: String): Int {
        return getNotificationsForUser(userId).count { !it.isRead }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            notificationRepository.markAllAsRead(userId)
        }
    }
}
