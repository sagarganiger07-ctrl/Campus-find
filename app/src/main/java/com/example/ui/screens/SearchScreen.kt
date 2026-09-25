package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.ItemEntity
import com.example.data.model.ItemType
import com.example.ui.components.CampusTopAppBar
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ItemCard
import com.example.ui.navigation.Screen

val CampusCategories = listOf(
    "All Categories",
    "Electronics",
    "Keys",
    "Bags & Wallets",
    "ID & Cards",
    "Books & Study",
    "Bottles & Lunch",
    "Clothing & Acc",
    "Other"
)

val CampusLocations = listOf(
    "All Locations",
    "Main Library",
    "Cafeteria",
    "Science Quad",
    "Engineering Block",
    "Sports Arena / Gym",
    "Student Union",
    "Dormitory"
)

@Composable
fun SearchScreen(
    items: List<ItemEntity>,
    initialType: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToItem: (String) -> Unit
) {
    BackHandler { onNavigateBack() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(initialType ?: "ALL") }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var selectedLocation by remember { mutableStateOf("All Locations") }

    val filteredList = items.filter { item ->
        // Status/type filter
        val matchesType = when (selectedType) {
            "LOST" -> item.type == ItemType.LOST.name
            "FOUND" -> item.type == ItemType.FOUND.name
            else -> true
        }

        // Category filter
        val matchesCategory = selectedCategory == "All Categories" ||
                item.category.equals(selectedCategory, ignoreCase = true)

        // Location filter
        val matchesLocation = selectedLocation == "All Locations" ||
                item.location.contains(selectedLocation, ignoreCase = true)

        // Text query filter
        val matchesQuery = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true)

        matchesType && matchesCategory && matchesLocation && matchesQuery
    }

    Scaffold(
        topBar = {
            CampusTopAppBar(
                title = "Search Campus Items",
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
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by item name, details, location...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_text_input")
            )

            // Type Filter Chips: All, Lost, Found
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == "ALL",
                    onClick = { selectedType = "ALL" },
                    label = { Text("All Status") },
                    modifier = Modifier.testTag("filter_chip_all")
                )
                FilterChip(
                    selected = selectedType == "LOST",
                    onClick = { selectedType = "LOST" },
                    label = { Text("Lost Items") },
                    modifier = Modifier.testTag("filter_chip_lost")
                )
                FilterChip(
                    selected = selectedType == "FOUND",
                    onClick = { selectedType = "FOUND" },
                    label = { Text("Found Items") },
                    modifier = Modifier.testTag("filter_chip_found")
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category Horizontal Scrollable Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CampusCategories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Location Horizontal Scrollable Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CampusLocations.forEach { loc ->
                    FilterChip(
                        selected = selectedLocation == loc,
                        onClick = { selectedLocation = loc },
                        label = { Text(loc) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Results count
            Text(
                text = "${filteredList.size} item(s) found",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Results List
            if (filteredList.isEmpty()) {
                EmptyStateView(
                    title = "No Matching Items",
                    message = "Try changing your search keywords or relaxing the category/location filters."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        ItemCard(
                            item = item,
                            onClick = { onNavigateToItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}
