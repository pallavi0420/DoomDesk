package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.ComplaintStatus
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.repository.DormDeskRepository
import kotlinx.coroutines.flow.*

data class StudentDashboardUiState(
    val currentUser: User? = null,
    val totalComplaintsCount: Int = 0,
    val pendingComplaintsCount: Int = 0,
    val resolvedComplaintsCount: Int = 0,
    val latestAnnouncement: Announcement? = null,
    val recentComplaints: List<Complaint> = emptyList(),
    val unreadNotificationsCount: Int = 0
)

class StudentDashboardViewModel(
    private val repository: DormDeskRepository
) : ViewModel() {

    val uiState: StateFlow<StudentDashboardUiState> = combine(
        repository.currentUser,
        repository.complaints,
        repository.announcements,
        repository.notifications
    ) { user, complaintsList, announcementsList, notifsList ->
        val studentComplaints = if (user != null) {
            complaintsList.filter { it.studentId == user.uid }
        } else {
            emptyList()
        }

        val total = studentComplaints.size
        val pending = studentComplaints.count { it.status != ComplaintStatus.RESOLVED && it.status != ComplaintStatus.CLOSED }
        val resolved = studentComplaints.count { it.status == ComplaintStatus.RESOLVED || it.status == ComplaintStatus.CLOSED }
        val latestAnn = announcementsList.firstOrNull { it.isPinned } ?: announcementsList.firstOrNull()
        val recent = studentComplaints.sortedByDescending { it.updatedAt }.take(3)
        val unreadNotifs = notifsList.count { !it.isRead && (user == null || it.userId == user.uid || it.userId == "all") }

        StudentDashboardUiState(
            currentUser = user,
            totalComplaintsCount = total,
            pendingComplaintsCount = pending,
            resolvedComplaintsCount = resolved,
            latestAnnouncement = latestAnn,
            recentComplaints = recent,
            unreadNotificationsCount = unreadNotifs
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        StudentDashboardUiState()
    )
}
