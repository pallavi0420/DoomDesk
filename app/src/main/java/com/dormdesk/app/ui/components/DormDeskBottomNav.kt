package com.dormdesk.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.UserRole
import com.dormdesk.app.ui.navigation.Screen
import com.dormdesk.app.ui.theme.*

data class NavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun DormDeskBottomBar(
    role: UserRole,
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = if (role == UserRole.STUDENT) {
        listOf(
            NavItem(
                route = Screen.StudentHome.route,
                label = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ),
            NavItem(
                route = Screen.StudentComplaints.route,
                label = "Complaints",
                selectedIcon = Icons.Filled.Assignment,
                unselectedIcon = Icons.Outlined.Assignment
            ),
            NavItem(
                route = Screen.DormDeskAI.route,
                label = "DormDesk AI",
                selectedIcon = Icons.Filled.SmartToy,
                unselectedIcon = Icons.Outlined.SmartToy
            ),
            NavItem(
                route = Screen.Announcements.route,
                label = "Notices",
                selectedIcon = Icons.Filled.Campaign,
                unselectedIcon = Icons.Outlined.Campaign
            ),
            NavItem(
                route = Screen.Profile.route,
                label = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person
            )
        )
    } else {
        listOf(
            NavItem(
                route = Screen.WardenDashboard.route,
                label = "Dashboard",
                selectedIcon = Icons.Filled.Dashboard,
                unselectedIcon = Icons.Outlined.Dashboard
            ),
            NavItem(
                route = Screen.WardenComplaints.route,
                label = "Complaints",
                selectedIcon = Icons.Filled.Assignment,
                unselectedIcon = Icons.Outlined.Assignment
            ),
            NavItem(
                route = Screen.WardenFeedback.route,
                label = "Feedback",
                selectedIcon = Icons.Filled.RateReview,
                unselectedIcon = Icons.Outlined.RateReview
            ),
            NavItem(
                route = Screen.WardenAnnouncements.route,
                label = "Notices",
                selectedIcon = Icons.Filled.Campaign,
                unselectedIcon = Icons.Outlined.Campaign
            ),
            NavItem(
                route = Screen.Profile.route,
                label = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person
            )
        )
    }

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = NeutralBorder),
        containerColor = SoftWhite,
        tonalElevation = 2.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigateToRoute(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EspressoBrown,
                    selectedTextColor = EspressoBrown,
                    indicatorColor = TerracottaContainer,
                    unselectedIconColor = SubtleGray,
                    unselectedTextColor = SubtleGray
                )
            )
        }
    }
}
