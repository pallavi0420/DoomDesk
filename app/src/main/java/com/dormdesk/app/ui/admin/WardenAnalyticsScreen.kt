package com.dormdesk.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.components.MetricCard
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.WardenDashboardViewModel

@Composable
fun WardenAnalyticsScreen(
    dashboardViewModel: WardenDashboardViewModel,
    onNavigateBack: () -> Unit
) {
    val state by dashboardViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Hostel Analytics",
                subtitle = "Maintenance performance & satisfaction",
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
            // KPI Highlight Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Avg Resolution",
                    value = "${state.averageResolutionHours}h",
                    icon = Icons.Outlined.Speed,
                    iconTint = SageGreen,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Resident Rating",
                    value = "${state.studentSatisfactionScore} / 5",
                    icon = Icons.Outlined.Star,
                    iconTint = MutedTerracotta,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Complaint Status Distribution Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Complaint Lifecycle Status Breakdown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    state.statusStats.forEach { stat ->
                        val pct = if (state.totalComplaints > 0) {
                            (stat.count.toFloat() / state.totalComplaints.toFloat()) * 100f
                        } else 0f

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stat.status.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DeepCharcoal
                                )
                                Text(
                                    text = "${stat.count} (${pct.toInt()}%)",
                                    fontSize = 11.sp,
                                    color = SubtleGray
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = (pct / 100f).coerceIn(0f, 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = when (stat.status.label) {
                                    "Resolved", "Closed" -> SageGreen
                                    "Work in Progress" -> StatusInProgressText
                                    "Submitted" -> StatusPendingText
                                    else -> EspressoBrown
                                },
                                trackColor = NeutralBorder
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Category Distribution Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Complaints by Category (All-Time)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    state.categoryStats.forEach { catStat ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = catStat.category.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DeepCharcoal
                                )
                                Text(
                                    text = "${catStat.count} ticket(s) (${catStat.percentage.toInt()}%)",
                                    fontSize = 11.sp,
                                    color = SubtleGray
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = (catStat.percentage / 100f).coerceIn(0f, 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = EspressoBrown,
                                trackColor = NeutralBorder
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Monthly Trend Snapshot Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Monthly Maintenance Volume Trend",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val months = listOf(
                        "Aug" to 12,
                        "Sep" to 18,
                        "Oct" to 22,
                        "Nov" to 15
                    )
                    val maxCount = months.maxOf { it.second }.toFloat()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        months.forEach { (month, count) ->
                            val heightFraction = (count / maxCount).coerceIn(0.1f, 1f)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(text = "$count", fontSize = 11.sp, color = SubtleGray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height((70 * heightFraction).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(MutedTerracotta)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = month,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepCharcoal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
