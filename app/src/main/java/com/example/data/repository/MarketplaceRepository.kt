package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.MarketplaceDao
import com.example.data.model.CartItemEntity
import com.example.data.model.CartItemWithProduct
import com.example.data.model.CategoryEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderStatus
import com.example.data.model.OrderWithItems
import com.example.data.model.PayoutEntity
import com.example.data.model.PlatformSettingsEntity
import com.example.data.model.PricingCalculation
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.SchoolEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRoles
import com.example.data.model.calculateProductPricing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MarketplaceRepository(private val dao: MarketplaceDao) {

    companion object {
        @Volatile
        private var INSTANCE: MarketplaceRepository? = null

        fun getInstance(context: Context): MarketplaceRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val instance = MarketplaceRepository(db.marketplaceDao())
                INSTANCE = instance
                // Seed initial data asynchronously on IO
                CoroutineScope(Dispatchers.IO).launch {
                    instance.seedInitialDataIfEmpty()
                }
                instance
            }
        }
    }

    // --- Users & Auth ---
    fun getUser(userId: String): Flow<UserEntity?> = dao.getUserById(userId)
    suspend fun getUserOnce(userId: String): UserEntity? = dao.getUserByIdOnce(userId)
    fun getAllSellers(): Flow<List<UserEntity>> = dao.getAllSellers()
    fun getPendingSellers(): Flow<List<UserEntity>> = dao.getPendingSellers()

    suspend fun registerUser(user: UserEntity) = withContext(Dispatchers.IO) {
        dao.insertUser(user)
    }

    suspend fun verifySeller(userId: String, verified: Boolean) = withContext(Dispatchers.IO) {
        dao.setSellerVerified(userId, verified)
        val statusText = if (verified) "approved and verified" else "unverified"
        dao.insertNotification(
            NotificationEntity(
                userId = userId,
                title = "Seller Verification Updated",
                message = "Your seller business profile has been $statusText by the administrator.",
                type = "ALERT"
            )
        )
    }

    // --- Schools & Categories ---
    fun getAllSchools(): Flow<List<SchoolEntity>> = dao.getAllSchools()
    fun getAllCategories(): Flow<List<CategoryEntity>> = dao.getAllCategories()

    // --- Products ---
    fun getAllActiveProducts(): Flow<List<ProductEntity>> = dao.getAllActiveProducts()
    fun getProductById(productId: String): Flow<ProductEntity?> = dao.getProductById(productId)
    fun getProductsBySchool(schoolId: String): Flow<List<ProductEntity>> = dao.getProductsBySchool(schoolId)
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = dao.getProductsByCategory(category)
    fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>> = dao.getProductsBySeller(sellerId)
    fun getPendingProducts(): Flow<List<ProductEntity>> = dao.getPendingProducts()

    fun getSimilarProductsForComparison(schoolName: String, category: String): Flow<List<ProductEntity>> {
        return dao.getSimilarProductsForComparison(schoolName, category)
    }

    suspend fun addProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        dao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        dao.updateProduct(product)
    }

    suspend fun deleteProduct(productId: String) = withContext(Dispatchers.IO) {
        dao.deleteProduct(productId)
    }

    suspend fun approveProduct(productId: String, approved: Boolean) = withContext(Dispatchers.IO) {
        dao.setProductApproval(productId, approved)
    }

    // --- Platform Settings & Dynamic Margin ---
    fun getPlatformSettings(): Flow<PlatformSettingsEntity?> = dao.getPlatformSettings()
    suspend fun getPlatformSettingsOnce(): PlatformSettingsEntity {
        return dao.getPlatformSettingsOnce() ?: PlatformSettingsEntity()
    }

    suspend fun updatePlatformMargin(marginPercentage: Double) = withContext(Dispatchers.IO) {
        dao.updatePlatformMargin(marginPercentage)
    }

    suspend fun updateDeliverySettings(deliveryFee: Double, freeThreshold: Double) = withContext(Dispatchers.IO) {
        dao.updateDeliverySettings(deliveryFee, freeThreshold)
    }

    // --- Cart with Live Margin Calculation ---
    fun getCartWithProducts(userId: String): Flow<List<CartItemWithProduct>> {
        return combine(
            dao.getCartItems(userId),
            dao.getAllActiveProducts(),
            dao.getPlatformSettings()
        ) { cartItems, products, settings ->
            val marginPercent = settings?.platformMarginPercentage ?: 5.0
            val productMap = products.associateBy { it.id }

            cartItems.mapNotNull { item ->
                val prod = productMap[item.productId]
                if (prod != null) {
                    val pricing = calculateProductPricing(prod.basePrice, marginPercent)
                    CartItemWithProduct(
                        cartItem = item,
                        product = prod,
                        pricing = pricing
                    )
                } else null
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addToCart(userId: String, productId: String, selectedSize: String, quantity: Int = 1) = withContext(Dispatchers.IO) {
        val existing = dao.findCartItem(userId, productId, selectedSize)
        if (existing != null) {
            dao.updateCartItemQuantity(existing.id, existing.quantity + quantity)
        } else {
            dao.insertCartItem(
                CartItemEntity(
                    userId = userId,
                    productId = productId,
                    selectedSize = selectedSize,
                    quantity = quantity
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, quantity: Int) = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            dao.deleteCartItem(cartItemId)
        } else {
            dao.updateCartItemQuantity(cartItemId, quantity)
        }
    }

    suspend fun removeFromCart(cartItemId: Long) = withContext(Dispatchers.IO) {
        dao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart(userId: String) = withContext(Dispatchers.IO) {
        dao.clearCart(userId)
    }

    // --- Checkout & Orders ---
    suspend fun placeOrder(
        customer: UserEntity,
        cartItems: List<CartItemWithProduct>,
        deliveryAddress: String,
        deliveryCity: String,
        deliveryPincode: String,
        deliveryType: String,
        paymentMethod: String,
        discountAmount: Double = 0.0,
        voucherCode: String = "",
        voucherDiscount: Double = 0.0,
        onlinePaymentDiscount: Double = 0.0
    ): String = withContext(Dispatchers.IO) {
        val settings = getPlatformSettingsOnce()
        val marginPercentage = settings.platformMarginPercentage

        var subtotalBase = 0.0
        var totalPlatformFee = 0.0

        cartItems.forEach { item ->
            val qty = item.cartItem.quantity
            val base = item.product.basePrice * qty
            val fee = (base * marginPercentage / 100.0)
            subtotalBase += base
            totalPlatformFee += fee
        }

        val deliveryFee = if (deliveryType == "STORE_PICKUP" || settings.defaultDeliveryFee == 0.0 || settings.freeDeliveryThreshold == 0.0 || (subtotalBase + totalPlatformFee) >= settings.freeDeliveryThreshold) {
            0.0
        } else {
            settings.defaultDeliveryFee
        }

        val effectiveOnlineDiscount = if (onlinePaymentDiscount > 0.0) {
            onlinePaymentDiscount
        } else if (paymentMethod == "UPI" || paymentMethod == "ONLINE") {
            (subtotalBase + totalPlatformFee) * 0.05
        } else {
            0.0
        }

        val effectiveVoucherDiscount = if (voucherDiscount > 0.0) voucherDiscount else discountAmount
        val totalCombinedDiscount = effectiveVoucherDiscount + effectiveOnlineDiscount

        val grandTotal = (subtotalBase + totalPlatformFee + deliveryFee - totalCombinedDiscount).coerceAtLeast(0.0)
        val orderId = "ORD-" + UUID.randomUUID().toString().take(8).uppercase()
        val orderNumber = "#SDH-${(1000..9999).random()}"

        val sellerIds = cartItems.map { it.product.sellerId }.distinct().joinToString(",")

        val order = OrderEntity(
            id = orderId,
            orderNumber = orderNumber,
            customerId = customer.id,
            customerName = customer.name,
            customerPhone = customer.phone,
            deliveryAddress = deliveryAddress,
            deliveryCity = deliveryCity,
            deliveryPincode = deliveryPincode,
            deliveryType = deliveryType,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "CASH_ON_DELIVERY") "PENDING" else "PAID",
            subtotalBasePrice = subtotalBase,
            platformMarginPercentage = marginPercentage,
            totalPlatformFee = totalPlatformFee,
            deliveryFee = deliveryFee,
            discountAmount = totalCombinedDiscount,
            grandTotal = grandTotal,
            orderStatus = OrderStatus.PLACED,
            sellerIds = sellerIds,
            estimatedDelivery = "2-4 Business Days",
            voucherCode = voucherCode,
            voucherDiscount = effectiveVoucherDiscount,
            onlinePaymentDiscount = effectiveOnlineDiscount
        )
        dao.insertOrder(order)

        val orderItems = cartItems.map { item ->
            val pricing = calculateProductPricing(item.product.basePrice, marginPercentage)
            OrderItemEntity(
                orderId = orderId,
                productId = item.product.id,
                productName = item.product.name,
                schoolName = item.product.schoolName,
                sellerId = item.product.sellerId,
                sellerName = item.product.sellerName,
                selectedSize = item.cartItem.selectedSize,
                quantity = item.cartItem.quantity,
                unitBasePrice = pricing.basePrice,
                unitPlatformFee = pricing.marginAmount,
                unitFinalPrice = pricing.customerPrice,
                itemStatus = OrderStatus.PLACED
            )
        }
        dao.insertOrderItems(orderItems)

        // Clear cart
        dao.clearCart(customer.id)

        // Send notifications
        dao.insertNotification(
            NotificationEntity(
                userId = customer.id,
                title = "Order Placed Successfully",
                message = "Your order $orderNumber for ₹$grandTotal has been received.",
                type = "ORDER"
            )
        )

        // Notify each seller
        val sellers = cartItems.map { it.product.sellerId }.distinct()
        sellers.forEach { sellerId ->
            dao.insertNotification(
                NotificationEntity(
                    userId = sellerId,
                    title = "New Order Received!",
                    message = "You received order $orderNumber with school uniform items.",
                    type = "ORDER"
                )
            )
        }

        orderId
    }

    fun getCustomerOrders(customerId: String): Flow<List<OrderEntity>> = dao.getCustomerOrders(customerId)
    fun getAllOrders(): Flow<List<OrderEntity>> = dao.getAllOrders()
    fun getOrderById(orderId: String): Flow<OrderEntity?> = dao.getOrderById(orderId)
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = dao.getOrderItems(orderId)

    fun getOrdersForSeller(sellerId: String): Flow<List<OrderWithItems>> {
        return combine(
            dao.getAllOrders(),
            dao.getOrderItemsForSeller(sellerId)
        ) { orders, sellerItems ->
            val itemsByOrderId = sellerItems.groupBy { it.orderId }
            orders.filter { order ->
                itemsByOrderId.containsKey(order.id) || order.sellerIds.contains(sellerId)
            }.map { order ->
                OrderWithItems(
                    order = order,
                    items = itemsByOrderId[order.id] ?: emptyList()
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateOrderStatus(orderId: String, status: String, customerId: String) = withContext(Dispatchers.IO) {
        dao.updateOrderStatus(orderId, status)
        dao.insertNotification(
            NotificationEntity(
                userId = customerId,
                title = "Order Status Updated",
                message = "Your order status is now: $status",
                type = "ORDER"
            )
        )
    }

    // --- Reviews ---
    fun getReviews(productId: String): Flow<List<ReviewEntity>> = dao.getReviewsForProduct(productId)

    suspend fun addReview(productId: String, customerId: String, customerName: String, rating: Int, comment: String) = withContext(Dispatchers.IO) {
        dao.insertReview(
            ReviewEntity(
                productId = productId,
                customerId = customerId,
                customerName = customerName,
                rating = rating,
                comment = comment
            )
        )
    }

    // --- Payouts ---
    fun getSellerPayouts(sellerId: String): Flow<List<PayoutEntity>> = dao.getPayoutsBySeller(sellerId)
    fun getAllPayouts(): Flow<List<PayoutEntity>> = dao.getAllPayouts()

    suspend fun requestPayout(sellerId: String, sellerName: String, amount: Double, bankAccount: String, ifsc: String) = withContext(Dispatchers.IO) {
        dao.insertPayout(
            PayoutEntity(
                sellerId = sellerId,
                sellerName = sellerName,
                amount = amount,
                status = "PENDING",
                bankAccount = bankAccount,
                ifscCode = ifsc
            )
        )
        dao.insertNotification(
            NotificationEntity(
                userId = sellerId,
                title = "Payout Requested",
                message = "Your payout request of ₹$amount has been submitted to Admin.",
                type = "PAYOUT"
            )
        )
    }

    suspend fun processPayout(payoutId: Long, sellerId: String, approved: Boolean) = withContext(Dispatchers.IO) {
        val status = if (approved) "APPROVED" else "REJECTED"
        dao.updatePayoutStatus(payoutId, status, System.currentTimeMillis())
        dao.insertNotification(
            NotificationEntity(
                userId = sellerId,
                title = "Payout $status",
                message = "Your payout request has been $status by SchoolDressHub Admin.",
                type = "PAYOUT"
            )
        )
    }

    // --- Notifications ---
    fun getNotifications(userId: String): Flow<List<NotificationEntity>> = dao.getNotifications(userId)
    suspend fun markNotificationsRead(userId: String) = withContext(Dispatchers.IO) {
        dao.markNotificationsAsRead(userId)
    }

    // --- Seeding ---
    suspend fun seedInitialDataIfEmpty() {
        val existingSettings = dao.getPlatformSettingsOnce()
        if (existingSettings == null) {
            dao.insertPlatformSettings(
                PlatformSettingsEntity(
                    id = 1,
                    platformMarginPercentage = 5.0,
                    defaultDeliveryFee = 0.0,
                    freeDeliveryThreshold = 0.0,
                    returnPeriodDays = 7,
                    sellerApprovalRequired = false,
                    productApprovalRequired = false
                )
            )
        } else if (existingSettings.defaultDeliveryFee > 0.0) {
            dao.updateDeliverySettings(0.0, 0.0)
        }

        val allUsers = dao.getAllUsers().first()
        if (allUsers.isEmpty()) {
            val users = listOf(
                UserEntity(
                    id = "user_customer_1",
                    name = "Priya Sharma",
                    email = "priya.sharma@example.com",
                    phone = "+91 98765 43210",
                    role = UserRoles.CUSTOMER,
                    city = "New Delhi",
                    address = "Flat 402, Shanti Vihar, Rohini Sector 14",
                    pincode = "110085",
                    isVerified = true
                ),
                UserEntity(
                    id = "seller_wholesaler_1",
                    name = "ABC Uniform Wholesale",
                    email = "sales@abcuniforms.com",
                    phone = "+91 98111 22334",
                    role = UserRoles.WHOLESALER,
                    businessName = "ABC Uniform Wholesale Depot",
                    city = "New Delhi",
                    address = "Chandni Chowk Textile Market #42",
                    pincode = "110006",
                    gstNumber = "07AAAAA0000A1Z5",
                    isVerified = true,
                    rating = 4.8,
                    totalOrders = 340
                ),
                UserEntity(
                    id = "seller_retailer_1",
                    name = "City School Dress & Shoes",
                    email = "contact@cityschooluniforms.in",
                    phone = "+91 98333 44556",
                    role = UserRoles.SHOPKEEPER,
                    businessName = "City Uniform Retail Store",
                    city = "New Delhi",
                    address = "Block C, Connaught Place Market",
                    pincode = "110001",
                    gstNumber = "07CCCCC2222C3Z7",
                    isVerified = true,
                    rating = 4.9,
                    totalOrders = 195
                ),
                UserEntity(
                    id = "seller_retailer_2",
                    name = "Royal Dressmakers & Blazers",
                    email = "info@royaldressmakers.in",
                    phone = "+91 98444 55667",
                    role = UserRoles.SHOPKEEPER,
                    businessName = "Royal Uniform Emporium",
                    city = "New Delhi",
                    address = "Lajpat Nagar Central Market #18",
                    pincode = "110024",
                    gstNumber = "07DDDDD3333D4Z8",
                    isVerified = true,
                    rating = 4.6,
                    totalOrders = 120
                ),
                UserEntity(
                    id = "seller_pending_1",
                    name = "Modern Uniform Traders",
                    email = "modern.uniforms@example.com",
                    phone = "+91 98555 66778",
                    role = UserRoles.WHOLESALER,
                    businessName = "Modern Textile & Uniforms",
                    city = "New Delhi",
                    address = "Karol Bagh Wholesale Center",
                    pincode = "110005",
                    gstNumber = "07EEEEE4444E5Z9",
                    isVerified = false,
                    rating = 4.5,
                    totalOrders = 0
                ),
                UserEntity(
                    id = "user_admin_1",
                    name = "Platform Administrator",
                    email = "admin@schooldresshub.com",
                    phone = "+91 99999 00000",
                    role = UserRoles.ADMIN,
                    city = "New Delhi",
                    isVerified = true
                )
            )
            dao.insertUsers(users)
        }

        val allSchools = dao.getAllSchools().first()
        if (allSchools.isEmpty()) {
            val schools = listOf(
                SchoolEntity(
                    id = "school_abc",
                    name = "ABC Public School",
                    board = "CBSE",
                    city = "New Delhi",
                    area = "Rohini Sector 9",
                    uniformColorDesc = "White Collared Shirt, Navy Bottoms & Red Stripe Tie"
                ),
                SchoolEntity(
                    id = "school_dps",
                    name = "Delhi Public School (DPS)",
                    board = "CBSE",
                    city = "New Delhi",
                    area = "R.K. Puram",
                    uniformColorDesc = "Crisp White Shirt, Grey Trousers/Skirts & Bottle Green Blazer"
                ),
                SchoolEntity(
                    id = "school_kv",
                    name = "Kendriya Vidyalaya (KV)",
                    board = "CBSE",
                    city = "New Delhi",
                    area = "Tagore Garden",
                    uniformColorDesc = "Blue & White Micro-Check Shirt with Navy Trousers"
                ),
                SchoolEntity(
                    id = "school_xavier",
                    name = "St. Xavier's High School",
                    board = "ICSE",
                    city = "New Delhi",
                    area = "Civil Lines",
                    uniformColorDesc = "Cream Shirt, Tartan Plaid Skirt/Pants & Maroon Blazer"
                ),
                SchoolEntity(
                    id = "school_ryan",
                    name = "Ryan International School",
                    board = "CBSE",
                    city = "New Delhi",
                    area = "Vasant Kunj",
                    uniformColorDesc = "Sky Blue Shirt, Dark Grey Bottoms & Navy Blue Blazer"
                )
            )
            dao.insertSchools(schools)
        }

        val allCategories = dao.getAllCategories().first()
        if (allCategories.isEmpty()) {
            val categories = listOf(
                CategoryEntity("cat_shirt", "School Shirts", "Checkroom", 1),
                CategoryEntity("cat_pant", "School Pants", "DryCleaning", 2),
                CategoryEntity("cat_skirt", "School Skirts", "Woman", 3),
                CategoryEntity("cat_trouser", "School Trousers", "Boy", 4),
                CategoryEntity("cat_blazer", "School Blazers", "Shield", 5),
                CategoryEntity("cat_sweater", "School Sweaters", "AcUnit", 6),
                CategoryEntity("cat_tracksuit", "Sports Uniforms", "DirectionsRun", 7),
                CategoryEntity("cat_shoes", "School Shoes", "Snowshoeing", 8),
                CategoryEntity("cat_socks", "School Socks", "Inventory", 9),
                CategoryEntity("cat_belt", "School Belts", "Straighten", 10),
                CategoryEntity("cat_tie", "School Ties", "CardGiftcard", 11),
                CategoryEntity("cat_bag", "School Bags", "Backpack", 12),
                CategoryEntity("cat_set", "Complete Uniform Sets", "ShoppingBag", 13)
            )
            dao.insertCategories(categories)
        }

        val allProducts = dao.getAllActiveProducts().first()
        if (allProducts.isEmpty()) {
            val demoProducts = listOf(
                // Multi-seller price comparison test products (ABC Public School White Shirt)
                ProductEntity(
                    id = "prod_abc_shirt_seller1",
                    sellerId = "seller_wholesaler_1",
                    sellerName = "ABC Uniform Wholesale",
                    sellerRole = UserRoles.WHOLESALER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 1.8,
                    schoolId = "school_abc",
                    schoolName = "ABC Public School",
                    name = "ABC Public School White Shirt",
                    category = "School Shirts",
                    description = "Official premium cotton rich school shirt. Non-iron breathable fabric with reinforced double-stitched buttons and school logo embroidery crest.",
                    brand = "SchoolCraft",
                    gender = "Unisex",
                    classGroup = "Class 1-12",
                    color = "Crisp White",
                    availableSizes = "26, 28, 30, 32, 34, 36, 38, 40",
                    material = "65% Cotton, 35% Polyester",
                    basePrice = 450.0, // Lowest seller base price
                    stockQuantity = 150,
                    imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.8,
                    reviewCount = 34,
                    isBestPrice = true,
                    deliveryDays = 2
                ),
                ProductEntity(
                    id = "prod_abc_shirt_seller2",
                    sellerId = "seller_retailer_1",
                    sellerName = "City School Dress & Shoes",
                    sellerRole = UserRoles.SHOPKEEPER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 0.9,
                    schoolId = "school_abc",
                    schoolName = "ABC Public School",
                    name = "ABC Public School White Shirt",
                    category = "School Shirts",
                    description = "Authorized standard ABC Public School uniform shirt. High yarn count fabric offering supreme softness and school badge on pocket.",
                    brand = "CityTailors",
                    gender = "Unisex",
                    classGroup = "Class 1-12",
                    color = "Crisp White",
                    availableSizes = "28, 30, 32, 34, 36, 38",
                    material = "100% Combed Cotton",
                    basePrice = 475.0,
                    stockQuantity = 80,
                    imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.9,
                    reviewCount = 28,
                    isBestPrice = false,
                    deliveryDays = 1
                ),
                ProductEntity(
                    id = "prod_abc_shirt_seller3",
                    sellerId = "seller_retailer_2",
                    sellerName = "Royal Dressmakers & Blazers",
                    sellerRole = UserRoles.SHOPKEEPER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 4.2,
                    schoolId = "school_abc",
                    schoolName = "ABC Public School",
                    name = "ABC Public School White Shirt",
                    category = "School Shirts",
                    description = "Tailored bespoke fit school dress shirt. Durable color fastness that withstands over 100 industrial washes with comfort neckband.",
                    brand = "RoyalTextiles",
                    gender = "Unisex",
                    classGroup = "Class 1-12",
                    color = "Crisp White",
                    availableSizes = "26, 28, 30, 32, 34, 36",
                    material = "Cotton Poly Mercerized",
                    basePrice = 500.0,
                    stockQuantity = 45,
                    imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.6,
                    reviewCount = 19,
                    isBestPrice = false,
                    deliveryDays = 3
                ),

                // ABC Public School Navy Trousers (Multi-seller)
                ProductEntity(
                    id = "prod_abc_trouser_seller1",
                    sellerId = "seller_wholesaler_1",
                    sellerName = "ABC Uniform Wholesale",
                    sellerRole = UserRoles.WHOLESALER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 1.8,
                    schoolId = "school_abc",
                    schoolName = "ABC Public School",
                    name = "ABC Public School Navy Trousers",
                    category = "School Trousers",
                    description = "Official navy blue uniform trousers with expandable waistband, double knee reinforcement, and deep pockets.",
                    brand = "SchoolCraft",
                    gender = "Boys",
                    classGroup = "Class 5-12",
                    color = "Deep Navy Blue",
                    availableSizes = "24, 26, 28, 30, 32, 34, 36",
                    material = "Poly Viscose Anti-Wrinkle",
                    basePrice = 520.0,
                    stockQuantity = 110,
                    imageUrl = "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.7,
                    reviewCount = 22,
                    isBestPrice = true,
                    deliveryDays = 2
                ),
                ProductEntity(
                    id = "prod_abc_trouser_seller2",
                    sellerId = "seller_retailer_1",
                    sellerName = "City School Dress & Shoes",
                    sellerRole = UserRoles.SHOPKEEPER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 0.9,
                    schoolId = "school_abc",
                    schoolName = "ABC Public School",
                    name = "ABC Public School Navy Trousers",
                    category = "School Trousers",
                    description = "Comfort-fit school dress trousers with stain-repellent Teflon coating and adjustable waist belt loops.",
                    brand = "CityTailors",
                    gender = "Boys",
                    classGroup = "Class 5-12",
                    color = "Deep Navy Blue",
                    availableSizes = "26, 28, 30, 32, 34",
                    material = "Poly Viscose Premium",
                    basePrice = 550.0,
                    stockQuantity = 60,
                    imageUrl = "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.8,
                    reviewCount = 15,
                    isBestPrice = false,
                    deliveryDays = 1
                ),

                // DPS Green Blazer (Multi-seller)
                ProductEntity(
                    id = "prod_dps_blazer_seller1",
                    sellerId = "seller_retailer_2",
                    sellerName = "Royal Dressmakers & Blazers",
                    sellerRole = UserRoles.SHOPKEEPER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 4.2,
                    schoolId = "school_dps",
                    schoolName = "Delhi Public School (DPS)",
                    name = "DPS Bottle Green Wool Blazer",
                    category = "School Blazers",
                    description = "Official DPS Bottle Green winter blazer with embroidered DPS gold crest, brass buttons, satin inner lining and dual inside pockets.",
                    brand = "RoyalBlazers",
                    gender = "Unisex",
                    classGroup = "Class 1-12",
                    color = "Bottle Green",
                    availableSizes = "28, 30, 32, 34, 36, 38, 40",
                    material = "Heavy Wool Blend with Satin Lining",
                    basePrice = 1450.0,
                    stockQuantity = 75,
                    imageUrl = "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.9,
                    reviewCount = 42,
                    isBestPrice = true,
                    deliveryDays = 2
                ),
                ProductEntity(
                    id = "prod_dps_blazer_seller2",
                    sellerId = "seller_wholesaler_1",
                    sellerName = "ABC Uniform Wholesale",
                    sellerRole = UserRoles.WHOLESALER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 1.8,
                    schoolId = "school_dps",
                    schoolName = "Delhi Public School (DPS)",
                    name = "DPS Bottle Green Wool Blazer",
                    category = "School Blazers",
                    description = "Warm winter blazer tailored as per DPS board guidelines. High durability stitching and matching pocket flap.",
                    brand = "SchoolCraft",
                    gender = "Unisex",
                    classGroup = "Class 1-12",
                    color = "Bottle Green",
                    availableSizes = "30, 32, 34, 36, 38",
                    material = "Wool Poly Blend",
                    basePrice = 1550.0,
                    stockQuantity = 50,
                    imageUrl = "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.7,
                    reviewCount = 18,
                    isBestPrice = false,
                    deliveryDays = 2
                ),

                // KV Blue Checkered Shirt
                ProductEntity(
                    id = "prod_kv_shirt_seller1",
                    sellerId = "seller_wholesaler_1",
                    sellerName = "ABC Uniform Wholesale",
                    sellerRole = UserRoles.WHOLESALER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 1.8,
                    schoolId = "school_kv",
                    schoolName = "Kendriya Vidyalaya (KV)",
                    name = "KV Blue & White Checkered Shirt",
                    category = "School Shirts",
                    description = "Standard Kendriya Vidyalaya uniform shirt. Cool breathable summer weave with KV logo patch on left chest pocket.",
                    brand = "SchoolCraft",
                    gender = "Unisex",
                    classGroup = "Class 1-12",
                    color = "Blue White Check",
                    availableSizes = "24, 26, 28, 30, 32, 34, 36",
                    material = "Poly Cotton Blend",
                    basePrice = 380.0,
                    stockQuantity = 180,
                    imageUrl = "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.8,
                    reviewCount = 39,
                    isBestPrice = true,
                    deliveryDays = 2
                ),

                // St. Xavier's Plaid Uniform Skirt
                ProductEntity(
                    id = "prod_xavier_skirt_seller1",
                    sellerId = "seller_retailer_1",
                    sellerName = "City School Dress & Shoes",
                    sellerRole = UserRoles.SHOPKEEPER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 0.9,
                    schoolId = "school_xavier",
                    schoolName = "St. Xavier's High School",
                    name = "St. Xavier's Pleated Uniform Skirt",
                    category = "School Skirts",
                    description = "Permanent knife pleats tartan plaid skirt with side zipper, elastic comfort back, and matching inner shorts.",
                    brand = "CityTailors",
                    gender = "Girls",
                    classGroup = "Class 1-10",
                    color = "Maroon Tartan Plaid",
                    availableSizes = "24, 26, 28, 30, 32, 34",
                    material = "Polyester Viscose Crease-Resistant",
                    basePrice = 490.0,
                    stockQuantity = 70,
                    imageUrl = "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.9,
                    reviewCount = 26,
                    isBestPrice = true,
                    deliveryDays = 1
                ),

                // Genuine Leather School Shoes (Multi-seller)
                ProductEntity(
                    id = "prod_shoes_seller1",
                    sellerId = "seller_retailer_1",
                    sellerName = "City School Dress & Shoes",
                    sellerRole = UserRoles.SHOPKEEPER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 0.9,
                    schoolId = "school_abc",
                    schoolName = "Universal School Standard",
                    name = "Action Black School Oxford Shoes",
                    category = "School Shoes",
                    description = "Durable genuine leather uniform shoes with anti-slip PU sole, cushioned footbed, and scuff-resistant finish for everyday school playground wear.",
                    brand = "ActionMilestone",
                    gender = "Unisex",
                    classGroup = "All Classes",
                    color = "Polished Black",
                    availableSizes = "1, 2, 3, 4, 5, 6, 7, 8, 9, 10",
                    material = "Genuine Leather & PU Sole",
                    basePrice = 620.0,
                    stockQuantity = 95,
                    imageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.8,
                    reviewCount = 51,
                    isBestPrice = true,
                    deliveryDays = 1
                ),
                ProductEntity(
                    id = "prod_shoes_seller2",
                    sellerId = "seller_wholesaler_1",
                    sellerName = "ABC Uniform Wholesale",
                    sellerRole = UserRoles.WHOLESALER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 1.8,
                    schoolId = "school_abc",
                    schoolName = "Universal School Standard",
                    name = "Bata Super Scholar Uniform Shoes",
                    category = "School Shoes",
                    description = "Classic lace-up school uniform footwear from Bata. High wear-resistant rubber outsole with arch support insoles.",
                    brand = "Bata",
                    gender = "Unisex",
                    classGroup = "All Classes",
                    color = "Polished Black",
                    availableSizes = "2, 3, 4, 5, 6, 7, 8, 9",
                    material = "Faux Leather & Rubber Sole",
                    basePrice = 670.0,
                    stockQuantity = 120,
                    imageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.7,
                    reviewCount = 37,
                    isBestPrice = false,
                    deliveryDays = 2
                ),

                // Ergonomic School Bag
                ProductEntity(
                    id = "prod_bag_seller1",
                    sellerId = "seller_wholesaler_1",
                    sellerName = "ABC Uniform Wholesale",
                    sellerRole = UserRoles.WHOLESALER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 1.8,
                    schoolId = "school_abc",
                    schoolName = "Universal School Standard",
                    name = "Heavy Duty Ergonomic School Bag",
                    category = "School Bags",
                    description = "Spacious 3-compartment water-resistant backpack with padded back airflow panel, reflective safety strips and dedicated lunch box pocket.",
                    brand = "SkyBags Junior",
                    gender = "Unisex",
                    classGroup = "Class 3-10",
                    color = "Navy Blue & Cyan",
                    availableSizes = "32 Liters (Standard)",
                    material = "Waterproof Cordura Polyester",
                    basePrice = 750.0,
                    stockQuantity = 65,
                    imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.9,
                    reviewCount = 44,
                    isBestPrice = true,
                    deliveryDays = 2
                ),

                // School Socks Pack of 3
                ProductEntity(
                    id = "prod_socks_seller1",
                    sellerId = "seller_retailer_1",
                    sellerName = "City School Dress & Shoes",
                    sellerRole = UserRoles.SHOPKEEPER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 0.9,
                    schoolId = "school_abc",
                    schoolName = "Universal School Standard",
                    name = "Cushioned School Socks (Pack of 3)",
                    category = "School Socks",
                    description = "Soft antibacterial cotton ribbed school socks with elastic grip band that prevents slipping. Reinforced heel and toe.",
                    brand = "CityTailors",
                    gender = "Unisex",
                    classGroup = "All Classes",
                    color = "White with Navy Band",
                    availableSizes = "S (Age 4-7), M (Age 8-12), L (Age 13+)",
                    material = "80% Combed Cotton, 20% Spandex",
                    basePrice = 180.0,
                    stockQuantity = 200,
                    imageUrl = "https://images.unsplash.com/photo-1582966770380-8473ae67117e?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.8,
                    reviewCount = 19,
                    isBestPrice = true,
                    deliveryDays = 1
                ),

                // School Belt & Brass Buckle
                ProductEntity(
                    id = "prod_belt_seller1",
                    sellerId = "seller_wholesaler_1",
                    sellerName = "ABC Uniform Wholesale",
                    sellerRole = UserRoles.WHOLESALER,
                    sellerCity = "New Delhi",
                    sellerDistanceKm = 1.8,
                    schoolId = "school_abc",
                    schoolName = "ABC Public School",
                    name = "ABC School Nylon Belt with Brass Buckle",
                    category = "School Belts",
                    description = "Heavy duty woven nylon uniform belt with engraved school monogram on high-shine golden brass buckle.",
                    brand = "SchoolCraft",
                    gender = "Unisex",
                    classGroup = "Class 1-12",
                    color = "Navy Blue with Red Stripe",
                    availableSizes = "Adjustable (Up to 38 inch)",
                    material = "Woven Nylon & Brass",
                    basePrice = 120.0,
                    stockQuantity = 140,
                    imageUrl = "https://images.unsplash.com/photo-1624222247344-550fb60583dc?w=600&auto=format&fit=crop",
                    isApproved = true,
                    isActive = true,
                    rating = 4.7,
                    reviewCount = 16,
                    isBestPrice = true,
                    deliveryDays = 2
                )
            )
            dao.insertProducts(demoProducts)
        }

        // Seed some demo reviews
        val existingReviews = dao.getReviewsForProduct("prod_abc_shirt_seller1").first()
        if (existingReviews.isEmpty()) {
            dao.insertReview(
                ReviewEntity(
                    productId = "prod_abc_shirt_seller1",
                    customerId = "user_customer_1",
                    customerName = "Priya Sharma",
                    rating = 5,
                    comment = "Excellent quality fabric! Exactly fits the ABC school guidelines. Delivered in just 2 days. The 5% platform fee was transparent and great value."
                )
            )
            dao.insertReview(
                ReviewEntity(
                    productId = "prod_abc_shirt_seller1",
                    customerId = "user_customer_2",
                    customerName = "Rajesh Verma",
                    rating = 5,
                    comment = "Compared prices between 3 sellers on SchoolDressHub and saved ₹50 per shirt. Wholesaler price directly passed on!"
                )
            )
        }

        // Seed demo orders for test dashboard analytics
        val existingOrders = dao.getAllOrders().first()
        if (existingOrders.isEmpty()) {
            val orderId1 = "ORD-DEMO101"
            val order1 = OrderEntity(
                id = orderId1,
                orderNumber = "#SDH-7842",
                customerId = "user_customer_1",
                customerName = "Priya Sharma",
                customerPhone = "+91 98765 43210",
                deliveryAddress = "Flat 402, Shanti Vihar, Rohini Sector 14",
                deliveryCity = "New Delhi",
                deliveryPincode = "110085",
                deliveryType = "HOME_DELIVERY",
                paymentMethod = "UPI",
                paymentStatus = "PAID",
                subtotalBasePrice = 900.0,
                platformMarginPercentage = 5.0,
                totalPlatformFee = 45.0,
                deliveryFee = 0.0,
                discountAmount = 0.0,
                grandTotal = 945.0,
                orderStatus = OrderStatus.DELIVERED,
                sellerIds = "seller_wholesaler_1",
                estimatedDelivery = "Delivered on Sep 21"
            )
            dao.insertOrder(order1)
            dao.insertOrderItems(
                listOf(
                    OrderItemEntity(
                        orderId = orderId1,
                        productId = "prod_abc_shirt_seller1",
                        productName = "ABC Public School White Shirt",
                        schoolName = "ABC Public School",
                        sellerId = "seller_wholesaler_1",
                        sellerName = "ABC Uniform Wholesale",
                        selectedSize = "32",
                        quantity = 2,
                        unitBasePrice = 450.0,
                        unitPlatformFee = 22.50,
                        unitFinalPrice = 472.50,
                        itemStatus = OrderStatus.DELIVERED
                    )
                )
            )

            val orderId2 = "ORD-DEMO102"
            val order2 = OrderEntity(
                id = orderId2,
                orderNumber = "#SDH-8199",
                customerId = "user_customer_1",
                customerName = "Priya Sharma",
                customerPhone = "+91 98765 43210",
                deliveryAddress = "Flat 402, Shanti Vihar, Rohini Sector 14",
                deliveryCity = "New Delhi",
                deliveryPincode = "110085",
                deliveryType = "HOME_DELIVERY",
                paymentMethod = "CASH_ON_DELIVERY",
                paymentStatus = "PENDING",
                subtotalBasePrice = 1140.0,
                platformMarginPercentage = 5.0,
                totalPlatformFee = 57.0,
                deliveryFee = 0.0,
                discountAmount = 50.0,
                grandTotal = 1147.0,
                orderStatus = OrderStatus.SHIPPED,
                sellerIds = "seller_retailer_1,seller_wholesaler_1",
                estimatedDelivery = "Arriving in 1 Day"
            )
            dao.insertOrder(order2)
            dao.insertOrderItems(
                listOf(
                    OrderItemEntity(
                        orderId = orderId2,
                        productId = "prod_shoes_seller1",
                        productName = "Action Black School Oxford Shoes",
                        schoolName = "Universal School Standard",
                        sellerId = "seller_retailer_1",
                        sellerName = "City School Dress & Shoes",
                        selectedSize = "6",
                        quantity = 1,
                        unitBasePrice = 620.0,
                        unitPlatformFee = 31.0,
                        unitFinalPrice = 651.0,
                        itemStatus = OrderStatus.SHIPPED
                    ),
                    OrderItemEntity(
                        orderId = orderId2,
                        productId = "prod_abc_trouser_seller1",
                        productName = "ABC Public School Navy Trousers",
                        schoolName = "ABC Public School",
                        sellerId = "seller_wholesaler_1",
                        sellerName = "ABC Uniform Wholesale",
                        selectedSize = "30",
                        quantity = 1,
                        unitBasePrice = 520.0,
                        unitPlatformFee = 26.0,
                        unitFinalPrice = 546.0,
                        itemStatus = OrderStatus.SHIPPED
                    )
                )
            )

            // Seed demo notifications
            dao.insertNotification(
                NotificationEntity(
                    userId = "user_customer_1",
                    title = "Order #SDH-8199 Shipped",
                    message = "Your school uniform order is on its way with express courier.",
                    type = "ORDER"
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    userId = "seller_wholesaler_1",
                    title = "Payout Processed",
                    message = "Payout of ₹15,000 for previous batch orders has been credited.",
                    type = "PAYOUT"
                )
            )
        }
    }
}
