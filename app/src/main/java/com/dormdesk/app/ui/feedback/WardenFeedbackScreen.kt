package com.dormdesk.app.ui.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.Feedback
import com.dormdesk.app.data.model.FeedbackCategory
import com.dormdesk.app.ui.components.DormDeskCard
import com.dormdesk.app.ui.components.EmptyStateView
import com.dormdesk.app.ui.components.StarRatingBar
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.FeedbackViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardenFeedbackScreen(
    feedbackViewModel: FeedbackViewModel,
    modifier: Modifier = Modifier
) {
    val allFeedbacks by feedbackViewModel.allFeedbacks.collectAsState()
    val selectedCategory by feedbackViewModel.selectedCategory.collectAsState()

    val filteredFeedbacks = remember(allFeedbacks, selectedCategory) {
        if (selectedCategory == null) allFeedbacks else allFeedbacks.filter { it.category == selectedCategory }
    }

    val avgRating = remember(allFeedbacks) {
        if (allFeedbacks.isEmpty()) 0.0 else allFeedbacks.map { it.rating }.average()
    }

    var respondingFeedback by remember { mutableStateOf<Feedback?>(null) }
    var replyInput by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header & Analytics Summary
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Resident Feedback",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Review student satisfaction and publish official replies",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Summary Card
            DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Average Satisfaction",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f", avgRating),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = EspressoBrown
                            )
                            Text(" / 5.0", style = MaterialTheme.typography.bodyMedium, color = WarmTaupe)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Submissions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${allFeedbacks.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = EspressoBrown)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { feedbackViewModel.setCategory(null) },
                    label = { Text("All Categories") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EspressoBrown, selectedLabelColor = SoftWhite)
                )
                FeedbackCategory.values().forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { feedbackViewModel.setCategory(cat) },
                        label = { Text(cat.displayName) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EspressoBrown, selectedLabelColor = SoftWhite)
                    )
                }
            }
        }

        if (filteredFeedbacks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    icon = Icons.Outlined.RateReview,
                    title = "No Feedback Records",
                    description = "No feedback matches the selected category."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredFeedbacks, key = { it.feedbackId }) { item ->
                    DormDeskCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.displayedAuthor,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isAnonymous) WarmTaupe else EspressoBrown
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${item.category.displayName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StarRatingBar(rating = item.rating, starSize = 14)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateFormat.format(Date(item.createdAt)),
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmTaupe,
                                fontSize = 11.sp
                            )

                            if (item.adminResponse.isBlank()) {
                                TextButton(
                                    onClick = {
                                        replyInput = ""
                                        respondingFeedback = item
                                    }
                                ) {
                                    Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(14.dp), tint = MutedTerracotta)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reply", style = MaterialTheme.typography.labelSmall, color = MutedTerracotta)
                                }
                            }
                        }

                        if (item.adminResponse.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SageGreen.copy(alpha = 0.08f))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Official Response",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = SageGreen
                                        )
                                        TextButton(onClick = {
                                            replyInput = item.adminResponse
                                            respondingFeedback = item
                                        }) {
                                            Text("Edit", fontSize = 10.sp, color = SageGreen)
                                        }
                                    }
                                    Text(
                                        text = item.adminResponse,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
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

    // Dialog: Respond to Feedback
    if (respondingFeedback != null) {
        AlertDialog(
            onDismissRequest = { respondingFeedback = null },
            title = { Text("Respond to Feedback", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    Text(
                        "Student feedback on ${respondingFeedback?.category?.displayName}:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "\"${respondingFeedback?.description}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = replyInput,
                        onValueChange = { replyInput = it },
                        label = { Text("Official Warden Response") },
                        placeholder = { Text("Write response or actions taken...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        respondingFeedback?.let {
                            feedbackViewModel.respondToFeedback(it.feedbackId, replyInput)
                        }
                        respondingFeedback = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown)
                ) {
                    Text("Publish Response", color = SoftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { respondingFeedback = null }) { Text("Cancel") }
            }
        )
    }
}
