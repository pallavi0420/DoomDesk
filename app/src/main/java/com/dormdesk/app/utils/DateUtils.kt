package com.dormdesk.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val fullDateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeOnlyFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun formatFull(timestamp: Long): String = fullDateFormat.format(Date(timestamp))
    fun formatShort(timestamp: Long): String = shortDateFormat.format(Date(timestamp))
    fun formatDateOnly(timestamp: Long): String = dateOnlyFormat.format(Date(timestamp))
    fun formatTimeOnly(timestamp: Long): String = timeOnlyFormat.format(Date(timestamp))

    fun getRelativeTimeSpan(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> formatDateOnly(timestamp)
        }
    }
}
