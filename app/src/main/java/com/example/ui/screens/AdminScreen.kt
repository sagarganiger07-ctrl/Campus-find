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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClaimEntity
import com.example.data.model.ItemEntity
import com.example.data.model.ItemType
import com.example.data.model.ListingReportEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.CampusTopAppBar
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ItemCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusCoralLight
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CampusEmeraldLight
import com.example.ui.theme.CampusGold
import com.example.ui.theme.CampusGoldLight
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    currentUser: UserEntity?,
    users: List<UserEntity>,
    items: List<ItemEntity>,
    claims: List<ClaimEntity>,
    reports: List<ListingReportEntity>,
    onNavigateBack: () -> Unit,
    onNavigateToItem: (String) -> Unit,
    onNavigateToClaim: (String) -> Unit,
    onDeleteItem: (String, () -> Unit) -> Unit,
    onToggleUserSuspension: (String, Boolean) -> Unit,
    onResolveReport: (String, Boolean) -> Unit
) {
    BackHandler { onNavigateBack() }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Flagged Queue", "Manage Items", "Campus Users", "Claims Log")

    val openReports = reports.filter { it.status == "OPEN" }

    Scaffold(
        topBar = {
            CampusTopAppBar(
                title = "Campus Security & Admin",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Admin Banner Header
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CampusGold,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF1E293B), modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Admin Moderation Portal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Manage listings, verify claims, resolve user flags & enforce campus safety",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                        )
                    }
                }
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = when (index) {
                                    0 -> "$title (${openReports.size})"
                                    1 -> "$title (${items.size})"
                                    2 -> "$title (${users.size})"
                                    3 -> "$title (${claims.size})"
                                    else -> title
                                },
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("admin_tab_$index")
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Flagged Queue
                    if (openReports.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Shield,
                            title = "No Reported Listings",
                            message = "All user reports have been reviewed and resolved. The campus network is safe!"
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(openReports, key = { it.id }) { report ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("report_card_${report.id}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = report.itemTitle,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = CampusCoral
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = CampusCoralLight
                                            ) {
                                                Text(
                                                    text = "FLAGGED",
                                                    color = CampusCoral,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Reason: ${report.reason}",
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        if (report.details.isNotBlank()) {
                                            Text(
                                                text = "Details: ${report.details}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Slate600
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    // Delete item and resolve report
                                                    onDeleteItem(report.itemId) {}
                                                    onResolveReport(report.id, false)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = CampusCoral),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f).testTag("admin_remove_item_${report.id}")
                                            ) {
                                                Text("Remove Item", fontSize = 12.sp)
                                            }

                                            OutlinedButton(
                                                onClick = { onResolveReport(report.id, true) },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f).testTag("admin_dismiss_report_${report.id}")
                                            ) {
                                                Text("Dismiss Flag", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Manage Items
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items, key = { it.id }) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_item_${item.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    ItemCard(item = item, onClick = { onNavigateToItem(item.id) })

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Posted by: ${item.postedByName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate600
                                        )

                                        Button(
                                            onClick = { onDeleteItem(item.id) {} },
                                            colors = ButtonDefaults.buttonColors(containerColor = CampusCoral),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Admin Remove", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Campus Users & Moderation
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(users, key = { it.id }) { user ->
                            val isMe = user.id == currentUser?.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_user_card_${user.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (user.isSuspended) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (user.isSuspended) CampusCoral else CampusNavy,
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = user.avatarInitials.ifEmpty { "U" },
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = user.fullName,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (user.role == UserRole.ADMIN.name) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = CampusGoldLight
                                                    ) {
                                                        Text(
                                                            text = "ADMIN",
                                                            color = Color(0xFF92400E),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = "${user.studentOrStaffId} • ${user.department}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Slate600
                                            )
                                            Text(
                                                text = user.email,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Slate600
                                            )
                                            if (user.isSuspended) {
                                                Text(
                                                    text = "Account Suspended",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = CampusCoral
                                                )
                                            }
                                        }
                                    }

                                    if (!isMe && user.role != UserRole.ADMIN.name) {
                                        Button(
                                            onClick = {
                                                onToggleUserSuspension(user.id, !user.isSuspended)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (user.isSuspended) CampusEmerald else CampusCoral
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("suspend_toggle_${user.id}")
                                        ) {
                                            Text(
                                                text = if (user.isSuspended) "Unsuspend" else "Suspend",
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Claims Log
                    if (claims.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Handshake,
                            title = "No Claims Logged",
                            message = "All campus claim requests will be tracked here for security auditing."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(claims, key = { it.id }) { claim ->
                                val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(claim.createdAt))
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToClaim(claim.id) }
                                        .testTag("admin_claim_${claim.id}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = claim.itemTitle,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            StatusPill(status = claim.status)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Claimer: ${claim.claimerName} (${claim.claimerStudentId})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CampusNavy
                                        )
                                        Text(
                                            text = "Date: $dateStr",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate600
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
