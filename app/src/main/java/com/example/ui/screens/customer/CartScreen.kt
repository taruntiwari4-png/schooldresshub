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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import com.example.ui.components.VouchersDialog
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItemWithProduct
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
fun CartScreen(
    cartItems: List<CartItemWithProduct>,
    platformMarginPercentage: Double,
    deliveryFee: Double,
    freeDeliveryThreshold: Double,
    couponCode: String,
    discountAmount: Double,
    couponMessage: String?,
    onQuantityChange: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onApplyCoupon: (String) -> Unit,
    onRemoveCoupon: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    var couponInput by remember { mutableStateOf(couponCode) }
    var showVouchersDialog by remember { mutableStateOf(false) }

    val baseTotal = cartItems.sumOf { it.product.basePrice * it.cartItem.quantity }
    val platformFeeTotal = cartItems.sumOf { (it.product.basePrice * platformMarginPercentage / 100.0) * it.cartItem.quantity }
    val customerSubtotal = baseTotal + platformFeeTotal
    val isFreeDeliveryAll = deliveryFee == 0.0 || freeDeliveryThreshold == 0.0
    val effectiveDelivery = if (isFreeDeliveryAll || customerSubtotal >= freeDeliveryThreshold || cartItems.isEmpty()) 0.0 else deliveryFee
    val grandTotal = (customerSubtotal + effectiveDelivery - discountAmount).coerceAtLeast(0.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Cart Header
        Surface(
            color = SchoolNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                    Text(
                        text = "My Uniform Cart (${cartItems.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                if (cartItems.isNotEmpty()) {
                    Text(
                        text = "Total: ₹${String.format("%.2f", grandTotal)}",
                        fontWeight = FontWeight.Bold,
                        color = SchoolAmber
                    )
                }
            }
        }

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Slate100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Slate600,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Uniform Cart is Empty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore dresses, blazers, shoes and bags for your school.",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onContinueShopping,
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Start Shopping")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Free Delivery Progress Banner
                item {
                    val remainingForFree = freeDeliveryThreshold - customerSubtotal
                    val isFreeUnlocked = isFreeDeliveryAll || remainingForFree <= 0
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isFreeUnlocked) SchoolEmeraldLight else Color(0xFFFEF3C7)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isFreeUnlocked) SchoolEmerald else Color(0xFFD97706),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isFreeDeliveryAll) {
                                    "🎉 FREE Delivery on All School Uniform Orders! No minimum cart value."
                                } else if (isFreeUnlocked) {
                                    "🎉 Congratulations! You unlocked FREE Uniform Delivery!"
                                } else {
                                    "Add ₹${String.format("%.2f", remainingForFree)} more to get FREE Delivery!"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isFreeUnlocked) Color(0xFF065F46) else Color(0xFF92400E)
                            )
                        }
                    }
                }

                // Cart Item Cards
                items(cartItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Product Image
                            AsyncImage(
                                model = item.product.imageUrl,
                                contentDescription = item.product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate100)
                            )

                            // Product details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.product.schoolName,
                                    fontSize = 11.sp,
                                    color = SchoolNavy,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Seller: ${item.product.sellerName}",
                                    fontSize = 10.sp,
                                    color = Slate600
                                )
                                Text(
                                    text = "Selected Size: ${item.cartItem.selectedSize}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Slate800
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "₹${String.format("%.2f", item.pricing.customerPrice * item.cartItem.quantity)}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SchoolNavy
                                        )
                                        Text(
                                            text = "₹${String.format("%.2f", item.pricing.customerPrice)} / unit",
                                            fontSize = 9.sp,
                                            color = Slate600
                                        )
                                    }

                                    // Stepper
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Slate100,
                                            modifier = Modifier.clickable {
                                                onQuantityChange(item.cartItem.id, item.cartItem.quantity - 1)
                                            }
                                        ) {
                                            Box(modifier = Modifier.size(26.dp), contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(14.dp))
                                            }
                                        }

                                        Text(
                                            text = "${item.cartItem.quantity}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )

                                        Surface(
                                            shape = CircleShape,
                                            color = Slate100,
                                            modifier = Modifier.clickable {
                                                onQuantityChange(item.cartItem.id, item.cartItem.quantity + 1)
                                            }
                                        ) {
                                            Box(modifier = Modifier.size(26.dp), contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(14.dp))
                                            }
                                        }

                                        IconButton(
                                            onClick = { onRemoveItem(item.cartItem.id) },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Remove",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Coupons & Vouchers Section
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
                                        text = "Coupons & Vouchers",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SchoolNavy
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SchoolNavy.copy(alpha = 0.08f),
                                    modifier = Modifier.clickable { showVouchersDialog = true }
                                ) {
                                    Text(
                                        text = "View All (5)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SchoolNavy,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // If coupon is applied, show clear card
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
                                                    text = "Saved ₹${String.format("%.2f", discountAmount)} on your order",
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
                                                .padding(6.dp)
                                        )
                                    }
                                }
                            } else {
                                // Input Box
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = couponInput,
                                        onValueChange = { couponInput = it.uppercase() },
                                        modifier = Modifier.weight(1f),
                                        placeholder = { Text("Enter Code: SCHOOL10, UNIFORM100", fontSize = 11.sp) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = SchoolNavy,
                                            unfocusedBorderColor = Slate200
                                        )
                                    )
                                    Button(
                                        onClick = { onApplyCoupon(couponInput) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Apply", fontSize = 11.sp)
                                    }
                                }
                            }

                            if (couponMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = couponMessage,
                                    fontSize = 11.sp,
                                    color = if (discountAmount > 0) SchoolEmerald else Color(0xFFDC2626),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Online Payment Discount Teaser Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A)))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SchoolAmber,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Column {
                                Text(
                                    text = "Extra 5% Instant Discount on Online Payments",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "Pay using UPI (GPay/PhonePe/Paytm) or Net Banking at checkout to save an additional 5% instantly!",
                                    fontSize = 10.sp,
                                    color = Color(0xFFB45309),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                // Price Breakdown Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Order Price Breakdown",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Seller Base Price Total", fontSize = 12.sp, color = Slate700)
                                Text("₹${String.format("%.2f", baseTotal)}", fontSize = 12.sp, color = Slate900)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("SchoolDressHub Fee (${String.format("%.1f", platformMarginPercentage)}%)", fontSize = 12.sp, color = Slate700)
                                Text("+ ₹${String.format("%.2f", platformFeeTotal)}", fontSize = 12.sp, color = SchoolNavy)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Delivery Charges", fontSize = 12.sp, color = Slate700)
                                if (effectiveDelivery == 0.0) {
                                    Text("FREE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
                                } else {
                                    Text("+ ₹${String.format("%.2f", effectiveDelivery)}", fontSize = 12.sp, color = Slate900)
                                }
                            }

                            if (discountAmount > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Coupon Discount ($couponCode)", fontSize = 12.sp, color = SchoolEmerald)
                                    Text("- ₹${String.format("%.2f", discountAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Slate200)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Final Payable Amount:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                Text(
                                    "₹${String.format("%.2f", grandTotal)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SchoolNavy
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Sticky Checkout Bar
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
                        Text("Grand Total", fontSize = 11.sp, color = Slate600)
                        Text(
                            "₹${String.format("%.2f", grandTotal)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SchoolNavy
                        )
                    }

                    Button(
                        onClick = onProceedToCheckout,
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Proceed to Checkout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
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
