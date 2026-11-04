package com.dormdesk.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.dormdesk.app.DormDeskApplication
import com.dormdesk.app.data.model.UserRole
import com.dormdesk.app.ui.admin.*
import com.dormdesk.app.ui.announcements.AnnouncementsScreen
import com.dormdesk.app.ui.auth.LoginScreen
import com.dormdesk.app.ui.auth.RegisterScreen
import com.dormdesk.app.ui.auth.SplashScreen
import com.dormdesk.app.ui.chatbot.DormDeskAIChatScreen
import com.dormdesk.app.ui.complaints.ComplaintDetailScreen
import com.dormdesk.app.ui.complaints.ComplaintHistoryScreen
import com.dormdesk.app.ui.complaints.RaiseComplaintScreen
import com.dormdesk.app.ui.components.DormDeskBottomBar
import com.dormdesk.app.ui.feedback.StudentFeedbackScreen
import com.dormdesk.app.ui.notifications.NotificationCenterScreen
import com.dormdesk.app.ui.profile.ProfileScreen
import com.dormdesk.app.ui.student.StudentHomeScreen
import com.dormdesk.app.viewmodel.*

@Composable
fun DormDeskNavHost(
    navController: NavHostController = rememberNavController(),
    app: DormDeskApplication = DormDeskApplication.instance
) {
    val repository = app.repository
    val groqChatApi = app.groqChatApi

    // Initialize ViewModels
    val authViewModel = remember { AuthViewModel(repository) }
    val studentDashboardViewModel = remember { StudentDashboardViewModel(repository) }
    val complaintsViewModel = remember { ComplaintsViewModel(repository) }
    val feedbackViewModel = remember { FeedbackViewModel(repository) }
    val announcementsViewModel = remember { AnnouncementsViewModel(repository) }
    val notificationsViewModel = remember { NotificationsViewModel(repository) }
    val chatbotViewModel = remember { ChatbotViewModel(repository, groqChatApi) }
    val wardenDashboardViewModel = remember { WardenDashboardViewModel(repository) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Top-level bottom nav screens
    val isBottomBarVisible = when (currentRoute) {
        Screen.StudentHome.route,
        Screen.StudentComplaints.route,
        Screen.DormDeskAI.route,
        Screen.Announcements.route,
        Screen.Profile.route,
        Screen.WardenDashboard.route,
        Screen.WardenComplaints.route,
        Screen.WardenFeedback.route,
        Screen.WardenAnnouncements.route -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible && currentUser != null) {
                DormDeskBottomBar(
                    role = currentUser!!.role,
                    currentRoute = currentRoute,
                    onNavigateToRoute = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(
                                if (currentUser!!.role == UserRole.STUDENT) Screen.StudentHome.route
                                else Screen.WardenDashboard.route
                            ) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Splash Screen
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        val user = currentUser
                        if (user != null) {
                            val startRoute = if (user.role == UserRole.STUDENT) {
                                Screen.StudentHome.route
                            } else {
                                Screen.WardenDashboard.route
                            }
                            navController.navigate(startRoute) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // Auth: Login
            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = { user ->
                        val destination = if (user.role == UserRole.STUDENT) {
                            Screen.StudentHome.route
                        } else {
                            Screen.WardenDashboard.route
                        }
                        navController.navigate(destination) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            // Auth: Register
            composable(Screen.Register.route) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.StudentHome.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Student Home
            composable(Screen.StudentHome.route) {
                StudentHomeScreen(
                    dashboardViewModel = studentDashboardViewModel,
                    onNavigateToRaiseComplaint = { navController.navigate(Screen.RaiseComplaint.route) },
                    onNavigateToComplaints = { navController.navigate(Screen.StudentComplaints.route) },
                    onNavigateToComplaintDetail = { id ->
                        navController.navigate(Screen.ComplaintDetail.createRoute(id))
                    },
                    onNavigateToFeedback = { navController.navigate(Screen.StudentFeedback.route) },
                    onNavigateToChatbot = { navController.navigate(Screen.DormDeskAI.route) },
                    onNavigateToAnnouncements = { navController.navigate(Screen.Announcements.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                )
            }

            // Student Complaints List
            composable(Screen.StudentComplaints.route) {
                ComplaintHistoryScreen(
                    complaintsViewModel = complaintsViewModel,
                    onNavigateToRaiseComplaint = { navController.navigate(Screen.RaiseComplaint.route) },
                    onNavigateToComplaintDetail = { id ->
                        navController.navigate(Screen.ComplaintDetail.createRoute(id))
                    }
                )
            }

            // Raise Complaint
            composable(Screen.RaiseComplaint.route) {
                RaiseComplaintScreen(
                    complaintsViewModel = complaintsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onComplaintCreated = { newComplaintId ->
                        navController.navigate(Screen.ComplaintDetail.createRoute(newComplaintId)) {
                            popUpTo(Screen.StudentComplaints.route)
                        }
                    }
                )
            }

            // Complaint Detail Screen
            composable(
                route = Screen.ComplaintDetail.route,
                arguments = listOf(navArgument("complaintId") { type = NavType.StringType })
            ) { backStackEntry ->
                val complaintId = backStackEntry.arguments?.getString("complaintId") ?: ""
                ComplaintDetailScreen(
                    complaintId = complaintId,
                    complaintsViewModel = complaintsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // DormDesk AI Chat
            composable(Screen.DormDeskAI.route) {
                DormDeskAIChatScreen(
                    chatbotViewModel = chatbotViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRoute = { routeTarget ->
                        when (routeTarget) {
                            "complaints" -> navController.navigate(Screen.StudentComplaints.route)
                            "raise_complaint" -> navController.navigate(Screen.RaiseComplaint.route)
                            "announcements" -> navController.navigate(Screen.Announcements.route)
                            "feedback" -> navController.navigate(Screen.StudentFeedback.route)
                        }
                    }
                )
            }

            // Announcements / Notice Board
            composable(Screen.Announcements.route) {
                AnnouncementsScreen(
                    announcementsViewModel = announcementsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Student Feedback
            composable(Screen.StudentFeedback.route) {
                StudentFeedbackScreen(
                    feedbackViewModel = feedbackViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Notifications
            composable(Screen.Notifications.route) {
                NotificationCenterScreen(
                    notificationsViewModel = notificationsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEntity = { entityId ->
                        if (entityId.startsWith("CMP-")) {
                            navController.navigate(Screen.ComplaintDetail.createRoute(entityId))
                        } else if (entityId.startsWith("ANN-")) {
                            navController.navigate(Screen.Announcements.route)
                        } else if (entityId.startsWith("FB-")) {
                            navController.navigate(Screen.StudentFeedback.route)
                        }
                    }
                )
            }

            // Profile (Shared for Student and Warden)
            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Warden Dashboard
            composable(Screen.WardenDashboard.route) {
                WardenDashboardScreen(
                    dashboardViewModel = wardenDashboardViewModel,
                    onNavigateToComplaints = { navController.navigate(Screen.WardenComplaints.route) },
                    onNavigateToComplaintDetail = { id ->
                        navController.navigate(Screen.ComplaintDetail.createRoute(id))
                    },
                    onNavigateToFeedback = { navController.navigate(Screen.WardenFeedback.route) },
                    onNavigateToAnnouncements = { navController.navigate(Screen.WardenAnnouncements.route) },
                    onNavigateToAnalytics = { navController.navigate(Screen.WardenAnalytics.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
                )
            }

            // Warden Complaints Management
            composable(Screen.WardenComplaints.route) {
                WardenComplaintsScreen(
                    complaintsViewModel = complaintsViewModel,
                    onNavigateToComplaintDetail = { id ->
                        navController.navigate(Screen.ComplaintDetail.createRoute(id))
                    }
                )
            }

            // Warden Feedback Management
            composable(Screen.WardenFeedback.route) {
                WardenFeedbackScreen(feedbackViewModel = feedbackViewModel)
            }

            // Warden Announcements Management
            composable(Screen.WardenAnnouncements.route) {
                WardenAnnouncementsScreen(announcementsViewModel = announcementsViewModel)
            }

            // Warden Analytics
            composable(Screen.WardenAnalytics.route) {
                WardenAnalyticsScreen(
                    dashboardViewModel = wardenDashboardViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
