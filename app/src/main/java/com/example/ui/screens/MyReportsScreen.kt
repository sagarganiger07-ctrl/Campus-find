package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClaimEntity
import com.example.data.model.ItemEntity
import com.example.data.model.ItemStatus
import com.example.data.model.ItemType
import com.example.data.model.UserEntity
import com.example.ui.components.CampusTopAppBar
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ItemCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.Slate600

@Composable
fun MyReportsScreen(
    items: List<ItemEntity>,
    claims: List<ClaimEntity>,
    currentUser: UserEntity?,
    initialTabIndex: Int = 0,
    onNavigateBack: () -> Unit,
    onNavigateToItem: (String) -> Unit,
    onNavigateToClaim: (String) -> Unit,
    onDeleteItem: (String, () -> Unit) -> Unit,
    onUpdateItem: (ItemEntity, () -> Unit) -> Unit
) {
    BackHandler { onNavigateBack() }

    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }
    val tabTitles = listOf("My Lost", "My Found", "Claims", "Returned")

    val myId = currentUser?.id ?: ""
    val myLostItems = items.filter { it.postedByUserId == myId && it.type == ItemType.LOST.name && it.status != ItemStatus.RETURNED.name }
    val myFoundItems = items.filter { it.postedByUserId == myId && it.type == ItemType.FOUND.name && it.status != ItemStatus.RETURNED.name }
    val myReturnedItems = items.filter { it.postedByUserId == myId && it.status == ItemStatus.RETURNED.name }
    val myClaims = claims.filter { it.claimerUserId == myId || it.ownerFinderUserId == myId }

    var editingItem by remember { mutableStateOf<ItemEntity?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editDesc by remember { mutableStateOf("") }
    var editLocation by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CampusTopAppBar(
                title = "My Reports & Claims",
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
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = when (index) {
                                    0 -> "$title (${myLostItems.size})"
                                    1 -> "$title (${myFoundItems.size})"
                                    2 -> "$title (${myClaims.size})"
                                    3 -> "$title (${myReturnedItems.size})"
                                    else -> title
                                },
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("tab_$index")
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    // My Lost Items
                    if (myLostItems.isEmpty()) {
                        EmptyStateView(
                            title = "No Lost Items Reported",
                            message = "You haven't reported any lost items. When you lose something on campus, report it here!"
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(myLostItems, key = { it.id }) { item ->
                                Column {
                                    ItemCard(item = item, onClick = { onNavigateToItem(item.id) })
                                    ItemActionRow(
                                        onEdit = {
                                            editingItem = item
                                            editTitle = item.title
                                            editDesc = item.description
                                            editLocation = item.location
                                        },
                                        onDelete = { onDeleteItem(item.id) {} }
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // My Found Items
                    if (myFoundItems.isEmpty()) {
                        EmptyStateView(
                            title = "No Found Items Reported",
                            message = "You haven't reported any found items. Help your fellow students by posting items you find!"
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(myFoundItems, key = { it.id }) { item ->
                                Column {
                                    ItemCard(item = item, onClick = { onNavigateToItem(item.id) })
                                    ItemActionRow(
                                        onEdit = {
                                            editingItem = item
                                            editTitle = item.title
                                            editDesc = item.description
                                            editLocation = item.location
                                        },
                                        onDelete = { onDeleteItem(item.id) {} }
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Claims
                    if (myClaims.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Handshake,
                            title = "No Active Claims",
                            message = "Claims you request or claims submitted by others on your items will appear here."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(myClaims, key = { it.id }) { claim ->
                                val isMyClaim = claim.claimerUserId == myId
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToClaim(claim.id) }
                                        .testTag("my_claim_${claim.id}"),
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
                                                text = if (isMyClaim) "You claimed: ${claim.itemTitle}" else "Claim on your item: ${claim.itemTitle}",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                            StatusPill(status = claim.status)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = if (isMyClaim) "Finder: Verified Member" else "Claimer: ${claim.claimerName} (${claim.claimerStudentId})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate600
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "Proof: ${claim.proofDetails}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Button(
                                            onClick = { onNavigateToClaim(claim.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("View Details & Safe Chat", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Returned Items
                    if (myReturnedItems.isEmpty()) {
                        EmptyStateView(
                            title = "No Returned Items Yet",
                            message = "Items you've reported that were successfully reunited with their owners will be archived here."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(myReturnedItems, key = { it.id }) { item ->
                                ItemCard(item = item, onClick = { onNavigateToItem(item.id) })
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Item Dialog
    if (editingItem != null) {
        val target = editingItem!!
        AlertDialog(
            onDismissRequest = { editingItem = null },
            title = { Text("Edit Listing", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_title_input")
                    )
                    OutlinedTextField(
                        value = editLocation,
                        onValueChange = { editLocation = it },
                        label = { Text("Location") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_location_input")
                    )
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("edit_desc_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = target.copy(
                            title = editTitle,
                            location = editLocation,
                            description = editDesc
                        )
                        onUpdateItem(updated) {
                            editingItem = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavy)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingItem = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ItemActionRow(
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Edit", fontSize = 12.sp)
        }
        TextButton(
            onClick = onDelete,
            colors = ButtonDefaults.textButtonColors(contentColor = CampusCoral)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Delete", fontSize = 12.sp)
        }
    }
}
