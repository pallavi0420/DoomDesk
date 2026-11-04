package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.NotificationItem
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.repository.DormDeskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val repository: DormDeskRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = repository.currentUser

    val notifications: StateFlow<List<NotificationItem>> = combine(
        repository.notifications,
        repository.currentUser
    ) { list, user ->
        list.filter { notif ->
            user == null || notif.userId == user.uid || notif.userId == "all"
        }.sortedByDescending { it.createdAt }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }
}
