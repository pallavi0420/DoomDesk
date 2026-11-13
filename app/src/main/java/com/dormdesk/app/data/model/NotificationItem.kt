package com.dormdesk.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class NotificationType {
    COMPLAINT,
    ANNOUNCEMENT,
    FEEDBACK,
    SYSTEM
}

@Serializable
data class NotificationItem(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val type: NotificationType = NotificationType.SYSTEM,
    val referenceId: String = ""
)
