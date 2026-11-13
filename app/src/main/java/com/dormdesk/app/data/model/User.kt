package com.dormdesk.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    STUDENT,
    WARDEN
}

@Serializable
data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val studentId: String = "",
    val phone: String = "",
    val hostel: String = "Aryabhatta Hostel",
    val block: String = "Block B",
    val roomNumber: String = "204",
    val role: UserRole = UserRole.STUDENT
) {
    val isWarden: Boolean get() = role == UserRole.WARDEN
    val isStudent: Boolean get() = role == UserRole.STUDENT
    val initials: String
        get() {
            val parts = fullName.trim().split(" ")
            return when {
                parts.size >= 2 -> "${parts[0].firstOrNull()?.uppercase() ?: ""}${parts[1].firstOrNull()?.uppercase() ?: ""}"
                parts.isNotEmpty() && parts[0].isNotEmpty() -> parts[0].take(2).uppercase()
                else -> "DD"
            }
        }
}
