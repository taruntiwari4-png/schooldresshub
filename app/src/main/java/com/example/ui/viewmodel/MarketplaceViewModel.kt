package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AvailableVouchers
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
import com.example.data.model.VoucherItem
import com.example.data.model.calculateProductPricing
import com.example.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class SortOption(val label: String) {
    LOWEST_PRICE("Lowest Price"),
    HIGHEST_PRICE("Highest Price"),
    RATING("Highest Rated"),
    NEARBY("Nearby Seller"),
    FAST_DELIVERY("Fastest Delivery")
}

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MarketplaceRepository.getInstance(application)

    // Current logged-in user
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Location
    private val _currentLocation = MutableStateFlow("Rohini, New Delhi")
    val currentLocation: StateFlow<String> = _currentLocation.asStateFlow()

    // Platform Settings (includes configurable Platform Margin %)
    val platformSettings: StateFlow<PlatformSettingsEntity> = repository.getPlatformSettings()
        .combine(MutableStateFlow(PlatformSettingsEntity())) { settings, default ->
            settings ?: default
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlatformSettingsEntity()
        )

    // Data streams
    val schools: StateFlow<List<SchoolEntity>> = repository.getAllSchools()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<ProductEntity>> = repository.getAllActiveProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSchoolId = MutableStateFlow<String?>(null)
    val selectedSchoolId: StateFlow<String?> = _selectedSchoolId.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedGender = MutableStateFlow<String?>(null)
    val selectedGender: StateFlow<String?> = _selectedGender.asStateFlow()

    private val _selectedClassGroup = MutableStateFlow<String?>(null)
    val selectedClassGroup: StateFlow<String?> = _selectedClassGroup.asStateFlow()

    private val _selectedSortOption = MutableStateFlow(SortOption.LOWEST_PRICE)
    val selectedSortOption: StateFlow<SortOption> = _selectedSortOption.asStateFlow()

    // Filtered Products
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        searchQuery,
        selectedSchoolId,
        selectedCategory,
        selectedGender,
        selectedClassGroup,
        selectedSortOption
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val products = args[0] as List<ProductEntity>
        val query = args[1] as String
        val schoolId = args[2] as String?
        val category = args[3] as String?
        val gender = args[4] as String?
        val classGroup = args[5] as String?
        val sort = args[6] as SortOption

        var list = products

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.schoolName.lowercase().contains(q) ||
                it.sellerName.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.sellerCity.lowercase().contains(q)
            }
        }

        if (schoolId != null) {
            list = list.filter { it.schoolId == schoolId }
        }

        if (category != null) {
            list = list.filter { it.category == category }
        }

        if (gender != null && gender != "All") {
            list = list.filter { it.gender.equals(gender, ignoreCase = true) || it.gender.equals("Unisex", ignoreCase = true) }
        }

        if (classGroup != null && classGroup != "All") {
            list = list.filter { it.classGroup.contains(classGroup) || it.classGroup.contains("All") }
        }

        when (sort) {
            SortOption.LOWEST_PRICE -> list.sortedBy { it.basePrice }
            SortOption.HIGHEST_PRICE -> list.sortedByDescending { it.basePrice }
            SortOption.RATING -> list.sortedByDescending { it.rating }
            SortOption.NEARBY -> list.sortedBy { it.sellerDistanceKm }
            SortOption.FAST_DELIVERY -> list.sortedBy { it.deliveryDays }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart Items with Products & Live Margin
    private val _cartItems = MutableStateFlow<List<CartItemWithProduct>>(emptyList())
    val cartItems: StateFlow<List<CartItemWithProduct>> = _cartItems.asStateFlow()

    // Customer Orders
    private val _customerOrders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val customerOrders: StateFlow<List<OrderEntity>> = _customerOrders.asStateFlow()

    // All Orders (for Admin)
    val allOrders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Sellers & Pending Sellers (for Admin)
    val allSellers: StateFlow<List<UserEntity>> = repository.getAllSellers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingSellers: StateFlow<List<UserEntity>> = repository.getPendingSellers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingProducts: StateFlow<List<ProductEntity>> = repository.getPendingProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayouts: StateFlow<List<PayoutEntity>> = repository.getAllPayouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    // Seller Specific States
    private val _sellerProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val sellerProducts: StateFlow<List<ProductEntity>> = _sellerProducts.asStateFlow()

    private val _sellerOrders = MutableStateFlow<List<OrderWithItems>>(emptyList())
    val sellerOrders: StateFlow<List<OrderWithItems>> = _sellerOrders.asStateFlow()

    private val _sellerPayouts = MutableStateFlow<List<PayoutEntity>>(emptyList())
    val sellerPayouts: StateFlow<List<PayoutEntity>> = _sellerPayouts.asStateFlow()

    // Selected Product for comparison / detail
    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    private val _comparisonProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val comparisonProducts: StateFlow<List<ProductEntity>> = _comparisonProducts.asStateFlow()

    private val _productReviews = MutableStateFlow<List<ReviewEntity>>(emptyList())
    val productReviews: StateFlow<List<ReviewEntity>> = _productReviews.asStateFlow()

    // Coupon Code & Vouchers
    private val _couponCode = MutableStateFlow("")
    val couponCode: StateFlow<String> = _couponCode.asStateFlow()

    private val _appliedVoucher = MutableStateFlow<VoucherItem?>(null)
    val appliedVoucher: StateFlow<VoucherItem?> = _appliedVoucher.asStateFlow()

    val availableVouchers: List<VoucherItem> = AvailableVouchers.list

    private val _discountAmount = MutableStateFlow(0.0)
    val discountAmount: StateFlow<Double> = _discountAmount.asStateFlow()

    private val _couponMessage = MutableStateFlow<String?>(null)
    val couponMessage: StateFlow<String?> = _couponMessage.asStateFlow()

    // Status message / toast
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        // Default login as Customer (Priya Sharma)
        switchRole(UserRoles.CUSTOMER)
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun setLocation(location: String) {
        _currentLocation.value = location
    }

    // Role switcher for seamless demo & multi-role testing
    fun switchRole(role: String) {
        viewModelScope.launch {
            val targetUserId = when (role) {
                UserRoles.CUSTOMER -> "user_customer_1"
                UserRoles.WHOLESALER -> "seller_wholesaler_1"
                UserRoles.SHOPKEEPER -> "seller_retailer_1"
                UserRoles.ADMIN -> "user_admin_1"
                else -> "user_customer_1"
            }
            val user = repository.getUserOnce(targetUserId)
            _currentUser.value = user

            // Reload user specific data
            user?.let { u ->
                observeUserData(u)
            }
        }
    }

    fun selectUser(user: UserEntity) {
        _currentUser.value = user
        observeUserData(user)
    }

    private fun observeUserData(user: UserEntity) {
        viewModelScope.launch {
            repository.getCartWithProducts(user.id).collect {
                _cartItems.value = it
            }
        }
        viewModelScope.launch {
            repository.getCustomerOrders(user.id).collect {
                _customerOrders.value = it
            }
        }
        viewModelScope.launch {
            repository.getNotifications(user.id).collect {
                _notifications.value = it
            }
        }

        if (user.role == UserRoles.WHOLESALER || user.role == UserRoles.SHOPKEEPER) {
            viewModelScope.launch {
                repository.getProductsBySeller(user.id).collect {
                    _sellerProducts.value = it
                }
            }
            viewModelScope.launch {
                repository.getOrdersForSeller(user.id).collect {
                    _sellerOrders.value = it
                }
            }
            viewModelScope.launch {
                repository.getSellerPayouts(user.id).collect {
                    _sellerPayouts.value = it
                }
            }
        }
    }

    // Search & Filter updates
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectSchoolFilter(schoolId: String?) {
        _selectedSchoolId.value = schoolId
    }

    fun selectCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun selectGenderFilter(gender: String?) {
        _selectedGender.value = gender
    }

    fun selectClassGroupFilter(classGroup: String?) {
        _selectedClassGroup.value = classGroup
    }

    fun setSortOption(option: SortOption) {
        _selectedSortOption.value = option
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedSchoolId.value = null
        _selectedCategory.value = null
        _selectedGender.value = null
        _selectedClassGroup.value = null
        _selectedSortOption.value = SortOption.LOWEST_PRICE
    }

    // Product Detail & Comparison
    fun selectProduct(product: ProductEntity) {
        _selectedProduct.value = product
        viewModelScope.launch {
            repository.getReviews(product.id).collect {
                _productReviews.value = it
            }
        }
        // Load comparison sellers for the same school and category
        viewModelScope.launch {
            repository.getSimilarProductsForComparison(product.schoolName, product.category).collect {
                _comparisonProducts.value = it
            }
        }
    }

    // Cart actions
    fun addToCart(product: ProductEntity, size: String, quantity: Int = 1) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.addToCart(user.id, product.id, size, quantity)
            _statusMessage.value = "Added ${product.name} (Size $size) to Cart"
        }
    }

    fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeFromCart(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(cartItemId)
            _statusMessage.value = "Item removed from cart"
        }
    }

    fun applyVoucher(voucher: VoucherItem) {
        val margin = platformSettings.value.platformMarginPercentage
        val cartSubtotal: Double = _cartItems.value.fold(0.0) { acc, item ->
            val base = item.product.basePrice * item.cartItem.quantity
            acc + base + (base * margin / 100.0)
        }
        if (cartSubtotal < voucher.minCartValue) {
            val needed = (voucher.minCartValue - cartSubtotal).toInt()
            _statusMessage.value = "Add ₹$needed more to apply '${voucher.code}'"
            return
        }
        val savings = voucher.calculateSavings(cartSubtotal)
        _appliedVoucher.value = voucher
        _couponCode.value = voucher.code
        _discountAmount.value = savings
        _couponMessage.value = "Voucher '${voucher.code}' applied! ₹${savings.toInt()} saved"
        _statusMessage.value = "🎉 Voucher applied! ₹${savings.toInt()} discount"
    }

    fun applyCoupon(code: String) {
        val trimmed = code.trim().uppercase()
        val matchedVoucher = AvailableVouchers.list.find { it.code.equals(trimmed, ignoreCase = true) }
        if (matchedVoucher != null) {
            applyVoucher(matchedVoucher)
            return
        }
        _couponCode.value = trimmed
        _appliedVoucher.value = null
        if (trimmed == "DRESS10" || trimmed == "SDH50") {
            _discountAmount.value = 50.0
            _couponMessage.value = "Coupon applied! ₹50 Discount"
            _statusMessage.value = "Coupon applied! ₹50 Discount"
        } else if (trimmed == "UNIFORM20") {
            _discountAmount.value = 100.0
            _couponMessage.value = "Special Promo applied! ₹100 Discount"
            _statusMessage.value = "Special Promo applied! ₹100 Discount"
        } else {
            _discountAmount.value = 0.0
            _couponMessage.value = "Invalid or expired coupon code"
            _statusMessage.value = "Invalid coupon code"
        }
    }

    fun removeCoupon() {
        _couponCode.value = ""
        _appliedVoucher.value = null
        _discountAmount.value = 0.0
        _couponMessage.value = null
        _statusMessage.value = "Coupon removed"
    }

    // Checkout & Order Placement
    fun placeOrder(
        deliveryAddress: String,
        deliveryCity: String,
        deliveryPincode: String,
        deliveryType: String,
        paymentMethod: String,
        onlineDiscount: Double = 0.0,
        voucherDiscount: Double = _discountAmount.value,
        voucherCode: String = _couponCode.value,
        onSuccess: (String) -> Unit
    ) {
        val user = _currentUser.value ?: return
        val items = _cartItems.value
        if (items.isEmpty()) {
            _statusMessage.value = "Cart is empty"
            return
        }

        viewModelScope.launch {
            try {
                val orderId = repository.placeOrder(
                    customer = user,
                    cartItems = items,
                    deliveryAddress = deliveryAddress,
                    deliveryCity = deliveryCity,
                    deliveryPincode = deliveryPincode,
                    deliveryType = deliveryType,
                    paymentMethod = paymentMethod,
                    discountAmount = voucherDiscount + onlineDiscount,
                    voucherCode = voucherCode,
                    voucherDiscount = voucherDiscount,
                    onlinePaymentDiscount = onlineDiscount
                )
                _discountAmount.value = 0.0
                _couponCode.value = ""
                _appliedVoucher.value = null
                _couponMessage.value = null
                _statusMessage.value = "Order placed successfully!"
                onSuccess(orderId)
            } catch (e: Exception) {
                _statusMessage.value = "Failed to place order: ${e.message}"
            }
        }
    }

    // Review product
    fun submitReview(productId: String, rating: Int, comment: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.addReview(productId, user.id, user.name, rating, comment)
            _statusMessage.value = "Thank you for your rating & review!"
        }
    }

    // Seller: Add Product with Live Margin Calculation
    fun calculatePreviewPricing(basePrice: Double): PricingCalculation {
        val margin = platformSettings.value.platformMarginPercentage
        return calculateProductPricing(basePrice, margin)
    }

    fun addSellerProduct(
        school: SchoolEntity,
        name: String,
        category: String,
        description: String,
        brand: String,
        gender: String,
        classGroup: String,
        color: String,
        sizes: String,
        material: String,
        basePrice: Double,
        stock: Int,
        deliveryDays: Int = 2,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val product = ProductEntity(
                id = "prod_" + UUID.randomUUID().toString().take(8),
                sellerId = user.id,
                sellerName = user.businessName.ifBlank { user.name },
                sellerRole = user.role,
                sellerCity = user.city,
                sellerDistanceKm = (1..6).random() + 0.5,
                schoolId = school.id,
                schoolName = school.name,
                name = name,
                category = category,
                description = description,
                brand = brand.ifBlank { "Standard Brand" },
                gender = gender,
                classGroup = classGroup,
                color = color,
                availableSizes = sizes,
                material = material,
                basePrice = basePrice,
                stockQuantity = stock,
                imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&auto=format&fit=crop",
                isApproved = !platformSettings.value.productApprovalRequired,
                isActive = true,
                rating = 5.0,
                reviewCount = 0,
                isBestPrice = false,
                deliveryDays = deliveryDays
            )
            repository.addProduct(product)
            _statusMessage.value = "Product published successfully!"
            onSuccess()
        }
    }

    fun updateProductStock(product: ProductEntity, newStock: Int) {
        viewModelScope.launch {
            repository.updateProduct(product.copy(stockQuantity = newStock))
            _statusMessage.value = "Stock updated to $newStock"
        }
    }

    fun toggleProductActive(product: ProductEntity) {
        viewModelScope.launch {
            val updated = product.copy(isActive = !product.isActive)
            repository.updateProduct(updated)
            _statusMessage.value = if (updated.isActive) "Product activated" else "Product deactivated"
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String, customerId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus, customerId)
            _statusMessage.value = "Order updated to $newStatus"
        }
    }

    fun requestSellerPayout(amount: Double, bankAccount: String, ifsc: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.requestPayout(user.id, user.businessName.ifBlank { user.name }, amount, bankAccount, ifsc)
            _statusMessage.value = "Payout request of ₹$amount submitted."
        }
    }

    // Admin Controls
    fun updatePlatformMargin(newMarginPercentage: Double) {
        viewModelScope.launch {
            repository.updatePlatformMargin(newMarginPercentage)
            _statusMessage.value = "Platform margin updated to $newMarginPercentage%!"
        }
    }

    fun updateDeliveryFeeSettings(deliveryFee: Double, freeThreshold: Double) {
        viewModelScope.launch {
            repository.updateDeliverySettings(deliveryFee, freeThreshold)
            _statusMessage.value = "Delivery settings updated!"
        }
    }

    fun verifySeller(sellerId: String, approve: Boolean) {
        viewModelScope.launch {
            repository.verifySeller(sellerId, approve)
            _statusMessage.value = if (approve) "Seller approved and verified!" else "Seller verification revoked"
        }
    }

    fun approveProduct(productId: String, approve: Boolean) {
        viewModelScope.launch {
            repository.approveProduct(productId, approve)
            _statusMessage.value = if (approve) "Product approved!" else "Product rejected"
        }
    }

    fun processPayout(payoutId: Long, sellerId: String, approve: Boolean) {
        viewModelScope.launch {
            repository.processPayout(payoutId, sellerId, approve)
            _statusMessage.value = if (approve) "Payout approved and sent to bank" else "Payout rejected"
        }
    }

    fun markNotificationsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markNotificationsRead(user.id)
        }
    }
}
