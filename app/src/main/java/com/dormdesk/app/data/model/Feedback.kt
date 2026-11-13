package com.dormdesk.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class FeedbackCategory(val displayName: String) {
    HOSTEL_FACILITIES("Hostel Facilities"),
    MESS_FOOD("Mess & Food"),
    CLEANLINESS("Cleanliness"),
    STAFF_BEHAVIOUR("Staff Behaviour"),
    GENERAL_SUGGESTIONS("General Suggestions")
}

@Serializable
data class Feedback(
    val feedbackId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val category: FeedbackCategory = FeedbackCategory.HOSTEL_FACILITIES,
    val description: String = "",
    val rating: Int = 5,
    val isAnonymous: Boolean = false,
    val adminResponse: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isReviewed: Boolean = false
) {
    val displayedAuthor: String
        get() = if (isAnonymous) "Anonymous Resident" else studentName
}
