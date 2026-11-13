package com.dormdesk.app.ui.student

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.User
import com.dormdesk.app.ui.components.*
import com.dormdesk.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentHomeScreen(
    currentUser: User,
    complaints: List<Complaint>,
    announcements: List<Announcement>,
    unreadNotificationCount: Int,
    onNavigateToNotifications: () -> Unit,
    onNavigateToRaiseComplaint: () -> Unit,
    onNavigateToComplaints: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onNavigateToComplaintDetail: (String) -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    modifier: Modifier = Modifier
) {
    val myComplaints = remember(complaints, currentUser.uid) {
        complaints.filter { it.studentId == currentUser.uid }
    }

    val totalComplaints = myComplaints.size
    val pendingComplaints = myComplaints.count { it.isPending || it.isAssigned || it.isInProgress }
    val resolvedComplaints = myComplaints.count { it.isResolved || it.isClosed }

    val latestAnnouncement = remember(announcements) {
        announcements.firstOrNull()
    }

    val recentComplaints = remember(myComplaints) {
        myComplaints.take(3)
    }

    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Header Section
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
                        // Avatar with Initials
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EspressoBrown),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser.initials,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SoftWhite
                            )
                        }

                        Column {
                            Text(
                                text = "Welcome back,",
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

                    // Notification Bell with Badge
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount > 0) {
                                    Badge(containerColor = MutedTerracotta) {
                                        Text(
                                            text = "$unreadNotificationCount",
                                            color = SoftWhite,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Location / Room Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MutedTerracotta,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${currentUser.hostel} • ${currentUser.block} • Room ${currentUser.roomNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. Summary Statistics Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Complaint Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total",
                        count = "$totalComplaints",
                        icon = Icons.Outlined.FolderCopy,
                        iconTint = EspressoBrown,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToComplaints
                    )
                    StatCard(
                        title = "Pending",
                        count = "$pendingComplaints",
                        icon = Icons.Outlined.HourglassEmpty,
                        iconTint = StatusPending,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToComplaints
                    )
                    StatCard(
                        title = "Resolved",
                        count = "$resolvedComplaints",
                        icon = Icons.Outlined.CheckCircle,
                        iconTint = SageGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToComplaints
                    )
                }
            }
        }

        // 3. Quick Actions Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Raise Complaint",
                        subtitle = "Report room issue",
                        icon = Icons.Default.AddCircleOutline,
                        iconTint = MutedTerracotta,
                        containerColor = MutedTerracotta.copy(alpha = 0.08f),
                        borderColor = MutedTerracotta.copy(alpha = 0.25f),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToRaiseComplaint
                    )
                    QuickActionCard(
                        title = "My Complaints",
                        subtitle = "Track active tickets",
                        icon = Icons.Outlined.Assignment,
                        iconTint = EspressoBrown,
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToComplaints
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Submit Feedback",
                        subtitle = "Mess & hostel review",
                        icon = Icons.Outlined.RateReview,
                        iconTint = SageGreen,
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFeedback
                    )
                    QuickActionCard(
                        title = "DormDesk AI",
                        subtitle = "24/7 Smart Assistant",
                        icon = Icons.Outlined.SmartToy,
                        iconTint = StatusAssigned,
                        containerColor = StatusAssigned.copy(alpha = 0.08f),
                        borderColor = StatusAssigned.copy(alpha = 0.25f),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToChatbot
                    )
                }
            }
        }

        // 4. Latest Announcement Banner
        if (latestAnnouncement != null) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notice Board Bulletin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(onClick = onNavigateToAnnouncements) {
                            Text("View All", color = MutedTerracotta, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    DormDeskCard(
                        onClick = onNavigateToAnnouncements,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        borderColor = if (latestAnnouncement.isPinned) MutedTerracotta.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (latestAnnouncement.isPinned) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = "Pinned",
                                        tint = MutedTerracotta,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = latestAnnouncement.category.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedTerracotta,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            PriorityBadge(priority = latestAnnouncement.priority)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = latestAnnouncement.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = latestAnnouncement.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 5. Recent Complaint Activity
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
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (myComplaints.isNotEmpty()) {
                        TextButton(onClick = onNavigateToComplaints) {
                            Text("See All (${myComplaints.size})", color = EspressoBrown, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (recentComplaints.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    EmptyStateView(
                        icon = Icons.Outlined.CheckCircleOutline,
                        title = "No complaints logged",
                        description = "Your room currently has zero reported maintenance issues.",
                        actionButtonText = "Raise Complaint",
                        onActionClick = onNavigateToRaiseComplaint
                    )
                }
            }
        } else {
            items(recentComplaints, key = { it.complaintId }) { complaint ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                    DormDeskCard(
                        onClick = { onNavigateToComplaintDetail(complaint.complaintId) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${complaint.complaintId}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = WarmTaupe
                            )
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
                                text = complaint.category.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            PriorityBadge(priority = complaint.priority)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    containerColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
