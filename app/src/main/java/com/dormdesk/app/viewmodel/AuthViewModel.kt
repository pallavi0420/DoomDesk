package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.model.UserRole
import com.dormdesk.app.data.repository.DormDeskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(
    private val repository: DormDeskRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.currentUser.value)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, role: UserRole, onSuccess: (User) -> Unit) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please enter your email address.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = repository.login(email.trim(), role)
            result.onSuccess { user ->
                _uiState.value = AuthUiState(successMessage = "Welcome back, ${user.fullName}!")
                onSuccess(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState(errorMessage = error.message ?: "Authentication failed")
            }
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        val uid = repository.currentUser.value?.uid ?: return
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = repository.deleteAccount(uid)
            result.onSuccess {
                _uiState.value = AuthUiState(successMessage = "Account deleted successfully.")
                onDeleted()
            }.onFailure { err ->
                _uiState.value = AuthUiState(errorMessage = err.message ?: "Account deletion failed")
            }
        }
    }

    fun registerStudent(
        fullName: String,
        studentId: String,
        email: String,
        phone: String,
        hostel: String,
        block: String,
        roomNumber: String,
        password: String,
        confirmPass: String,
        onSuccess: (User) -> Unit
    ) {
        if (fullName.isBlank() || studentId.isBlank() || email.isBlank() || phone.isBlank() || roomNumber.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please fill in all mandatory fields.")
            return
        }

        if (password.length < 6) {
            _uiState.value = AuthUiState(errorMessage = "Password must be at least 6 characters.")
            return
        }

        if (password != confirmPass) {
            _uiState.value = AuthUiState(errorMessage = "Passwords do not match.")
            return
        }

        val newUser = User(
            uid = "stu_" + System.currentTimeMillis(),
            fullName = fullName.trim(),
            email = email.trim(),
            studentId = studentId.trim().uppercase(),
            phone = phone.trim(),
            hostel = hostel.trim(),
            block = block.trim(),
            roomNumber = roomNumber.trim(),
            role = UserRole.STUDENT
        )

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = repository.registerStudent(newUser)
            result.onSuccess {
                _uiState.value = AuthUiState(successMessage = "Registration successful! Welcome to DormDesk.")
                onSuccess(newUser)
            }.onFailure {
                _uiState.value = AuthUiState(errorMessage = it.message ?: "Registration failed")
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onLoggedOut()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
