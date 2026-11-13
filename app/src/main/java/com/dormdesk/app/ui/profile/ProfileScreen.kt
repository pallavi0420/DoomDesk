package com.dormdesk.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
                .background(EspressoBrown),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentUser.initials,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = SoftWhite
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

        // Resident Details Card
        DormDeskCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Account Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = EspressoBrown
            )
            Spacer(modifier = Modifier.height(14.dp))

            ProfileInfoRow(icon = Icons.Outlined.Badge, label = "ID Number", value = currentUser.studentId)
            ProfileInfoRow(icon = Icons.Outlined.Email, label = "Email", value = currentUser.email)
            ProfileInfoRow(icon = Icons.Outlined.Phone, label = "Phone", value = currentUser.phone)
            ProfileInfoRow(icon = Icons.Outlined.Apartment, label = "Hostel", value = currentUser.hostel)
            ProfileInfoRow(icon = Icons.Outlined.MeetingRoom, label = "Room", value = "${currentUser.block}, Room ${currentUser.roomNumber}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Role Switch Card for Evaluation
        DormDeskCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Role Switcher (Testing)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = EspressoBrown
            )
            Text(
                text = "Quickly test Student vs Warden perspectives with seeded demo data",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { authViewModel.loginAsDemo(UserRole.STUDENT) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (currentUser.isStudent) EspressoBrown.copy(alpha = 0.08f) else Color.Transparent
                    ),
                    border = BorderStroke(1.dp, if (currentUser.isStudent) EspressoBrown else BorderLight),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp), tint = EspressoBrown)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Student", color = EspressoBrown, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { authViewModel.loginAsDemo(UserRole.WARDEN) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (currentUser.isWarden) MutedTerracotta.copy(alpha = 0.08f) else Color.Transparent
                    ),
                    border = BorderStroke(1.dp, if (currentUser.isWarden) MutedTerracotta else BorderLight),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp), tint = MutedTerracotta)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Warden", color = MutedTerracotta, fontSize = 12.sp)
                }
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
                        .background(EspressoBrown),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.HomeWork, contentDescription = null, tint = SoftWhite, modifier = Modifier.size(22.dp))
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
            colors = ButtonDefaults.buttonColors(containerColor = StatusUrgent.copy(alpha = 0.12f), contentColor = StatusUrgent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(80.dp))
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
        Icon(icon, contentDescription = null, tint = WarmTaupe, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(90.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
