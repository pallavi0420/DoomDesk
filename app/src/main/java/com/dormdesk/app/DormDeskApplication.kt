package com.dormdesk.app

import android.app.Application
import com.dormdesk.app.data.remote.GroqChatApi
import com.dormdesk.app.data.repository.DormDeskRepository
import com.dormdesk.app.data.repository.InMemoryDormDeskRepository

class DormDeskApplication : Application() {

    lateinit var repository: DormDeskRepository
        private set

    lateinit var groqChatApi: GroqChatApi
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize repository with complete preloaded demo dataset
        repository = InMemoryDormDeskRepository()

        // Initialize remote Groq API client
        groqChatApi = GroqChatApi()
    }

    companion object {
        lateinit var instance: DormDeskApplication
            private set
    }
}
