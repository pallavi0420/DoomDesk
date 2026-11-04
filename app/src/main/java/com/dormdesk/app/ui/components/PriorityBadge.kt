package com.dormdesk.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.dormdesk.app.data.model.ComplaintPriority
import com.dormdesk.app.ui.theme.*

@Composable
fun PriorityBadge(
    priority: ComplaintPriority,
    modifier: Modifier = Modifier
) {
    val (dotColor, bgColor, textColor) = when (priority) {
        ComplaintPriority.LOW -> Triple(PriorityLow, SageContainer, SageGreen)
        ComplaintPriority.MEDIUM -> Triple(PriorityMedium, Color(0xFFFFF7EC), PriorityMedium)
        ComplaintPriority.HIGH -> Triple(PriorityHigh, TerracottaContainer, MutedTerracotta)
        ComplaintPriority.URGENT -> Triple(PriorityUrgent, Color(0xFFFFEBEE), PriorityUrgent)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = priority.label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
