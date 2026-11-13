package com.dormdesk.app.data.repository

import com.dormdesk.app.data.model.NotificationItem
import com.dormdesk.app.data.model.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationRepository {
    private val _notifications = MutableStateFlow<List<NotificationItem>>(
        DemoDataProvider.getInitialNotifications(DemoDataProvider.studentUser1.uid)
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun getNotificationsForUser(userId: String): List<NotificationItem> {
        return _notifications.value.filter { it.userId == userId || it.userId.isEmpty() }
    }

    fun notifyComplaintEvent(userId: String, title: String, message: String, referenceId: String) {
        val item = NotificationItem(
            notificationId = "NOTIF-${System.currentTimeMillis()}",
            userId = userId,
            title = title,
            message = message,
            isRead = false,
            createdAt = System.currentTimeMillis(),
            type = NotificationType.COMPLAINT,
            referenceId = referenceId
        )
        _notifications.value = listOf(item) + _notifications.value
    }

    fun notifyFeedbackResponse(userId: String, title: String, message: String, referenceId: String) {
        val item = NotificationItem(
            notificationId = "NOTIF-${System.currentTimeMillis()}",
            userId = userId,
            title = title,
            message = message,
            isRead = false,
            createdAt = System.currentTimeMillis(),
            type = NotificationType.FEEDBACK,
            referenceId = referenceId
        )
        _notifications.value = listOf(item) + _notifications.value
    }

    fun notifyGlobalAnnouncement(title: String, message: String, referenceId: String) {
        val item = NotificationItem(
            notificationId = "NOTIF-${System.currentTimeMillis()}",
            userId = "", // broadcast to all
            title = title,
            message = message,
            isRead = false,
            createdAt = System.currentTimeMillis(),
            type = NotificationType.ANNOUNCEMENT,
            referenceId = referenceId
        )
        _notifications.value = listOf(item) + _notifications.value
    }

    suspend fun markAsRead(notificationId: String) {
        val currentList = _notifications.value.toMutableList()
        val index = currentList.indexOfFirst { it.notificationId == notificationId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isRead = true)
            _notifications.value = currentList
        }
    }

    suspend fun markAllAsRead(userId: String) {
        _notifications.value = _notifications.value.map { item ->
            if (item.userId == userId || item.userId.isEmpty()) {
                item.copy(isRead = true)
            } else item
        }
    }
}
