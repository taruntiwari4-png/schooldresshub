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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.theme.SchoolAmber
import com.example.ui.theme.SchoolEmerald
import com.example.ui.theme.SchoolEmeraldLight
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    orders: List<OrderEntity>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOrderForTracking by remember { mutableStateOf<OrderEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders & Uniform Tracking", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SchoolNavy)
            )
        }
    ) { innerPadding ->
        if (orders.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = Slate600
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Orders Placed Yet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                    Text("When you order uniforms, they will appear here.", fontSize = 12.sp, color = Slate600)
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF8FAFC)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(
                        order = order,
                        onTrackClick = { selectedOrderForTracking = order }
                    )
                }
            }
        }
    }

    // Tracking Dialog
    selectedOrderForTracking?.let { order ->
        OrderTrackingDialog(
            order = order,
            onDismiss = { selectedOrderForTracking = null }
        )
    }
}

@Composable
private fun OrderCard(
    order: OrderEntity,
    onTrackClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(order.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick() },
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
                Column {
                    Text(
                        text = "Order ${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Slate900
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 10.sp,
                        color = Slate600
                    )
                }

                // Status Badge
                StatusPill(status = order.orderStatus)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Slate200)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Delivery: ${order.deliveryCity}", fontSize = 11.sp, color = Slate600)
                    Text("Mode: ${if (order.deliveryType == "HOME_DELIVERY") "Home Delivery" else "Store Pickup"}", fontSize = 11.sp, color = Slate600)
                    Text("Payment: ${order.paymentMethod} (${order.paymentStatus})", fontSize = 11.sp, color = Slate600)
                }

                Column(horizontalAlignment = Alignment.End) {
                    val totalSaved = order.voucherDiscount + order.onlinePaymentDiscount + if (order.voucherDiscount == 0.0) order.discountAmount else 0.0
                    if (totalSaved > 0) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SchoolEmeraldLight,
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Text(
                                "Saved ₹${String.format("%.2f", totalSaved)}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SchoolEmerald,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text("Grand Total", fontSize = 10.sp, color = Slate600)
                    Text(
                        "₹${String.format("%.2f", order.grandTotal)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = SchoolNavy
                    )
                    Text(
                        "(includes ${String.format("%.0f", order.platformMarginPercentage)}% platform fee)",
                        fontSize = 9.sp,
                        color = Slate600
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Slate100,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTrackClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = SchoolNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Track Delivery Progress & Details",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SchoolNavy
                    )
                }
            }
        }
    }
}

@Composable
fun StatusPill(status: String) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.DELIVERED -> Pair(SchoolEmeraldLight, SchoolEmerald)
        OrderStatus.SHIPPED, OrderStatus.OUT_FOR_DELIVERY -> Pair(Color(0xFFEFF6FF), SchoolNavy)
        OrderStatus.CANCELLED -> Pair(Color(0xFFFEE2E2), Color(0xFFDC2626))
        else -> Pair(Color(0xFFFEF3C7), Color(0xFFD97706))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun OrderTrackingDialog(
    order: OrderEntity,
    onDismiss: () -> Unit
) {
    val steps = listOf(
        OrderStatus.PLACED,
        OrderStatus.CONFIRMED,
        OrderStatus.PACKED,
        OrderStatus.SHIPPED,
        OrderStatus.DELIVERED
    )

    val currentStepIndex = steps.indexOf(order.orderStatus).let {
        if (it == -1) {
            if (order.orderStatus == OrderStatus.OUT_FOR_DELIVERY) 3 else 0
        } else it
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tracking ${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SchoolNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Delivery to: ${order.deliveryAddress}, ${order.deliveryCity} - ${order.deliveryPincode}",
                    fontSize = 11.sp,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Timeline
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    steps.forEachIndexed { index, stepName ->
                        val isCompleted = index <= currentStepIndex
                        val isCurrent = index == currentStepIndex

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isCompleted) SchoolEmerald else Slate200),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text("${index + 1}", fontSize = 10.sp, color = Slate600)
                                }
                            }

                            Column {
                                Text(
                                    text = stepName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isCurrent) FontWeight.ExtraBold else if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCompleted) Slate900 else Slate600
                                )
                                if (isCurrent) {
                                    Text("Current Status (${order.estimatedDelivery})", fontSize = 10.sp, color = SchoolEmerald, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Slate200)

                // Cost Summary
                Text("Price Details:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Base Products Subtotal:", fontSize = 11.sp, color = Slate700)
                    Text("₹${String.format("%.2f", order.subtotalBasePrice)}", fontSize = 11.sp, color = Slate900)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SchoolDressHub Platform Fee (${String.format("%.0f", order.platformMarginPercentage)}%):", fontSize = 11.sp, color = Slate700)
                    Text("₹${String.format("%.2f", order.totalPlatformFee)}", fontSize = 11.sp, color = SchoolNavy)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delivery Fee:", fontSize = 11.sp, color = Slate700)
                    if (order.deliveryFee == 0.0) {
                        Text("FREE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
                    } else {
                        Text("₹${String.format("%.2f", order.deliveryFee)}", fontSize = 11.sp, color = Slate900)
                    }
                }
                if (order.voucherDiscount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Voucher Discount (${order.voucherCode.ifBlank { "Voucher" }}):", fontSize = 11.sp, color = SchoolEmerald, fontWeight = FontWeight.SemiBold)
                        Text("- ₹${String.format("%.2f", order.voucherDiscount)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
                    }
                } else if (order.discountAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Coupon Discount:", fontSize = 11.sp, color = SchoolEmerald, fontWeight = FontWeight.SemiBold)
                        Text("- ₹${String.format("%.2f", order.discountAmount)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
                    }
                }
                if (order.onlinePaymentDiscount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Online Payment Discount (5% OFF):", fontSize = 11.sp, color = SchoolEmerald, fontWeight = FontWeight.SemiBold)
                        Text("- ₹${String.format("%.2f", order.onlinePaymentDiscount)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Paid:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    Text("₹${String.format("%.2f", order.grandTotal)}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = SchoolNavy)
                }
            }
        }
    }
}
