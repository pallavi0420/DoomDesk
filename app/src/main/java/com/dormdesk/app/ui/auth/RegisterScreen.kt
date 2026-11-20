package com.dormdesk.app.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.dormdesk.app.data.model.UserRole
import com.dormdesk.app.ui.components.DormDeskCard
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: (UserRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }
    var fullName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var hostel by remember { mutableStateOf("Aryabhatta Hostel") }
    var block by remember { mutableStateOf("Block B") }
    var roomNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            onRegisterSuccess(user.role)
        }
    }

    LaunchedEffect(selectedRole) {
        if (selectedRole == UserRole.WARDEN) {
            if (block == "Block B") block = "Admin Block"
            if (roomNumber.isBlank()) roomNumber = "G-05"
        } else {
            if (block == "Admin Block") block = "Block B"
            if (roomNumber == "G-05") roomNumber = ""
        }
    }

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = if (selectedRole == UserRole.WARDEN) "Create Warden Account" else "Create Resident Account",
                subtitle = if (selectedRole == UserRole.WARDEN) "Hostel Administration Registration" else "Student Registration",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Role Selector Tab
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, WarmTaupe.copy(alpha = 0.35f))
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .clickable { selectedRole = UserRole.STUDENT },
                        color = if (selectedRole == UserRole.STUDENT) EspressoBrown else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedRole == UserRole.STUDENT) SoftWhite else WarmTaupe
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Resident",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedRole == UserRole.STUDENT) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedRole == UserRole.STUDENT) SoftWhite else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .clickable { selectedRole = UserRole.WARDEN },
                        color = if (selectedRole == UserRole.WARDEN) MutedTerracotta else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedRole == UserRole.WARDEN) SoftWhite else WarmTaupe
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hostel Warden",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedRole == UserRole.WARDEN) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedRole == UserRole.WARDEN) SoftWhite else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                DormDeskCard(
                    backgroundColor = StatusUrgent.copy(alpha = 0.08f),
                    borderColor = StatusUrgent.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = StatusUrgent,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Personal Information
            DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = EspressoBrown
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(if (selectedRole == UserRole.WARDEN) "Warden Full Name *" else "Full Name *") },
                    placeholder = { Text(if (selectedRole == UserRole.WARDEN) "e.g. Dr. Rajesh Kumar" else "e.g. Aarav Patel") },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = WarmTaupe) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text(if (selectedRole == UserRole.WARDEN) "Staff / Warden ID *" else "Student Registration ID *") },
                    placeholder = { Text(if (selectedRole == UserRole.WARDEN) "e.g. STAFF-W03" else "e.g. 2024CS101") },
                    leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null, tint = WarmTaupe) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (selectedRole == UserRole.WARDEN) "Official Warden Email *" else "Email Address *") },
                    placeholder = { Text(if (selectedRole == UserRole.WARDEN) "warden.name@dormdesk.app" else "resident@dormdesk.app") },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = WarmTaupe) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("+91 98765 43210") },
                    leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = WarmTaupe) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hostel & Room / Office Allocation Card
            DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (selectedRole == UserRole.WARDEN) "Hostel & Office Allocation" else "Hostel Allocation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = EspressoBrown
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = hostel,
                    onValueChange = { hostel = it },
                    label = { Text("Hostel Name *") },
                    leadingIcon = { Icon(Icons.Outlined.Apartment, contentDescription = null, tint = WarmTaupe) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = block,
                        onValueChange = { block = it },
                        label = { Text(if (selectedRole == UserRole.WARDEN) "Admin Wing *" else "Block *") },
                        placeholder = { Text(if (selectedRole == UserRole.WARDEN) "Admin Block" else "Block B") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = { roomNumber = it },
                        label = { Text(if (selectedRole == UserRole.WARDEN) "Office No. *" else "Room No. *") },
                        placeholder = { Text(if (selectedRole == UserRole.WARDEN) "G-02" else "204") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security & Password Card
            DormDeskCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Security",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = EspressoBrown
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password * (min 6 characters)") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = WarmTaupe) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = WarmTaupe
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password *") },
                    leadingIcon = { Icon(Icons.Outlined.LockClock, contentDescription = null, tint = WarmTaupe) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    authViewModel.clearMessages()
                    authViewModel.register(
                        fullName = fullName,
                        studentId = studentId,
                        email = email,
                        phone = phone,
                        hostel = hostel,
                        block = block,
                        roomNumber = roomNumber,
                        password = password,
                        confirmPassword = confirmPassword,
                        role = selectedRole
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == UserRole.WARDEN) MutedTerracotta else EspressoBrown
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = SoftWhite)
                } else {
                    Text(
                        text = if (selectedRole == UserRole.WARDEN) "Complete Warden Registration" else "Complete Resident Registration",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
