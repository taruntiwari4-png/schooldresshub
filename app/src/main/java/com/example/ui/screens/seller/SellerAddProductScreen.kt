package com.example.ui.screens.seller

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.PricingCalculation
import com.example.data.model.SchoolEntity
import com.example.data.model.calculateProductPricing
import com.example.ui.components.PriceBreakdownCard
import com.example.ui.theme.SchoolAmber
import com.example.ui.theme.SchoolEmerald
import com.example.ui.theme.SchoolEmeraldLight
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerAddProductScreen(
    schools: List<SchoolEntity>,
    categories: List<CategoryEntity>,
    platformMarginPercentage: Double,
    onBackClick: () -> Unit,
    onPublishProduct: (SchoolEntity, String, String, String, String, String, String, String, String, String, Double, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSchool by remember { mutableStateOf(schools.firstOrNull()) }
    var schoolExpanded by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()?.name ?: "School Shirts") }
    var categoryExpanded by remember { mutableStateOf(false) }

    var productName by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Unisex") }
    var classGroup by remember { mutableStateOf("Class 1-12") }
    var color by remember { mutableStateOf("") }
    var availableSizes by remember { mutableStateOf("28, 30, 32, 34, 36, 38") }
    var material by remember { mutableStateOf("Cotton Poly Mercerized") }
    var description by remember { mutableStateOf("") }
    var basePriceInput by remember { mutableStateOf("450") }
    var stockQuantityInput by remember { mutableStateOf("100") }
    var deliveryDaysInput by remember { mutableStateOf("2") }

    val basePrice = basePriceInput.toDoubleOrNull() ?: 0.0
    val pricing = calculateProductPricing(basePrice, platformMarginPercentage)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List New School Uniform", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SchoolNavy)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Customer Sees", fontSize = 10.sp, color = Slate600)
                        Text(
                            "₹${String.format("%.2f", pricing.customerPrice)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SchoolNavy
                        )
                        Text(
                            "You receive: ₹${String.format("%.2f", pricing.basePrice)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SchoolEmerald
                        )
                    }

                    Button(
                        onClick = {
                            val school = selectedSchool ?: return@Button
                            if (productName.isNotBlank() && basePrice > 0) {
                                onPublishProduct(
                                    school,
                                    productName,
                                    selectedCategory,
                                    description.ifBlank { "High quality uniform adhering to official school guidelines." },
                                    brand,
                                    gender,
                                    classGroup,
                                    color.ifBlank { "Standard School Color" },
                                    availableSizes,
                                    material,
                                    basePrice,
                                    stockQuantityInput.toIntOrNull() ?: 50,
                                    deliveryDaysInput.toIntOrNull() ?: 2
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publish Uniform", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Margin Formula Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = SchoolNavy, modifier = Modifier.size(20.dp))
                            Text(
                                text = "SchoolDressHub Automatic 5% Pricing Model",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SchoolNavy
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Enter your desired base selling price. The platform adds ${String.format("%.1f", platformMarginPercentage)}% platform fee. You will receive 100% of your entered base price upon delivery.",
                            fontSize = 11.sp,
                            color = Slate700,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // School & Category Dropdowns
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "School & Category",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // School dropdown
                        ExposedDropdownMenuBox(
                            expanded = schoolExpanded,
                            onExpandedChange = { schoolExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedSchool?.name ?: "Select School",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Associated School") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = schoolExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = schoolExpanded,
                                onDismissRequest = { schoolExpanded = false }
                            ) {
                                schools.forEach { school ->
                                    DropdownMenuItem(
                                        text = { Text("${school.name} (${school.board})") },
                                        onClick = {
                                            selectedSchool = school
                                            schoolExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category dropdown
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Uniform Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name) },
                                        onClick = {
                                            selectedCategory = cat.name
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Product Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Product Details",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = { Text("Product Title (e.g. ABC School White Shirt)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = brand,
                                onValueChange = { brand = it },
                                label = { Text("Brand / Maker") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = gender,
                                onValueChange = { gender = it },
                                label = { Text("Gender (Boys/Girls/Unisex)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = color,
                                onValueChange = { color = it },
                                label = { Text("Color (e.g. Navy Blue)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = classGroup,
                                onValueChange = { classGroup = it },
                                label = { Text("Class Group") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = availableSizes,
                            onValueChange = { availableSizes = it },
                            label = { Text("Available Sizes (comma separated, e.g. 28, 30, 32, 34)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = material,
                            onValueChange = { material = it },
                            label = { Text("Material / Fabric Specifications") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Uniform Description & Features") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Pricing & Live Formula Calculation
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Base Price & Inventory",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = basePriceInput,
                                onValueChange = { basePriceInput = it },
                                label = { Text("Your Base Price (₹)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = stockQuantityInput,
                                onValueChange = { stockQuantityInput = it },
                                label = { Text("Stock Quantity") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = deliveryDaysInput,
                            onValueChange = { deliveryDaysInput = it },
                            label = { Text("Estimated Delivery Days") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Real-time price breakdown
                        PriceBreakdownCard(
                            pricing = pricing,
                            isSellerView = true
                        )
                    }
                }
            }
        }
    }
}
