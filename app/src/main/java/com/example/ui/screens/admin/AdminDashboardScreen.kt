package com.example.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.PayoutEntity
import com.example.data.model.PlatformSettingsEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRoles
import com.example.ui.components.VerifiedBadge
import com.example.ui.screens.customer.StatusPill
import com.example.ui.theme.SchoolAmber
import com.example.ui.theme.SchoolEmerald
import com.example.ui.theme.SchoolEmeraldLight
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun AdminDashboardScreen(
    settings: PlatformSettingsEntity,
    allOrders: List<OrderEntity>,
    allSellers: List<UserEntity>,
    pendingSellers: List<UserEntity>,
    pendingProducts: List<ProductEntity>,
    allProducts: List<ProductEntity>,
    payouts: List<PayoutEntity>,
    onUpdatePlatformMargin: (Double) -> Unit,
    onUpdateDeliveryFeeSettings: (Double, Double) -> Unit,
    onVerifySeller: (String, Boolean) -> Unit,
    onApproveProduct: (String, Boolean) -> Unit,
    onProcessPayout: (Long, String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var marginSliderValue by remember(settings.platformMarginPercentage) {
        mutableDoubleStateOf(settings.platformMarginPercentage)
    }
    var deliveryFeeInput by remember(settings.defaultDeliveryFee) {
        mutableStateOf(settings.defaultDeliveryFee.toInt().toString())
    }
    var freeThresholdInput by remember(settings.freeDeliveryThreshold) {
        mutableStateOf(settings.freeDeliveryThreshold.toInt().toString())
    }

    val totalGrossSales = allOrders.sumOf { it.grandTotal }
    val totalPlatformRevenue = allOrders.sumOf { it.totalPlatformFee }
    val wholesalersCount = allSellers.count { it.role == UserRoles.WHOLESALER }
    val retailersCount = allSellers.count { it.role == UserRoles.SHOPKEEPER }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SchoolNavy)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SchoolAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Slate900,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "SchoolDressHub Platform Administration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Multi-Seller Governance, Commission & Approvals",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Platform KPIs
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminKpiCard(
                        title = "Gross Marketplace Sales",
                        value = "₹${String.format("%.0f", totalGrossSales)}",
                        subtitle = "${allOrders.size} total orders",
                        icon = Icons.Default.TrendingUp,
                        color = SchoolNavy,
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiCard(
                        title = "Platform Fee Revenue",
                        value = "₹${String.format("%.0f", totalPlatformRevenue)}",
                        subtitle = "${String.format("%.1f", settings.platformMarginPercentage)}% platform margin",
                        icon = Icons.Default.Receipt,
                        color = SchoolEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminKpiCard(
                        title = "Registered Sellers",
                        value = "${allSellers.size}",
                        subtitle = "$wholesalersCount Wholesalers, $retailersCount Retailers",
                        icon = Icons.Default.Store,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiCard(
                        title = "Catalog Items",
                        value = "${allProducts.size}",
                        subtitle = "Active school uniforms",
                        icon = Icons.Default.ShoppingBag,
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Configurable Platform Margin Section (KEY USER REQUIREMENT)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = SchoolNavy)
                            Text(
                                text = "Configurable Platform Margin",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SchoolNavy
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SchoolEmeraldLight
                        ) {
                            Text(
                                text = "Current: ${String.format("%.1f", settings.platformMarginPercentage)}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SchoolEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "SchoolDressHub adds this margin to the seller's base price. Wholesalers & shopkeepers receive 100% of their base price.",
                        fontSize = 11.sp,
                        color = Slate600,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Selected Margin:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate800)
                        Text(
                            text = "${String.format("%.1f", marginSliderValue)}%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SchoolNavy
                        )
                    }

                    Slider(
                        value = marginSliderValue.toFloat(),
                        onValueChange = { marginSliderValue = it.toDouble() },
                        valueRange = 1f..20f,
                        steps = 37, // 0.5 increments
                        colors = SliderDefaults.colors(
                            thumbColor = SchoolNavy,
                            activeTrackColor = SchoolNavy
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1%", fontSize = 10.sp, color = Slate600)
                        Text("5% (Default)", fontSize = 10.sp, color = SchoolNavy, fontWeight = FontWeight.Bold)
                        Text("10%", fontSize = 10.sp, color = Slate600)
                        Text("20%", fontSize = 10.sp, color = Slate600)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val rounded = (Math.round(marginSliderValue * 10.0) / 10.0)
                            onUpdatePlatformMargin(rounded)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save & Apply New Margin (${String.format("%.1f", marginSliderValue)}%)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Delivery Fee Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = SchoolNavy)
                            Text(
                                text = "Delivery Fee Configuration",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SchoolNavy
                            )
                        }

                        val isFreeDelivery = settings.defaultDeliveryFee == 0.0 || settings.freeDeliveryThreshold == 0.0
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isFreeDelivery) SchoolEmeraldLight else Slate100
                        ) {
                            Text(
                                text = if (isFreeDelivery) "FREE Delivery Active" else "₹${settings.defaultDeliveryFee.toInt()} Delivery",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFreeDelivery) SchoolEmerald else Slate700,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = deliveryFeeInput,
                            onValueChange = { deliveryFeeInput = it },
                            label = { Text("Standard Delivery Fee (₹)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = freeThresholdInput,
                            onValueChange = { freeThresholdInput = it },
                            label = { Text("Free Delivery Over (₹)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                deliveryFeeInput = "0"
                                freeThresholdInput = "0"
                                onUpdateDeliveryFeeSettings(0.0, 0.0)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Set 100% Free Delivery", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val fee = deliveryFeeInput.toDoubleOrNull() ?: 0.0
                                val threshold = freeThresholdInput.toDoubleOrNull() ?: 0.0
                                onUpdateDeliveryFeeSettings(fee, threshold)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save Custom Rules", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Pending Seller Verifications
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Pending Seller Approvals (${pendingSellers.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (pendingSellers.isEmpty()) {
                        Text("All seller accounts are reviewed and active.", fontSize = 12.sp, color = Slate600)
                    } else {
                        pendingSellers.forEach { seller ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate100)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(seller.businessName.ifBlank { seller.name }, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                                        Text("${seller.role} • GST: ${seller.gstNumber}", fontSize = 10.sp, color = Slate600)
                                        Text("${seller.city} • Phone: ${seller.phone}", fontSize = 10.sp, color = Slate600)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = { onVerifySeller(seller.id, true) },
                                            colors = ButtonDefaults.buttonColors(containerColor = SchoolEmerald),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Approve", fontSize = 11.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { onVerifySeller(seller.id, false) },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Reject", fontSize = 11.sp, color = Color(0xFFDC2626))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Payout Requests
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Seller Payout Requests (${payouts.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (payouts.isEmpty()) {
                        Text("No pending payout requests.", fontSize = 12.sp, color = Slate600)
                    } else {
                        payouts.forEach { payout ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate100)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(payout.sellerName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                                        Text("Amount: ₹${payout.amount.toInt()} • A/C: ${payout.bankAccount}", fontSize = 11.sp, color = Slate700)
                                        Text("IFSC: ${payout.ifscCode} • Status: ${payout.status}", fontSize = 10.sp, color = Slate600)
                                    }

                                    if (payout.status == "PENDING") {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Button(
                                                onClick = { onProcessPayout(payout.id, payout.sellerId, true) },
                                                colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text("Approve", fontSize = 11.sp)
                                            }
                                        }
                                    } else {
                                        Text(payout.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
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

@Composable
private fun AdminKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = Slate600)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Slate900)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 9.sp, color = Slate600)
        }
    }
}
