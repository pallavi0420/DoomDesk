package com.dormdesk.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class AnnouncementCategory(val displayName: String) {
    GENERAL_NOTICES("General Notices"),
    MAINTENANCE_UPDATES("Maintenance Updates"),
    WATER_SUPPLY("Water Supply"),
    ELECTRICITY("Electricity"),
    MESS_UPDATES("Mess Updates"),
    HOSTEL_EVENTS("Hostel Events"),
    EMERGENCY_ANNOUNCEMENTS("Emergency Announcements")
}

@Serializable
data class Announcement(
    val announcementId: String = "",
    val title: String = "",
    val description: String = "",
    val category: AnnouncementCategory = AnnouncementCategory.GENERAL_NOTICES,
    val priority: ComplaintPriority = ComplaintPriority.MEDIUM,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val createdBy: String = "Hostel Warden Office",
    val isRead: Boolean = false
)
