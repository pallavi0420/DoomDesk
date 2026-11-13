package com.dormdesk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.model.UserRole
import com.dormdesk.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.Eagerly, authRepository.getCurrentUserSync())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Please enter both email and password."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.login(email, pass)
            _isLoading.value = false
            result.onFailure {
                _errorMessage.value = it.message ?: "Authentication failed"
            }
        }
    }

    fun loginAsDemo(role: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            authRepository.loginAsDemo(role)
            _isLoading.value = false
        }
    }

    fun register(
        fullName: String,
        studentId: String,
        email: String,
        phone: String,
        hostel: String,
        block: String,
        roomNumber: String,
        password: String,
        confirmPassword: String
    ) {
        if (fullName.isBlank() || studentId.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill in all required fields."
            return
        }
        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match."
            return
        }
        if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters long."
            return
        }

        val newUser = User(
            fullName = fullName.trim(),
            studentId = studentId.trim().uppercase(),
            email = email.trim(),
            phone = phone.trim(),
            hostel = hostel.trim(),
            block = block.trim(),
            roomNumber = roomNumber.trim(),
            role = UserRole.STUDENT
        )

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.register(newUser, password)
            _isLoading.value = false
            result.onFailure {
                _errorMessage.value = it.message ?: "Registration failed"
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _errorMessage.value = "Please enter your registered email address."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.resetPassword(email)
            _isLoading.value = false
            result.onSuccess {
                _successMessage.value = "Password reset instructions sent to $email."
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "Reset request failed"
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
