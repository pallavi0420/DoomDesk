package com.dormdesk.app.ui.complaints

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
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
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.ui.components.*
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.utils.DateUtils
import com.dormdesk.app.viewmodel.ComplaintFilterTab
import com.dormdesk.app.viewmodel.ComplaintsViewModel

@Composable
fun ComplaintHistoryScreen(
    complaintsViewModel: ComplaintsViewModel,
    onNavigateToRaiseComplaint: () -> Unit,
    onNavigateToComplaintDetail: (String) -> Unit
) {
    val uiState by complaintsViewModel.uiState.collectAsState()
    val complaints by complaintsViewModel.studentComplaints.collectAsState()

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "My Complaints",
                subtitle = "Track maintenance requests & updates"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRaiseComplaint,
                containerColor = EspressoBrown,
                contentColor = SoftWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Raise Complaint")
            }
        },
        containerColor = WarmIvory
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { complaintsViewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by ID, keyword, category...") },
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

            // Filter Tabs (All, Pending, Assigned, In Progress, Resolved, Closed)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ComplaintFilterTab.values()) { tab ->
                    CategoryChip(
                        label = tab.label,
                        isSelected = uiState.selectedTab == tab,
                        onClick = { complaintsViewModel.setFilterTab(tab) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Complaints List
            if (complaints.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Assignment,
                    title = "No Complaints Found",
                    description = if (uiState.searchQuery.isNotBlank() || uiState.selectedTab != ComplaintFilterTab.ALL) {
                        "No complaints match your active filter criteria."
                    } else {
                        "You haven't logged any complaints yet. Tap the '+' button below if you need room maintenance."
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(complaints, key = { it.complaintId }) { complaint ->
                        ComplaintListItemCard(
                            complaint = complaint,
                            onClick = { onNavigateToComplaintDetail(complaint.complaintId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComplaintListItemCard(
    complaint: Complaint,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NeutralBorder, RoundedCornerShape(14.dp))
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
                Text(
                    text = complaint.complaintId,
                    color = SubtleGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = complaint.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = complaint.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepCharcoal,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = complaint.description,
                fontSize = 13.sp,
                color = SubtleGray,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeutralSurface)
                            .border(1.dp, NeutralBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = complaint.category.label,
                            fontSize = 11.sp,
                            color = EspressoLight,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    PriorityBadge(priority = complaint.priority)
                }

                Text(
                    text = DateUtils.formatDateOnly(complaint.createdAt),
                    fontSize = 11.sp,
                    color = SubtleGray
                )
            }
        }
    }
}
