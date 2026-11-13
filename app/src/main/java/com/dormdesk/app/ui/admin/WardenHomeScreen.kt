package com.dormdesk.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.*
import com.dormdesk.app.ui.components.*
import com.dormdesk.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WardenHomeScreen(
    currentUser: User,
    complaints: List<Complaint>,
    feedbacks: List<Feedback>,
    announcements: List<Announcement>,
    onNavigateToComplaints: () -> Unit,
    onNavigateToComplaintDetail: (String) -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalComplaints = complaints.size
    val pendingComplaints = complaints.count { it.status == ComplaintStatus.SUBMITTED || it.status == ComplaintStatus.RECEIVED }
    val inProgressComplaints = complaints.count { it.status == ComplaintStatus.ASSIGNED || it.status == ComplaintStatus.IN_PROGRESS || it.status == ComplaintStatus.RESOLUTION_UPDATED }
    val resolvedComplaints = complaints.count { it.status == ComplaintStatus.RESOLVED || it.status == ComplaintStatus.CLOSED }
    val urgentComplaints = complaints.count { it.priority == ComplaintPriority.URGENT && !it.isClosed }

    val recentComplaints = remember(complaints) {
        complaints.take(4)
    }

    val latestFeedbacks = remember(feedbacks) {
        feedbacks.take(2)
    }

    val avgSatisfaction = remember(feedbacks) {
        if (feedbacks.isEmpty()) 4.5 else feedbacks.map { it.rating }.average()
    }

    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Warden Header Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EspressoBrown),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = SoftWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Warden Administration",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = currentUser.fullName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Role Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MutedTerracotta.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "WARDEN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MutedTerracotta
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${currentUser.hostel} • Admin Office ${currentUser.roomNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Metrics Statistics Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Operational Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Row 1: Total, Pending, Urgent
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Active",
                        count = "$totalComplaints",
                        icon = Icons.Outlined.Folder,
                        iconTint = EspressoBrown,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToComplaints
                    )
                    StatCard(
                        title = "Pending Action",
                        count = "$pendingComplaints",
                        icon = Icons.Outlined.HourglassTop,
                        iconTint = StatusPending,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToComplaints
                    )
                    StatCard(
                        title = "Urgent Priority",
                        count = "$urgentComplaints",
                        icon = Icons.Outlined.WarningAmber,
                        iconTint = StatusUrgent,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToComplaints
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: In-Progress, Resolved
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "In Progress",
                        count = "$inProgressComplaints",
                        icon = Icons.Outlined.Build,
                        iconTint = StatusAssigned,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToComplaints
                    )
                    StatCard(
                        title = "Resolved / Closed",
                        count = "$resolvedComplaints",
                        icon = Icons.Outlined.CheckCircle,
                        iconTint = SageGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToComplaints
                    )
                }
            }
        }

        // 3. Simple Visual Category & Status Distribution Bar Chart
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Status Distribution",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EspressoBrown
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Multi-segmented horizontal bar
                    if (totalComplaints > 0) {
                        val pendingPct = pendingComplaints.toFloat() / totalComplaints
                        val inProgressPct = inProgressComplaints.toFloat() / totalComplaints
                        val resolvedPct = resolvedComplaints.toFloat() / totalComplaints

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                        ) {
                            if (pendingPct > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(pendingPct)
                                        .fillMaxHeight()
                                        .background(StatusPending)
                                )
                            }
                            if (inProgressPct > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(inProgressPct)
                                        .fillMaxHeight()
                                        .background(StatusAssigned)
                                )
                            }
                            if (resolvedPct > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(resolvedPct)
                                        .fillMaxHeight()
                                        .background(SageGreen)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LegendItem(color = StatusPending, label = "Pending ($pendingComplaints)")
                            LegendItem(color = StatusAssigned, label = "In Progress ($inProgressComplaints)")
                            LegendItem(color = SageGreen, label = "Resolved ($resolvedComplaints)")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Resident Satisfaction", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                String.format(Locale.getDefault(), "%.1f / 5.0", avgSatisfaction),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EspressoBrown
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Avg Resolution Time", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("~ 18 Hours", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EspressoBrown)
                        }
                    }
                }
            }
        }

        // 4. Recent Complaints Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Incoming Complaints",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onNavigateToComplaints) {
                        Text("Manage All", color = EspressoBrown, style = MaterialTheme.typography.labelMedium)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        items(recentComplaints, key = { it.complaintId }) { complaint ->
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                DormDeskCard(onClick = { onNavigateToComplaintDetail(complaint.complaintId) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#${complaint.complaintId}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = WarmTaupe
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            PriorityBadge(priority = complaint.priority)
                        }
                        StatusBadge(status = complaint.status)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = complaint.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${complaint.studentName} (${complaint.roomNumber})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (complaint.assignedStaffName.isNotBlank()) "Tech: ${complaint.assignedStaffName}" else "Unassigned",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (complaint.assignedStaffName.isNotBlank()) StatusAssigned else StatusPending,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 5. Latest Feedback Preview Section
        if (latestFeedbacks.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Resident Feedback",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(onClick = onNavigateToFeedback) {
                            Text("View All", color = MutedTerracotta, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    DormDeskCard(onClick = onNavigateToFeedback) {
                        val fb = latestFeedbacks.first()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fb.displayedAuthor,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = EspressoBrown
                            )
                            StarRatingBar(rating = fb.rating, starSize = 14)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = fb.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
    }
}
