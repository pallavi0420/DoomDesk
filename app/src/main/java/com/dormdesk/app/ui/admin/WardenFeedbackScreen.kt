package com.dormdesk.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.FeedbackCategory
import com.dormdesk.app.data.model.FeedbackItem
import com.dormdesk.app.ui.components.CategoryChip
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.components.EmptyStateView
import com.dormdesk.app.ui.components.StarRatingBar
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.utils.DateUtils
import com.dormdesk.app.viewmodel.FeedbackViewModel

@Composable
fun WardenFeedbackScreen(
    feedbackViewModel: FeedbackViewModel
) {
    val feedbacks by feedbackViewModel.allFeedbacks.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf<FeedbackCategory?>(null) }
    var selectedFeedbackForReply by remember { mutableStateOf<FeedbackItem?>(null) }
    var replyText by remember { mutableStateOf("") }

    val filteredFeedbacks = feedbacks.filter { item ->
        selectedCategoryFilter == null || item.category == selectedCategoryFilter
    }.sortedByDescending { it.createdAt }

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Resident Feedback",
                subtitle = "Student reviews & administration replies"
            )
        },
        containerColor = WarmIvory
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category Filter Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        label = "All Feedback (${feedbacks.size})",
                        isSelected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null }
                    )
                }
                items(FeedbackCategory.values()) { cat ->
                    val count = feedbacks.count { it.category == cat }
                    CategoryChip(
                        label = "${cat.label} ($count)",
                        isSelected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (filteredFeedbacks.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.RateReview,
                    title = "No Feedback in this Category",
                    description = "Student submissions will appear here.",
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
                    items(filteredFeedbacks, key = { it.feedbackId }) { item ->
                        WardenFeedbackCard(
                            feedback = item,
                            onReplyClick = {
                                selectedFeedbackForReply = item
                                replyText = item.adminResponse ?: ""
                            }
                        )
                    }
                }
            }
        }
    }

    // Warden Response Dialog
    selectedFeedbackForReply?.let { fb ->
        AlertDialog(
            onDismissRequest = { selectedFeedbackForReply = null },
            title = {
                Column {
                    Text(
                        text = "Respond to Feedback",
                        fontWeight = FontWeight.Bold,
                        color = EspressoBrown,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "From: ${if (fb.isAnonymous) "Anonymous Resident" else fb.studentName} • ${fb.category.label}",
                        fontSize = 12.sp,
                        color = SubtleGray
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "\"${fb.description}\"",
                        fontSize = 13.sp,
                        color = DeepCharcoal,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Official Warden Response",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Write administrative action taken or reassurance...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        feedbackViewModel.wardenRespond(
                            feedbackId = fb.feedbackId,
                            responseText = replyText,
                            onSuccess = { selectedFeedbackForReply = null }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown)
                ) {
                    Text("Publish Response")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedFeedbackForReply = null }) {
                    Text("Cancel", color = SubtleGray)
                }
            },
            containerColor = SoftWhite
        )
    }
}

@Composable
fun WardenFeedbackCard(
    feedback: FeedbackItem,
    onReplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NeutralBorder, RoundedCornerShape(14.dp)),
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
                            text = feedback.category.label,
                            color = MutedTerracotta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (feedback.isAnonymous) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeutralSurface)
                                .border(1.dp, NeutralBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Anonymous",
                                fontSize = 10.sp,
                                color = SubtleGray
                            )
                        }
                    }
                }

                StarRatingBar(rating = feedback.rating, starSize = 16.dp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = feedback.description,
                fontSize = 14.sp,
                color = DeepCharcoal,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${if (feedback.isAnonymous) "Anonymous Resident" else feedback.studentName} • ${DateUtils.formatDateOnly(feedback.createdAt)}",
                    fontSize = 11.sp,
                    color = SubtleGray
                )

                Button(
                    onClick = onReplyClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (feedback.adminResponse.isNullOrBlank()) EspressoBrown else NeutralBorder,
                        contentColor = if (feedback.adminResponse.isNullOrBlank()) SoftWhite else DeepCharcoal
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (feedback.adminResponse.isNullOrBlank()) "Respond" else "Edit Reply",
                        fontSize = 11.sp
                    )
                }
            }

            // Existing response preview
            if (!feedback.adminResponse.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SageContainer)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Admin Reply by ${feedback.adminRespondedBy ?: "Warden"}:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = feedback.adminResponse,
                            fontSize = 12.sp,
                            color = DeepCharcoal
                        )
                    }
                }
            }
        }
    }
}
