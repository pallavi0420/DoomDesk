package com.dormdesk.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Complaint(
    val complaintId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val roomNumber: String = "",
    val title: String = "",
    val description: String = "",
    val category: ComplaintCategory = ComplaintCategory.OTHER,
    val priority: ComplaintPriority = ComplaintPriority.MEDIUM,
    val location: String = "",
    val imageUrl: String? = null,
    val completionImageUrl: String? = null,
    val status: ComplaintStatus = ComplaintStatus.SUBMITTED,
    val assignedStaffName: String = "",
    val remarks: String = "",
    val resolutionNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val studentRating: Int = 0,
    val studentConfirmed: Boolean = false,
    val reopenReason: String = ""
) {
    val isPending: Boolean get() = status == ComplaintStatus.SUBMITTED || status == ComplaintStatus.RECEIVED
    val isAssigned: Boolean get() = status == ComplaintStatus.ASSIGNED
    val isInProgress: Boolean get() = status == ComplaintStatus.IN_PROGRESS || status == ComplaintStatus.RESOLUTION_UPDATED
    val isResolved: Boolean get() = status == ComplaintStatus.RESOLVED
    val isClosed: Boolean get() = status == ComplaintStatus.CLOSED
    val isUrgent: Boolean get() = priority == ComplaintPriority.URGENT
}
