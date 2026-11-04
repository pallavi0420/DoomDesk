package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.*
import com.dormdesk.app.data.repository.DormDeskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ComplaintFilterTab(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    CLOSED("Closed")
}

data class ComplaintsUiState(
    val selectedTab: ComplaintFilterTab = ComplaintFilterTab.ALL,
    val searchQuery: String = "",
    val selectedCategory: ComplaintCategory? = null,
    val selectedPriority: ComplaintPriority? = null,
    val isSubmitting: Boolean = false,
    val submissionSuccessComplaintId: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ComplaintsViewModel(
    private val repository: DormDeskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComplaintsUiState())
    val uiState: StateFlow<ComplaintsUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<User?> = repository.currentUser

    val allComplaints: StateFlow<List<Complaint>> = repository.complaints

    // Filtered list for student (only student's own tickets)
    val studentComplaints: StateFlow<List<Complaint>> = combine(
        repository.complaints,
        repository.currentUser,
        _uiState
    ) { complaints, user, state ->
        val userTickets = if (user != null) {
            complaints.filter { it.studentId == user.uid }
        } else {
            emptyList()
        }

        filterComplaints(userTickets, state.selectedTab, state.searchQuery, state.selectedCategory, state.selectedPriority)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered list for warden (all tickets across hostel)
    val wardenComplaints: StateFlow<List<Complaint>> = combine(
        repository.complaints,
        _uiState
    ) { complaints, state ->
        filterComplaints(complaints, state.selectedTab, state.searchQuery, state.selectedCategory, state.selectedPriority)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun filterComplaints(
        list: List<Complaint>,
        tab: ComplaintFilterTab,
        query: String,
        category: ComplaintCategory?,
        priority: ComplaintPriority?
    ): List<Complaint> {
        return list.filter { cmp ->
            val matchesTab = when (tab) {
                ComplaintFilterTab.ALL -> true
                ComplaintFilterTab.PENDING -> cmp.status == ComplaintStatus.SUBMITTED || cmp.status == ComplaintStatus.RECEIVED
                ComplaintFilterTab.ASSIGNED -> cmp.status == ComplaintStatus.ASSIGNED
                ComplaintFilterTab.IN_PROGRESS -> cmp.status == ComplaintStatus.IN_PROGRESS || cmp.status == ComplaintStatus.RESOLUTION_UPDATED
                ComplaintFilterTab.RESOLVED -> cmp.status == ComplaintStatus.RESOLVED
                ComplaintFilterTab.CLOSED -> cmp.status == ComplaintStatus.CLOSED
            }

            val matchesQuery = query.isBlank() ||
                    cmp.title.contains(query, ignoreCase = true) ||
                    cmp.complaintId.contains(query, ignoreCase = true) ||
                    cmp.studentName.contains(query, ignoreCase = true) ||
                    cmp.location.contains(query, ignoreCase = true) ||
                    cmp.description.contains(query, ignoreCase = true)

            val matchesCategory = category == null || cmp.category == category
            val matchesPriority = priority == null || cmp.priority == priority

            matchesTab && matchesQuery && matchesCategory && matchesPriority
        }.sortedByDescending { it.updatedAt }
    }

    fun setFilterTab(tab: ComplaintFilterTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCategoryFilter(category: ComplaintCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setPriorityFilter(priority: ComplaintPriority?) {
        _uiState.update { it.copy(selectedPriority = priority) }
    }

    fun raiseComplaint(
        title: String,
        category: ComplaintCategory,
        description: String,
        priority: ComplaintPriority,
        location: String,
        imageUrl: String?,
        onSuccess: (String) -> Unit
    ) {
        if (title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a complaint title.") }
            return
        }
        if (description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please describe the problem.") }
            return
        }
        if (location.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter the location (room number or wing).") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.createComplaint(
                title = title,
                category = category,
                description = description,
                priority = priority,
                location = location,
                imageUrl = imageUrl
            )
            result.onSuccess { complaint ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submissionSuccessComplaintId = complaint.complaintId,
                        successMessage = "Complaint ${complaint.complaintId} submitted successfully!"
                    )
                }
                onSuccess(complaint.complaintId)
            }.onFailure { err ->
                _uiState.update { it.copy(isSubmitting = false, errorMessage = err.message ?: "Failed to submit") }
            }
        }
    }

    fun confirmResolution(complaintId: String, rating: Int, feedback: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val result = repository.confirmComplaintResolution(complaintId, rating, feedback)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        successMessage = "Thank you! Complaint resolution confirmed and closed."
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { s -> s.copy(isSubmitting = false, errorMessage = err.message) }
            }
        }
    }

    fun reopenComplaint(complaintId: String, reason: String, onSuccess: () -> Unit) {
        if (reason.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please provide a reason for reopening this ticket.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val result = repository.reopenComplaint(complaintId, reason)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        successMessage = "Ticket reopened and assigned back to warden queue."
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { s -> s.copy(isSubmitting = false, errorMessage = err.message) }
            }
        }
    }

    fun wardenUpdateComplaint(
        complaintId: String,
        newStatus: ComplaintStatus,
        assignedStaffName: String?,
        remarks: String?,
        resolutionNotes: String?,
        completionPhotoUrl: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val result = repository.updateComplaintStatus(
                complaintId = complaintId,
                newStatus = newStatus,
                assignedStaffName = assignedStaffName,
                remarks = remarks,
                resolutionNotes = resolutionNotes,
                completionPhotoUrl = completionPhotoUrl
            )
            result.onSuccess {
                _uiState.update { s ->
                    s.copy(
                        isSubmitting = false,
                        successMessage = "Complaint $complaintId updated to ${newStatus.label}."
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { s -> s.copy(isSubmitting = false, errorMessage = err.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null, submissionSuccessComplaintId = null) }
    }
}
