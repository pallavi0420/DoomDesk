package com.dormdesk.app.ui.complaints

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Search
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
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.ComplaintCategory
import com.dormdesk.app.data.model.User
import com.dormdesk.app.ui.components.*
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.ComplaintFilter
import com.dormdesk.app.viewmodel.ComplaintViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintListScreen(
    currentUser: User,
    complaintViewModel: ComplaintViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToRaiseComplaint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedFilter by complaintViewModel.selectedFilter.collectAsState()
    val selectedCategory by complaintViewModel.selectedCategory.collectAsState()
    val searchQuery by complaintViewModel.searchQuery.collectAsState()

    // For warden, shows all complaints; for student, shows only their complaints
    val complaints = remember(
        complaintViewModel.allComplaints.collectAsState().value,
        selectedFilter,
        selectedCategory,
        searchQuery,
        currentUser
    ) {
        complaintViewModel.getFilteredComplaints(if (currentUser.isStudent) currentUser.uid else null)
    }

    var showCategoryMenu by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Scaffold(
        floatingActionButton = {
            if (currentUser.isStudent) {
                FloatingActionButton(
                    onClick = onNavigateToRaiseComplaint,
                    containerColor = EspressoBrown,
                    contentColor = SoftWhite,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Raise")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Raise Complaint", style = MaterialTheme.typography.labelLarge)
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
            // Screen Header & Search Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (currentUser.isWarden) "Hostel Complaints" else "My Complaints",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (currentUser.isWarden) "Manage & coordinate resident maintenance requests" else "Track and verify your registered complaints",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { complaintViewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by ID, title, room, or issue...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = WarmTaupe) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { complaintViewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = WarmTaupe)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ComplaintFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { complaintViewModel.setFilter(filter) },
                            label = { Text(filter.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EspressoBrown,
                                selectedLabelColor = SoftWhite,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                // Category Filter Pills / Dropdown trigger
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == null) "All Categories" else "Category: ${selectedCategory?.displayName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MutedTerracotta,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box {
                        TextButton(onClick = { showCategoryMenu = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp), tint = MutedTerracotta)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Filter Category", style = MaterialTheme.typography.labelSmall, color = MutedTerracotta)
                        }

                        DropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Categories") },
                                onClick = {
                                    complaintViewModel.setCategory(null)
                                    showCategoryMenu = false
                                }
                            )
                            ComplaintCategory.values().forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName) },
                                    onClick = {
                                        complaintViewModel.setCategory(cat)
                                        showCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // List of Complaints
            if (complaints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        icon = Icons.Outlined.Search,
                        title = "No complaints found",
                        description = "Try adjusting your search query or filter selection.",
                        actionButtonText = if (currentUser.isStudent) "Raise Complaint" else null,
                        onActionClick = onNavigateToRaiseComplaint
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(complaints, key = { it.complaintId }) { complaint ->
                        DormDeskCard(
                            onClick = { onNavigateToDetail(complaint.complaintId) }
                        ) {
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

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = complaint.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = complaint.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = complaint.category.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = " • ",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = WarmTaupe
                                    )
                                    Text(
                                        text = complaint.location,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Text(
                                    text = dateFormat.format(Date(complaint.createdAt)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmTaupe
                                )
                            }

                            if (complaint.assignedStaffName.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tech: ${complaint.assignedStaffName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StatusAssigned,
                                    fontWeight = FontWeight.Medium
                                )
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
}
