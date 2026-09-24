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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.OrderStatus
import com.example.data.model.OrderWithItems
import com.example.data.model.PayoutEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRoles
import com.example.ui.components.VerifiedBadge
import com.example.ui.screens.customer.StatusPill
import com.example.ui.theme.SchoolAmber
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
fun SellerDashboardScreen(
    user: UserEntity,
    products: List<ProductEntity>,
    ordersWithItems: List<OrderWithItems>,
    payouts: List<PayoutEntity>,
    platformMarginPercentage: Double,
    onAddNewProduct: () -> Unit,
    onUpdateStock: (ProductEntity, Int) -> Unit,
    onToggleActive: (ProductEntity) -> Unit,
    onUpdateOrderStatus: (String, String, String) -> Unit,
    onRequestPayout: (Double, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPayoutDialog by remember { mutableStateOf(false) }

    // Analytics calculations
    val totalSellerItems = ordersWithItems.flatMap { it.items.filter { item -> item.sellerId == user.id } }
    val totalSalesBase = totalSellerItems.sumOf { it.unitBasePrice * it.quantity }
    val totalPlatformFees = totalSellerItems.sumOf { it.unitPlatformFee * it.quantity }
    val approvedPayoutsTotal = payouts.filter { it.status == "APPROVED" }.sumOf { it.amount }
    val pendingPayoutsTotal = payouts.filter { it.status == "PENDING" }.sumOf { it.amount }
    val availablePayout = (totalSalesBase - approvedPayoutsTotal - pendingPayoutsTotal).coerceAtLeast(0.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Seller Profile Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SchoolNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                Text(
                                    text = user.businessName.ifBlank { user.name },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                VerifiedBadge(user.role)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "GST: ${user.gstNumber.ifBlank { "07AAAAA0000A1Z5" }} • ${user.city}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Button(
                            onClick = onAddNewProduct,
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolAmber),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Slate900, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Product", color = Slate900, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Platform Margin: ${String.format("%.1f", platformMarginPercentage)}% added to customer price",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                            Text(
                                text = "You receive 100% of base price",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SchoolAmber
                            )
                        }
                    }
                }
            }
        }

        // Financial KPIs Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Total Gross Sales",
                        value = "₹${String.format("%.0f", totalSalesBase + totalPlatformFees)}",
                        subtitle = "${ordersWithItems.size} Orders",
                        icon = Icons.Default.TrendingUp,
                        tint = SchoolNavy,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Your Base Revenue",
                        value = "₹${String.format("%.0f", totalSalesBase)}",
                        subtitle = "Net seller earnings",
                        icon = Icons.Default.Payments,
                        tint = SchoolEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Available Payout",
                        value = "₹${String.format("%.0f", availablePayout)}",
                        subtitle = "Ready to withdraw",
                        icon = Icons.Default.AccountBalance,
                        tint = Color(0xFF2563EB),
                        actionLabel = if (availablePayout > 0) "Withdraw" else null,
                        onAction = { showPayoutDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "SchoolDressHub Fee",
                        value = "₹${String.format("%.0f", totalPlatformFees)}",
                        subtitle = "${String.format("%.0f", platformMarginPercentage)}% margin collected",
                        icon = Icons.Default.Storefront,
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recent Orders to Fulfill
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            text = "Orders to Fulfill (${ordersWithItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Live Order Updates",
                            fontSize = 10.sp,
                            color = SchoolEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (ordersWithItems.isEmpty()) {
                        Text("No orders placed yet.", fontSize = 12.sp, color = Slate600)
                    } else {
                        ordersWithItems.forEach { orderWithItems ->
                            val order = orderWithItems.order
                            val myItems = orderWithItems.items.filter { it.sellerId == user.id }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate100)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Order ${order.orderNumber}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Slate900
                                        )
                                        StatusPill(status = order.orderStatus)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Customer: ${order.customerName} (${order.customerPhone})",
                                        fontSize = 11.sp,
                                        color = Slate700
                                    )
                                    Text(
                                        text = "Address: ${order.deliveryAddress}, ${order.deliveryCity}",
                                        fontSize = 10.sp,
                                        color = Slate600
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    myItems.forEach { item ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "• ${item.productName} (Size ${item.selectedSize}) x ${item.quantity}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SchoolNavy
                                            )
                                            Text(
                                                text = "Payout: ₹${(item.unitBasePrice * item.quantity).toInt()}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate900
                                            )
                                        }
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)

                                    // Seller Action buttons to update status
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (order.orderStatus == OrderStatus.PLACED) {
                                            Button(
                                                onClick = {
                                                    onUpdateOrderStatus(order.id, OrderStatus.CONFIRMED, order.customerId)
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Confirm Order", fontSize = 11.sp)
                                            }
                                        }

                                        if (order.orderStatus == OrderStatus.CONFIRMED) {
                                            Button(
                                                onClick = {
                                                    onUpdateOrderStatus(order.id, OrderStatus.PACKED, order.customerId)
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Mark Packed", fontSize = 11.sp)
                                            }
                                        }

                                        if (order.orderStatus == OrderStatus.PACKED) {
                                            Button(
                                                onClick = {
                                                    onUpdateOrderStatus(order.id, OrderStatus.SHIPPED, order.customerId)
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = SchoolEmerald),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Ship / Handover", fontSize = 11.sp)
                                            }
                                        }

                                        if (order.orderStatus == OrderStatus.SHIPPED || order.orderStatus == OrderStatus.OUT_FOR_DELIVERY) {
                                            Button(
                                                onClick = {
                                                    onUpdateOrderStatus(order.id, OrderStatus.DELIVERED, order.customerId)
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = SchoolEmerald),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Mark Delivered", fontSize = 11.sp)
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

        // Seller Products & Inventory Management
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            text = "My Listed Products (${products.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        OutlinedButton(
                            onClick = onAddNewProduct,
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("+ Add Uniform", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (products.isEmpty()) {
                        Text("No products listed yet. Click 'Add Product' to list school uniforms.", fontSize = 12.sp, color = Slate600)
                    } else {
                        products.forEach { prod ->
                            val marginAmt = prod.basePrice * platformMarginPercentage / 100.0
                            val customerPrice = prod.basePrice + marginAmt

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate100)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = prod.imageUrl,
                                        contentDescription = prod.name,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                                        Text(prod.schoolName, fontSize = 10.sp, color = SchoolNavy)
                                        Text(
                                            text = "Your Base: ₹${prod.basePrice.toInt()} | Customer: ₹${customerPrice.toInt()}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Slate800
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Stock adjust
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text("Stock: ${prod.stockQuantity}", fontSize = 11.sp, color = Slate700)
                                            TextButton(
                                                onClick = { onUpdateStock(prod, prod.stockQuantity + 10) },
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                            ) {
                                                Text("+10", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = if (prod.isActive) "Active" else "Hidden",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (prod.isActive) SchoolEmerald else Slate600
                                        )
                                        Switch(
                                            checked = prod.isActive,
                                            onCheckedChange = { onToggleActive(prod) },
                                            colors = SwitchDefaults.colors(checkedThumbColor = SchoolEmerald)
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

    // Payout Request Dialog
    if (showPayoutDialog) {
        var payoutAmountInput by remember { mutableStateOf(availablePayout.toInt().toString()) }
        var bankAccount by remember { mutableStateOf("918237192837") }
        var ifsc by remember { mutableStateOf("HDFC0001234") }

        Dialog(onDismissRequest = { showPayoutDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Request Seller Payout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SchoolNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Available Balance: ₹${String.format("%.2f", availablePayout)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SchoolEmerald
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = payoutAmountInput,
                        onValueChange = { payoutAmountInput = it },
                        label = { Text("Payout Amount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bankAccount,
                        onValueChange = { bankAccount = it },
                        label = { Text("Bank Account Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ifsc,
                        onValueChange = { ifsc = it },
                        label = { Text("IFSC Code") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showPayoutDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amt = payoutAmountInput.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    onRequestPayout(amt, bankAccount, ifsc)
                                    showPayoutDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                        ) {
                            Text("Submit Request")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
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
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Slate900)
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(subtitle, fontSize = 9.sp, color = Slate600)
                if (actionLabel != null && onAction != null) {
                    Text(
                        text = actionLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tint,
                        modifier = Modifier.clickable { onAction() }
                    )
                }
            }
        }
    }
}
