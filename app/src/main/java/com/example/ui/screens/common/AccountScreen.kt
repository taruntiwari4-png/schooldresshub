package com.example.ui.screens.common

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
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
import com.example.data.model.UserEntity
import com.example.data.model.UserRoles
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
fun AccountScreen(
    user: UserEntity,
    platformMarginPercentage: Double,
    onSwitchRole: (String) -> Unit,
    onViewMyOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Header Card
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
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SchoolAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (user.role == UserRoles.WHOLESALER || user.role == UserRoles.SHOPKEEPER) {
                                VerifiedBadge(user.role)
                            }
                        }
                        Text(
                            text = "Role: ${user.role} • ${user.city}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = user.phone,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Switch Role Card (Demo / Marketplace tester)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = SchoolNavy)
                        Text(
                            text = "Switch Marketplace Role",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SchoolNavy
                        )
                    }
                    Text(
                        text = "Experience the app from any of the 4 user perspectives:",
                        fontSize = 11.sp,
                        color = Slate600
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val roles = listOf(
                        Triple(UserRoles.CUSTOMER, "Customer (Priya Sharma)", "Browse, compare prices, order uniforms"),
                        Triple(UserRoles.WHOLESALER, "Wholesaler (ABC Depot)", "Bulk uniforms, base prices, wholesale sales"),
                        Triple(UserRoles.SHOPKEEPER, "Shopkeeper (City Store)", "Retail uniforms, stock updates, local orders"),
                        Triple(UserRoles.ADMIN, "Admin (Director)", "Manage ${String.format("%.1f", platformMarginPercentage)}% margin, approve sellers, platform stats")
                    )

                    roles.forEach { (roleKey, title, desc) ->
                        val isCurrent = user.role == roleKey
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) Color(0xFFEFF6FF) else Slate100)
                                .clickable { onSwitchRole(roleKey) }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = if (isCurrent) SchoolNavy else Slate900
                                    )
                                    Text(text = desc, fontSize = 10.sp, color = Slate600)
                                }
                                if (isCurrent) {
                                    Text("Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolEmerald)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Links for Customer
        if (user.role == UserRoles.CUSTOMER) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onViewMyOrders() }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = SchoolNavy)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("My Orders & Tracking", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                Text("View uniform orders and delivery progress", fontSize = 11.sp, color = Slate600)
                            }
                        }
                    }
                }
            }
        }

        // Marketplace Guarantees
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SchoolDressHub Policies",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PolicyItem(
                        icon = Icons.Default.Info,
                        title = "Transparent ${String.format("%.1f", platformMarginPercentage)}% Margin",
                        desc = "We charge only a configurable platform fee on sellers' base prices. No hidden costs."
                    )
                    PolicyItem(
                        icon = Icons.Default.AssignmentReturn,
                        title = "7-Day Uniform Size Exchange",
                        desc = "If sizes do not fit your child, exchange with the seller within 7 days."
                    )
                    PolicyItem(
                        icon = Icons.Default.Security,
                        title = "School Dress Compliance",
                        desc = "All dresses and uniforms are checked against official school guidelines."
                    )
                }
            }
        }

        // App Branding & Version
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SchoolDressHub",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = SchoolNavy
                )
                Text(
                    text = "“Connect. Compare. Buy School Dresses at Better Prices.”",
                    fontSize = 11.sp,
                    color = Slate600
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version 1.0.0 • Multi-Seller Platform",
                    fontSize = 10.sp,
                    color = Slate600
                )
            }
        }
    }
}

@Composable
private fun PolicyItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = SchoolNavy, modifier = Modifier.size(18.dp))
        Column {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
            Text(desc, fontSize = 10.sp, color = Slate600)
        }
    }
}
