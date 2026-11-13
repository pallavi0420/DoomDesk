package com.dormdesk.app.ui.announcements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.AnnouncementCategory
import com.dormdesk.app.data.model.ComplaintPriority
import com.dormdesk.app.data.model.User
import com.dormdesk.app.ui.components.DormDeskCard
import com.dormdesk.app.ui.components.EmptyStateView
import com.dormdesk.app.ui.components.PriorityBadge
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.AnnouncementViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementListScreen(
    currentUser: User,
    announcementViewModel: AnnouncementViewModel,
    modifier: Modifier = Modifier
) {
    val allAnnouncements by announcementViewModel.allAnnouncements.collectAsState()
    val selectedCategory by announcementViewModel.selectedCategory.collectAsState()
    val searchQuery by announcementViewModel.searchQuery.collectAsState()

    val announcements = remember(allAnnouncements, selectedCategory, searchQuery) {
        announcementViewModel.getFilteredAnnouncements()
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingAnnouncement by remember { mutableStateOf<Announcement?>(null) }
    var deletingAnnouncementId by remember { mutableStateOf<String?>(null) }

    // Form inputs for create/edit
    var formTitle by remember { mutableStateOf("") }
    var formDescription by remember { mutableStateOf("") }
    var formCategory by remember { mutableStateOf(AnnouncementCategory.GENERAL_NOTICES) }
    var formPriority by remember { mutableStateOf(ComplaintPriority.MEDIUM) }
    var formIsPinned by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Scaffold(
        floatingActionButton = {
            if (currentUser.isWarden) {
                FloatingActionButton(
                    onClick = {
                        formTitle = ""
                        formDescription = ""
                        formCategory = AnnouncementCategory.GENERAL_NOTICES
                        formPriority = ComplaintPriority.MEDIUM
                        formIsPinned = false
                        editingAnnouncement = null
                        showCreateDialog = true
                    },
                    containerColor = EspressoBrown,
                    contentColor = SoftWhite,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Publish Notice")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Post Notice", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Hostel Notice Board",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Official announcements, utility updates, and hostel notices",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { announcementViewModel.setSearchQuery(it) },
                    placeholder = { Text("Search notices...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = WarmTaupe) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { announcementViewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = WarmTaupe)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { announcementViewModel.setCategory(null) },
                        label = { Text("All Notices") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EspressoBrown, selectedLabelColor = SoftWhite)
                    )
                    AnnouncementCategory.values().forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { announcementViewModel.setCategory(cat) },
                            label = { Text(cat.displayName) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EspressoBrown, selectedLabelColor = SoftWhite)
                        )
                    }
                }
            }

            if (announcements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        icon = Icons.Outlined.Campaign,
                        title = "No Notices Posted",
                        description = "There are no announcements in this category."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(announcements, key = { it.announcementId }) { notice ->
                        DormDeskCard(
                            backgroundColor = if (notice.isPinned) SoftWhite else MaterialTheme.colorScheme.surface,
                            borderColor = if (notice.isPinned) MutedTerracotta.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (notice.isPinned) {
                                        Icon(
                                            imageVector = Icons.Default.PushPin,
                                            contentDescription = "Pinned",
                                            tint = MutedTerracotta,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = notice.category.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (notice.isPinned) MutedTerracotta else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                PriorityBadge(priority = notice.priority)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = notice.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = notice.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${notice.createdBy} • ${dateFormat.format(Date(notice.createdAt))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WarmTaupe,
                                    fontSize = 11.sp
                                )

                                // Warden Control Actions
                                if (currentUser.isWarden) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = { announcementViewModel.togglePin(notice.announcementId) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (notice.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                                contentDescription = "Pin/Unpin",
                                                tint = if (notice.isPinned) MutedTerracotta else WarmTaupe,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                editingAnnouncement = notice
                                                formTitle = notice.title
                                                formDescription = notice.description
                                                formCategory = notice.category
                                                formPriority = notice.priority
                                                formIsPinned = notice.isPinned
                                                showCreateDialog = true
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Edit,
                                                contentDescription = "Edit",
                                                tint = WarmTaupe,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { deletingAnnouncementId = notice.announcementId },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Delete,
                                                contentDescription = "Delete",
                                                tint = StatusUrgent,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Dialog: Create or Edit Announcement
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = if (editingAnnouncement != null) "Edit Announcement" else "Publish New Notice",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = formTitle,
                        onValueChange = { formTitle = it },
                        label = { Text("Notice Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryMenuExpanded,
                        onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = formCategory.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryMenuExpanded,
                            onDismissRequest = { categoryMenuExpanded = false }
                        ) {
                            AnnouncementCategory.values().forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName) },
                                    onClick = {
                                        formCategory = cat
                                        categoryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Priority Selector
                    Text("Priority", style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ComplaintPriority.values().forEach { prio ->
                            val sel = formPriority == prio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (sel) prio.badgeColor else MaterialTheme.colorScheme.surface)
                                    .border(BorderStroke(1.dp, prio.badgeColor), RoundedCornerShape(6.dp))
                                    .clickable { formPriority = prio }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    prio.displayName,
                                    fontSize = 11.sp,
                                    color = if (sel) SoftWhite else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = formDescription,
                        onValueChange = { formDescription = it },
                        label = { Text("Notice Description *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 6
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pin Notice to Top", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = formIsPinned,
                            onCheckedChange = { formIsPinned = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MutedTerracotta)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingAnnouncement != null) {
                            announcementViewModel.updateAnnouncement(
                                editingAnnouncement!!.copy(
                                    title = formTitle.trim(),
                                    description = formDescription.trim(),
                                    category = formCategory,
                                    priority = formPriority,
                                    isPinned = formIsPinned
                                )
                            )
                        } else {
                            announcementViewModel.createAnnouncement(
                                title = formTitle,
                                description = formDescription,
                                category = formCategory,
                                priority = formPriority,
                                isPinned = formIsPinned,
                                author = currentUser.fullName
                            )
                        }
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown)
                ) {
                    Text(if (editingAnnouncement != null) "Save Changes" else "Publish", color = SoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Confirm Delete
    if (deletingAnnouncementId != null) {
        AlertDialog(
            onDismissRequest = { deletingAnnouncementId = null },
            title = { Text("Delete Announcement?", style = MaterialTheme.typography.titleMedium) },
            text = { Text("This notice will be permanently removed from the hostel notice board.") },
            confirmButton = {
                Button(
                    onClick = {
                        deletingAnnouncementId?.let { announcementViewModel.deleteAnnouncement(it) }
                        deletingAnnouncementId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusUrgent)
                ) {
                    Text("Delete", color = SoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingAnnouncementId = null }) { Text("Cancel") }
            }
        )
    }
}
