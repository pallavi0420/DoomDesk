package com.dormdesk.app.ui.announcements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.AnnouncementCategory
import com.dormdesk.app.data.model.AnnouncementPriority
import com.dormdesk.app.ui.components.CategoryChip
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.components.EmptyStateView
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.utils.DateUtils
import com.dormdesk.app.viewmodel.AnnouncementsViewModel

@Composable
fun AnnouncementsScreen(
    announcementsViewModel: AnnouncementsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by announcementsViewModel.uiState.collectAsState()
    val announcements by announcementsViewModel.filteredAnnouncements.collectAsState()

    var selectedAnnouncementForDetail by remember { mutableStateOf<Announcement?>(null) }

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Hostel Notice Board",
                subtitle = "Official circulars and maintenance alerts",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = WarmIvory
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { announcementsViewModel.setSearchQuery(it) },
                    placeholder = { Text("Search notices...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = SubtleGray)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EspressoBrown,
                        unfocusedBorderColor = NeutralBorder,
                        focusedContainerColor = SoftWhite,
                        unfocusedContainerColor = SoftWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Categories
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        label = "All Notices",
                        isSelected = uiState.selectedCategory == null,
                        onClick = { announcementsViewModel.setCategory(null) }
                    )
                }
                items(AnnouncementCategory.values()) { cat ->
                    CategoryChip(
                        label = cat.label,
                        isSelected = uiState.selectedCategory == cat,
                        onClick = { announcementsViewModel.setCategory(cat) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (announcements.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Campaign,
                    title = "No Announcements Found",
                    description = "No notices matching your filter. Check back later for administrative bulletins.",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(announcements, key = { it.announcementId }) { ann ->
                        AnnouncementCard(
                            announcement = ann,
                            onClick = {
                                announcementsViewModel.markAsRead(ann.announcementId)
                                selectedAnnouncementForDetail = ann
                            }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog
    selectedAnnouncementForDetail?.let { ann ->
        AlertDialog(
            onDismissRequest = { selectedAnnouncementForDetail = null },
            title = {
                Column {
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

                        if (ann.priority == AnnouncementPriority.URGENT) {
                            Text(
                                text = "Emergency",
                                color = PriorityUrgent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = ann.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = DeepCharcoal
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = ann.description,
                        fontSize = 14.sp,
                        color = DeepCharcoal,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = NeutralDivider)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Author: ${ann.authorName}",
                            fontSize = 11.sp,
                            color = SubtleGray
                        )
                        Text(
                            text = DateUtils.formatFull(ann.createdAt),
                            fontSize = 11.sp,
                            color = SubtleGray
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedAnnouncementForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown)
                ) {
                    Text("Close")
                }
            },
            containerColor = SoftWhite
        )
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (announcement.isPinned) MutedTerracotta.copy(alpha = 0.5f) else NeutralBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SoftWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (announcement.priority == AnnouncementPriority.URGENT) Color(0xFFFFEBEE)
                                else TerracottaContainer
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = announcement.category.label,
                            color = if (announcement.priority == AnnouncementPriority.URGENT) PriorityUrgent else MutedTerracotta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (announcement.priority == AnnouncementPriority.URGENT) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Urgent",
                            fontSize = 11.sp,
                            color = PriorityUrgent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (announcement.isPinned) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = MutedTerracotta,
                            modifier = Modifier.size(14.dp)
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
                text = announcement.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DeepCharcoal,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = announcement.description,
                fontSize = 13.sp,
                color = SubtleGray,
                maxLines = 3,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = announcement.authorName,
                    fontSize = 11.sp,
                    color = SubtleGray
                )
                Text(
                    text = DateUtils.getRelativeTimeSpan(announcement.createdAt),
                    fontSize = 11.sp,
                    color = SubtleGray
                )
            }
        }
    }
}
