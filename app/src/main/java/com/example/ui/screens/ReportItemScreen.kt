package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ItemType
import com.example.ui.components.CampusTopAppBar
import com.example.ui.theme.CampusCoral
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CampusNavy
import com.example.ui.theme.Slate600
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportItemScreen(
    type: ItemType,
    onNavigateBack: () -> Unit,
    onSubmitItem: (ItemType, String, String, String, String, String, String, String?, (String) -> Unit, (String) -> Unit) -> Unit,
    onSuccessNav: (String) -> Unit
) {
    BackHandler { onNavigateBack() }

    val isLost = type == ItemType.LOST
    val themeColor = if (isLost) CampusCoral else CampusEmerald
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    var itemName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CampusCategories[1]) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf(todayDate) }
    var identifyingDetails by remember { mutableStateOf("") }
    var imageUriString by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Android Zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        imageUriString = uri?.toString()
    }

    Scaffold(
        topBar = {
            CampusTopAppBar(
                title = if (isLost) "Report Lost Item" else "Report Found Item",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header hint
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = themeColor.copy(alpha = 0.1f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = themeColor,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isLost) Icons.Default.Close else Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isLost) "Did you lose something on campus?" else "Did you find an item on campus?",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isLost)
                                "Provide clear details to help finders identify your belonging."
                            else
                                "Help return this to its owner. Keep distinct identifying marks private for proof.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }
                }
            }

            // Item Name
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it; errorMessage = null },
                label = { Text("Item Name *") },
                placeholder = { Text("e.g., Blue Hydro Flask, AirPods Pro 2, Dorm Keys") },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("report_item_name_input")
            )

            // Category Exposed Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category *") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("report_category_dropdown")
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    CampusCategories.filter { it != "All Categories" }.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory = cat
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Photo Upload Section (Android Photo Picker)
            Column {
                Text(
                    text = "Photo Upload (Optional)",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (imageUriString != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = imageUriString,
                            contentDescription = "Selected Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { imageUriString = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = Color.White)
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("report_photo_picker_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Select Photo",
                                tint = CampusNavy
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Select Photo from Device",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = CampusNavy
                            )
                        }
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it; errorMessage = null },
                label = { Text("Description *") },
                placeholder = { Text("Color, brand, size, where it might have been misplaced or discovered...") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("report_description_input")
            )

            // Location
            Column {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it; errorMessage = null },
                    label = { Text(if (isLost) "Last Seen Location *" else "Found Location *") },
                    placeholder = { Text("e.g., Library 2nd floor, Cafeteria booth 4") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_location_input")
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Quick campus location presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Library", "Cafeteria", "Science Quad", "Gym", "Engg Block", "Student Union").forEach { loc ->
                        FilterChip(
                            selected = location.contains(loc),
                            onClick = {
                                location = if (location.isBlank()) loc else "$loc - $location"
                            },
                            label = { Text(loc, fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Date
            OutlinedTextField(
                value = eventDate,
                onValueChange = { eventDate = it },
                label = { Text(if (isLost) "Date Lost (YYYY-MM-DD) *" else "Date Found (YYYY-MM-DD) *") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("report_date_input")
            )

            // Optional Identifying Details (Secret marks)
            OutlinedTextField(
                value = identifyingDetails,
                onValueChange = { identifyingDetails = it },
                label = { Text("Identifying Details / Secret Marks (Optional)") },
                placeholder = {
                    Text(
                        if (isLost)
                            "e.g., Lock screen wallpaper, serial ending in 49J, scratch on corner..."
                        else
                            "Private details only the true owner would know to verify claims..."
                    )
                },
                leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null) },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("report_identifying_details_input")
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Submit Button
            Button(
                onClick = {
                    if (itemName.isBlank() || description.isBlank() || location.isBlank() || eventDate.isBlank()) {
                        errorMessage = "Please fill in all mandatory fields (*)."
                        return@Button
                    }
                    isSubmitting = true
                    onSubmitItem(
                        type,
                        itemName,
                        selectedCategory,
                        description,
                        location,
                        eventDate,
                        identifyingDetails,
                        imageUriString,
                        { itemId ->
                            isSubmitting = false
                            onSuccessNav(itemId)
                        },
                        { err ->
                            isSubmitting = false
                            errorMessage = err
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("report_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                enabled = !isSubmitting
            ) {
                Text(
                    text = if (isSubmitting)
                        "Submitting..."
                    else if (isLost)
                        "Report Lost Item"
                    else
                        "Report Found Item",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
