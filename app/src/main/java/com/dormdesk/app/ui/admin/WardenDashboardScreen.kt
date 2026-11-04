package com.dormdesk.app.ui.admin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.components.MetricCard
import com.dormdesk.app.ui.components.RecentComplaintCard
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.WardenDashboardViewModel

@Composable
fun WardenDashboardScreen(
    dashboardViewModel: WardenDashboardViewModel,
    onNavigateToComplaints: () -> Unit,
    onNavigateToComplaintDetail: (String) -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val state by dashboardViewModel.uiState.collectAsState()
    val warden = state.currentUser

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Warden Administration",
                subtitle = "Tagore Hostel Management",
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
            // Warden Greeting Card
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
                            .background(SageContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (warden?.fullName?.take(2) ?: "DR").uppercase(),
                            color = SageGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = warden?.fullName ?: "Chief Warden",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${warden?.hostel ?: "Tagore Hostel"} • Administration",
                            fontSize = 12.sp,
                            color = SubtleGray
                        )
                    }

                    // Analytics Pill Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TerracottaContainer)
                            .clickable { onNavigateToAnalytics() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = MutedTerracotta,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Analytics",
                                color = MutedTerracotta,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Urgent Alert Banner if urgent complaints exist
            if (state.urgentComplaints > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PriorityUrgent, RoundedCornerShape(14.dp))
                        .clickable { onNavigateToComplaints() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PriorityUrgent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = SoftWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${state.urgentComplaints} Urgent Complaint(s) Require Attention",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PriorityUrgent
                            )
                            Text(
                                text = "Critical safety, power, or water issues logged by residents.",
                                fontSize = 11.sp,
                                color = DeepCharcoal
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = PriorityUrgent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Summary Statistics Grid
            Text(
                text = "HOSTEL MAINTENANCE OVERVIEW",
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
                    title = "Pending",
                    value = state.pendingComplaints.toString(),
                    icon = Icons.Outlined.HourglassEmpty,
                    iconTint = StatusPendingText,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "In Progress",
                    value = state.inProgressComplaints.toString(),
                    icon = Icons.Outlined.Build,
                    iconTint = StatusInProgressText,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Resolved",
                    value = state.resolvedComplaints.toString(),
                    icon = Icons.Outlined.CheckCircle,
                    iconTint = SageGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Total Complaints",
                    value = state.totalComplaints.toString(),
                    icon = Icons.Outlined.Assignment,
                    iconTint = EspressoBrown,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Urgent Priority",
                    value = state.urgentComplaints.toString(),
                    icon = Icons.Outlined.PriorityHigh,
                    iconTint = PriorityUrgent,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Satisfaction",
                    value = "${state.studentSatisfactionScore} ★",
                    icon = Icons.Outlined.Star,
                    iconTint = MutedTerracotta,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category Distribution Preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Complaints by Category",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                        Text(
                            text = "Full Analytics",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedTerracotta,
                            modifier = Modifier.clickable { onNavigateToAnalytics() }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    state.categoryStats.take(4).forEach { catStat ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = catStat.category.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DeepCharcoal
                                )
                                Text(
                                    text = "${catStat.count} ticket(s) (${catStat.percentage.toInt()}%)",
                                    fontSize = 11.sp,
                                    color = SubtleGray
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = (catStat.percentage / 100f).coerceIn(0f, 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = EspressoBrown,
                                trackColor = NeutralBorder
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Complaints Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LATEST STUDENT COMPLAINTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SubtleGray,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Manage All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedTerracotta,
                    modifier = Modifier.clickable { onNavigateToComplaints() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.recentComplaints.forEach { complaint ->
                    RecentComplaintCard(
                        complaint = complaint,
                        onClick = { onNavigateToComplaintDetail(complaint.complaintId) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
