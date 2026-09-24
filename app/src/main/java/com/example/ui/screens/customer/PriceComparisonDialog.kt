package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ProductEntity
import com.example.data.model.calculateProductPricing
import com.example.ui.components.BestPriceTag
import com.example.ui.components.RatingBar
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.SchoolAmber
import com.example.ui.theme.SchoolEmerald
import com.example.ui.theme.SchoolEmeraldLight
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun PriceComparisonDialog(
    selectedProduct: ProductEntity,
    comparisonSellers: List<ProductEntity>,
    platformMarginPercentage: Double,
    onSelectSellerProduct: (ProductEntity) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            tint = SchoolNavy,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Compare All Sellers",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SchoolNavy
                            )
                            Text(
                                text = selectedProduct.schoolName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate700)
                    }
                }

                Text(
                    text = selectedProduct.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate900,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Informational callout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate100)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Wholesalers and local retailers set their own base price. SchoolDressHub adds a transparent ${String.format("%.1f", platformMarginPercentage)}% platform margin to ensure genuine school dresses at honest prices.",
                        fontSize = 11.sp,
                        color = Slate700,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val sortedList = comparisonSellers.sortedBy { it.basePrice }

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sortedList) { sellerProduct ->
                        val pricing = calculateProductPricing(sellerProduct.basePrice, platformMarginPercentage)
                        val isCurrentSelected = sellerProduct.id == selectedProduct.id
                        val isLowestPrice = sortedList.firstOrNull()?.id == sellerProduct.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isCurrentSelected) 2.dp else 1.dp,
                                    color = if (isCurrentSelected) SchoolNavy else Slate200,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentSelected) Color(0xFFF0F5FF) else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = sellerProduct.sellerName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate900
                                            )
                                            if (isLowestPrice) {
                                                BestPriceTag()
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        VerifiedBadge(sellerProduct.sellerRole)

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RatingBar(sellerProduct.rating, sellerProduct.reviewCount)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = Slate600,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = "${sellerProduct.sellerDistanceKm} km (${sellerProduct.sellerCity})",
                                                    fontSize = 11.sp,
                                                    color = Slate600
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.LocalShipping,
                                                    contentDescription = null,
                                                    tint = Slate600,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = "${sellerProduct.deliveryDays}d delivery",
                                                    fontSize = 11.sp,
                                                    color = Slate600
                                                )
                                            }
                                        }
                                    }

                                    // Pricing side
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${String.format("%.2f", pricing.customerPrice)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SchoolNavy
                                        )
                                        Text(
                                            text = "Base ₹${pricing.basePrice.toInt()} + Fee ₹${pricing.marginAmount.toInt()}",
                                            fontSize = 10.sp,
                                            color = Slate600
                                        )
                                        Text(
                                            text = "${sellerProduct.stockQuantity} in stock",
                                            fontSize = 10.sp,
                                            color = if (sellerProduct.stockQuantity < 20) Color(0xFFDC2626) else SchoolEmerald,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        onSelectSellerProduct(sellerProduct)
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCurrentSelected) Slate700 else SchoolNavy
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    if (isCurrentSelected) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Text("Currently Selected", fontSize = 12.sp)
                                        }
                                    } else {
                                        Text(
                                            "Buy From This Seller (₹${String.format("%.2f", pricing.customerPrice)})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
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
}
