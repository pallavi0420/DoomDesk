package com.dormdesk.app.data.model

enum class NotificationType {
    COMPLAINT_REGISTERED,
    COMPLAINT_STATUS_CHANGED,
    WARDEN_REMARK,
    RESOLUTION_UPDATED,
    NEW_ANNOUNCEMENT,
    FEEDBACK_RESPONSE
}

data class NotificationItem(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.COMPLAINT_STATUS_CHANGED,
    val relatedEntityId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
