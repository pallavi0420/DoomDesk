package com.dormdesk.app.data.model

enum class FeedbackCategory(val label: String) {
    HOSTEL_FACILITIES("Hostel Facilities"),
    MESS_AND_FOOD("Mess and Food"),
    CLEANLINESS("Cleanliness"),
    STAFF_BEHAVIOUR("Staff Behaviour"),
    GENERAL_SUGGESTIONS("General Suggestions")
}

data class FeedbackItem(
    val feedbackId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val category: FeedbackCategory = FeedbackCategory.GENERAL_SUGGESTIONS,
    val description: String = "",
    val rating: Int = 5, // 1 to 5
    val isAnonymous: Boolean = false,
    val adminResponse: String? = null,
    val adminRespondedBy: String? = null,
    val isReviewed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val respondedAt: Long? = null
)
