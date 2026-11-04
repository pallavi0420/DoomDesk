package com.dormdesk.app.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")

    // Student Flow
    object StudentHome : Screen("student_home")
    object StudentComplaints : Screen("student_complaints")
    object RaiseComplaint : Screen("raise_complaint")
    object ComplaintDetail : Screen("complaint_detail/{complaintId}") {
        fun createRoute(complaintId: String) = "complaint_detail/$complaintId"
    }
    object DormDeskAI : Screen("dormdesk_ai")
    object Announcements : Screen("announcements")
    object StudentFeedback : Screen("student_feedback")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")

    // Warden Flow
    object WardenDashboard : Screen("warden_dashboard")
    object WardenComplaints : Screen("warden_complaints")
    object WardenFeedback : Screen("warden_feedback")
    object WardenAnnouncements : Screen("warden_announcements")
    object WardenAnalytics : Screen("warden_analytics")
}
