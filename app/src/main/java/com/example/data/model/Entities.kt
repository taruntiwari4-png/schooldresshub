package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.math.RoundingMode

object UserRoles {
    const val CUSTOMER = "CUSTOMER"
    const val WHOLESALER = "WHOLESALER"
    const val SHOPKEEPER = "SHOPKEEPER"
    const val ADMIN = "ADMIN"
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // CUSTOMER, WHOLESALER, SHOPKEEPER, ADMIN
    val businessName: String = "",
    val city: String = "New Delhi",
    val address: String = "",
    val pincode: String = "110001",
    val gstNumber: String = "",
    val isVerified: Boolean = false,
    val rating: Double = 4.8,
    val totalOrders: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val board: String, // CBSE, ICSE, State Board
    val city: String,
    val area: String,
    val uniformColorDesc: String
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val displayOrder: Int
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val sellerId: String,
    val sellerName: String,
    val sellerRole: String, // WHOLESALER, SHOPKEEPER
    val sellerCity: String,
    val sellerDistanceKm: Double = 2.5,
    val schoolId: String,
    val schoolName: String,
    val name: String,
    val category: String,
    val description: String,
    val brand: String,
    val gender: String, // Boys, Girls, Unisex
    val classGroup: String, // Class 1-5, Class 6-8, Class 9-12
    val color: String,
    val availableSizes: String, // e.g. "26, 28, 30, 32, 34, 36"
    val material: String,
    val basePrice: Double, // Seller's entered base price
    val stockQuantity: Int,
    val imageUrl: String,
    val isApproved: Boolean = true,
    val isActive: Boolean = true,
    val rating: Double = 4.7,
    val reviewCount: Int = 18,
    val isBestPrice: Boolean = false,
    val deliveryDays: Int = 2,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun calculatePricing(platformMarginPercentage: Double): PricingCalculation {
        return calculateProductPricing(basePrice, platformMarginPercentage)
    }
}

data class PricingCalculation(
    val basePrice: Double,
    val marginPercentage: Double,
    val marginAmount: Double,
    val customerPrice: Double
) {
    val sellerPayout: Double get() = basePrice
    val platformEarnings: Double get() = marginAmount
}

fun calculateProductPricing(basePrice: Double, marginPercentage: Double): PricingCalculation {
    val margin = BigDecimal(basePrice * marginPercentage / 100.0)
        .setScale(2, RoundingMode.HALF_UP)
        .toDouble()
    val finalPrice = BigDecimal(basePrice + margin)
        .setScale(2, RoundingMode.HALF_UP)
        .toDouble()
    return PricingCalculation(
        basePrice = basePrice,
        marginPercentage = marginPercentage,
        marginAmount = margin,
        customerPrice = finalPrice
    )
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val productId: String,
    val selectedSize: String,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)

data class CartItemWithProduct(
    val cartItem: CartItemEntity,
    val product: ProductEntity,
    val pricing: PricingCalculation
)

object OrderStatus {
    const val PLACED = "Order Placed"
    const val CONFIRMED = "Seller Confirmed"
    const val PROCESSING = "Processing"
    const val PACKED = "Packed"
    const val SHIPPED = "Shipped"
    const val OUT_FOR_DELIVERY = "Out for Delivery"
    const val DELIVERED = "Delivered"
    const val CANCELLED = "Cancelled"
    const val RETURNED = "Returned"
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val deliveryCity: String,
    val deliveryPincode: String,
    val deliveryType: String, // "HOME_DELIVERY", "STORE_PICKUP"
    val paymentMethod: String, // "CASH_ON_DELIVERY", "UPI", "ONLINE"
    val paymentStatus: String, // "PAID", "PENDING"
    val subtotalBasePrice: Double,
    val platformMarginPercentage: Double,
    val totalPlatformFee: Double,
    val deliveryFee: Double,
    val discountAmount: Double,
    val grandTotal: Double,
    val orderStatus: String,
    val sellerIds: String, // Comma-separated seller IDs for seller filtering
    val estimatedDelivery: String,
    val voucherCode: String = "",
    val voucherDiscount: Double = 0.0,
    val onlinePaymentDiscount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String,
    val productId: String,
    val productName: String,
    val schoolName: String,
    val sellerId: String,
    val sellerName: String,
    val selectedSize: String,
    val quantity: Int,
    val unitBasePrice: Double,
    val unitPlatformFee: Double,
    val unitFinalPrice: Double,
    val itemStatus: String = OrderStatus.PLACED
)

data class OrderWithItems(
    val order: OrderEntity,
    val items: List<OrderItemEntity>
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val customerId: String,
    val customerName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "platform_settings")
data class PlatformSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val platformMarginPercentage: Double = 5.0,
    val defaultDeliveryFee: Double = 0.0, // FREE Delivery for all customers
    val freeDeliveryThreshold: Double = 0.0, // FREE delivery on all orders
    val returnPeriodDays: Int = 7,
    val sellerApprovalRequired: Boolean = false,
    val productApprovalRequired: Boolean = false
)

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sellerId: String,
    val sellerName: String,
    val amount: Double,
    val status: String, // "PENDING", "APPROVED", "REJECTED"
    val bankAccount: String,
    val ifscCode: String,
    val requestDate: Long = System.currentTimeMillis(),
    val processedDate: Long? = null
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // "ORDER", "PAYOUT", "ALERT", "PROMO"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

// --- Vouchers & Promo System ---
enum class VoucherDiscountType {
    PERCENTAGE,
    FLAT
}

data class VoucherItem(
    val code: String,
    val title: String,
    val description: String,
    val discountType: VoucherDiscountType,
    val discountValue: Double,
    val minCartValue: Double = 0.0,
    val maxDiscount: Double = 1000.0,
    val badge: String = "ACTIVE"
) {
    fun calculateSavings(cartSubtotal: Double): Double {
        if (cartSubtotal < minCartValue) return 0.0
        return when (discountType) {
            VoucherDiscountType.FLAT -> discountValue.coerceAtMost(cartSubtotal)
            VoucherDiscountType.PERCENTAGE -> {
                val calc = (cartSubtotal * discountValue / 100.0)
                calc.coerceAtMost(maxDiscount).coerceAtMost(cartSubtotal)
            }
        }
    }
}

object AvailableVouchers {
    val list = listOf(
        VoucherItem(
            code = "SCHOOL10",
            title = "10% OFF on School Uniforms",
            description = "Get 10% instant discount up to ₹150 on your school uniform order.",
            discountType = VoucherDiscountType.PERCENTAGE,
            discountValue = 10.0,
            minCartValue = 499.0,
            maxDiscount = 150.0,
            badge = "POPULAR"
        ),
        VoucherItem(
            code = "UNIFORM100",
            title = "Flat ₹100 OFF Full Uniform Set",
            description = "Save flat ₹100 on orders above ₹800 across all schools.",
            discountType = VoucherDiscountType.FLAT,
            discountValue = 100.0,
            minCartValue = 800.0,
            badge = "BEST VALUE"
        ),
        VoucherItem(
            code = "FIRSTBUY",
            title = "Flat ₹75 Welcome Voucher",
            description = "Special welcome voucher for parents ordering uniforms for the first time.",
            discountType = VoucherDiscountType.FLAT,
            discountValue = 75.0,
            minCartValue = 299.0,
            badge = "NEW USER"
        ),
        VoucherItem(
            code = "BIGSAVER15",
            title = "15% Mega Uniform Savings",
            description = "15% off up to ₹250 on bulk uniforms, sweaters, blazers and shoes.",
            discountType = VoucherDiscountType.PERCENTAGE,
            discountValue = 15.0,
            minCartValue = 1200.0,
            maxDiscount = 250.0,
            badge = "SAVE BIG"
        ),
        VoucherItem(
            code = "SIBLING200",
            title = "Flat ₹200 Sibling Combo Voucher",
            description = "Ordering for siblings? Get flat ₹200 off on uniform orders of ₹1,500 or more.",
            discountType = VoucherDiscountType.FLAT,
            discountValue = 200.0,
            minCartValue = 1500.0,
            badge = "FAMILY COMBO"
        )
    )
}

