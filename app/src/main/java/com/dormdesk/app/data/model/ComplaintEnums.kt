package com.dormdesk.app.data.model

import androidx.compose.ui.graphics.Color
import com.dormdesk.app.ui.theme.*
import kotlinx.serialization.Serializable

@Serializable
enum class ComplaintCategory(val displayName: String) {
    ELECTRICAL("Electrical"),
    PLUMBING("Plumbing"),
    CLEANING_HYGIENE("Cleaning & Hygiene"),
    FURNITURE("Furniture"),
    INTERNET_WIFI("Internet / Wi-Fi"),
    WASHROOM("Washroom"),
    WATER_SUPPLY("Water Supply"),
    SECURITY("Security"),
    MESS_FOOD("Mess / Food"),
    OTHER("Other")
}

@Serializable
enum class ComplaintPriority(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent");

    val badgeColor: Color
        get() = when (this) {
            LOW -> SageGreen
            MEDIUM -> WarmTaupe
            HIGH -> StatusPending
            URGENT -> StatusUrgent
        }
}

@Serializable
enum class ComplaintStatus(val displayName: String, val stepIndex: Int) {
    SUBMITTED("Submitted", 1),
    RECEIVED("Received by Warden", 2),
    ASSIGNED("Maintenance Assigned", 3),
    IN_PROGRESS("Work in Progress", 4),
    RESOLUTION_UPDATED("Resolution Updated", 5),
    RESOLVED("Resolved", 6),
    CLOSED("Closed", 7);

    val badgeColor: Color
        get() = when (this) {
            SUBMITTED -> StatusPending
            RECEIVED -> StatusAssigned
            ASSIGNED -> StatusAssigned
            IN_PROGRESS -> StatusInProgress
            RESOLUTION_UPDATED -> MutedTerracotta
            RESOLVED -> StatusResolved
            CLOSED -> StatusClosed
        }
}
