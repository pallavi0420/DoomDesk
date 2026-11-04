package com.dormdesk.app.data.repository

import com.dormdesk.app.data.model.*
import com.dormdesk.app.utils.IdGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface DormDeskRepository {
    val currentUser: StateFlow<User?>
    val complaints: StateFlow<List<Complaint>>
    val announcements: StateFlow<List<Announcement>>
    val feedbacks: StateFlow<List<FeedbackItem>>
    val notifications: StateFlow<List<NotificationItem>>
    val chatMessages: StateFlow<List<ChatMessage>>

    // Auth
    suspend fun login(email: String, role: UserRole): Result<User>
    suspend fun registerStudent(user: User): Result<User>
    suspend fun logout()
    suspend fun deleteAccount(userId: String): Result<Unit>
    fun switchUser(user: User)

    // Complaints
    suspend fun createComplaint(
        title: String,
        category: ComplaintCategory,
        description: String,
        priority: ComplaintPriority,
        location: String,
        imageUrl: String?
    ): Result<Complaint>

    suspend fun getComplaintById(complaintId: String): Complaint?
    suspend fun updateComplaintStatus(
        complaintId: String,
        newStatus: ComplaintStatus,
        assignedStaffName: String?,
        remarks: String?,
        resolutionNotes: String?,
        completionPhotoUrl: String?
    ): Result<Complaint>

    suspend fun confirmComplaintResolution(
        complaintId: String,
        rating: Int,
        feedback: String?
    ): Result<Complaint>

    suspend fun reopenComplaint(
        complaintId: String,
        reason: String
    ): Result<Complaint>

    // Feedback
    suspend fun submitFeedback(
        category: FeedbackCategory,
        description: String,
        rating: Int,
        isAnonymous: Boolean
    ): Result<FeedbackItem>

    suspend fun respondToFeedback(
        feedbackId: String,
        adminResponse: String
    ): Result<FeedbackItem>

    // Announcements
    suspend fun createAnnouncement(
        title: String,
        description: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        isPinned: Boolean
    ): Result<Announcement>

    suspend fun updateAnnouncement(
        announcementId: String,
        title: String,
        description: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        isPinned: Boolean
    ): Result<Announcement>

    suspend fun deleteAnnouncement(announcementId: String): Result<Boolean>
    suspend fun markAnnouncementAsRead(announcementId: String)

    // Notifications
    suspend fun markNotificationAsRead(notificationId: String)
    suspend fun markAllNotificationsAsRead()
    suspend fun clearAllNotifications()

    // Chatbot
    suspend fun addChatMessage(message: ChatMessage)
    suspend fun clearChatHistory()
}
