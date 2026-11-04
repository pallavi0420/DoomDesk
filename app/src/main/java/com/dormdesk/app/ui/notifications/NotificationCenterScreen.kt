package com.dormdesk.app.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
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
import com.dormdesk.app.data.model.NotificationItem
import com.dormdesk.app.data.model.NotificationType
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.components.EmptyStateView
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.utils.DateUtils
import com.dormdesk.app.viewmodel.NotificationsViewModel

@Composable
fun NotificationCenterScreen(
    notificationsViewModel: NotificationsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEntity: ((String) -> Unit)? = null
) {
    val notifications by notificationsViewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Notifications",
                subtitle = "Updates, status changes & notices",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    if (notifications.isNotEmpty()) {
                        IconButton(onClick = { notificationsViewModel.markAllAsRead() }) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Mark All Read",
                                tint = DeepCharcoal
                            )
                        }
                        IconButton(onClick = { notificationsViewModel.clearAll() }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear All",
                                tint = SubtleGray
                            )
                        }
                    }
                }
            )
        },
        containerColor = WarmIvory
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.Notifications,
                title = "No Notifications",
                description = "You're all caught up! New alerts on complaints and circulars will appear here.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(notifications, key = { it.notificationId }) { notif ->
                    NotificationCard(
                        notification = notif,
                        onClick = {
                            notificationsViewModel.markAsRead(notif.notificationId)
                            notif.relatedEntityId?.let { id ->
                                onNavigateToEntity?.invoke(id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (notification.type) {
        NotificationType.COMPLAINT_REGISTERED -> Icons.Default.Assignment
        NotificationType.COMPLAINT_STATUS_CHANGED -> Icons.Default.Update
        NotificationType.WARDEN_REMARK -> Icons.Default.Comment
        NotificationType.RESOLUTION_UPDATED -> Icons.Default.CheckCircle
        NotificationType.NEW_ANNOUNCEMENT -> Icons.Default.Campaign
        NotificationType.FEEDBACK_RESPONSE -> Icons.Default.RateReview
    }

    val iconTint: Color = when (notification.type) {
        NotificationType.COMPLAINT_REGISTERED -> EspressoBrown
        NotificationType.COMPLAINT_STATUS_CHANGED -> MutedTerracotta
        NotificationType.WARDEN_REMARK -> EspressoLight
        NotificationType.RESOLUTION_UPDATED -> SageGreen
        NotificationType.NEW_ANNOUNCEMENT -> MutedTerracotta
        NotificationType.FEEDBACK_RESPONSE -> SageGreen
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (!notification.isRead) MutedTerracotta.copy(alpha = 0.5f) else NeutralBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) SoftWhite else NeutralSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 14.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = DeepCharcoal,
                        maxLines = 1
                    )

                    if (!notification.isRead) {
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
                    text = notification.message,
                    fontSize = 12.sp,
                    color = if (!notification.isRead) DeepCharcoal else SubtleGray,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = DateUtils.getRelativeTimeSpan(notification.createdAt),
                    fontSize = 10.sp,
                    color = SubtleGray
                )
            }
        }
    }
}
