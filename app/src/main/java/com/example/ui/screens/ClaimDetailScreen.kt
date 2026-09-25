package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.ClaimStatus
import com.example.data.model.MessageEntity
import com.example.data.model.UserEntity
import com.example.ui.components.CampusTopAppBar
import com.example.ui.components.StatusPill
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CampusEmeraldLight
import com.example.ui.theme.CampusGold
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClaimDetailScreen(
    claimFlow: Flow<ClaimEntity?>,
    messagesFlow: Flow<List<MessageEntity>>,
    currentUser: UserEntity?,
    onNavigateBack: () -> Unit,
    onReviewClaim: (String, Boolean, String?, () -> Unit) -> Unit,
    onSendMessage: (String, String, String) -> Unit,
    onMarkAsReturned: (String) -> Unit
) {
    BackHandler { onNavigateBack() }

    val claim by claimFlow.collectAsState(initial = null)
    val messages by messagesFlow.collectAsState(initial = emptyList())
    var messageInput by remember { mutableStateOf("") }

    if (claim == null) {
        Scaffold(
            topBar = {
                CampusTopAppBar(
                    title = "Claim Details",
                    canNavigateBack = true,
                    onNavigateBack = onNavigateBack
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Claim not found.")
            }
        }
        return
    }

    val currentClaim = claim!!
    val isFinder = currentUser?.id == currentClaim.ownerFinderUserId
    val isClaimer = currentUser?.id == currentClaim.claimerUserId
    val isPending = currentClaim.status == ClaimStatus.PENDING.name
    val isAccepted = currentClaim.status == ClaimStatus.ACCEPTED.name
    val isReturned = currentClaim.status == ClaimStatus.RETURNED.name

    Scaffold(
        topBar = {
            CampusTopAppBar(
                title = "Claim Verification",
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
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Claim Header Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentClaim.itemTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                StatusPill(status = currentClaim.status)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Claimed by: ${currentClaim.claimerName} (${currentClaim.claimerStudentId})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = CampusNavy
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Proof Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Submitted Proof of Ownership:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Slate600
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentClaim.proofDetails,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Buttons for Finder if Pending
                if (isFinder && isPending) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Finder Decision Required",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "Does this proof match the item's identifying marks?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF78350F)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onReviewClaim(currentClaim.id, true, null) {}
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CampusEmerald),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f).testTag("accept_claim_btn")
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Accept Claim")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onReviewClaim(currentClaim.id, false, "Proof did not match item details.") {}
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CampusCoral),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f).testTag("reject_claim_btn")
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Reject")
                                    }
                                }
                            }
                        }
                    }
                }

                // If Claim is accepted, allow marking as returned
                if ((isFinder || isClaimer) && isAccepted && !isReturned) {
                    item {
                        Button(
                            onClick = { onMarkAsReturned(currentClaim.itemId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("claim_mark_returned_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CampusEmerald)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Item Handed Over — Mark as Returned", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Campus Safe Meetup Reminder
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDBEAFE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = CampusNavy, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Safe Campus Handover: Always meet at public, staffed locations such as the Campus Library Circulation Desk or Student Union.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CampusNavy
                            )
                        }
                    }
                }

                // Chat Messages Divider
                item {
                    Text(
                        text = "Safe In-App Communication",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (messages.isEmpty()) {
                    item {
                        Text(
                            text = "No messages yet. Send a message to coordinate a safe on-campus meeting time and place.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }
                } else {
                    items(messages, key = { it.id }) { msg ->
                        val isMe = msg.senderUserId == currentUser?.id
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isMe) 14.dp else 2.dp,
                                    bottomEnd = if (isMe) 2.dp else 14.dp
                                ),
                                color = if (isMe) CampusNavy else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = msg.senderName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isMe) CampusGold else CampusNavy
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = msg.messageText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Chat Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Type a safe coordination message...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input"),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                onSendMessage(currentClaim.id, currentClaim.itemId, messageInput)
                                messageInput = ""
                            }
                        },
                        modifier = Modifier
                            .background(CampusNavy, CircleShape)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
