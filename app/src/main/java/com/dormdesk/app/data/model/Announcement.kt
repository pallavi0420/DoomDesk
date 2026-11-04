package com.dormdesk.app.data.model

enum class AnnouncementCategory(val label: String) {
    GENERAL_NOTICES("General Notices"),
    MAINTENANCE_UPDATES("Maintenance Updates"),
    WATER_SUPPLY("Water Supply"),
    ELECTRICITY("Electricity"),
    MESS_UPDATES("Mess Updates"),
    HOSTEL_EVENTS("Hostel Events"),
    EMERGENCY("Emergency Announcements")
}

enum class AnnouncementPriority(val label: String) {
    NORMAL("Normal"),
    HIGH("High"),
    URGENT("Emergency / Urgent")
}

data class Announcement(
    val announcementId: String = "",
    val title: String = "",
    val description: String = "",
    val category: AnnouncementCategory = AnnouncementCategory.GENERAL_NOTICES,
    val priority: AnnouncementPriority = AnnouncementPriority.NORMAL,
    val isPinned: Boolean = false,
    val authorName: String = "Hostel Administration",
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
