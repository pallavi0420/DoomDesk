package com.dormdesk.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.utils.DateUtils

@Composable
fun RecentComplaintCard(
    complaint: Complaint,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NeutralBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SoftWhite)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = complaint.complaintId,
                    color = SubtleGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                StatusBadge(status = complaint.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = complaint.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepCharcoal,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = complaint.category.label,
                        fontSize = 12.sp,
                        color = SubtleGray
                    )
                    PriorityBadge(priority = complaint.priority)
                }

                Text(
                    text = DateUtils.getRelativeTimeSpan(complaint.updatedAt),
                    fontSize = 11.sp,
                    color = SubtleGray
                )
            }
        }
    }
}
