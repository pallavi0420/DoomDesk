package com.dormdesk.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.security.MessageDigest

@Serializable
data class UserAccount(
    val user: User,
    val passwordHash: String
)

class AuthRepository(context: Context? = null) {

    private val prefs: SharedPreferences? = context?.getSharedPreferences("dormdesk_auth_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val registeredAccounts = mutableListOf<UserAccount>()

    init {
        loadFromStorage()
    }

    private fun loadFromStorage() {
        prefs?.let { sp ->
            // 1. Load registered accounts from persistent storage
            val accountsJson = sp.getString("registered_accounts", null)
            if (!accountsJson.isNullOrBlank()) {
                try {
                    val loaded = json.decodeFromString(ListSerializer(UserAccount.serializer()), accountsJson)
                    registeredAccounts.clear()
                    registeredAccounts.addAll(loaded)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 2. Restore active session if available
            val sessionUserJson = sp.getString("active_session_user", null)
            if (!sessionUserJson.isNullOrBlank()) {
                try {
                    val sessionUser = json.decodeFromString(User.serializer(), sessionUserJson)
                    _currentUser.value = sessionUser
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun saveToStorage() {
        prefs?.edit()?.apply {
            val accountsJson = json.encodeToString(ListSerializer(UserAccount.serializer()), registeredAccounts)
            putString("registered_accounts", accountsJson)

            val sessionUser = _currentUser.value
            if (sessionUser != null) {
                putString("active_session_user", json.encodeToString(User.serializer(), sessionUser))
            } else {
                remove("active_session_user")
            }
            apply()
        }
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun getCurrentUserSync(): User? = _currentUser.value

    suspend fun login(email: String, password: String, expectedRole: UserRole? = null): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val account = registeredAccounts.find { it.user.email.trim().lowercase() == trimmedEmail }

        if (account == null) {
            return Result.failure(Exception("No account registered with $email. Please create an account."))
        }

        // Verify Role Separation
        if (expectedRole != null && account.user.role != expectedRole) {
            return if (expectedRole == UserRole.STUDENT) {
                Result.failure(Exception("This is a Warden / Staff account. Please switch to the Warden Portal to sign in."))
            } else {
                Result.failure(Exception("This is a Student / Resident account. Please switch to the Resident Portal to sign in."))
            }
        }

        // Verify Password
        val inputHash = hashPassword(password)
        if (account.passwordHash != inputHash) {
            return Result.failure(Exception("Incorrect password. Please verify your credentials and try again."))
        }

        _currentUser.value = account.user
        saveToStorage()
        return Result.success(account.user)
    }

    suspend fun register(user: User, password: String): Result<User> {
        val trimmedEmail = user.email.trim().lowercase()
        if (registeredAccounts.any { it.user.email.trim().lowercase() == trimmedEmail }) {
            return Result.failure(Exception("An account with this email address already exists. Please sign in."))
        }

        val prefix = if (user.isWarden) "warden" else "student"
        val generatedUid = "${prefix}_${System.currentTimeMillis()}"
        val newUser = user.copy(
            uid = generatedUid,
            email = trimmedEmail
        )

        val newAccount = UserAccount(
            user = newUser,
            passwordHash = hashPassword(password)
        )

        registeredAccounts.add(newAccount)
        _currentUser.value = newUser
        saveToStorage()
        return Result.success(newUser)
    }

    suspend fun logout() {
        _currentUser.value = null
        prefs?.edit()?.remove("active_session_user")?.apply()
    }

    suspend fun deleteAccount(uid: String): Result<Unit> {
        val removed = registeredAccounts.removeAll { it.user.uid == uid }
        if (_currentUser.value?.uid == uid) {
            _currentUser.value = null
        }
        saveToStorage()
        return if (removed) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Account not found or already deleted."))
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        val trimmed = email.trim().lowercase()
        val exists = registeredAccounts.any { it.user.email.trim().lowercase() == trimmed }
        return if (exists) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Email not registered in the hostel system."))
        }
    }

    fun switchUser(user: User) {
        _currentUser.value = user
        saveToStorage()
    }
}
