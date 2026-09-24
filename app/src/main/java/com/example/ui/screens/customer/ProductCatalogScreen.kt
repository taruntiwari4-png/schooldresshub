package com.example.ui.screens.customer

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SchoolEntity
import com.example.ui.theme.SchoolAmber
import com.example.ui.theme.SchoolEmerald
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.SchoolNavyLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.SortOption

@Composable
fun ProductCatalogScreen(
    searchQuery: String,
    selectedSchoolId: String?,
    selectedCategory: String?,
    selectedGender: String?,
    selectedSortOption: SortOption,
    schools: List<SchoolEntity>,
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    platformMarginPercentage: Double,
    onSearchChange: (String) -> Unit,
    onSchoolSelect: (String?) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onGenderSelect: (String?) -> Unit,
    onSortSelect: (SortOption) -> Unit,
    onClearFilters: () -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onCompareClick: (ProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Search & Filter Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search school, class, product, size or seller...", fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = SchoolNavy)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SchoolNavy,
                        unfocusedBorderColor = Slate200
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sort and Active Filter Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Slate100,
                            modifier = Modifier.clickable { showSortMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp), tint = Slate700)
                                Text(
                                    text = "Sort: ${selectedSortOption.label}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate900
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label, fontSize = 13.sp) },
                                    onClick = {
                                        onSortSelect(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedSchoolId != null || selectedCategory != null || selectedGender != null || searchQuery.isNotEmpty()) {
                        TextButton(
                            onClick = onClearFilters,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("Clear Filters", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Schools Horizontal Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    item {
                        FilterChip(
                            label = "All Schools",
                            isSelected = selectedSchoolId == null,
                            onClick = { onSchoolSelect(null) }
                        )
                    }
                    items(schools) { school ->
                        FilterChip(
                            label = school.name,
                            isSelected = selectedSchoolId == school.id,
                            onClick = { onSchoolSelect(school.id) }
                        )
                    }
                }

                // Categories Horizontal Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    item {
                        FilterChip(
                            label = "All Categories",
                            isSelected = selectedCategory == null,
                            onClick = { onCategorySelect(null) }
                        )
                    }
                    items(categories) { category ->
                        FilterChip(
                            label = category.name,
                            isSelected = selectedCategory == category.name,
                            onClick = { onCategorySelect(category.name) }
                        )
                    }
                }

                // Gender Filters
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val genders = listOf("All", "Boys", "Girls", "Unisex")
                    items(genders) { gender ->
                        FilterChip(
                            label = gender,
                            isSelected = (selectedGender == null && gender == "All") || (selectedGender == gender),
                            onClick = { onGenderSelect(if (gender == "All") null else gender) }
                        )
                    }
                }
            }
        }

        // Product Count / Results Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${products.size} school uniform items found",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate700
            )
            Text(
                text = "Includes 5% SchoolDressHub fee",
                fontSize = 10.sp,
                color = SchoolEmerald,
                fontWeight = FontWeight.Bold
            )
        }

        // Products Grid
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No uniforms found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your school or category filters.",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SchoolNavy,
                        modifier = Modifier.clickable { onClearFilters() }
                    ) {
                        Text(
                            text = "Reset All Filters",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        platformMarginPercentage = platformMarginPercentage,
                        onProductClick = onProductClick,
                        onCompareClick = onCompareClick
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) SchoolNavy else Slate100,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Slate700,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
