package com.dormdesk.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.User
import com.dormdesk.app.data.model.UserRole
import com.dormdesk.app.ui.components.DormDeskCard
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    currentUser: User,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(if (currentUser.isWarden) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentUser.initials,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = currentUser.fullName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (currentUser.isWarden) MutedTerracotta.copy(alpha = 0.15f) else SageGreen.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (currentUser.isWarden) "WARDEN / ADMINISTRATOR" else "HOSTEL RESIDENT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (currentUser.isWarden) MutedTerracotta else SageGreen
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Resident / Warden Details Card
        DormDeskCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (currentUser.isWarden) "Warden Official Details" else "Resident Account Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            ProfileInfoRow(
                icon = Icons.Outlined.Badge,
                label = if (currentUser.isWarden) "Staff ID" else "Student ID",
                value = currentUser.studentId
            )
            ProfileInfoRow(icon = Icons.Outlined.Email, label = "Email", value = currentUser.email)
            ProfileInfoRow(icon = Icons.Outlined.Phone, label = "Phone", value = currentUser.phone)
            ProfileInfoRow(icon = Icons.Outlined.Apartment, label = "Hostel", value = currentUser.hostel)
            ProfileInfoRow(
                icon = Icons.Outlined.MeetingRoom,
                label = if (currentUser.isWarden) "Office" else "Room",
                value = "${currentUser.block}, ${if (currentUser.isWarden) "Office" else "Room"} ${currentUser.roomNumber}"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy & Account Data Safety Card (Play Store Requirement)
        DormDeskCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPrivacyPolicyDialog = true }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Shield, contentDescription = null, tint = SageGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Privacy & Data Safety Policy",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Review how your data & tickets are handled",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Branding / Info Card
        DormDeskCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.HomeWork, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "DormDesk v1.0.0", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(text = "Your Hostel. Your Voice. One Desk.", style = MaterialTheme.typography.bodySmall, color = MutedTerracotta)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = {
                authViewModel.logout()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Delete Account Button (Play Store Policy Compliant)
        OutlinedButton(
            onClick = { showDeleteAccountDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = StatusUrgent
            ),
            border = BorderStroke(1.dp, StatusUrgent.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Outlined.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp), tint = StatusUrgent)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete Account", fontWeight = FontWeight.SemiBold, color = StatusUrgent)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(
                    text = "Delete Account Permanently?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = StatusUrgent
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete your ${if (currentUser.isWarden) "Warden" else "Resident"} account? All associated personal records, preferences, and session data will be permanently removed. This action cannot be reversed.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        authViewModel.deleteAccount(onComplete = onLogout)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusUrgent)
                ) {
                    Text("Delete Account", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = {
                Text("DormDesk Privacy & Data Safety", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "1. Information Collection & Usage\n" +
                                "DormDesk collects resident identity details (name, room, registration ID) solely to facilitate hostel complaint tracking, maintenance verification, and administrative notice bulletins.\n\n" +
                                "2. Account Deletion & Data Retention\n" +
                                "In full compliance with Google Play Store policies, users have complete rights to delete their profile and associated data directly within the application via the 'Delete Account' button.\n\n" +
                                "3. Security & Safety\n" +
                                "We do not sell personal data to third parties. All communication between residents and wardens remains strictly confidential within the hostel administration system.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyPolicyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Close", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        )
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(90.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
