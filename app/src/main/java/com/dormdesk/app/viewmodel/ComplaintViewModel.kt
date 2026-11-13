package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.*
import com.dormdesk.app.data.repository.ComplaintRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ComplaintFilter(val displayName: String) {
    ALL("All"),
    PENDING("Pending"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    CLOSED("Closed")
}

class ComplaintViewModel(
    private val complaintRepository: ComplaintRepository
) : ViewModel() {

    val allComplaints: StateFlow<List<Complaint>> = complaintRepository.complaints

    private val _selectedFilter = MutableStateFlow(ComplaintFilter.ALL)
    val selectedFilter: StateFlow<ComplaintFilter> = _selectedFilter.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ComplaintCategory?>(null)
    val selectedCategory: StateFlow<ComplaintCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submissionSuccess = MutableStateFlow<String?>(null)
    val submissionSuccess: StateFlow<String?> = _submissionSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun setFilter(filter: ComplaintFilter) {
        _selectedFilter.value = filter
    }

    fun setCategory(category: ComplaintCategory?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredComplaints(forStudentId: String? = null): List<Complaint> {
        val base = if (forStudentId != null) {
            allComplaints.value.filter { it.studentId == forStudentId }
        } else {
            allComplaints.value
        }

        val q = _searchQuery.value.trim().lowercase()
        val cat = _selectedCategory.value
        val filt = _selectedFilter.value

        return base.filter { c ->
            val matchesSearch = q.isEmpty() ||
                    c.title.lowercase().contains(q) ||
                    c.complaintId.lowercase().contains(q) ||
                    c.description.lowercase().contains(q) ||
                    c.location.lowercase().contains(q) ||
                    c.studentName.lowercase().contains(q)

            val matchesCategory = cat == null || c.category == cat

            val matchesStatus = when (filt) {
                ComplaintFilter.ALL -> true
                ComplaintFilter.PENDING -> c.isPending
                ComplaintFilter.ASSIGNED -> c.isAssigned
                ComplaintFilter.IN_PROGRESS -> c.isInProgress
                ComplaintFilter.RESOLVED -> c.isResolved
                ComplaintFilter.CLOSED -> c.isClosed
            }

            matchesSearch && matchesCategory && matchesStatus
        }
    }

    fun raiseComplaint(
        currentUser: User,
        title: String,
        category: ComplaintCategory,
        priority: ComplaintPriority,
        description: String,
        location: String,
        imageUrl: String?,
        onSuccess: (String) -> Unit
    ) {
        if (title.isBlank() || description.isBlank() || location.isBlank()) {
            _errorMessage.value = "Please complete all required fields."
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            val newComplaint = Complaint(
                studentId = currentUser.uid,
                studentName = currentUser.fullName,
                roomNumber = currentUser.roomNumber,
                title = title.trim(),
                category = category,
                priority = priority,
                description = description.trim(),
                location = location.trim(),
                imageUrl = imageUrl
            )

            val result = complaintRepository.createComplaint(newComplaint)
            _isSubmitting.value = false
            result.onSuccess { created ->
                _submissionSuccess.value = created.complaintId
                onSuccess(created.complaintId)
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "Failed to submit complaint"
            }
        }
    }

    fun assignStaff(complaintId: String, staffName: String, remarks: String) {
        if (staffName.isBlank()) {
            _errorMessage.value = "Please enter maintenance technician's name."
            return
        }
        viewModelScope.launch {
            complaintRepository.assignStaff(complaintId, staffName.trim(), remarks.trim())
        }
    }

    fun updateStatus(complaintId: String, newStatus: ComplaintStatus, remarks: String) {
        viewModelScope.launch {
            complaintRepository.updateStatus(complaintId, newStatus, remarks)
        }
    }

    fun updateResolution(complaintId: String, notes: String, completionImageUrl: String? = null) {
        if (notes.isBlank()) {
            _errorMessage.value = "Please enter resolution notes."
            return
        }
        viewModelScope.launch {
            complaintRepository.updateResolution(complaintId, notes.trim(), completionImageUrl)
        }
    }

    fun addRemarks(complaintId: String, remarks: String) {
        if (remarks.isBlank()) return
        viewModelScope.launch {
            complaintRepository.addRemarks(complaintId, remarks.trim())
        }
    }

    fun confirmResolution(complaintId: String, rating: Int) {
        viewModelScope.launch {
            complaintRepository.confirmResolution(complaintId, rating)
        }
    }

    fun reopenComplaint(complaintId: String, reason: String) {
        if (reason.isBlank()) {
            _errorMessage.value = "Please explain why the issue is unresolved."
            return
        }
        viewModelScope.launch {
            complaintRepository.reopenComplaint(complaintId, reason.trim())
        }
    }

    fun administrativeClose(complaintId: String, reason: String) {
        if (reason.isBlank()) {
            _errorMessage.value = "Please state closing reason."
            return
        }
        viewModelScope.launch {
            complaintRepository.administrativeClose(complaintId, reason.trim())
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _submissionSuccess.value = null
    }
}
