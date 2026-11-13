package com.dormdesk.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dormdesk.app.data.model.UserRole
import com.dormdesk.app.ui.auth.LoginScreen
import com.dormdesk.app.ui.auth.RegisterScreen
import com.dormdesk.app.ui.complaints.ComplaintDetailScreen
import com.dormdesk.app.ui.complaints.RaiseComplaintScreen
import com.dormdesk.app.ui.feedback.FeedbackScreen
import com.dormdesk.app.ui.notifications.NotificationCenterScreen
import com.dormdesk.app.viewmodel.*

object DormDeskDestinations {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val STUDENT_MAIN = "student_main"
    const val WARDEN_MAIN = "warden_main"
    const val RAISE_COMPLAINT = "raise_complaint"
    const val COMPLAINT_DETAIL = "complaint_detail/{complaintId}"
    const val FEEDBACK = "feedback"
    const val NOTIFICATIONS = "notifications"

    fun complaintDetailRoute(complaintId: String): String = "complaint_detail/$complaintId"
}

@Composable
fun DormDeskNavGraph(
    authViewModel: AuthViewModel,
    complaintViewModel: ComplaintViewModel,
    feedbackViewModel: FeedbackViewModel,
    announcementViewModel: AnnouncementViewModel,
    notificationViewModel: NotificationViewModel,
    chatbotViewModel: ChatbotViewModel,
    navController: NavHostController = rememberNavController()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    val startDestination = when {
        currentUser?.isWarden == true -> DormDeskDestinations.WARDEN_MAIN
        currentUser?.isStudent == true -> DormDeskDestinations.STUDENT_MAIN
        else -> DormDeskDestinations.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth: Login
        composable(DormDeskDestinations.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(DormDeskDestinations.REGISTER)
                },
                onLoginSuccess = { role ->
                    if (role == UserRole.WARDEN) {
                        navController.navigate(DormDeskDestinations.WARDEN_MAIN) {
                            popUpTo(DormDeskDestinations.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(DormDeskDestinations.STUDENT_MAIN) {
                            popUpTo(DormDeskDestinations.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Auth: Register
        composable(DormDeskDestinations.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = { role ->
                    if (role == UserRole.WARDEN) {
                        navController.navigate(DormDeskDestinations.WARDEN_MAIN) {
                            popUpTo(DormDeskDestinations.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(DormDeskDestinations.STUDENT_MAIN) {
                            popUpTo(DormDeskDestinations.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Student Main (5 Tabs)
        composable(DormDeskDestinations.STUDENT_MAIN) {
            val user = currentUser
            if (user != null) {
                StudentMainScreen(
                    currentUser = user,
                    authViewModel = authViewModel,
                    complaintViewModel = complaintViewModel,
                    feedbackViewModel = feedbackViewModel,
                    announcementViewModel = announcementViewModel,
                    notificationViewModel = notificationViewModel,
                    chatbotViewModel = chatbotViewModel,
                    onNavigateToNotifications = {
                        navController.navigate(DormDeskDestinations.NOTIFICATIONS)
                    },
                    onNavigateToRaiseComplaint = {
                        navController.navigate(DormDeskDestinations.RAISE_COMPLAINT)
                    },
                    onNavigateToComplaintDetail = { id ->
                        navController.navigate(DormDeskDestinations.complaintDetailRoute(id))
                    },
                    onLogout = {
                        navController.navigate(DormDeskDestinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Warden Main (5 Tabs)
        composable(DormDeskDestinations.WARDEN_MAIN) {
            val user = currentUser
            if (user != null) {
                WardenMainScreen(
                    currentUser = user,
                    authViewModel = authViewModel,
                    complaintViewModel = complaintViewModel,
                    feedbackViewModel = feedbackViewModel,
                    announcementViewModel = announcementViewModel,
                    onNavigateToComplaintDetail = { id ->
                        navController.navigate(DormDeskDestinations.complaintDetailRoute(id))
                    },
                    onLogout = {
                        navController.navigate(DormDeskDestinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Raise Complaint
        composable(DormDeskDestinations.RAISE_COMPLAINT) {
            val user = currentUser
            if (user != null) {
                RaiseComplaintScreen(
                    currentUser = user,
                    complaintViewModel = complaintViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onComplaintSubmitted = { newId ->
                        navController.navigate(DormDeskDestinations.complaintDetailRoute(newId)) {
                            popUpTo(DormDeskDestinations.RAISE_COMPLAINT) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Complaint Detail
        composable(
            route = DormDeskDestinations.COMPLAINT_DETAIL,
            arguments = listOf(navArgument("complaintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId") ?: ""
            val user = currentUser
            if (user != null) {
                ComplaintDetailScreen(
                    complaintId = complaintId,
                    currentUser = user,
                    complaintViewModel = complaintViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // Student Feedback Screen
        composable(DormDeskDestinations.FEEDBACK) {
            val user = currentUser
            if (user != null) {
                FeedbackScreen(
                    currentUser = user,
                    feedbackViewModel = feedbackViewModel
                )
            }
        }

        // Notifications Center
        composable(DormDeskDestinations.NOTIFICATIONS) {
            val user = currentUser
            if (user != null) {
                NotificationCenterScreen(
                    currentUser = user,
                    notificationViewModel = notificationViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToComplaintDetail = { id ->
                        navController.navigate(DormDeskDestinations.complaintDetailRoute(id))
                    },
                    onNavigateToAnnouncements = {
                        navController.popBackStack()
                    },
                    onNavigateToFeedback = {
                        navController.navigate(DormDeskDestinations.FEEDBACK)
                    }
                )
            }
        }
    }
}
