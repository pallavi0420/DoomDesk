package com.dormdesk.app.ui.complaints

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dormdesk.app.data.model.ComplaintStatus
import com.dormdesk.app.ui.components.*
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.utils.DateUtils
import com.dormdesk.app.viewmodel.ComplaintsViewModel

@Composable
fun ComplaintDetailScreen(
    complaintId: String,
    complaintsViewModel: ComplaintsViewModel,
    onNavigateBack: () -> Unit
) {
    val allComplaints by complaintsViewModel.allComplaints.collectAsState()
    val complaint = allComplaints.find { it.complaintId == complaintId }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var ratingValue by remember { mutableStateOf(5) }
    var ratingFeedback by remember { mutableStateOf("") }

    var showReopenDialog by remember { mutableStateOf(false) }
    var reopenReason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = complaint?.complaintId ?: "Complaint Details",
                subtitle = complaint?.category?.label ?: "Maintenance Ticket",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = WarmIvory
    ) { paddingValues ->
        if (complaint == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Complaint ticket not found", color = SubtleGray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header Status Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftWhite)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = complaint.complaintId,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SubtleGray
                            )
                            StatusBadge(status = complaint.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = complaint.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = complaint.description,
                            fontSize = 14.sp,
                            color = DeepCharcoal,
                            lineHeight = 20.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        HorizontalDivider(color = NeutralDivider, thickness = 1.dp)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Metadata Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Category", fontSize = 11.sp, color = SubtleGray)
                                Text(
                                    text = complaint.category.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DeepCharcoal
                                )
                            }

                            Column {
                                Text(text = "Priority", fontSize = 11.sp, color = SubtleGray)
                                PriorityBadge(priority = complaint.priority)
                            }

                            Column {
                                Text(text = "Submitted On", fontSize = 11.sp, color = SubtleGray)
                                Text(
                                    text = DateUtils.formatDateOnly(complaint.createdAt),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DeepCharcoal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column {
                            Text(text = "Location", fontSize = 11.sp, color = SubtleGray)
                            Text(
                                text = complaint.location,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = DeepCharcoal
                            )
                        }
                    }
                }

                // Attached Photograph (if available)
                complaint.imageUrl?.let { imgUrl ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftWhite)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Resident Attached Photograph",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepCharcoal
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            AsyncImage(
                                model = imgUrl,
                                contentDescription = "Attached photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }
                    }
                }

                // Assigned Staff & Warden Remarks Card
                if (!complaint.assignedStaffName.isNullOrBlank() || !complaint.remarks.isNullOrBlank() || !complaint.resolutionNotes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftWhite)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Administration & Maintenance Notes",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepCharcoal
                            )

                            if (!complaint.assignedStaffName.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Engineering,
                                        contentDescription = null,
                                        tint = MutedTerracotta,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = "Assigned Maintenance Personnel", fontSize = 11.sp, color = SubtleGray)
                                        Text(
                                            text = complaint.assignedStaffName!!,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = DeepCharcoal
                                        )
                                    }
                                }
                            }

                            if (!complaint.remarks.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "Warden Remarks", fontSize = 11.sp, color = SubtleGray)
                                Text(
                                    text = complaint.remarks!!,
                                    fontSize = 13.sp,
                                    color = DeepCharcoal,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (!complaint.resolutionNotes.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "Resolution Summary", fontSize = 11.sp, color = SageGreen, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = complaint.resolutionNotes!!,
                                    fontSize = 13.sp,
                                    color = DeepCharcoal,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Student Feedback & Rating Display (If already closed/confirmed)
                if (complaint.isConfirmedByStudent && complaint.studentRating > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SageContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Resident Resolution Confirmation",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SageGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            StarRatingBar(rating = complaint.studentRating, starSize = 20.dp)
                            if (!complaint.studentFeedback.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "\"${complaint.studentFeedback}\"",
                                    fontSize = 12.sp,
                                    color = DeepCharcoal,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                // 7-Stage Visual Progress Timeline Card
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftWhite)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Resolution Progress Timeline",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        ComplaintTimelineView(
                            currentStatus = complaint.status,
                            activityLogs = complaint.activityLogs
                        )
                    }
                }

                // Student Action Buttons
                // When marked RESOLVED: Resident can Confirm Resolution or Reopen
                if (complaint.status == ComplaintStatus.RESOLVED && !complaint.isConfirmedByStudent) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MutedTerracotta, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TerracottaContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Action Required: Verify Resolution",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EspressoBrown
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "The warden has marked this issue as resolved. Please verify the repair in your room and confirm or request reopening.",
                                fontSize = 12.sp,
                                color = DeepCharcoal
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showConfirmDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SageGreen,
                                        contentColor = SoftWhite
                                    )
                                ) {
                                    Text("Confirm & Rate", fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = { showReopenDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = PriorityUrgent
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(PriorityUrgent)
                                    )
                                ) {
                                    Text("Still Broken", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Confirm Resolution Dialog with Star Rating
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "Confirm Repair & Rate",
                    fontWeight = FontWeight.Bold,
                    color = EspressoBrown
                )
            },
            text = {
                Column {
                    Text(
                        text = "Are you satisfied with the maintenance work conducted?",
                        fontSize = 13.sp,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "Rating (Tap to rate)", fontSize = 12.sp, color = SubtleGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    StarRatingBar(
                        rating = ratingValue,
                        isInteractive = true,
                        onRatingChanged = { ratingValue = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = ratingFeedback,
                        onValueChange = { ratingFeedback = it },
                        placeholder = { Text("Optional feedback on technician punctuality / quality...") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        complaintsViewModel.confirmResolution(
                            complaintId = complaintId,
                            rating = ratingValue,
                            feedback = ratingFeedback.ifBlank { null },
                            onSuccess = { showConfirmDialog = false }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                ) {
                    Text("Confirm & Close Ticket")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = SubtleGray)
                }
            },
            containerColor = SoftWhite
        )
    }

    // Reopen Complaint Dialog
    if (showReopenDialog) {
        AlertDialog(
            onDismissRequest = { showReopenDialog = false },
            title = {
                Text(
                    text = "Reopen Complaint",
                    fontWeight = FontWeight.Bold,
                    color = PriorityUrgent
                )
            },
            text = {
                Column {
                    Text(
                        text = "Please state what remains unresolved or broken so the warden can dispatch maintenance again.",
                        fontSize = 13.sp,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reopenReason,
                        onValueChange = { reopenReason = it },
                        placeholder = { Text("e.g. Tap still drips after replacement, or fan makes sound again...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        complaintsViewModel.reopenComplaint(
                            complaintId = complaintId,
                            reason = reopenReason,
                            onSuccess = { showReopenDialog = false }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PriorityUrgent)
                ) {
                    Text("Submit Reopen Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReopenDialog = false }) {
                    Text("Cancel", color = SubtleGray)
                }
            },
            containerColor = SoftWhite
        )
    }
}
