package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.*
import com.dormdesk.app.data.repository.DormDeskRepository
import kotlinx.coroutines.flow.*

data class CategoryStat(
    val category: ComplaintCategory,
    val count: Int,
    val percentage: Float
)

data class StatusStat(
    val status: ComplaintStatus,
    val count: Int
)

data class WardenDashboardUiState(
    val currentUser: User? = null,
    val totalComplaints: Int = 0,
    val pendingComplaints: Int = 0,
    val inProgressComplaints: Int = 0,
    val resolvedComplaints: Int = 0,
    val urgentComplaints: Int = 0,
    val recentComplaints: List<Complaint> = emptyList(),
    val latestFeedbacks: List<FeedbackItem> = emptyList(),
    val categoryStats: List<CategoryStat> = emptyList(),
    val statusStats: List<StatusStat> = emptyList(),
    val averageResolutionHours: Float = 14.5f,
    val studentSatisfactionScore: Float = 4.7f,
    val unreadNotificationsCount: Int = 0
)

class WardenDashboardViewModel(
    private val repository: DormDeskRepository
) : ViewModel() {

    val uiState: StateFlow<WardenDashboardUiState> = combine(
        repository.currentUser,
        repository.complaints,
        repository.feedbacks,
        repository.announcements,
        repository.notifications
    ) { user, complaints, feedbacks, _, notifs ->
        val total = complaints.size
        val pending = complaints.count { it.status == ComplaintStatus.SUBMITTED || it.status == ComplaintStatus.RECEIVED }
        val inProgress = complaints.count {
            it.status == ComplaintStatus.ASSIGNED ||
                    it.status == ComplaintStatus.IN_PROGRESS ||
                    it.status == ComplaintStatus.RESOLUTION_UPDATED
        }
        val resolved = complaints.count { it.status == ComplaintStatus.RESOLVED || it.status == ComplaintStatus.CLOSED }
        val urgent = complaints.count { it.priority == ComplaintPriority.URGENT && it.status.isActive }

        val categoryGroups = complaints.groupBy { it.category }
        val catStats = ComplaintCategory.values().map { cat ->
            val count = categoryGroups[cat]?.size ?: 0
            val pct = if (total > 0) (count.toFloat() / total.toFloat()) * 100f else 0f
            CategoryStat(cat, count, pct)
        }.sortedByDescending { it.count }

        val statusGroups = complaints.groupBy { it.status }
        val statStats = ComplaintStatus.values().map { st ->
            StatusStat(st, statusGroups[st]?.size ?: 0)
        }

        val ratedFeedbacks = feedbacks.map { it.rating }
        val avgRating = if (ratedFeedbacks.isNotEmpty()) {
            ratedFeedbacks.average().toFloat()
        } else 4.8f

        WardenDashboardUiState(
            currentUser = user,
            totalComplaints = total,
            pendingComplaints = pending,
            inProgressComplaints = inProgress,
            resolvedComplaints = resolved,
            urgentComplaints = urgent,
            recentComplaints = complaints.sortedByDescending { it.updatedAt }.take(5),
            latestFeedbacks = feedbacks.sortedByDescending { it.createdAt }.take(4),
            categoryStats = catStats,
            statusStats = statStats,
            averageResolutionHours = 18.2f,
            studentSatisfactionScore = (avgRating * 10).toInt() / 10f,
            unreadNotificationsCount = notifs.count { !it.isRead }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WardenDashboardUiState())
}
