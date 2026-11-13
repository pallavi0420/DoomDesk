package com.dormdesk.app.data.repository

import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.ComplaintStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class ComplaintRepository(
    private val notificationRepository: NotificationRepository? = null
) {
    private val _complaints = MutableStateFlow<List<Complaint>>(DemoDataProvider.getInitialComplaints())
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    fun getComplaintsForStudent(studentId: String): List<Complaint> {
        return _complaints.value.filter { it.studentId == studentId }
    }

    fun getComplaintById(complaintId: String): Complaint? {
        return _complaints.value.find { it.complaintId == complaintId }
    }

    suspend fun createComplaint(complaint: Complaint): Result<Complaint> {
        val uniqueId = "DD-${(1000 + _complaints.value.size + 1)}"
        val newComplaint = complaint.copy(
            complaintId = uniqueId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            status = ComplaintStatus.SUBMITTED
        )
        val updatedList = listOf(newComplaint) + _complaints.value
        _complaints.value = updatedList

        // Trigger notification
        notificationRepository?.notifyComplaintEvent(
            userId = newComplaint.studentId,
            title = "Complaint Registered (#${newComplaint.complaintId})",
            message = "Your complaint '${newComplaint.title}' has been submitted to the Warden.",
            referenceId = newComplaint.complaintId
        )

        return Result.success(newComplaint)
    }

    suspend fun updateStatus(
        complaintId: String,
        newStatus: ComplaintStatus,
        remarks: String = ""
    ): Result<Complaint> {
        var updatedItem: Complaint? = null
        val currentList = _complaints.value.toMutableList()
        val index = currentList.indexOfFirst { it.complaintId == complaintId }
        if (index != -1) {
            val existing = currentList[index]
            val updated = existing.copy(
                status = newStatus,
                remarks = if (remarks.isNotBlank()) remarks else existing.remarks,
                updatedAt = System.currentTimeMillis()
            )
            currentList[index] = updated
            _complaints.value = currentList
            updatedItem = updated

            notificationRepository?.notifyComplaintEvent(
                userId = updated.studentId,
                title = "Status Changed: ${newStatus.displayName}",
                message = "Complaint #${updated.complaintId} has been updated to '${newStatus.displayName}'.",
                referenceId = updated.complaintId
            )
            return Result.success(updated)
        }
        return Result.failure(Exception("Complaint not found"))
    }

    suspend fun assignStaff(
        complaintId: String,
        staffName: String,
        remarks: String = ""
    ): Result<Complaint> {
        val currentList = _complaints.value.toMutableList()
        val index = currentList.indexOfFirst { it.complaintId == complaintId }
        if (index != -1) {
            val existing = currentList[index]
            val updated = existing.copy(
                assignedStaffName = staffName,
                status = ComplaintStatus.ASSIGNED,
                remarks = if (remarks.isNotBlank()) remarks else "Technician $staffName assigned.",
                updatedAt = System.currentTimeMillis()
            )
            currentList[index] = updated
            _complaints.value = currentList

            notificationRepository?.notifyComplaintEvent(
                userId = updated.studentId,
                title = "Maintenance Assigned: #${updated.complaintId}",
                message = "Warden assigned $staffName to attend to your complaint.",
                referenceId = updated.complaintId
            )
            return Result.success(updated)
        }
        return Result.failure(Exception("Complaint not found"))
    }

    suspend fun updateResolution(
        complaintId: String,
        notes: String,
        completionImageUrl: String? = null
    ): Result<Complaint> {
        val currentList = _complaints.value.toMutableList()
        val index = currentList.indexOfFirst { it.complaintId == complaintId }
        if (index != -1) {
            val existing = currentList[index]
            val updated = existing.copy(
                resolutionNotes = notes,
                completionImageUrl = completionImageUrl ?: existing.completionImageUrl,
                status = ComplaintStatus.RESOLUTION_UPDATED,
                updatedAt = System.currentTimeMillis()
            )
            currentList[index] = updated
            _complaints.value = currentList

            notificationRepository?.notifyComplaintEvent(
                userId = updated.studentId,
                title = "Resolution Updated: #${updated.complaintId}",
                message = "Maintenance completed work on '${updated.title}'. Please verify.",
                referenceId = updated.complaintId
            )
            return Result.success(updated)
        }
        return Result.failure(Exception("Complaint not found"))
    }

    suspend fun addRemarks(complaintId: String, remarks: String): Result<Complaint> {
        val currentList = _complaints.value.toMutableList()
        val index = currentList.indexOfFirst { it.complaintId == complaintId }
        if (index != -1) {
            val existing = currentList[index]
            val updated = existing.copy(
                remarks = remarks,
                updatedAt = System.currentTimeMillis()
            )
            currentList[index] = updated
            _complaints.value = currentList
            return Result.success(updated)
        }
        return Result.failure(Exception("Complaint not found"))
    }

    suspend fun confirmResolution(complaintId: String, rating: Int): Result<Complaint> {
        val currentList = _complaints.value.toMutableList()
        val index = currentList.indexOfFirst { it.complaintId == complaintId }
        if (index != -1) {
            val existing = currentList[index]
            val updated = existing.copy(
                status = ComplaintStatus.CLOSED,
                studentConfirmed = true,
                studentRating = rating,
                updatedAt = System.currentTimeMillis()
            )
            currentList[index] = updated
            _complaints.value = currentList

            notificationRepository?.notifyComplaintEvent(
                userId = updated.studentId,
                title = "Complaint Closed: #${updated.complaintId}",
                message = "Thank you for confirming resolution! Rating: $rating/5 stars.",
                referenceId = updated.complaintId
            )
            return Result.success(updated)
        }
        return Result.failure(Exception("Complaint not found"))
    }

    suspend fun reopenComplaint(complaintId: String, reason: String): Result<Complaint> {
        val currentList = _complaints.value.toMutableList()
        val index = currentList.indexOfFirst { it.complaintId == complaintId }
        if (index != -1) {
            val existing = currentList[index]
            val updated = existing.copy(
                status = ComplaintStatus.IN_PROGRESS,
                reopenReason = reason,
                remarks = "Reopened by resident: $reason",
                studentConfirmed = false,
                updatedAt = System.currentTimeMillis()
            )
            currentList[index] = updated
            _complaints.value = currentList

            notificationRepository?.notifyComplaintEvent(
                userId = updated.studentId,
                title = "Complaint Reopened: #${updated.complaintId}",
                message = "Your complaint was reopened for re-inspection.",
                referenceId = updated.complaintId
            )
            return Result.success(updated)
        }
        return Result.failure(Exception("Complaint not found"))
    }

    suspend fun administrativeClose(complaintId: String, reason: String): Result<Complaint> {
        val currentList = _complaints.value.toMutableList()
        val index = currentList.indexOfFirst { it.complaintId == complaintId }
        if (index != -1) {
            val existing = currentList[index]
            val updated = existing.copy(
                status = ComplaintStatus.CLOSED,
                remarks = "Admin Closed: $reason",
                updatedAt = System.currentTimeMillis()
            )
            currentList[index] = updated
            _complaints.value = currentList
            return Result.success(updated)
        }
        return Result.failure(Exception("Complaint not found"))
    }
}
