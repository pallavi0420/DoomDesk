package com.dormdesk.app.ui.complaints

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
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
import com.dormdesk.app.data.model.ComplaintCategory
import com.dormdesk.app.data.model.ComplaintPriority
import com.dormdesk.app.ui.components.CategoryChip
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.components.PriorityBadge
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.ComplaintsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseComplaintScreen(
    complaintsViewModel: ComplaintsViewModel,
    onNavigateBack: () -> Unit,
    onComplaintCreated: (String) -> Unit
) {
    val uiState by complaintsViewModel.uiState.collectAsState()
    val currentUser by complaintsViewModel.currentUser.collectAsState()

    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ComplaintCategory.ELECTRICAL) }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(ComplaintPriority.MEDIUM) }
    var location by remember {
        mutableStateOf(
            if (currentUser != null) "${currentUser?.hostel}, ${currentUser?.block}, Room ${currentUser?.roomNumber}" else "Room 204"
        )
    }
    var attachedImageUrl by remember { mutableStateOf<String?>(null) }
    var showSamplePhotoPicker by remember { mutableStateOf(false) }

    val samplePhotos = listOf(
        "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=600&q=80" to "Electrical / Appliance",
        "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=600&q=80" to "Plumbing / Tap leak",
        "https://images.unsplash.com/photo-1584467735871-8e85353a8413?auto=format&fit=crop&w=600&q=80" to "Furniture / Carpentry",
        "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=600&q=80" to "Wi-Fi / Router issue"
    )

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Raise New Complaint",
                subtitle = "Hostel maintenance request",
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Category Selection
                    Text(
                        text = "Complaint Category *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ComplaintCategory.values()) { category ->
                            CategoryChip(
                                label = category.label,
                                isSelected = selectedCategory == category,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Title
                    Text(
                        text = "Complaint Title *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; complaintsViewModel.clearMessages() },
                        placeholder = { Text("e.g. Washroom tap continuous leaking") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EspressoBrown,
                            unfocusedBorderColor = NeutralBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Description
                    Text(
                        text = "Detailed Description *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it; complaintsViewModel.clearMessages() },
                        placeholder = { Text("Explain when the issue started, noise/leak rate, or specific hazards...") },
                        minLines = 4,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EspressoBrown,
                            unfocusedBorderColor = NeutralBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Priority
                    Text(
                        text = "Priority Level *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ComplaintPriority.values().forEach { priority ->
                            val isSelected = selectedPriority == priority
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) EspressoBrown else SoftWhite)
                                    .border(
                                        1.dp,
                                        if (isSelected) EspressoBrown else NeutralBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedPriority = priority }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = priority.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SoftWhite else DeepCharcoal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Location
                    Text(
                        text = "Exact Location *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("e.g. Tagore Hostel, Block B, Room 204") },
                        leadingIcon = {
                            Icon(Icons.Default.Place, contentDescription = null, tint = SubtleGray)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EspressoBrown,
                            unfocusedBorderColor = NeutralBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Optional Photograph
                    Text(
                        text = "Attach Photograph (Optional)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (attachedImageUrl == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, NeutralBorder, RoundedCornerShape(10.dp))
                                .clickable { showSamplePhotoPicker = true }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = MutedTerracotta
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add Photo from Camera or Gallery",
                                    color = EspressoBrown,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, NeutralBorder, RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = attachedImageUrl,
                                contentDescription = "Attached photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { attachedImageUrl = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepCharcoal.copy(alpha = 0.7f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove photo",
                                    tint = SoftWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            complaintsViewModel.raiseComplaint(
                                title = title,
                                category = selectedCategory,
                                description = description,
                                priority = selectedPriority,
                                location = location,
                                imageUrl = attachedImageUrl,
                                onSuccess = onComplaintCreated
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EspressoBrown,
                            contentColor = SoftWhite
                        ),
                        enabled = !uiState.isSubmitting
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SoftWhite,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Submit Complaint",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Sample Photo Selection Dialog
    if (showSamplePhotoPicker) {
        AlertDialog(
            onDismissRequest = { showSamplePhotoPicker = false },
            title = {
                Text(
                    text = "Select Damage Photo",
                    fontWeight = FontWeight.Bold,
                    color = EspressoBrown
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Choose an image showing the issue:",
                        fontSize = 13.sp,
                        color = DeepCharcoal
                    )

                    samplePhotos.forEach { (url, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, NeutralBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    attachedImageUrl = url
                                    showSamplePhotoPicker = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = DeepCharcoal
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSamplePhotoPicker = false }) {
                    Text("Cancel", color = SubtleGray)
                }
            },
            containerColor = SoftWhite
        )
    }
}
