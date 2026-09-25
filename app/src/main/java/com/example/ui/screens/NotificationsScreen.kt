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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationEntity
import com.example.ui.components.CampusTopAppBar
import com.example.ui.components.EmptyStateView
import com.example.ui.navigation.Screen
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CampusGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.Slate600
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onNavigateBack: () -> Unit,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onNavigateToItem: (String) -> Unit,
    onNavigateToClaim: (String) -> Unit
) {
    BackHandler { onNavigateBack() }

    Scaffold(
        topBar = {
            CampusTopAppBar(
                title = "Notifications",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    if (notifications.any { !it.isRead }) {
                        IconButton(
                            onClick = onMarkAllAsRead,
                            modifier = Modifier.testTag("mark_all_read_btn")
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = "Mark all as read", tint = CampusNavy)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.NotificationsNone,
                title = "No Notifications",
                message = "You're all caught up! Updates regarding claims, matching items, and returned reports will show up here.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    val (icon, tintColor) = getNotificationIcon(notif.type)
                    val formattedDate = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(notif.timestamp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMarkAsRead(notif.id)
                                if (notif.relatedClaimId != null) {
                                    onNavigateToClaim(notif.relatedClaimId)
                                } else if (notif.relatedItemId != null) {
                                    onNavigateToItem(notif.relatedItemId)
                                }
                            }
                            .testTag("notification_item_${notif.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!notif.isRead) Color(0xFFF0F7FF) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (!notif.isRead) 2.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = tintColor.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = tintColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (!notif.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(CampusNavy, CircleShape)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = formattedDate,
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

private fun getNotificationIcon(type: String): Pair<ImageVector, Color> {
    return when (type) {
        "CLAIM_REQUEST" -> Icons.Default.Handshake to CampusGold
        "CLAIM_ACCEPTED" -> Icons.Default.CheckCircle to CampusEmerald
        "CLAIM_REJECTED" -> Icons.Default.Cancel to CampusCoral
        "MESSAGE" -> Icons.Default.Chat to CampusNavy
        "STATUS_CHANGE" -> Icons.Default.Sync to CampusNavy
        else -> Icons.Default.Notifications to CampusNavy
    }
}
