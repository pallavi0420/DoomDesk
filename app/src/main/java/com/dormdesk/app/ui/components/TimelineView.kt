package com.dormdesk.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.ActivityLogEntry
import com.dormdesk.app.data.model.ComplaintStatus
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.utils.DateUtils

@Composable
fun ComplaintTimelineView(
    currentStatus: ComplaintStatus,
    activityLogs: List<ActivityLogEntry>,
    modifier: Modifier = Modifier
) {
    val stages = ComplaintStatus.values().toList()
    val currentStep = currentStatus.stepIndex

    Column(modifier = modifier) {
        stages.forEachIndexed { index, stage ->
            val stepNumber = stage.stepIndex
            val isCompleted = stepNumber < currentStep || (currentStatus == ComplaintStatus.CLOSED)
            val isCurrent = stepNumber == currentStep && currentStatus != ComplaintStatus.CLOSED
            val isUpcoming = stepNumber > currentStep

            val matchingLog = activityLogs.findLast {
                it.action.contains(stage.label, ignoreCase = true) ||
                        (stage == ComplaintStatus.SUBMITTED && it.action.contains("Submitted", ignoreCase = true)) ||
                        (stage == ComplaintStatus.RESOLVED && it.action.contains("Resolved", ignoreCase = true)) ||
                        (stage == ComplaintStatus.CLOSED && it.action.contains("Closed", ignoreCase = true))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Circle & vertical connector column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> SageGreen
                                    isCurrent -> MutedTerracotta
                                    else -> SoftWhite
                                }
                            )
                            .border(
                                width = 1.5.dp,
                                color = when {
                                    isCompleted -> SageGreen
                                    isCurrent -> MutedTerracotta
                                    else -> NeutralBorder
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SoftWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        } else if (isCurrent) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = SoftWhite,
                                modifier = Modifier.size(13.dp)
                            )
                        } else {
                            Text(
                                text = "$stepNumber",
                                color = SubtleGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (index < stages.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(36.dp)
                                .background(if (isCompleted) SageGreen else NeutralBorder)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Stage content description
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stage.label,
                            color = when {
                                isCurrent -> EspressoBrown
                                isCompleted -> DeepCharcoal
                                else -> SubtleGray
                            },
                            fontSize = 13.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            style = MaterialTheme.typography.titleSmall
                        )

                        if (matchingLog != null) {
                            Text(
                                text = DateUtils.formatShort(matchingLog.timestamp),
                                color = SubtleGray,
                                fontSize = 11.sp,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    if (matchingLog != null && matchingLog.note.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = matchingLog.note,
                            color = SubtleGray,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (isCurrent) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Currently in progress with hostel team",
                            color = MutedTerracotta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
