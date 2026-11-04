package com.dormdesk.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun WardenAnnouncementsScreen(
    announcementsViewModel: AnnouncementsViewModel
) {
    val announcements by announcementsViewModel.filteredAnnouncements.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingAnnouncement by remember { mutableStateOf<Announcement?>(null) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(AnnouncementCategory.GENERAL_NOTICES) }
    var priority by remember { mutableStateOf(AnnouncementPriority.NORMAL) }
    var isPinned by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Manage Notice Board",
                subtitle = "Publish & coordinate hostel notices"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    title = ""
                    description = ""
                    category = AnnouncementCategory.GENERAL_NOTICES
                    priority = AnnouncementPriority.NORMAL
                    isPinned = false
                    editingAnnouncement = null
                    showCreateDialog = true
                },
                containerColor = EspressoBrown,
                contentColor = SoftWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Announcement")
            }
        },
        containerColor = WarmIvory
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (announcements.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Campaign,
                    title = "No Announcements Published",
                    description = "Tap '+' below to compose and publish an official hostel notice.",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(announcements, key = { it.announcementId }) { ann ->
                        WardenAnnouncementItemCard(
                            announcement = ann,
                            onEditClick = {
                                editingAnnouncement = ann
                                title = ann.title
                                description = ann.description
                                category = ann.category
                                priority = ann.priority
                                isPinned = ann.isPinned
                                showCreateDialog = true
                            },
                            onDeleteClick = {
                                announcementsViewModel.deleteAnnouncement(ann.announcementId)
                            }
                        )
                    }
                }
            }
        }
    }

    // Compose / Edit Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = if (editingAnnouncement == null) "Publish New Circular" else "Edit Circular",
                    fontWeight = FontWeight.Bold,
                    color = EspressoBrown
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = "Notice Title *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("e.g. Water Tank Maintenance") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Category *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(AnnouncementCategory.values()) { cat ->
                            CategoryChip(
                                label = cat.label,
                                isSelected = category == cat,
                                onClick = { category = cat }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Priority Level", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AnnouncementPriority.values().forEach { prio ->
                            val isSel = priority == prio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) EspressoBrown else SoftWhite)
                                    .border(1.dp, if (isSel) EspressoBrown else NeutralBorder, RoundedCornerShape(8.dp))
                                    .clickable { priority = prio }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prio.label.split(" ").first(),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) SoftWhite else DeepCharcoal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Description / Circular Body *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Write clear instructions, dates, timings, or safety measures...") },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPinned = !isPinned }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isPinned,
                            onCheckedChange = { isPinned = it },
                            colors = CheckboxDefaults.colors(checkedColor = EspressoBrown)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pin to top of student notice board",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = DeepCharcoal
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && description.isNotBlank()) {
                            if (editingAnnouncement == null) {
                                announcementsViewModel.createAnnouncement(
                                    title = title,
                                    description = description,
                                    category = category,
                                    priority = priority,
                                    isPinned = isPinned,
                                    onSuccess = { showCreateDialog = false }
                                )
                            } else {
                                announcementsViewModel.updateAnnouncement(
                                    announcementId = editingAnnouncement!!.announcementId,
                                    title = title,
                                    description = description,
                                    category = category,
                                    priority = priority,
                                    isPinned = isPinned,
                                    onSuccess = { showCreateDialog = false }
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown)
                ) {
                    Text(if (editingAnnouncement == null) "Publish Notice" else "Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = SubtleGray)
                }
            },
            containerColor = SoftWhite
        )
    }
}

@Composable
fun WardenAnnouncementItemCard(
    announcement: Announcement,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (announcement.isPinned) MutedTerracotta.copy(alpha = 0.5f) else NeutralBorder,
                RoundedCornerShape(14.dp)
            ),
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
                            .background(TerracottaContainer)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = announcement.category.label,
                            color = MutedTerracotta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (announcement.isPinned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = MutedTerracotta,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Pinned",
                                color = MutedTerracotta,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = SubtleGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = PriorityUrgent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Priority: ${announcement.priority.label}",
                    fontSize = 11.sp,
                    color = if (announcement.priority == AnnouncementPriority.URGENT) PriorityUrgent else SubtleGray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = DateUtils.formatDateOnly(announcement.createdAt),
                    fontSize = 11.sp,
                    color = SubtleGray
                )
            }
        }
    }
}
