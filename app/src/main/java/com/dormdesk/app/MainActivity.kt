package com.dormdesk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dormdesk.app.ui.navigation.DormDeskNavGraph
import com.dormdesk.app.ui.theme.DormDeskTheme
import com.dormdesk.app.viewmodel.*

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DormDeskApp.instance.authRepository) as T
            }
        }
    }

    private val complaintViewModel: ComplaintViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ComplaintViewModel(DormDeskApp.instance.complaintRepository) as T
            }
        }
    }

    private val feedbackViewModel: FeedbackViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedbackViewModel(DormDeskApp.instance.feedbackRepository) as T
            }
        }
    }

    private val announcementViewModel: AnnouncementViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AnnouncementViewModel(DormDeskApp.instance.announcementRepository) as T
            }
        }
    }

    private val notificationViewModel: NotificationViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationViewModel(DormDeskApp.instance.notificationRepository) as T
            }
        }
    }

    private val chatbotViewModel: ChatbotViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChatbotViewModel(DormDeskApp.instance.chatRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DormDeskTheme {
                DormDeskNavGraph(
                    authViewModel = authViewModel,
                    complaintViewModel = complaintViewModel,
                    feedbackViewModel = feedbackViewModel,
                    announcementViewModel = announcementViewModel,
                    notificationViewModel = notificationViewModel,
                    chatbotViewModel = chatbotViewModel
                )
            }
        }
    }
}
