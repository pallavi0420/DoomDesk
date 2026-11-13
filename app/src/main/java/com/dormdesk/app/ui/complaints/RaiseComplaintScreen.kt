package com.dormdesk.app.ui.complaints

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.dormdesk.app.data.model.ComplaintCategory
import com.dormdesk.app.data.model.ComplaintPriority
import com.dormdesk.app.data.model.User
import com.dormdesk.app.ui.components.DormDeskCard
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseComplaintScreen(
    currentUser: User,
    complaintViewModel: ComplaintViewModel,
    onNavigateBack: () -> Unit,
    onComplaintSubmitted: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ComplaintCategory.ELECTRICAL) }
    var selectedPriority by remember { mutableStateOf(ComplaintPriority.MEDIUM) }
    var location by remember { mutableStateOf("${currentUser.block}, Room ${currentUser.roomNumber}") }
    var description by remember { mutableStateOf("") }
    var attachedImageUrl by remember { mutableStateOf<String?>(null) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val isSubmitting by complaintViewModel.isSubmitting.collectAsState()
    val errorMessage by complaintViewModel.errorMessage.collectAsState()

    // Sample photographs that users can attach to test photo upload functionality
    val samplePhotos = listOf(
        "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=600&q=80",
        "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?auto=format&fit=crop&w=600&q=80",
        "https://images.unsplash.com/photo-1584820927498-cfe5211fd8bf?auto=format&fit=crop&w=600&q=80"
    )

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Raise Complaint",
                subtitle = "Report an issue to the Warden Office",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage != null) {
                DormDeskCard(
                    backgroundColor = StatusUrgent.copy(alpha = 0.08f),
                    borderColor = StatusUrgent.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = StatusUrgent,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // 1. Complaint Basic Details
            DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Issue Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = EspressoBrown
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Complaint Title *") },
                    placeholder = { Text("e.g. Ceiling fan not working or water leak") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        ComplaintCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    selectedCategory = category
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Priority Selection
                Text(
                    text = "Priority Level",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ComplaintPriority.values().forEach { priority ->
                        val isSelected = selectedPriority == priority
                        val bg = if (isSelected) priority.badgeColor else MaterialTheme.colorScheme.surface
                        val textCol = if (isSelected) SoftWhite else MaterialTheme.colorScheme.onSurface

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bg)
                                .border(
                                    BorderStroke(1.dp, if (isSelected) priority.badgeColor else BorderLight),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedPriority = priority }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = priority.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = textCol
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / Room *") },
                    placeholder = { Text("Block B, Room 204") },
                    leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = WarmTaupe) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed Description *") },
                    placeholder = { Text("Describe the maintenance problem, when it started, and specific location...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 5
                )
            }

            // 2. Attached Photograph Section
            DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Photograph (Optional)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = EspressoBrown
                )
                Text(
                    text = "Attach an image to help the maintenance team assess tools required",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (attachedImageUrl != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, BorderLight), RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = attachedImageUrl,
                            contentDescription = "Attached issue photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { attachedImageUrl = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(DeepCharcoal.copy(alpha = 0.65f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = SoftWhite)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        samplePhotos.forEachIndexed { idx, url ->
                            OutlinedButton(
                                onClick = { attachedImageUrl = url },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, WarmTaupe.copy(alpha = 0.4f))
                            ) {
                                Icon(Icons.Outlined.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Photo ${idx + 1}", fontSize = 11.sp, color = EspressoBrown)
                            }
                        }
                    }
                }
            }

            // 3. Submit Button
            Button(
                onClick = {
                    complaintViewModel.clearMessages()
                    complaintViewModel.raiseComplaint(
                        currentUser = currentUser,
                        title = title,
                        category = selectedCategory,
                        priority = selectedPriority,
                        description = description,
                        location = location,
                        imageUrl = attachedImageUrl,
                        onSuccess = { newId ->
                            onComplaintSubmitted(newId)
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = SoftWhite)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null, tint = SoftWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Submit Complaint",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
