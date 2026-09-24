package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.calculateProductPricing
import com.example.ui.components.BestPriceTag
import com.example.ui.components.PriceBreakdownCard
import com.example.ui.components.RatingBar
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.SchoolAmber
import com.example.ui.theme.SchoolEmerald
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.SchoolNavyDark
import com.example.ui.theme.SchoolNavyLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: ProductEntity,
    reviews: List<ReviewEntity>,
    platformMarginPercentage: Double,
    onBackClick: () -> Unit,
    onCompareSellersClick: () -> Unit,
    onAddToCart: (ProductEntity, String, Int) -> Unit,
    onBuyNow: (ProductEntity, String, Int) -> Unit,
    onSubmitReview: (String, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sizesList = remember(product.availableSizes) {
        product.availableSizes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
    var selectedSize by remember { mutableStateOf(sizesList.firstOrNull() ?: "Standard") }
    var quantity by remember { mutableIntStateOf(1) }
    var showReviewDialog by remember { mutableStateOf(false) }

    val pricing = calculateProductPricing(product.basePrice, platformMarginPercentage)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = product.name,
                        maxLines = 1,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SchoolNavy)
            )
        },
        bottomBar = {
            // Sticky Bottom Action Bar
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(0.9f)) {
                        Text(
                            text = "₹${String.format("%.2f", pricing.customerPrice * quantity)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SchoolNavy
                        )
                        Text(
                            text = "Size $selectedSize • Qty $quantity",
                            fontSize = 10.sp,
                            color = Slate600
                        )
                    }

                    OutlinedButton(
                        onClick = { onAddToCart(product, selectedSize, quantity) },
                        modifier = Modifier.weight(1.1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SchoolNavy)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to Cart", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onBuyNow(product, selectedSize, quantity) },
                        modifier = Modifier.weight(1.1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                    ) {
                        Text("Buy Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(Slate100)
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    // Best Price badge
                    if (product.isBestPrice) {
                        Box(modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)) {
                            BestPriceTag()
                        }
                    }
                }
            }

            // Title & School Info
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // School tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = SchoolNavy, modifier = Modifier.size(18.dp))
                            Text(
                                text = product.schoolName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SchoolNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Brand: ${product.brand}",
                                fontSize = 12.sp,
                                color = Slate600
                            )
                            Text(text = "•", color = Slate600)
                            Text(
                                text = "${product.gender} • ${product.classGroup}",
                                fontSize = 12.sp,
                                color = Slate600
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RatingBar(product.rating, product.reviewCount)
                            Text(
                                text = "${product.stockQuantity} in stock",
                                fontSize = 11.sp,
                                color = if (product.stockQuantity < 20) Color(0xFFDC2626) else SchoolEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Price Breakdown Card (Transparent SchoolDressHub 5% model)
            item {
                Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                    PriceBreakdownCard(pricing = pricing)
                }
            }

            // Compare Sellers Button
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CompareArrows, contentDescription = null, tint = SchoolNavy, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Compare Other Sellers",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SchoolNavy
                                )
                            }
                            Text(
                                text = "Multiple wholesalers and shopkeepers list this uniform. Compare prices, delivery time and distance.",
                                fontSize = 10.sp,
                                color = Slate700
                            )
                        }

                        Button(
                            onClick = onCompareSellersClick,
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Compare", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Seller Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Sold By",
                            fontSize = 11.sp,
                            color = Slate600,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = SchoolNavy)
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = product.sellerName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                    VerifiedBadge(product.sellerRole)
                                }
                                Text(
                                    text = "${product.sellerCity} • ${product.sellerDistanceKm} km away",
                                    fontSize = 11.sp,
                                    color = Slate600
                                )
                            }
                        }
                    }
                }
            }

            // Size Selector & Quantity
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Select Size:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(sizesList) { size ->
                                val isSelected = selectedSize == size
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) SchoolNavy else Slate100)
                                        .clickable { selectedSize = size }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = size,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Slate900
                                    )
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Slate200)

                        // Quantity Stepper
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quantity:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Slate100,
                                    modifier = Modifier.clickable { if (quantity > 1) quantity-- }
                                ) {
                                    Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }
                                }

                                Text(
                                    text = "$quantity",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Slate900
                                )

                                Surface(
                                    shape = CircleShape,
                                    color = Slate100,
                                    modifier = Modifier.clickable { if (quantity < product.stockQuantity) quantity++ }
                                ) {
                                    Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Product Details & Specifications
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Product Specifications",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SpecRow("Material / Fabric", product.material)
                        SpecRow("Color", product.color)
                        SpecRow("Gender", product.gender)
                        SpecRow("Class Group", product.classGroup)
                        SpecRow("Category", product.category)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate700,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Trust & Delivery Badges
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = SchoolEmerald, modifier = Modifier.size(20.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "FREE Delivery",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SchoolEmerald
                                    )
                                    Text(
                                        text = "• Delivered in ${product.deliveryDays} Business Days",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate800
                                    )
                                }
                                Text(
                                    text = "Zero shipping fee for customer orders",
                                    fontSize = 10.sp,
                                    color = Slate600
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AssignmentReturn, contentDescription = null, tint = SchoolNavy, modifier = Modifier.size(18.dp))
                            Text(
                                text = "7 Days Easy Uniform Size Exchange Policy",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate800
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = SchoolNavy, modifier = Modifier.size(18.dp))
                            Text(
                                text = "100% School Board Dress Compliance Guarantee",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate800
                            )
                        }
                    }
                }
            }

            // Customer Reviews Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Customer Reviews (${reviews.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Button(
                                onClick = { showReviewDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Write Review", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (reviews.isEmpty()) {
                            Text(
                                text = "No reviews yet. Be the first to review this school uniform!",
                                fontSize = 12.sp,
                                color = Slate600
                            )
                        } else {
                            reviews.forEach { rev ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = rev.customerName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )
                                        Row {
                                            repeat(rev.rating) {
                                                Icon(
                                                    Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = SchoolAmber,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = rev.comment,
                                        fontSize = 11.sp,
                                        color = Slate700
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(top = 8.dp),
                                        color = Slate100
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (showReviewDialog) {
        var userRating by remember { mutableIntStateOf(5) }
        var reviewText by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showReviewDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Review this School Uniform",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SchoolNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rate your experience with quality, fit & size accuracy:",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { userRating = star }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$star stars",
                                    tint = if (star <= userRating) SchoolAmber else Slate200,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Write your review...", fontSize = 12.sp) },
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showReviewDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (reviewText.isNotBlank()) {
                                    onSubmitReview(product.id, userRating, reviewText)
                                    showReviewDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                        ) {
                            Text("Submit Review")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Slate600)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
    }
}
