package com.dormdesk.app.data.model

enum class ComplaintCategory(val label: String) {
    ELECTRICAL("Electrical"),
    PLUMBING("Plumbing"),
    CLEANING_HYGIENE("Cleaning & Hygiene"),
    FURNITURE("Furniture"),
    INTERNET_WIFI("Internet / Wi-Fi"),
    WASHROOM("Washroom"),
    WATER_SUPPLY("Water Supply"),
    SECURITY("Security"),
    MESS_FOOD("Mess / Food"),
    OTHER("Other")
}

enum class ComplaintPriority(val label: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4)
}

enum class ComplaintStatus(val label: String, val stepIndex: Int) {
    SUBMITTED("Submitted", 1),
    RECEIVED("Received by Warden", 2),
    ASSIGNED("Maintenance Assigned", 3),
    IN_PROGRESS("Work in Progress", 4),
    RESOLUTION_UPDATED("Resolution Updated", 5),
    RESOLVED("Resolved", 6),
    CLOSED("Closed", 7);

    val isPending: Boolean
        get() = this == SUBMITTED || this == RECEIVED

    val isActive: Boolean
        get() = this != CLOSED && this != RESOLVED

    val isResolvedOrClosed: Boolean
        get() = this == RESOLVED || this == CLOSED
}

data class ActivityLogEntry(
    val id: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val action: String = "",
    val actorRole: String = "System",
    val actorName: String = "",
    val note: String = ""
)

data class Complaint(
    val complaintId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val studentPhone: String = "",
    val hostel: String = "",
    val block: String = "",
    val roomNumber: String = "",
    val title: String = "",
    val description: String = "",
    val category: ComplaintCategory = ComplaintCategory.OTHER,
    val priority: ComplaintPriority = ComplaintPriority.MEDIUM,
    val location: String = "",
    val imageUrl: String? = null,
    val status: ComplaintStatus = ComplaintStatus.SUBMITTED,
    val assignedStaffName: String? = null,
    val remarks: String? = null,
    val resolutionNotes: String? = null,
    val resolutionImageUrl: String? = null,
    val isConfirmedByStudent: Boolean = false,
    val studentRating: Int = 0, // 1 to 5
    val studentFeedback: String? = null,
    val reopenReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val activityLogs: List<ActivityLogEntry> = emptyList()
)
