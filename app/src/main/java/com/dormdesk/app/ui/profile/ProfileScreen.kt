package com.dormdesk.app.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dormdesk.app.data.model.UserRole
import com.dormdesk.app.ui.components.DormDeskTopBar
import com.dormdesk.app.ui.theme.*
import com.dormdesk.app.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val user = currentUser
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            DormDeskTopBar(
                title = "Account Profile",
                subtitle = user?.role?.displayName ?: "User Profile"
            )
        },
        containerColor = WarmIvory
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // User Avatar Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(EspressoBrown),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.fullName?.take(2) ?: "DD").uppercase(),
                            color = SoftWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = user?.fullName ?: "Resident User",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (user?.role == UserRole.WARDEN) SageContainer else TerracottaContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = user?.role?.displayName ?: "Student",
                            color = if (user?.role == UserRole.WARDEN) SageGreen else MutedTerracotta,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Details Section
            Text(
                text = "HOSTEL & CONTACT INFORMATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SubtleGray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    ProfileInfoRow(
                        icon = Icons.Outlined.Badge,
                        label = if (user?.role == UserRole.WARDEN) "Employee ID" else "Student ID",
                        value = user?.studentId ?: "N/A"
                    )

                    HorizontalDivider(color = NeutralDivider, modifier = Modifier.padding(vertical = 12.dp))

                    ProfileInfoRow(
                        icon = Icons.Outlined.Email,
                        label = "Email Address",
                        value = user?.email ?: "N/A"
                    )

                    HorizontalDivider(color = NeutralDivider, modifier = Modifier.padding(vertical = 12.dp))

                    ProfileInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = "Phone Number",
                        value = user?.phone ?: "N/A"
                    )

                    HorizontalDivider(color = NeutralDivider, modifier = Modifier.padding(vertical = 12.dp))

                    ProfileInfoRow(
                        icon = Icons.Outlined.Apartment,
                        label = "Hostel Allocation",
                        value = "${user?.hostel ?: "Tagore Hostel"}, ${user?.block ?: "Block B"}"
                    )

                    HorizontalDivider(color = NeutralDivider, modifier = Modifier.padding(vertical = 12.dp))

                    ProfileInfoRow(
                        icon = Icons.Outlined.MeetingRoom,
                        label = "Room / Office",
                        value = "Room ${user?.roomNumber ?: "204"}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Legal & Policies Section
            Text(
                text = "LEGAL & POLICIES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SubtleGray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    PolicyLinkRow(
                        icon = Icons.Outlined.PrivacyTip,
                        label = "Privacy Policy",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dormdesk.app/privacy"))
                            context.startActivity(intent)
                        }
                    )

                    HorizontalDivider(color = NeutralDivider, modifier = Modifier.padding(horizontal = 18.dp))

                    PolicyLinkRow(
                        icon = Icons.Outlined.Description,
                        label = "Terms of Service",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dormdesk.app/terms"))
                            context.startActivity(intent)
                        }
                    )

                    HorizontalDivider(color = NeutralDivider, modifier = Modifier.padding(horizontal = 18.dp))

                    PolicyLinkRow(
                        icon = Icons.Outlined.DeleteSweep,
                        label = "Data Deletion Policy",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dormdesk.app/data-deletion"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Information
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeutralBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "About DormDesk",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0.0 • Your Hostel. Your Voice. One Desk.",
                        fontSize = 12.sp,
                        color = SubtleGray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Contact: support@dormdesk.app",
                        fontSize = 11.sp,
                        color = SubtleGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Out Button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EspressoBrown,
                    contentColor = SoftWhite
                )
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = "Sign out",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign Out",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delete Account Button
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PriorityUrgent
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(PriorityUrgent)
                )
            ) {
                Icon(
                    Icons.Default.DeleteForever,
                    contentDescription = "Delete account",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delete Account",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Sign Out",
                    fontWeight = FontWeight.Bold,
                    color = EspressoBrown
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out of DormDesk?",
                    fontSize = 14.sp,
                    color = DeepCharcoal
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout(onLogout)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EspressoBrown)
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = SubtleGray)
                }
            },
            containerColor = SoftWhite
        )
    }

    // Delete Account Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = PriorityUrgent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete Account",
                        fontWeight = FontWeight.Bold,
                        color = PriorityUrgent
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to permanently delete your account?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "This will permanently remove:",
                        fontSize = 13.sp,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val deletedItems = listOf(
                        "Your personal profile and login credentials",
                        "All complaints you have submitted",
                        "All feedback and ratings",
                        "Chat history with DormDesk AI",
                        "Uploaded photos and attachments"
                    )
                    deletedItems.forEach { item ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text("•  ", fontSize = 13.sp, color = PriorityUrgent)
                            Text(item, fontSize = 13.sp, color = DeepCharcoal)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "This action cannot be undone.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PriorityUrgent
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        authViewModel.deleteAccount(onLogout)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PriorityUrgent,
                        contentColor = SoftWhite
                    )
                ) {
                    Text("Delete Permanently", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = SubtleGray)
                }
            },
            containerColor = SoftWhite
        )
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(NeutralSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = EspressoBrown,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = SubtleGray
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DeepCharcoal
            )
        }
    }
}

@Composable
fun PolicyLinkRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = EspressoBrown,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DeepCharcoal,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = "Open $label",
            tint = SubtleGray,
            modifier = Modifier.size(20.dp)
        )
    }
}
