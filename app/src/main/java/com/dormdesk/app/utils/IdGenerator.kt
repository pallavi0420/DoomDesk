package com.dormdesk.app.utils

import java.util.UUID

object IdGenerator {
    fun generateComplaintId(): String {
        val randomNum = (1000..9999).random()
        return "CMP-2026-$randomNum"
    }

    fun generateFeedbackId(): String {
        val randomNum = (100..999).random()
        return "FB-2026-$randomNum"
    }

    fun generateAnnouncementId(): String {
        val randomNum = (100..999).random()
        return "ANN-2026-$randomNum"
    }

    fun generateNotificationId(): String {
        return "NOTIF-" + UUID.randomUUID().toString().take(8).uppercase()
    }

    fun generateMessageId(): String {
        return "MSG-" + UUID.randomUUID().toString().take(8).uppercase()
    }
}
