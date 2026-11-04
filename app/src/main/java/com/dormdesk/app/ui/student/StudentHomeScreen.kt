package com.dormdesk.app.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.ui.components.*
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.utils.DateUtils
import com.dormdesk.app.viewmodel.StudentDashboardViewModel

@Composable
fun StudentHomeScreen(
    dashboardViewModel: StudentDashboardViewModel,
    onNavigateToRaiseComplaint: () -> Unit,
    onNavigateToComplaints: () -> Unit,
    onNavigateToComplaintDetail: (String) -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by dashboardViewModel.uiState.collectAsState()
    val user = state.currentUser

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "DormDesk",
                subtitle = "Tagore Hostel",
                unreadNotifCount = state.unreadNotificationsCount,
                onNotificationsClick = onNavigateToNotifications
            )
        },
        containerColor = WarmIvory
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Student Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(TerracottaContainer)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.fullName?.take(2) ?: "ST").uppercase(),
                            color = MutedTerracotta,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Good day, ${user?.fullName?.split(" ")?.firstOrNull() ?: "Resident"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${user?.hostel ?: "Tagore Hostel"} • ${user?.block ?: "Block B"}, Room ${user?.roomNumber ?: "204"}",
                                fontSize = 12.sp,
                                color = SubtleGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Summary Statistics Section
            Text(
                text = "TICKET SUMMARY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SubtleGray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Total",
                    value = state.totalComplaintsCount.toString(),
                    icon = Icons.Outlined.Assignment,
                    iconTint = EspressoBrown,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Pending",
                    value = state.pendingComplaintsCount.toString(),
                    icon = Icons.Outlined.HourglassTop,
                    iconTint = StatusPendingText,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Resolved",
                    value = state.resolvedComplaintsCount.toString(),
                    icon = Icons.Outlined.CheckCircle,
                    iconTint = SageGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions Section
            Text(
                text = "QUICK ACTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SubtleGray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    title = "Raise\nComplaint",
                    icon = Icons.Default.AddCircleOutline,
                    backgroundColor = EspressoBrown,
                    contentColor = SoftWhite,
                    onClick = onNavigateToRaiseComplaint,
                    modifier = Modifier.weight(1f)
                )

                QuickActionButton(
                    title = "My\nComplaints",
                    icon = Icons.Outlined.ListAlt,
                    backgroundColor = SoftWhite,
                    contentColor = DeepCharcoal,
                    onClick = onNavigateToComplaints,
                    modifier = Modifier.weight(1f)
                )

                QuickActionButton(
                    title = "Submit\nFeedback",
                    icon = Icons.Outlined.RateReview,
                    backgroundColor = SoftWhite,
                    contentColor = DeepCharcoal,
                    onClick = onNavigateToFeedback,
                    modifier = Modifier.weight(1f)
                )

                QuickActionButton(
                    title = "Ask\nDormDesk AI",
                    icon = Icons.Outlined.SmartToy,
                    backgroundColor = SoftWhite,
                    contentColor = DeepCharcoal,
                    onClick = onNavigateToChatbot,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Latest Announcement Card
            state.latestAnnouncement?.let { ann ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LATEST ANNOUNCEMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SubtleGray,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedTerracotta,
                        modifier = Modifier.clickable { onNavigateToAnnouncements() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeutralBorder, RoundedCornerShape(14.dp))
                        .clickable { onNavigateToAnnouncements() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TerracottaContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = ann.category.label,
                                    color = MutedTerracotta,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (ann.isPinned) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = "Pinned",
                                        tint = MutedTerracotta,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Pinned",
                                        fontSize = 11.sp,
                                        color = MutedTerracotta,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = ann.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = ann.description,
                            fontSize = 13.sp,
                            color = SubtleGray,
                            maxLines = 2,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "By ${ann.authorName}",
                                fontSize = 11.sp,
                                color = SubtleGray
                            )
                            Text(
                                text = DateUtils.getRelativeTimeSpan(ann.createdAt),
                                fontSize = 11.sp,
                                color = SubtleGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Recent Complaint Activity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT COMPLAINT ACTIVITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SubtleGray,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedTerracotta,
                    modifier = Modifier.clickable { onNavigateToComplaints() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (state.recentComplaints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftWhite)
                        .border(1.dp, NeutralBorder, RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent complaints recorded. Room is in great shape!",
                        fontSize = 13.sp,
                        color = SubtleGray
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.recentComplaints.forEach { complaint ->
                        RecentComplaintCard(
                            complaint = complaint,
                            onClick = { onNavigateToComplaintDetail(complaint.complaintId) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, NeutralBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
