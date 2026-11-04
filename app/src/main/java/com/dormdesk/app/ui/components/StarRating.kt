package com.dormdesk.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dormdesk.app.ui.theme.MutedTerracotta
import com.dormdesk.app.ui.theme.NeutralBorder

@Composable
fun StarRatingBar(
    rating: Int,
    maxStars: Int = 5,
    starSize: Dp = 24.dp,
    isInteractive: Boolean = false,
    activeColor: Color = MutedTerracotta,
    inactiveColor: Color = NeutralBorder,
    onRatingChanged: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isFilled = i <= rating
            Icon(
                imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.Star,
                contentDescription = "$i stars",
                tint = if (isFilled) activeColor else inactiveColor,
                modifier = Modifier
                    .size(starSize)
                    .padding(2.dp)
                    .then(
                        if (isInteractive && onRatingChanged != null) {
                            Modifier.clickable { onRatingChanged(i) }
                        } else Modifier
                    )
            )
        }
    }
}
