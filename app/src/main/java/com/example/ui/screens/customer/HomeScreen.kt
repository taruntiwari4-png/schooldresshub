package com.example.ui.screens.customer

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SchoolEntity
import com.example.data.model.UserEntity
import com.example.data.model.calculateProductPricing
import com.example.ui.components.BestPriceTag
import com.example.ui.components.RatingBar
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.SchoolAmber
import com.example.ui.theme.SchoolAmberLight
import com.example.ui.theme.SchoolEmerald
import com.example.ui.theme.SchoolEmeraldLight
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.SchoolNavyDark
import com.example.ui.theme.SchoolNavyLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun HomeScreen(
    currentLocation: String,
    schools: List<SchoolEntity>,
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    sellers: List<UserEntity>,
    cartItemCount: Int,
    notificationCount: Int,
    platformMarginPercentage: Double,
    onSearchClick: () -> Unit,
    onSchoolSelect: (SchoolEntity) -> Unit,
    onCategorySelect: (String) -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onCompareClick: (ProductEntity) -> Unit,
    onCartClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onLocationClick: () -> Unit,
    onViewAllProducts: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // App Header
        item {
            Surface(
                color = SchoolNavy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Logo and Top Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(SchoolAmber),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = Slate900,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "SchoolDressHub",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "School Uniforms at the Best Price",
                                fontSize = 11.sp,
                                color = SchoolAmberLight,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Notifications
                            IconButton(onClick = onNotificationClick) {
                                BadgedBox(
                                    badge = {
                                        if (notificationCount > 0) {
                                            Badge(containerColor = Color(0xFFEF4444)) {
                                                Text("$notificationCount")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color.White
                                    )
                                }
                            }

                            // Cart
                            IconButton(onClick = onCartClick) {
                                BadgedBox(
                                    badge = {
                                        if (cartItemCount > 0) {
                                            Badge(containerColor = SchoolAmber) {
                                                Text(
                                                    "$cartItemCount",
                                                    color = Slate900,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = "Cart",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Location Selector
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { onLocationClick() }
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = SchoolAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Deliver to: $currentLocation",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Trigger Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable { onSearchClick() }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = SchoolNavyLight
                            )
                            Text(
                                text = "Search school, uniform, product or seller...",
                                color = Slate600,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Promotional Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .height(160.dp)
                    .background(SchoolNavyLight)
            ) {
                // Generated Hero Image background
                Image(
                    painter = painterResource(id = R.drawable.hero_banner_uniforms_1790226847121),
                    contentDescription = "School Uniforms Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    SchoolNavyDark.copy(alpha = 0.90f),
                                    SchoolNavyDark.copy(alpha = 0.45f),
                                    Color.Transparent
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SchoolAmber)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "OFFICIAL SCHOOL DRESS MARKETPLACE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Connect. Compare. Buy.\nSchool Dresses at Better Prices.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Wholesaler & Shopkeeper Direct Rates + ${String.format("%.0f", platformMarginPercentage)}% transparent margin",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Free Delivery & Value Proposition Banners
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Free Delivery Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF86EFAC)))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SchoolEmerald,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "100% FREE DELIVERY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF166534)
                                )
                                Surface(
                                    color = SchoolEmerald,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "FOR ALL CUSTOMERS",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Zero delivery charges on all school dresses, shoes & uniform accessories. No minimum order limit!",
                                fontSize = 10.sp,
                                color = Color(0xFF15803D),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // Multi-Seller Comparison Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SchoolEmeraldLight)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SchoolEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Guaranteed Best Price & Multi-Seller Comparison",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                            Text(
                                text = "Multiple wholesalers & local shopkeepers compete on price. Compare base prices, platform fees and sizes before buying.",
                                fontSize = 10.sp,
                                color = Color(0xFF047857),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Popular Schools Section
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Popular Schools",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Select your school to find compliant uniforms",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }
                    TextButton(onClick = onSearchClick) {
                        Text("All Schools", color = SchoolNavy, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(schools) { school ->
                        Card(
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { onSchoolSelect(school) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SchoolNavyLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = school.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${school.board} • ${school.city}",
                                    fontSize = 10.sp,
                                    color = Slate600
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = school.uniformColorDesc,
                                    fontSize = 9.sp,
                                    color = SchoolNavy,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Product Categories Section
        item {
            Column(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = "Shop by Uniform Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "Dresses, blazers, shoes, bags & accessories",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2-row category chips / cards
                val chunked = categories.chunked(6)
                chunked.forEach { rowCategories ->
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(rowCategories) { category ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.clickable { onCategorySelect(category.name) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(SchoolAmber)
                                    )
                                    Text(
                                        text = category.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate800
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Price Comparison Spotlight Feature
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = null,
                                tint = SchoolNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Multi-Seller Price Comparison",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SchoolNavy
                            )
                        }
                        BestPriceTag()
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Example: ABC Public School White Shirt is listed by 3 verified sellers starting from ₹450 base price (₹472.50 final customer price with ${String.format("%.0f", platformMarginPercentage)}% fee).",
                        fontSize = 11.sp,
                        color = Slate700,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val comparisonSample = products.find { it.name.contains("White Shirt") }
                    if (comparisonSample != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SchoolNavy,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCompareClick(comparisonSample) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Compare All Sellers for ABC White Shirt",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = SchoolAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Best Price Uniforms Carousel
        item {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Best Price Uniforms",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Wholesaler direct prices with 5% transparent fee",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }
                    TextButton(onClick = onViewAllProducts) {
                        Text("View All", color = SchoolNavy, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products.take(6)) { product ->
                        ProductCard(
                            product = product,
                            platformMarginPercentage = platformMarginPercentage,
                            onProductClick = onProductClick,
                            onCompareClick = onCompareClick,
                            modifier = Modifier.width(190.dp)
                        )
                    }
                }
            }
        }

        // Nearby Sellers Section
        item {
            Column(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Nearby Uniform Sellers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Wholesalers & shopkeepers near $currentLocation",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                sellers.forEach { seller ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Slate100),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Store,
                                    contentDescription = null,
                                    tint = SchoolNavy,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = seller.businessName.ifBlank { seller.name },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                    VerifiedBadge(seller.role)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${seller.address}, ${seller.city}",
                                    fontSize = 11.sp,
                                    color = Slate600,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RatingBar(seller.rating)
                                    Text(
                                        text = "• ${seller.totalOrders}+ orders fulfilled",
                                        fontSize = 10.sp,
                                        color = SchoolEmerald,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
