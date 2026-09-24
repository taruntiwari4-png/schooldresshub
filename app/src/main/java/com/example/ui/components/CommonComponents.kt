package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.data.model.PricingCalculation
import com.example.data.model.UserRoles
import com.example.ui.theme.SchoolAmber
import com.example.ui.theme.SchoolAmberLight
import com.example.ui.theme.SchoolEmerald
import com.example.ui.theme.SchoolEmeraldLight
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.SchoolNavyLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun RoleSwitcherBar(
    currentRole: String,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SchoolNavy,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SWITCH DEMO ROLE:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap to test all 4 perspectives",
                    style = MaterialTheme.typography.labelSmall,
                    color = SchoolAmberLight
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val roles = listOf(
                    Triple(UserRoles.CUSTOMER, "Customer", "Priya"),
                    Triple(UserRoles.WHOLESALER, "Wholesaler", "ABC Depot"),
                    Triple(UserRoles.SHOPKEEPER, "Retailer", "City Store"),
                    Triple(UserRoles.ADMIN, "Admin", "Controls")
                )

                roles.forEach { (roleKey, label, sub) ->
                    val isSelected = currentRole == roleKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) SchoolAmber else Color.White.copy(alpha = 0.15f)
                            )
                            .clickable { onRoleSelected(roleKey) }
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Slate900 else Color.White
                            )
                            Text(
                                text = sub,
                                fontSize = 9.sp,
                                color = if (isSelected) Slate800 else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriceBreakdownCard(
    pricing: PricingCalculation,
    modifier: Modifier = Modifier,
    isSellerView: Boolean = false,
    deliveryFee: Double? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate100),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = SchoolNavy,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isSellerView) "Seller Earnings Breakdown" else "Transparent Price Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    color = SchoolNavy,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seller Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isSellerView) "Your Base Selling Price" else "Seller Product Price",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate700
                )
                Text(
                    text = "₹${String.format("%.2f", pricing.basePrice)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate900
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // SchoolDressHub Fee (5% configurable)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SchoolDressHub Fee (${String.format("%.1f", pricing.marginPercentage)}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate700
                )
                Text(
                    text = "+ ₹${String.format("%.2f", pricing.marginAmount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SchoolNavyLight
                )
            }

            if (deliveryFee != null && deliveryFee > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Standard Delivery Fee",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate700
                    )
                    Text(
                        text = "+ ₹${String.format("%.2f", deliveryFee)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate900
                    )
                }
            } else if (deliveryFee != null && deliveryFee == 0.0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Delivery Fee",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate700
                    )
                    Text(
                        text = "FREE",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = SchoolEmerald
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Slate200
            )

            // Final Price / Expected Payout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSellerView) "Customer Final Price:" else "Final Customer Price:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                val finalTotal = pricing.customerPrice + (deliveryFee ?: 0.0)
                Text(
                    text = "₹${String.format("%.2f", finalTotal)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = SchoolNavy
                )
            }

            if (isSellerView) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SchoolEmeraldLight)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Expected Payout:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = SchoolEmerald
                    )
                    Text(
                        text = "₹${String.format("%.2f", pricing.basePrice)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = SchoolEmerald
                    )
                }
            }
        }
    }
}

@Composable
fun VerifiedBadge(
    role: String,
    modifier: Modifier = Modifier
) {
    val isWholesaler = role == UserRoles.WHOLESALER
    val text = if (isWholesaler) "Verified Wholesaler" else "Verified Retailer"
    val bgColor = if (isWholesaler) SchoolEmeraldLight else Color(0xFFEFF6FF)
    val textColor = if (isWholesaler) SchoolEmerald else SchoolNavy

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun BestPriceTag(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SchoolAmber)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Slate900,
            modifier = Modifier.size(11.dp)
        )
        Text(
            text = "BEST PRICE",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Slate900
        )
    }
}

@Composable
fun RatingBar(
    rating: Double,
    reviewCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = SchoolAmber,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = String.format("%.1f", rating),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Slate800
        )
        if (reviewCount != null) {
            Text(
                text = "($reviewCount)",
                fontSize = 11.sp,
                color = Slate600
            )
        }
    }
}
