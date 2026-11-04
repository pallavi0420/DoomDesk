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
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.ComplaintStatus
import com.dormdesk.app.ui.components.*
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.utils.DateUtils
import com.dormdesk.app.viewmodel.ComplaintFilterTab
import com.dormdesk.app.viewmodel.ComplaintsViewModel

@Composable
fun WardenComplaintsScreen(
    complaintsViewModel: ComplaintsViewModel,
    onNavigateToComplaintDetail: (String) -> Unit
) {
    val uiState by complaintsViewModel.uiState.collectAsState()
    val complaints by complaintsViewModel.wardenComplaints.collectAsState()

    var selectedComplaintForAction by remember { mutableStateOf<Complaint?>(null) }
    var newStatus by remember { mutableStateOf(ComplaintStatus.IN_PROGRESS) }
    var staffName by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var resolutionNotes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Complaints Management",
                subtitle = "Assign personnel & track work orders"
            )
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
                    placeholder = { Text("Search by student, room, ID, issue...") },
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

            // Status Filter Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
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
                    description = "No tickets match your filter criteria.",
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
                        WardenComplaintItemCard(
                            complaint = complaint,
                            onCardClick = { onNavigateToComplaintDetail(complaint.complaintId) },
                            onManageClick = {
                                selectedComplaintForAction = complaint
                                newStatus = complaint.status
                                staffName = complaint.assignedStaffName ?: ""
                                remarks = complaint.remarks ?: ""
                                resolutionNotes = complaint.resolutionNotes ?: ""
                            }
                        )
                    }
                }
            }
        }
    }

    // Warden Action / Status Update Dialog
    selectedComplaintForAction?.let { complaint ->
        AlertDialog(
            onDismissRequest = { selectedComplaintForAction = null },
            title = {
                Column {
                    Text(
                        text = "Manage ${complaint.complaintId}",
                        fontWeight = FontWeight.Bold,
                        color = EspressoBrown,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "${complaint.studentName} • ${complaint.location}",
                        fontSize = 12.sp,
                        color = SubtleGray
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = "Update Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(ComplaintStatus.values()) { st ->
                            CategoryChip(
                                label = st.label,
                                isSelected = newStatus == st,
                                onClick = { newStatus = st }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Assigned Maintenance Personnel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = staffName,
                        onValueChange = { staffName = it },
                        placeholder = { Text("e.g. Ramesh Kumar (Electrician)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Administrative Remarks / Progress Update", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        placeholder = { Text("e.g. Replacement capacitor ordered from store...") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (newStatus == ComplaintStatus.RESOLVED || newStatus == ComplaintStatus.RESOLUTION_UPDATED || newStatus == ComplaintStatus.CLOSED) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Resolution Notes / Justification", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SageGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = resolutionNotes,
                            onValueChange = { resolutionNotes = it },
                            placeholder = { Text("Details of work performed and components tested...") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        complaintsViewModel.wardenUpdateComplaint(
                            complaintId = complaint.complaintId,
                            newStatus = newStatus,
                            assignedStaffName = staffName.ifBlank { null },
                            remarks = remarks.ifBlank { null },
                            resolutionNotes = resolutionNotes.ifBlank { null },
                            completionPhotoUrl = null,
                            onSuccess = { selectedComplaintForAction = null }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown)
                ) {
                    Text("Save & Notify Resident")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedComplaintForAction = null }) {
                    Text("Cancel", color = SubtleGray)
                }
            },
            containerColor = SoftWhite
        )
    }
}

@Composable
fun WardenComplaintItemCard(
    complaint: Complaint,
    onCardClick: () -> Unit,
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NeutralBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onCardClick),
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
                    text = "${complaint.complaintId} • ${complaint.studentName} (${complaint.roomNumber})",
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

            if (!complaint.assignedStaffName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Engineering,
                        contentDescription = null,
                        tint = MutedTerracotta,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Assigned: ${complaint.assignedStaffName}",
                        fontSize = 11.sp,
                        color = DeepCharcoal,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

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
                    PriorityBadge(priority = complaint.priority)
                    Text(
                        text = complaint.category.label,
                        fontSize = 11.sp,
                        color = SubtleGray
                    )
                }

                Button(
                    onClick = onManageClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EspressoBrown,
                        contentColor = SoftWhite
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Update", fontSize = 12.sp)
                }
            }
        }
    }
}
