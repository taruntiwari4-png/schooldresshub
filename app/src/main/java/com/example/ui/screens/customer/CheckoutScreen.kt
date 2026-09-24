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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PhoneAndroid
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItemWithProduct
import com.example.data.model.UserEntity
import com.example.ui.components.VouchersDialog
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    user: UserEntity,
    cartItems: List<CartItemWithProduct>,
    platformMarginPercentage: Double,
    deliveryFee: Double,
    freeDeliveryThreshold: Double,
    discountAmount: Double,
    couponCode: String = "",
    onApplyCoupon: (String) -> Unit = {},
    onRemoveCoupon: () -> Unit = {},
    onBackClick: () -> Unit,
    onPlaceOrder: (String, String, String, String, String, Double, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var address by remember { mutableStateOf(user.address.ifBlank { "Flat 402, Shanti Vihar, Rohini Sector 14" }) }
    var city by remember { mutableStateOf(user.city.ifBlank { "New Delhi" }) }
    var pincode by remember { mutableStateOf(user.pincode.ifBlank { "110085" }) }
    var deliveryType by remember { mutableStateOf("HOME_DELIVERY") } // or STORE_PICKUP
    var paymentMethod by remember { mutableStateOf("UPI") } // UPI, CASH_ON_DELIVERY, ONLINE
    var showVouchersDialog by remember { mutableStateOf(false) }

    val baseTotal = cartItems.sumOf { it.product.basePrice * it.cartItem.quantity }
    val platformFeeTotal = cartItems.sumOf { (it.product.basePrice * platformMarginPercentage / 100.0) * it.cartItem.quantity }
    val customerSubtotal = baseTotal + platformFeeTotal
    val isFreeDeliveryAll = deliveryFee == 0.0 || freeDeliveryThreshold == 0.0
    val effectiveDelivery = if (deliveryType == "STORE_PICKUP" || isFreeDeliveryAll || customerSubtotal >= freeDeliveryThreshold) 0.0 else deliveryFee

    // 5% Instant Discount on Online Payment Methods (UPI, Cards, Net Banking)
    val isOnlinePayment = paymentMethod == "UPI" || paymentMethod == "ONLINE"
    val potentialOnlineDiscount = customerSubtotal * 0.05
    val onlinePaymentDiscount = if (isOnlinePayment) potentialOnlineDiscount else 0.0
    val totalDiscount = discountAmount + onlinePaymentDiscount
    val grandTotal = (customerSubtotal + effectiveDelivery - totalDiscount).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout & Order", fontWeight = FontWeight.Bold, color = Color.White) },
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
                        Text("Payable Amount", fontSize = 11.sp, color = Slate600)
                        Text(
                            "₹${String.format("%.2f", grandTotal)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SchoolNavy
                        )
                        if (totalDiscount > 0) {
                            Text(
                                "Total saved: ₹${String.format("%.2f", totalDiscount)}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SchoolEmerald
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onPlaceOrder(
                                address,
                                city,
                                pincode,
                                deliveryType,
                                paymentMethod,
                                onlinePaymentDiscount,
                                discountAmount,
                                couponCode
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("confirm_order_btn")
                    ) {
                        Text("Confirm & Place Order", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            // Delivery Mode Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Choose Delivery Mode",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Home Delivery Card
                            val isHome = deliveryType == "HOME_DELIVERY"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isHome) Color(0xFFEFF6FF) else Slate100)
                                    .clickable { deliveryType = "HOME_DELIVERY" }
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.LocalShipping,
                                            contentDescription = null,
                                            tint = if (isHome) SchoolNavy else Slate600,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            "Home Delivery",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isHome) SchoolNavy else Slate800
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (effectiveDelivery == 0.0) "FREE Doorstep Delivery (2-3 Days)" else "₹${deliveryFee.toInt()} Delivery (2-3 Days)",
                                        fontSize = 10.sp,
                                        color = if (effectiveDelivery == 0.0) SchoolEmerald else Slate600,
                                        fontWeight = if (effectiveDelivery == 0.0) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }

                            // Store Pickup Card
                            val isStore = deliveryType == "STORE_PICKUP"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isStore) Color(0xFFEFF6FF) else Slate100)
                                    .clickable { deliveryType = "STORE_PICKUP" }
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Store,
                                            contentDescription = null,
                                            tint = if (isStore) SchoolNavy else Slate600,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            "Store Pickup",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isStore) SchoolNavy else Slate800
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("FREE from Seller Store", fontSize = 10.sp, color = SchoolEmerald, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Customer Contact & Delivery Address
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (deliveryType == "HOME_DELIVERY") "Delivery Address" else "Contact Information",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Recipient: ${user.name} (${user.phone})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate800)

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("House / Flat, Street / Area") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SchoolNavy,
                                unfocusedBorderColor = Slate200
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SchoolNavy,
                                    unfocusedBorderColor = Slate200
                                )
                            )
                            OutlinedTextField(
                                value = pincode,
                                onValueChange = { pincode = it },
                                label = { Text("Pincode") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SchoolNavy,
                                    unfocusedBorderColor = Slate200
                                )
                            )
                        }
                    }
                }
            }

            // Offers & Vouchers Card in Checkout
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = SchoolNavy, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Offers & Vouchers",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SchoolNavy.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .clickable { showVouchersDialog = true }
                                    .testTag("checkout_view_vouchers_btn")
                            ) {
                                Text(
                                    text = if (discountAmount > 0) "Change Voucher" else "View Vouchers (5)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SchoolNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (discountAmount > 0 && couponCode.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SchoolEmeraldLight,
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SchoolEmerald))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = SchoolEmerald, modifier = Modifier.size(16.dp))
                                        Column {
                                            Text(
                                                text = "Voucher '$couponCode' Applied",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = SchoolEmerald
                                            )
                                            Text(
                                                text = "You save ₹${String.format("%.2f", discountAmount)} with this voucher",
                                                fontSize = 10.sp,
                                                color = Color(0xFF065F46)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Remove",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFFDC2626),
                                        modifier = Modifier
                                            .clickable { onRemoveCoupon() }
                                            .padding(4.dp)
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showVouchersDialog = true }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.LocalOffer, contentDescription = null, tint = SchoolAmber, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Tap to apply school vouchers and save up to ₹250",
                                    fontSize = 11.sp,
                                    color = Slate600
                                )
                            }
                        }
                    }
                }
            }

            // Payment Options with 5% Instant Online Discount Highlight
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
                                text = "Payment Method",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )

                            // Online payment discount badge
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SchoolEmeraldLight
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = SchoolEmerald, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "5% OFF Online",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SchoolEmerald
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Choose an online payment method to get an extra 5% instant discount!",
                            fontSize = 11.sp,
                            color = Slate600
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // UPI (with 5% instant discount)
                        PaymentOptionRow(
                            title = "UPI (Instant & Secure)",
                            subtitle = "Google Pay, PhonePe, Paytm, BHIM • Saves ₹${String.format("%.2f", potentialOnlineDiscount)}",
                            icon = Icons.Default.PhoneAndroid,
                            selected = paymentMethod == "UPI",
                            badge = "5% OFF",
                            onSelect = { paymentMethod = "UPI" }
                        )

                        // Cards & Net Banking (with 5% instant discount)
                        PaymentOptionRow(
                            title = "Cards & Net Banking",
                            subtitle = "Debit / Credit Cards & Net Banking • Saves ₹${String.format("%.2f", potentialOnlineDiscount)}",
                            icon = Icons.Default.CreditCard,
                            selected = paymentMethod == "ONLINE",
                            badge = "5% OFF",
                            onSelect = { paymentMethod = "ONLINE" }
                        )

                        // Cash on Delivery
                        PaymentOptionRow(
                            title = "Cash on Delivery (COD)",
                            subtitle = "Pay cash when uniform arrives (Online discount not applicable)",
                            icon = Icons.Default.Money,
                            selected = paymentMethod == "CASH_ON_DELIVERY",
                            badge = null,
                            onSelect = { paymentMethod = "CASH_ON_DELIVERY" }
                        )
                    }
                }
            }

            // Order Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Items in Order (${cartItems.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.product.name} (Size ${item.cartItem.selectedSize})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate900
                                    )
                                    Text(
                                        text = "${item.product.schoolName} • Seller: ${item.product.sellerName}",
                                        fontSize = 9.sp,
                                        color = Slate600
                                    )
                                }
                                Text(
                                    text = "Qty ${item.cartItem.quantity} × ₹${item.pricing.customerPrice.toInt()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SchoolNavy
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Base Product Price", fontSize = 11.sp, color = Slate700)
                            Text("₹${String.format("%.2f", baseTotal)}", fontSize = 11.sp, color = Slate900)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("SchoolDressHub Platform Fee (${String.format("%.1f", platformMarginPercentage)}%)", fontSize = 11.sp, color = Slate700)
                            Text("+ ₹${String.format("%.2f", platformFeeTotal)}", fontSize = 11.sp, color = SchoolNavy)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Fee", fontSize = 11.sp, color = Slate700)
                            if (effectiveDelivery == 0.0) {
                                Text("FREE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
                            } else {
                                Text("+ ₹${String.format("%.2f", effectiveDelivery)}", fontSize = 11.sp, color = Slate900)
                            }
                        }

                        // Voucher discount row
                        if (discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Voucher Discount (${couponCode.ifBlank { "Voucher" }})", fontSize = 11.sp, color = SchoolEmerald, fontWeight = FontWeight.SemiBold)
                                Text("- ₹${String.format("%.2f", discountAmount)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
                            }
                        }

                        // Online payment discount row
                        if (onlinePaymentDiscount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = SchoolEmerald, modifier = Modifier.size(14.dp))
                                    Text("Online Payment Discount (5% OFF)", fontSize = 11.sp, color = SchoolEmerald, fontWeight = FontWeight.SemiBold)
                                }
                                Text("- ₹${String.format("%.2f", onlinePaymentDiscount)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
                            }
                        }

                        // Savings summary banner
                        if (totalDiscount > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SchoolEmeraldLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "🎉 Total Savings on this order: ₹${String.format("%.2f", totalDiscount)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF065F46)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Payable:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            Text(
                                "₹${String.format("%.2f", grandTotal)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SchoolNavy
                            )
                        }
                    }
                }
            }
        }

        if (showVouchersDialog) {
            VouchersDialog(
                cartSubtotal = customerSubtotal,
                appliedCouponCode = couponCode,
                onApplyVoucher = { voucher ->
                    onApplyCoupon(voucher.code)
                },
                onApplyCustomCode = { code ->
                    onApplyCoupon(code)
                },
                onRemoveVoucher = {
                    onRemoveCoupon()
                },
                onDismiss = {
                    showVouchersDialog = false
                }
            )
        }
    }
}

// 5-parameter backward compatibility overload
@Composable
fun CheckoutScreen(
    user: UserEntity,
    cartItems: List<CartItemWithProduct>,
    platformMarginPercentage: Double,
    deliveryFee: Double,
    freeDeliveryThreshold: Double,
    discountAmount: Double,
    onBackClick: () -> Unit,
    onPlaceOrder: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    CheckoutScreen(
        user = user,
        cartItems = cartItems,
        platformMarginPercentage = platformMarginPercentage,
        deliveryFee = deliveryFee,
        freeDeliveryThreshold = freeDeliveryThreshold,
        discountAmount = discountAmount,
        couponCode = "",
        onApplyCoupon = {},
        onRemoveCoupon = {},
        onBackClick = onBackClick,
        onPlaceOrder = { addr, city, pincode, dType, pMethod, _, _, _ ->
            onPlaceOrder(addr, city, pincode, dType, pMethod)
        },
        modifier = modifier
    )
}

@Composable
private fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    badge: String? = null,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = SchoolNavy)
        )
        Icon(icon, contentDescription = null, tint = SchoolNavy, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                if (badge != null) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = SchoolEmeraldLight
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = SchoolEmerald,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            Text(subtitle, fontSize = 10.sp, color = Slate600)
        }
    }
}
