package com.dormdesk.app.ui.feedback

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
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Send
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
fun StudentFeedbackScreen(
    feedbackViewModel: FeedbackViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by feedbackViewModel.uiState.collectAsState()
    val feedbacks by feedbackViewModel.studentFeedbacks.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0 = Submit Feedback, 1 = My Submissions

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Feedback & Suggestions",
                subtitle = "Help us improve hostel life",
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
        ) {
            // Segmented Tab Switch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeutralBorder)
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == 0) SoftWhite else Color.Transparent)
                            .clickable { activeTab = 0 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "New Feedback",
                            fontSize = 13.sp,
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (activeTab == 0) EspressoBrown else SubtleGray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == 1) SoftWhite else Color.Transparent)
                            .clickable { activeTab = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "My Submissions (${feedbacks.size})",
                            fontSize = 13.sp,
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (activeTab == 1) EspressoBrown else SubtleGray
                        )
                    }
                }
            }

            if (activeTab == 0) {
                // Submit Feedback Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftWhite)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Feedback Category",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(FeedbackCategory.values()) { category ->
                                    CategoryChip(
                                        label = category.label,
                                        isSelected = uiState.selectedCategory == category,
                                        onClick = { feedbackViewModel.setCategory(category) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Overall Experience Rating",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            StarRatingBar(
                                rating = uiState.rating,
                                starSize = 28.dp,
                                isInteractive = true,
                                onRatingChanged = { feedbackViewModel.setRating(it) }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Your Observations & Suggestions",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = uiState.description,
                                onValueChange = { feedbackViewModel.setDescription(it) },
                                placeholder = { Text("What did you like or what needs improvement? (e.g. food quality, study hours, sanitation)...") },
                                minLines = 4,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EspressoBrown,
                                    unfocusedBorderColor = NeutralBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Anonymous Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { feedbackViewModel.setAnonymous(!uiState.isAnonymous) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = uiState.isAnonymous,
                                    onCheckedChange = { feedbackViewModel.setAnonymous(it) },
                                    colors = CheckboxDefaults.colors(checkedColor = EspressoBrown)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Submit Anonymously",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DeepCharcoal
                                    )
                                    Text(
                                        text = "Your name and room number will be hidden from the warden committee.",
                                        fontSize = 11.sp,
                                        color = SubtleGray
                                    )
                                }
                            }

                            if (uiState.errorMessage != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = uiState.errorMessage!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }

                            if (uiState.successMessage != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = uiState.successMessage!!,
                                    color = SageGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { feedbackViewModel.submitFeedback { activeTab = 1 } },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown),
                                enabled = !uiState.isSubmitting
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Submit Feedback",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                // Submissions List
                if (feedbacks.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.RateReview,
                        title = "No Feedback Submitted Yet",
                        description = "Switch to 'New Feedback' to share your thoughts with the hostel administration.",
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
                        items(feedbacks, key = { it.feedbackId }) { item ->
                            FeedbackItemCard(feedback = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackItemCard(
    feedback: FeedbackItem,
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (feedback.isAnonymous) "Submitted Anonymously" else feedback.studentName,
                    fontSize = 11.sp,
                    color = SubtleGray
                )
                Text(
                    text = DateUtils.formatDateOnly(feedback.createdAt),
                    fontSize = 11.sp,
                    color = SubtleGray
                )
            }

            // Warden Response Section
            if (!feedback.adminResponse.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SageContainer)
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Warden Response (${feedback.adminRespondedBy ?: "Hostel Office"})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SageGreen
                            )
                            feedback.respondedAt?.let {
                                Text(
                                    text = DateUtils.getRelativeTimeSpan(it),
                                    fontSize = 10.sp,
                                    color = SubtleGray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
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
