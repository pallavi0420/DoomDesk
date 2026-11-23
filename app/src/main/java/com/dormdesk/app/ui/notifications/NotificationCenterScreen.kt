package com.dormdesk.app.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.NotificationItem
import com.dormdesk.app.data.model.NotificationType
import com.dormdesk.app.data.model.User
import com.dormdesk.app.ui.components.DormDeskCard
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.components.EmptyStateView
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationCenterScreen(
    currentUser: User,
    notificationViewModel: NotificationViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToComplaintDetail: (String) -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allNotifications by notificationViewModel.allNotifications.collectAsState()
    val userNotifications = remember(allNotifications, currentUser.uid) {
        notificationViewModel.getNotificationsForUser(currentUser.uid)
    }

    val unreadCount = remember(userNotifications) {
        userNotifications.count { !it.isRead }
    }

    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Notifications",
                subtitle = if (unreadCount > 0) "$unreadCount unread updates" else "All caught up",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    if (unreadCount > 0) {
                        TextButton(onClick = { notificationViewModel.markAllAsRead(currentUser.uid) }) {
                            Text("Mark All Read", color = MutedTerracotta, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (userNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    icon = Icons.Outlined.NotificationsNone,
                    title = "No Notifications",
                    description = "You have no unread updates at this moment."
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(userNotifications, key = { it.notificationId }) { item ->
                    val icon = when (item.type) {
                        NotificationType.COMPLAINT -> Icons.Outlined.Assignment
                        NotificationType.ANNOUNCEMENT -> Icons.Outlined.Campaign
                        NotificationType.FEEDBACK -> Icons.Outlined.RateReview
                        NotificationType.SYSTEM -> Icons.Outlined.Info
                    }

                    val iconTint = when (item.type) {
                        NotificationType.COMPLAINT -> if (!item.isRead) MutedTerracotta else MaterialTheme.colorScheme.onSurfaceVariant
                        NotificationType.ANNOUNCEMENT -> if (!item.isRead) StatusAssigned else MaterialTheme.colorScheme.onSurfaceVariant
                        NotificationType.FEEDBACK -> if (!item.isRead) SageGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        NotificationType.SYSTEM -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    DormDeskCard(
                        onClick = {
                            notificationViewModel.markAsRead(item.notificationId)
                            when (item.type) {
                                NotificationType.COMPLAINT -> {
                                    if (item.referenceId.isNotBlank()) {
                                        onNavigateToComplaintDetail(item.referenceId)
                                    }
                                }
                                NotificationType.ANNOUNCEMENT -> onNavigateToAnnouncements()
                                NotificationType.FEEDBACK -> onNavigateToFeedback()
                                else -> {}
                            }
                        },
                        backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        borderColor = if (!item.isRead) MutedTerracotta.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(iconTint.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (!item.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(MutedTerracotta)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = dateFormat.format(Date(item.createdAt)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
