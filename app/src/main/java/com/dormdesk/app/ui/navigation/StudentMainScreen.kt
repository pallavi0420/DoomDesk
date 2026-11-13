package com.dormdesk.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.User
import com.dormdesk.app.ui.announcements.AnnouncementListScreen
import com.dormdesk.app.ui.chatbot.DormDeskAiScreen
import com.dormdesk.app.ui.complaints.ComplaintListScreen
import com.dormdesk.app.ui.feedback.FeedbackScreen
import com.dormdesk.app.ui.profile.ProfileScreen
import com.dormdesk.app.ui.student.StudentHomeScreen
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.*

sealed class StudentTab(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : StudentTab("Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Complaints : StudentTab("Complaints", Icons.Filled.Assignment, Icons.Outlined.Assignment)
    object AiChat : StudentTab("AI Desk", Icons.Filled.SmartToy, Icons.Outlined.SmartToy)
    object Notices : StudentTab("Notices", Icons.Filled.Campaign, Icons.Outlined.Campaign)
    object Profile : StudentTab("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun StudentMainScreen(
    currentUser: User,
    authViewModel: AuthViewModel,
    complaintViewModel: ComplaintViewModel,
    feedbackViewModel: FeedbackViewModel,
    announcementViewModel: AnnouncementViewModel,
    notificationViewModel: NotificationViewModel,
    chatbotViewModel: ChatbotViewModel,
    onNavigateToNotifications: () -> Unit,
    onNavigateToRaiseComplaint: () -> Unit,
    onNavigateToComplaintDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf<StudentTab>(StudentTab.Home) }

    val complaints by complaintViewModel.allComplaints.collectAsState()
    val announcements by announcementViewModel.allAnnouncements.collectAsState()
    val unreadCount = remember(notificationViewModel.allNotifications.collectAsState().value) {
        notificationViewModel.getUnreadCount(currentUser.uid)
    }

    val tabs = listOf(
        StudentTab.Home,
        StudentTab.Complaints,
        StudentTab.AiChat,
        StudentTab.Notices,
        StudentTab.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EspressoBrown,
                            selectedTextColor = EspressoBrown,
                            indicatorColor = EspressoBrown.copy(alpha = 0.12f),
                            unselectedIconColor = WarmTaupe,
                            unselectedTextColor = WarmTaupe
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            StudentTab.Home -> {
                StudentHomeScreen(
                    currentUser = currentUser,
                    complaints = complaints,
                    announcements = announcements,
                    unreadNotificationCount = unreadCount,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToRaiseComplaint = onNavigateToRaiseComplaint,
                    onNavigateToComplaints = { selectedTab = StudentTab.Complaints },
                    onNavigateToFeedback = { selectedTab = StudentTab.Profile },
                    onNavigateToChatbot = { selectedTab = StudentTab.AiChat },
                    onNavigateToComplaintDetail = onNavigateToComplaintDetail,
                    onNavigateToAnnouncements = { selectedTab = StudentTab.Notices },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            StudentTab.Complaints -> {
                ComplaintListScreen(
                    currentUser = currentUser,
                    complaintViewModel = complaintViewModel,
                    onNavigateToDetail = onNavigateToComplaintDetail,
                    onNavigateToRaiseComplaint = onNavigateToRaiseComplaint,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            StudentTab.AiChat -> {
                DormDeskAiScreen(
                    currentUser = currentUser,
                    complaints = complaints,
                    announcements = announcements,
                    chatbotViewModel = chatbotViewModel,
                    onNavigateToRoute = { route ->
                        when (route) {
                            "student_complaint_raise" -> onNavigateToRaiseComplaint()
                            "student_complaints" -> selectedTab = StudentTab.Complaints
                            "student_notices" -> selectedTab = StudentTab.Notices
                            "student_feedback" -> selectedTab = StudentTab.Profile
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            StudentTab.Notices -> {
                AnnouncementListScreen(
                    currentUser = currentUser,
                    announcementViewModel = announcementViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            StudentTab.Profile -> {
                ProfileScreen(
                    currentUser = currentUser,
                    authViewModel = authViewModel,
                    onLogout = onLogout,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
