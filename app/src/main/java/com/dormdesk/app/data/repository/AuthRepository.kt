package com.dormdesk.app.data.repository

import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {

    // Default to Aarav Patel (Student Demo) so app starts in an active, testable state immediately
    private val _currentUser = MutableStateFlow<User?>(DemoDataProvider.studentUser1)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val usersList = DemoDataProvider.allUsers.toMutableList()

    fun getCurrentUserSync(): User? = _currentUser.value

    suspend fun login(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val found = usersList.find { it.email.lowercase() == trimmedEmail }
        return if (found != null) {
            _currentUser.value = found
            Result.success(found)
        } else {
            // Provide friendly error
            Result.failure(Exception("No account found with $email. Please register or use Demo Login."))
        }
    }

    suspend fun loginAsDemo(role: UserRole): User {
        val demoUser = if (role == UserRole.WARDEN) {
            DemoDataProvider.wardenUser1
        } else {
            DemoDataProvider.studentUser1
        }
        _currentUser.value = demoUser
        return demoUser
    }

    suspend fun register(user: User, password: String): Result<User> {
        if (usersList.any { it.email.lowercase() == user.email.lowercase() }) {
            return Result.failure(Exception("An account with this email already exists."))
        }
        val generatedUid = "user_${System.currentTimeMillis()}"
        val newUser = user.copy(uid = generatedUid)
        usersList.add(newUser)
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    suspend fun logout() {
        _currentUser.value = null
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        val exists = usersList.any { it.email.lowercase() == email.trim().lowercase() }
        return if (exists) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Email not registered in the hostel database."))
        }
    }

    fun switchUser(user: User) {
        _currentUser.value = user
    }
}
