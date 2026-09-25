package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ItemEntity
import com.example.data.model.ItemStatus
import com.example.data.model.ItemType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ItemCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusCoralLight
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CampusEmeraldLight
import com.example.ui.theme.CampusGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.Slate600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentUser: UserEntity?,
    items: List<ItemEntity>,
    unreadCount: Int,
    onNavigate: (Screen) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val activeItems = items.filter { it.status != ItemStatus.CLOSED.name }
    val filteredRecentItems = when (selectedFilter) {
        "LOST" -> activeItems.filter { it.type == ItemType.LOST.name }
        "FOUND" -> activeItems.filter { it.type == ItemType.FOUND.name }
        else -> activeItems
    }.take(10)

    val lostCount = items.count { it.type == ItemType.LOST.name && it.status != ItemStatus.RETURNED.name }
    val foundCount = items.count { it.type == ItemType.FOUND.name && it.status != ItemStatus.RETURNED.name }
    val returnedCount = items.count { it.status == ItemStatus.RETURNED.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CampusGold,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_campus_finder),
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CampusFind",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentUser?.let { "Hi, ${it.fullName.split(" ").first()}" } ?: "College Portal",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate600
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onNavigate(Screen.Notifications) },
                        modifier = Modifier.testTag("nav_notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = CampusCoral,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = CampusNavy
                            )
                        }
                    }

                    IconButton(
                        onClick = { onNavigate(Screen.Profile) },
                        modifier = Modifier.testTag("nav_profile_button")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CampusNavy,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentUser?.avatarInitials ?: "U",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campus Hero Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_campus_banner),
                            contentDescription = "Campus Quad",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            CampusNavyDark.copy(alpha = 0.88f),
                                            CampusNavy.copy(alpha = 0.55f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Lost or Found on Campus?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Safely report, claim & reunite items with fellow students.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }
            }

            // Campus Stats Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        count = "$lostCount",
                        label = "Lost Items",
                        color = CampusCoral,
                        bgColor = CampusCoralLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        count = "$foundCount",
                        label = "Found Items",
                        color = CampusEmerald,
                        bgColor = CampusEmeraldLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        count = "$returnedCount",
                        label = "Reunited",
                        color = CampusNavy,
                        bgColor = Color(0xFFDBEAFE),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Main User Action Buttons (Required by prompt: Search, I Lost, I Found, My Reports, Notifications, Profile)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Row 1: Search & I Lost Something
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionCard(
                            title = "Search Items",
                            subtitle = "Filter by location, category",
                            icon = Icons.Default.Search,
                            iconTint = CampusNavy,
                            iconBg = Color(0xFFDBEAFE),
                            testTag = "home_search_items_btn",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.Search()) }
                        )

                        ActionCard(
                            title = "I Lost Something",
                            subtitle = "Post item you lost",
                            icon = Icons.Default.Cancel,
                            iconTint = CampusCoral,
                            iconBg = CampusCoralLight,
                            testTag = "home_i_lost_something_btn",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.ReportItem(type = ItemType.LOST)) }
                        )
                    }

                    // Row 2: I Found Something & My Reports
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionCard(
                            title = "I Found Something",
                            subtitle = "Help return an item",
                            icon = Icons.Default.CheckCircle,
                            iconTint = CampusEmerald,
                            iconBg = CampusEmeraldLight,
                            testTag = "home_i_found_something_btn",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.ReportItem(type = ItemType.FOUND)) }
                        )

                        ActionCard(
                            title = "My Reports",
                            subtitle = "Manage your posts & claims",
                            icon = Icons.Default.Assignment,
                            iconTint = CampusGold,
                            iconBg = Color(0xFFFEF3C7),
                            testTag = "home_my_reports_btn",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.MyReports()) }
                        )
                    }

                    // Admin Shortcut if admin or staff
                    if (currentUser?.role == UserRole.ADMIN.name) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(Screen.Admin) }
                                .testTag("home_admin_panel_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = "Admin",
                                        tint = CampusGold
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Campus Safety & Admin Portal",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "Moderate listings, review reports & manage users",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF94A3B8)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recently Reported Items Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recently Reported Items",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(
                        onClick = { onNavigate(Screen.Search()) },
                        modifier = Modifier.testTag("home_see_all_btn")
                    ) {
                        Text("See All", color = CampusNavy, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Filter Chips for Recent Items
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("All Recent") }
                    )
                    FilterChip(
                        selected = selectedFilter == "LOST",
                        onClick = { selectedFilter = "LOST" },
                        label = { Text("Lost Only") }
                    )
                    FilterChip(
                        selected = selectedFilter == "FOUND",
                        onClick = { selectedFilter = "FOUND" },
                        label = { Text("Found Only") }
                    )
                }
            }

            if (filteredRecentItems.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Items Reported Yet",
                        message = "Be the first to report a lost or found item on campus!"
                    )
                }
            } else {
                items(filteredRecentItems, key = { it.id }) { item ->
                    ItemCard(
                        item = item,
                        onClick = { onNavigate(Screen.ItemDetail(item.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    count: String,
    label: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = color
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Slate600,
                maxLines = 1
            )
        }
    }
}
