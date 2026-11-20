package com.dormdesk.app.ui.complaints

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.ComplaintStatus
import com.dormdesk.app.data.model.User
import com.dormdesk.app.ui.components.*
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.ComplaintViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    complaintId: String,
    currentUser: User,
    complaintViewModel: ComplaintViewModel,
    onNavigateBack: () -> Unit
) {
    val allComplaints by complaintViewModel.allComplaints.collectAsState()
    val complaint = allComplaints.find { it.complaintId == complaintId }

    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()) }

    // Dialog States
    var showAssignDialog by remember { mutableStateOf(false) }
    var staffNameInput by remember { mutableStateOf("") }
    var staffRemarkInput by remember { mutableStateOf("") }

    var showUpdateStatusDialog by remember { mutableStateOf(false) }
    var selectedNewStatus by remember { mutableStateOf(complaint?.status ?: ComplaintStatus.SUBMITTED) }
    var statusRemarkInput by remember { mutableStateOf("") }

    var showResolutionNotesDialog by remember { mutableStateOf(false) }
    var resolutionNotesInput by remember { mutableStateOf("") }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmRating by remember { mutableStateOf(5) }

    var showReopenDialog by remember { mutableStateOf(false) }
    var reopenReasonInput by remember { mutableStateOf("") }

    var showAdminCloseDialog by remember { mutableStateOf(false) }
    var adminCloseReasonInput by remember { mutableStateOf("") }

    var showWardenFeedbackDialog by remember { mutableStateOf(false) }
    var wardenFeedbackInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Complaint #${complaintId}",
                subtitle = complaint?.category?.displayName ?: "Complaint Details",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (complaint == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Complaint record not found.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Complaint Status & Priority Banner
                DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PriorityBadge(priority = complaint.priority)
                        StatusBadge(status = complaint.status)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = complaint.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Submitted by ${complaint.studentName} (${complaint.roomNumber}) on ${dateFormat.format(Date(complaint.createdAt))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = MutedTerracotta, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = complaint.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = complaint.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 2. Attached Photographs (if any)
                if (complaint.imageUrl != null) {
                    DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Resident Attachment",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = EspressoBrown
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = complaint.imageUrl,
                                contentDescription = "Attached Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Completion Image (if uploaded by maintenance team)
                if (complaint.completionImageUrl != null) {
                    DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Work Completion Verification Photo",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = SageGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = complaint.completionImageUrl,
                                contentDescription = "Work Completion Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // 3. Maintenance Personnel & Warden Remarks
                DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Maintenance & Administration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = EspressoBrown
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Engineering, contentDescription = null, tint = StatusAssigned, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (complaint.assignedStaffName.isNotBlank()) "Assigned: ${complaint.assignedStaffName}" else "Technician: Unassigned",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (complaint.remarks.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MutedTerracotta.copy(alpha = 0.08f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.RateReview, contentDescription = null, tint = MutedTerracotta, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Official Warden Feedback / Remarks",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MutedTerracotta
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = complaint.remarks,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    if (complaint.resolutionNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Resolution Notes: ${complaint.resolutionNotes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SageGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (complaint.studentRating > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Resident Rating: ", style = MaterialTheme.typography.bodySmall, color = WarmTaupe)
                            StarRatingBar(rating = complaint.studentRating, starSize = 16)
                        }
                    }

                    if (complaint.reopenReason.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reopened Notice: ${complaint.reopenReason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusUrgent
                        )
                    }
                }

                // 4. Visual 7-Step Progress Timeline
                DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Lifecycle Progress Timeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = EspressoBrown
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ComplaintTimelineView(currentStatus = complaint.status)
                }

                // 5. Actions Section for Student
                if (currentUser.isStudent) {
                    // If Resolution Updated or Resolved, prompt student to confirm or reopen
                    if (complaint.status == ComplaintStatus.RESOLUTION_UPDATED || complaint.status == ComplaintStatus.RESOLVED) {
                        DormDeskCard(
                            backgroundColor = SageGreen.copy(alpha = 0.08f),
                            borderColor = SageGreen.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Action Required: Verify Resolution",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SageGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Maintenance has updated resolution notes. Please test your room fixture. If satisfied, confirm closure with a rating; otherwise reopen for re-inspection.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showConfirmDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Confirm Closure", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { showReopenDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusUrgent),
                                    border = BorderStroke(1.dp, StatusUrgent),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reopen Issue", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // 6. Actions Section for Warden / Admin
                if (currentUser.isWarden) {
                    DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Administrative Controls",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = EspressoBrown
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Quick Action based on current stage
                            when (complaint.status) {
                                ComplaintStatus.SUBMITTED -> {
                                    Button(
                                        onClick = {
                                            complaintViewModel.updateStatus(
                                                complaintId,
                                                ComplaintStatus.RECEIVED,
                                                "Complaint received and reviewed by Warden administration."
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Mark as Received by Warden")
                                    }
                                }
                                ComplaintStatus.RECEIVED -> {
                                    Button(
                                        onClick = {
                                            staffNameInput = complaint.assignedStaffName
                                            showAssignDialog = true
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Outlined.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Assign Maintenance Technician")
                                    }
                                }
                                ComplaintStatus.ASSIGNED -> {
                                    Button(
                                        onClick = {
                                            complaintViewModel.updateStatus(
                                                complaintId,
                                                ComplaintStatus.IN_PROGRESS,
                                                "Technician ${complaint.assignedStaffName} commenced on-site repair."
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusInProgress),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Outlined.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Mark Work In Progress")
                                    }
                                }
                                ComplaintStatus.IN_PROGRESS -> {
                                    Button(
                                        onClick = {
                                            resolutionNotesInput = complaint.resolutionNotes
                                            showResolutionNotesDialog = true
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Record Resolution & Update")
                                    }
                                }
                                else -> {}
                            }

                            // 1. Submit Feedback / Remarks on Complaint
                            Button(
                                onClick = {
                                    wardenFeedbackInput = complaint.remarks
                                    showWardenFeedbackDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MutedTerracotta),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Outlined.RateReview, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit Warden Feedback / Remarks")
                            }

                            // 2. Update Status
                            OutlinedButton(
                                onClick = {
                                    selectedNewStatus = complaint.status
                                    statusRemarkInput = ""
                                    showUpdateStatusDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, WarmTaupe.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(18.dp), tint = EspressoBrown)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Update Status (${complaint.status.displayName})", color = EspressoBrown)
                            }

                            // 3. Assign / Reassign Staff
                            OutlinedButton(
                                onClick = {
                                    staffNameInput = complaint.assignedStaffName
                                    staffRemarkInput = ""
                                    showAssignDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, WarmTaupe.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Outlined.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp), tint = EspressoBrown)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (complaint.assignedStaffName.isBlank()) "Assign Maintenance Staff" else "Reassign Staff (${complaint.assignedStaffName})", color = EspressoBrown)
                            }

                            // 4. Add Resolution Notes
                            OutlinedButton(
                                onClick = {
                                    resolutionNotesInput = complaint.resolutionNotes
                                    showResolutionNotesDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, WarmTaupe.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(18.dp), tint = SageGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Record Resolution Notes", color = SageGreen)
                            }

                            // 5. Administrative Close
                            if (!complaint.isClosed) {
                                TextButton(
                                    onClick = { showAdminCloseDialog = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Administrative Close With Reason", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Dialog: Assign Staff
    if (showAssignDialog) {
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Assign Maintenance Personnel", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    Text("Enter the name and specialization of the technician:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = staffNameInput,
                        onValueChange = { staffNameInput = it },
                        label = { Text("Personnel Name") },
                        placeholder = { Text("e.g. Ramesh Kumar (Carpenter)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = staffRemarkInput,
                        onValueChange = { staffRemarkInput = it },
                        label = { Text("Remarks (Optional)") },
                        placeholder = { Text("e.g. Dispatched with replacement parts") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        complaintViewModel.assignStaff(complaintId, staffNameInput, staffRemarkInput)
                        showAssignDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown)
                ) {
                    Text("Assign", color = SoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Update Status
    if (showUpdateStatusDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateStatusDialog = false },
            title = { Text("Update Complaint Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Select new progress stage for #${complaint?.complaintId ?: complaintId}:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    ComplaintStatus.values().forEach { st ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedNewStatus = st },
                            color = if (selectedNewStatus == st) st.badgeColor.copy(alpha = 0.12f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (selectedNewStatus == st) st.badgeColor else BorderLight.copy(alpha = 0.4f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                RadioButton(
                                    selected = selectedNewStatus == st,
                                    onClick = { selectedNewStatus = st }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = st.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (selectedNewStatus == st) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = statusRemarkInput,
                        onValueChange = { statusRemarkInput = it },
                        label = { Text("Progress Update Remark (Optional)") },
                        placeholder = { Text("e.g. Parts arrived, dispatched to Room 204") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        complaintViewModel.updateStatus(complaintId, selectedNewStatus, statusRemarkInput)
                        showUpdateStatusDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown)
                ) {
                    Text("Update Status", color = SoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateStatusDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Submit Warden Feedback / Remarks on Complaint
    if (showWardenFeedbackDialog) {
        AlertDialog(
            onDismissRequest = { showWardenFeedbackDialog = false },
            title = {
                Text("Submit Warden Feedback / Remarks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        "Add official administrative feedback, inspection updates, or action notes for ticket #${complaint?.complaintId ?: complaintId}:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = wardenFeedbackInput,
                        onValueChange = { wardenFeedbackInput = it },
                        label = { Text("Official Remarks / Feedback *") },
                        placeholder = { Text("e.g. Inspected fixture. Electrician Prakash assigned to replace switchboard by 4 PM.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (wardenFeedbackInput.isNotBlank()) {
                            complaintViewModel.addRemarks(complaintId, wardenFeedbackInput)
                            showWardenFeedbackDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MutedTerracotta),
                    enabled = wardenFeedbackInput.isNotBlank()
                ) {
                    Text("Submit Feedback", color = SoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWardenFeedbackDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Record Resolution Notes
    if (showResolutionNotesDialog) {
        AlertDialog(
            onDismissRequest = { showResolutionNotesDialog = false },
            title = { Text("Record Resolution Notes", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    Text("Explain the repair work performed by maintenance:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = resolutionNotesInput,
                        onValueChange = { resolutionNotesInput = it },
                        label = { Text("Resolution Notes") },
                        placeholder = { Text("Replaced defective part, inspected wiring, tested...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        complaintViewModel.updateResolution(complaintId, resolutionNotesInput)
                        showResolutionNotesDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                ) {
                    Text("Save Notes", color = SoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResolutionNotesDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Student Confirm Closure & Rating
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Resolution", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Are you satisfied with the maintenance work? Please rate your experience:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    StarRatingBar(
                        rating = confirmRating,
                        onRatingChanged = { confirmRating = it },
                        starSize = 32
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        complaintViewModel.confirmResolution(complaintId, confirmRating)
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                ) {
                    Text("Confirm & Close Ticket", color = SoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Student Reopen Issue
    if (showReopenDialog) {
        AlertDialog(
            onDismissRequest = { showReopenDialog = false },
            title = { Text("Reopen Complaint", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    Text("Please explain why the issue is not resolved:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = reopenReasonInput,
                        onValueChange = { reopenReasonInput = it },
                        label = { Text("Reason for Reopening *") },
                        placeholder = { Text("e.g. Issue persists, water still dripping...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        complaintViewModel.reopenComplaint(complaintId, reopenReasonInput)
                        showReopenDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusUrgent)
                ) {
                    Text("Reopen Ticket", color = SoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReopenDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Administrative Close
    if (showAdminCloseDialog) {
        AlertDialog(
            onDismissRequest = { showAdminCloseDialog = false },
            title = { Text("Administrative Closure", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    Text("State reason for administrative closure without student verification:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = adminCloseReasonInput,
                        onValueChange = { adminCloseReasonInput = it },
                        label = { Text("Administrative Justification *") },
                        placeholder = { Text("e.g. Verified by warden during evening inspection rounds") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        complaintViewModel.administrativeClose(complaintId, adminCloseReasonInput)
                        showAdminCloseDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown)
                ) {
                    Text("Confirm Closure", color = SoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminCloseDialog = false }) { Text("Cancel") }
            }
        )
    }
}
