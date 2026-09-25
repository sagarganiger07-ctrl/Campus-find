package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.CampusTopAppBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CampusGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.Slate600

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    onNavigateBack: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onSwitchUser: (UserEntity) -> Unit,
    onLogout: () -> Unit
) {
    BackHandler { onNavigateBack() }

    Scaffold(
        topBar = {
            CampusTopAppBar(
                title = "Campus Profile",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CampusNavy,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentUser?.avatarInitials ?: "U",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentUser?.fullName ?: "Campus Student",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentUser?.role == UserRole.ADMIN.name) Color(0xFFFEF3C7) else Color(0xFFDBEAFE)
                        ) {
                            Text(
                                text = "${currentUser?.role ?: "STUDENT"} • VERIFIED",
                                color = if (currentUser?.role == UserRole.ADMIN.name) Color(0xFF92400E) else CampusNavy,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Info rows
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "College Email",
                            value = currentUser?.email ?: "N/A"
                        )
                        ProfileInfoRow(
                            icon = Icons.Default.Badge,
                            label = "Roll / Employee ID",
                            value = currentUser?.studentOrStaffId ?: "N/A"
                        )
                        ProfileInfoRow(
                            icon = Icons.Default.School,
                            label = "Department",
                            value = currentUser?.department ?: "N/A"
                        )
                        ProfileInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Contact Phone (Private)",
                            value = currentUser?.phone?.ifEmpty { "Not Provided" } ?: "Not Provided"
                        )
                    }
                }
            }

            // Quick Role/User Switcher (Essential for testing claimer vs finder vs admin instantly)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = CampusNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Switch Profile (Testing & Evaluation)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Easily toggle between Student (Finder/Claimer) and College Admin accounts to test workflows without logging out:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        allUsers.forEach { user ->
                            val isCurrent = user.id == currentUser?.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSwitchUser(user) }
                                    .background(if (isCurrent) Color(0xFFDBEAFE) else Color.Transparent)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${user.fullName} (${user.role})",
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isCurrent) CampusNavy else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = user.email,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate600
                                    )
                                }
                                if (isCurrent) {
                                    Text(
                                        text = "Active",
                                        color = CampusNavy,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Campus Safety Guidelines
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFD1FAE5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF065F46))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Campus Lost & Found Guidelines",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF065F46)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Meet only in official campus areas (Library desk, Student Union, Campus Security).\n• Ask the claimer to verify non-public identifying details.\n• Once returned, remember to mark the listing as Returned to keep campus statistics up-to-date.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF047857),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Admin Shortcut
            if (currentUser?.role == UserRole.ADMIN.name) {
                item {
                    Button(
                        onClick = onNavigateToAdmin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("profile_admin_portal_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = CampusGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Campus Admin Dashboard", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Logout Button
            item {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("logout_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CampusCoral)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = CampusNavy, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Slate600)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
