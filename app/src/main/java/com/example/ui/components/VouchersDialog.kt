package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AvailableVouchers
import com.example.data.model.VoucherDiscountType
import com.example.data.model.VoucherItem
import com.example.ui.theme.SchoolAmber
import com.example.ui.theme.SchoolEmerald
import com.example.ui.theme.SchoolEmeraldLight
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun VouchersDialog(
    cartSubtotal: Double,
    appliedCouponCode: String,
    onApplyVoucher: (VoucherItem) -> Unit,
    onApplyCustomCode: (String) -> Unit,
    onRemoveVoucher: () -> Unit,
    onDismiss: () -> Unit
) {
    var customCodeInput by remember { mutableStateOf("") }
    val vouchers = AvailableVouchers.list

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .testTag("vouchers_dialog")
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
                        Surface(
                            shape = CircleShape,
                            color = SchoolEmeraldLight,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = SchoolEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Coupons & Vouchers",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SchoolNavy
                            )
                            Text(
                                text = "Apply to get extra discounts on your order",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_vouchers_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Coupon Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customCodeInput,
                        onValueChange = { customCodeInput = it.uppercase() },
                        placeholder = { Text("Enter Promo / Voucher Code", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_voucher_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Button(
                        onClick = {
                            if (customCodeInput.isNotBlank()) {
                                onApplyCustomCode(customCodeInput.trim())
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                        shape = RoundedCornerShape(8.dp),
                        enabled = customCodeInput.isNotBlank(),
                        modifier = Modifier.testTag("apply_custom_voucher_btn")
                    ) {
                        Text("Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Currently applied banner
                if (appliedCouponCode.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = SchoolEmeraldLight)
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
                                Text(
                                    text = "Coupon '$appliedCouponCode' Applied",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SchoolEmerald
                                )
                            }
                            Text(
                                text = "Remove",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFDC2626),
                                modifier = Modifier
                                    .clickable { onRemoveVoucher() }
                                    .padding(4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                HorizontalDivider(color = Slate200)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "AVAILABLE VOUCHERS (${vouchers.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(8.dp))

                // List of Vouchers
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(vouchers) { voucher ->
                        val isApplied = appliedCouponCode.equals(voucher.code, ignoreCase = true)
                        val savings = voucher.calculateSavings(cartSubtotal)
                        val isEligible = cartSubtotal >= voucher.minCartValue

                        VoucherTicketCard(
                            voucher = voucher,
                            isApplied = isApplied,
                            isEligible = isEligible,
                            savings = savings,
                            cartSubtotal = cartSubtotal,
                            onApply = {
                                onApplyVoucher(voucher)
                                onDismiss()
                            },
                            onRemove = {
                                onRemoveVoucher()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoucherTicketCard(
    voucher: VoucherItem,
    isApplied: Boolean,
    isEligible: Boolean,
    savings: Double,
    cartSubtotal: Double,
    onApply: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("voucher_card_${voucher.code}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isApplied) Color(0xFFF0FDF4) else Color.White
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isApplied) SchoolEmerald else Slate200
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Code badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isApplied) SchoolEmerald else SchoolNavy.copy(alpha = 0.08f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isApplied) SchoolEmerald else SchoolNavy.copy(alpha = 0.4f)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = if (isApplied) Color.White else SchoolNavy,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = voucher.code,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = if (isApplied) Color.White else SchoolNavy
                        )
                    }
                }

                // Badge tag (Popular, Best Value, etc.)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (voucher.badge) {
                        "POPULAR" -> Color(0xFFEFF6FF)
                        "BEST VALUE" -> SchoolEmeraldLight
                        "NEW USER" -> Color(0xFFFEF3C7)
                        else -> Slate100
                    }
                ) {
                    Text(
                        text = voucher.badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (voucher.badge) {
                            "POPULAR" -> SchoolNavy
                            "BEST VALUE" -> SchoolEmerald
                            "NEW USER" -> Color(0xFFB45309)
                            else -> Slate800
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = voucher.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Slate900
            )

            Text(
                text = voucher.description,
                fontSize = 11.sp,
                color = Slate600,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Terms & Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (isEligible) {
                        if (savings > 0) {
                            Text(
                                text = "Save ₹${savings.toInt()} on this order",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SchoolEmerald
                            )
                        } else {
                            Text(
                                text = "Applicable on your cart",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    } else {
                        val needed = (voucher.minCartValue - cartSubtotal).toInt()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Slate400, modifier = Modifier.size(12.dp))
                            Text(
                                text = "Add ₹$needed more to unlock",
                                fontSize = 10.sp,
                                color = Color(0xFFD97706),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (isApplied) {
                    Button(
                        onClick = onRemove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("REMOVE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    }
                } else {
                    Button(
                        onClick = onApply,
                        enabled = isEligible,
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("apply_voucher_${voucher.code}")
                    ) {
                        Text("APPLY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
