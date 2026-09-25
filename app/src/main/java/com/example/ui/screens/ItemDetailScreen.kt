package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ClaimEntity
import com.example.data.model.ItemEntity
import com.example.data.model.ItemStatus
import com.example.data.model.ItemType
import com.example.data.model.UserEntity
import com.example.ui.components.CampusTopAppBar
import com.example.ui.components.StatusPill
import com.example.ui.components.TypeBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CampusEmeraldLight
import com.example.ui.theme.CampusGold
import com.example.ui.theme.CampusGoldLight
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemFlow: Flow<ItemEntity?>,
    claimsFlow: Flow<List<ClaimEntity>>,
    currentUser: UserEntity?,
    onNavigateBack: () -> Unit,
    onRequestClaim: (String, String, () -> Unit, (String) -> Unit) -> Unit,
    onReportListing: (String, String, String, () -> Unit) -> Unit,
    onMarkAsReturned: (String) -> Unit,
    onDeleteItem: (String, () -> Unit) -> Unit,
    onNavigateToClaim: (String) -> Unit
) {
    BackHandler { onNavigateBack() }

    val item by itemFlow.collectAsState(initial = null)
    val claims by claimsFlow.collectAsState(initial = emptyList())

    var showClaimDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Dialog state
    var claimProofText by remember { mutableStateOf("") }
    var claimError by remember { mutableStateOf<String?>(null) }
    var reportReason by remember { mutableStateOf("Inappropriate Content") }
    var reportDetails by remember { mutableStateOf("") }
    var contactMessageText by remember { mutableStateOf("") }

    if (item == null) {
        Scaffold(
            topBar = {
                CampusTopAppBar(
                    title = "Item Details",
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
                Text("Item not found or has been removed.")
            }
        }
        return
    }

    val currentItem = item!!
    val isMyItem = currentUser?.id == currentItem.postedByUserId
    val isReturned = currentItem.status == ItemStatus.RETURNED.name
    val isLost = currentItem.type == ItemType.LOST.name

    Scaffold(
        topBar = {
            CampusTopAppBar(
                title = currentItem.title,
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    if (isMyItem) {
                        IconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.testTag("item_delete_button")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete item", tint = CampusCoral)
                        }
                    } else {
                        IconButton(
                            onClick = { showReportDialog = true },
                            modifier = Modifier.testTag("item_report_flag_button")
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = "Report Listing", tint = Slate600)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Large Item Image / Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                if (currentItem.imageUri != null) {
                    AsyncImage(
                        model = currentItem.imageUri,
                        contentDescription = currentItem.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Modern placeholder image
                    Image(
                        painter = painterResource(id = R.drawable.img_campus_banner),
                        contentDescription = "Default item illustration",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                    )
                }

                // Type & Status Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TypeBadge(type = currentItem.type)
                    StatusPill(status = currentItem.status)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title & Category
                Column {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = currentItem.category,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentItem.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Meta Info Card (Location, Date, Posted By)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = CampusNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isLost) "Last Seen Location" else "Found Location",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                                Text(
                                    text = currentItem.location,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = CampusGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isLost) "Date Lost" else "Date Found",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                                Text(
                                    text = currentItem.eventDate,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = CampusEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Reported By (Privacy Protected)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                                Text(
                                    text = "${currentItem.postedByName} • Verified Campus Member",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Description
                Column {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentItem.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }

                // If owner is viewing own item, show Identifying Details
                if (isMyItem && currentItem.identifyingDetails.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Your Private Identifying Details (Proof)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF92400E)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentItem.identifyingDetails,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                }

                // Claims list on this item (if owner is viewing)
                if (isMyItem && claims.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Claims Received on this Item (${claims.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        claims.forEach { claim ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("claim_item_${claim.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Claim by ${claim.claimerName} (${claim.claimerStudentId})",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        StatusPill(status = claim.status)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Proof given: ${claim.proofDetails}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate600
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { onNavigateToClaim(claim.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                                        modifier = Modifier.testTag("open_claim_${claim.id}")
                                    ) {
                                        Text("Review Claim & Chat", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons Section
                if (isMyItem) {
                    // Owner controls: Mark Returned
                    if (!isReturned) {
                        Button(
                            onClick = { onMarkAsReturned(currentItem.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("mark_returned_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CampusEmerald)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mark as Returned / Reunited", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CampusEmeraldLight,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CampusEmerald)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "This item has been returned and marked resolved!",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF065F46)
                                )
                            }
                        }
                    }
                } else {
                    // Non-owner controls:
                    // 1. Request Claim
                    // 2. Contact User
                    // 3. Report Listing
                    if (!isReturned) {
                        Button(
                            onClick = { showClaimDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("request_claim_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)
                        ) {
                            Icon(Icons.Default.Handshake, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Request Claim", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showContactDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("contact_user_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = CampusNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Contact Finder / Owner", color = CampusNavy, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    OutlinedButton(
                        onClick = { showReportDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_listing_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CampusCoral)
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = CampusCoral)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Report Listing to Campus Admin")
                    }
                }
            }
        }
    }

    // Dialog: Request Claim
    if (showClaimDialog) {
        AlertDialog(
            onDismissRequest = { showClaimDialog = false },
            title = { Text("Request Claim for '${currentItem.title}'", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Provide identifying proof that confirms this item belongs to you (e.g., serial number, password/lock screen wallpaper, secret marks, exact contents).",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = claimProofText,
                        onValueChange = { claimProofText = it; claimError = null },
                        label = { Text("Proof of Ownership *") },
                        placeholder = { Text("Describe identifying marks or distinctive details...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("claim_proof_input")
                    )
                    if (claimError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = claimError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (claimProofText.isBlank()) {
                            claimError = "Please describe your proof of ownership."
                            return@Button
                        }
                        onRequestClaim(
                            currentItem.id,
                            claimProofText,
                            {
                                showClaimDialog = false
                            },
                            { err ->
                                claimError = err
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                    modifier = Modifier.testTag("submit_claim_request_btn")
                ) {
                    Text("Submit Claim")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClaimDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Contact User Safe Prompt
    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text("Safe Campus Communication", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "To protect privacy and safety, communications are monitored and coordinated through the verified campus lost and found claim flow.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "If this item belongs to you, submit a Claim Request with your proof so the finder can review and unlock safe messaging.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showContactDialog = false
                        showClaimDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)
                ) {
                    Text("Proceed to Claim")
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }

    // Dialog: Report Listing
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Listing", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Help maintain safety by flagging spam, fake items, or offensive content to campus admins.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        label = { Text("Reason") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("report_reason_input")
                    )
                    OutlinedTextField(
                        value = reportDetails,
                        onValueChange = { reportDetails = it },
                        label = { Text("Additional Details") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("report_details_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportListing(currentItem.id, reportReason, reportDetails) {
                            showReportDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusCoral),
                    modifier = Modifier.testTag("submit_report_listing_btn")
                ) {
                    Text("Report to Admin")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Confirm Delete
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Listing?") },
            text = { Text("Are you sure you want to remove this report from CampusFind? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteItem(currentItem.id) {
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusCoral)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
