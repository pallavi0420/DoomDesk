package com.dormdesk.app

import android.app.Application
import com.dormdesk.app.data.repository.AnnouncementRepository
import com.dormdesk.app.data.repository.AuthRepository
import com.dormdesk.app.data.repository.ChatRepository
import com.dormdesk.app.data.repository.ComplaintRepository
import com.dormdesk.app.data.repository.FeedbackRepository
import com.dormdesk.app.data.repository.NotificationRepository

class DormDeskApp : Application() {

    lateinit var authRepository: AuthRepository
        private set

    lateinit var notificationRepository: NotificationRepository
        private set

    lateinit var complaintRepository: ComplaintRepository
        private set

    lateinit var feedbackRepository: FeedbackRepository
        private set

    lateinit var announcementRepository: AnnouncementRepository
        private set

    lateinit var chatRepository: ChatRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        authRepository = AuthRepository()
        notificationRepository = NotificationRepository()
        complaintRepository = ComplaintRepository(notificationRepository)
        feedbackRepository = FeedbackRepository(notificationRepository)
        announcementRepository = AnnouncementRepository(notificationRepository)
        chatRepository = ChatRepository()
    }

    companion object {
        lateinit var instance: DormDeskApp
            private set
    }
}
