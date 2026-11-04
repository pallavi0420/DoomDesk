package com.dormdesk.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.ComplaintStatus
import com.dormdesk.app.ui.theme.*

@Composable
fun StatusBadge(
    status: ComplaintStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        ComplaintStatus.SUBMITTED -> StatusPendingBg to StatusPendingText
        ComplaintStatus.RECEIVED -> StatusAssignedBg to StatusAssignedText
        ComplaintStatus.ASSIGNED -> StatusAssignedBg to StatusAssignedText
        ComplaintStatus.IN_PROGRESS -> StatusInProgressBg to StatusInProgressText
        ComplaintStatus.RESOLUTION_UPDATED -> SageContainer to SageGreen
        ComplaintStatus.RESOLVED -> StatusResolvedBg to StatusResolvedText
        ComplaintStatus.CLOSED -> StatusClosedBg to StatusClosedText
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
