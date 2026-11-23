package com.dormdesk.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
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
import com.dormdesk.app.ui.admin.WardenHomeScreen
import com.dormdesk.app.ui.announcements.AnnouncementListScreen
import com.dormdesk.app.ui.complaints.ComplaintListScreen
import com.dormdesk.app.ui.feedback.WardenFeedbackScreen
import com.dormdesk.app.ui.profile.ProfileScreen
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.*

sealed class WardenTab(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Dashboard : WardenTab("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard)
    object Complaints : WardenTab("Complaints", Icons.AutoMirrored.Filled.Assignment, Icons.AutoMirrored.Outlined.Assignment)
    object Feedback : WardenTab("Feedback", Icons.Filled.RateReview, Icons.Outlined.RateReview)
    object Notices : WardenTab("Notices", Icons.Filled.Campaign, Icons.Outlined.Campaign)
    object Profile : WardenTab("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun WardenMainScreen(
    currentUser: User,
    authViewModel: AuthViewModel,
    complaintViewModel: ComplaintViewModel,
    feedbackViewModel: FeedbackViewModel,
    announcementViewModel: AnnouncementViewModel,
    onNavigateToComplaintDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf<WardenTab>(WardenTab.Dashboard) }

    val complaints by complaintViewModel.allComplaints.collectAsState()
    val feedbacks by feedbackViewModel.allFeedbacks.collectAsState()
    val announcements by announcementViewModel.allAnnouncements.collectAsState()

    val tabs = listOf(
        WardenTab.Dashboard,
        WardenTab.Complaints,
        WardenTab.Feedback,
        WardenTab.Notices,
        WardenTab.Profile
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
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            unselectedIconColor = WarmTaupe,
                            unselectedTextColor = WarmTaupe
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            WardenTab.Dashboard -> {
                WardenHomeScreen(
                    currentUser = currentUser,
                    complaints = complaints,
                    feedbacks = feedbacks,
                    announcements = announcements,
                    onNavigateToComplaints = { selectedTab = WardenTab.Complaints },
                    onNavigateToComplaintDetail = onNavigateToComplaintDetail,
                    onNavigateToFeedback = { selectedTab = WardenTab.Feedback },
                    onNavigateToAnnouncements = { selectedTab = WardenTab.Notices },
                    onLogout = {
                        authViewModel.logout()
                        onLogout()
                    },
                    onDeleteAccount = {
                        authViewModel.deleteAccount(onComplete = onLogout)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            WardenTab.Complaints -> {
                ComplaintListScreen(
                    currentUser = currentUser,
                    complaintViewModel = complaintViewModel,
                    onNavigateToDetail = onNavigateToComplaintDetail,
                    onNavigateToRaiseComplaint = {},
                    modifier = Modifier.padding(innerPadding)
                )
            }
            WardenTab.Feedback -> {
                WardenFeedbackScreen(
                    feedbackViewModel = feedbackViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            WardenTab.Notices -> {
                AnnouncementListScreen(
                    currentUser = currentUser,
                    announcementViewModel = announcementViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            WardenTab.Profile -> {
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
