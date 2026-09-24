package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ProductEntity
import com.example.data.model.UserRoles
import com.example.ui.components.RoleSwitcherBar
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.common.AccountScreen
import com.example.ui.screens.common.LocationDialog
import com.example.ui.screens.common.NotificationsDialog
import com.example.ui.screens.customer.CartScreen
import com.example.ui.screens.customer.CheckoutScreen
import com.example.ui.screens.customer.HomeScreen
import com.example.ui.screens.customer.OrderHistoryScreen
import com.example.ui.screens.customer.PriceComparisonDialog
import com.example.ui.screens.customer.ProductCatalogScreen
import com.example.ui.screens.customer.ProductDetailScreen
import com.example.ui.screens.seller.SellerAddProductScreen
import com.example.ui.screens.seller.SellerDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SchoolAmber
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.MarketplaceViewModel

object NavRoutes {
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val PRODUCT_DETAIL = "product_detail"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDERS = "orders"
    const val SELLER_DASHBOARD = "seller_dashboard"
    const val SELLER_ADD_PRODUCT = "seller_add_product"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ACCOUNT = "account"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

class MainActivity : ComponentActivity() {

    private val viewModel: MarketplaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MarketplaceViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val platformSettings by viewModel.platformSettings.collectAsStateWithLifecycle()

    val schools by viewModel.schools.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedSchoolId by viewModel.selectedSchoolId.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedGender by viewModel.selectedGender.collectAsStateWithLifecycle()
    val selectedSortOption by viewModel.selectedSortOption.collectAsStateWithLifecycle()

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val customerOrders by viewModel.customerOrders.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val allSellers by viewModel.allSellers.collectAsStateWithLifecycle()
    val pendingSellers by viewModel.pendingSellers.collectAsStateWithLifecycle()
    val pendingProducts by viewModel.pendingProducts.collectAsStateWithLifecycle()
    val allPayouts by viewModel.allPayouts.collectAsStateWithLifecycle()

    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val sellerProducts by viewModel.sellerProducts.collectAsStateWithLifecycle()
    val sellerOrders by viewModel.sellerOrders.collectAsStateWithLifecycle()
    val sellerPayouts by viewModel.sellerPayouts.collectAsStateWithLifecycle()

    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val comparisonProducts by viewModel.comparisonProducts.collectAsStateWithLifecycle()
    val productReviews by viewModel.productReviews.collectAsStateWithLifecycle()

    val couponCode by viewModel.couponCode.collectAsStateWithLifecycle()
    val discountAmount by viewModel.discountAmount.collectAsStateWithLifecycle()
    val couponMessage by viewModel.couponMessage.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    // Navigation and Dialog state
    var currentRoute by remember { mutableStateOf(NavRoutes.HOME) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showComparisonDialog by remember { mutableStateOf(false) }
    var comparisonTargetProduct by remember { mutableStateOf<ProductEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    // Role-dependent route adjustment when switching
    LaunchedEffect(currentUser?.role) {
        when (currentUser?.role) {
            UserRoles.WHOLESALER, UserRoles.SHOPKEEPER -> {
                if (currentRoute == NavRoutes.HOME || currentRoute == NavRoutes.ADMIN_DASHBOARD) {
                    currentRoute = NavRoutes.SELLER_DASHBOARD
                }
            }
            UserRoles.ADMIN -> {
                if (currentRoute == NavRoutes.HOME || currentRoute == NavRoutes.SELLER_DASHBOARD) {
                    currentRoute = NavRoutes.ADMIN_DASHBOARD
                }
            }
            UserRoles.CUSTOMER -> {
                if (currentRoute == NavRoutes.SELLER_DASHBOARD || currentRoute == NavRoutes.ADMIN_DASHBOARD) {
                    currentRoute = NavRoutes.HOME
                }
            }
        }
    }

    // Back handler
    BackHandler(enabled = currentRoute != NavRoutes.HOME && currentRoute != NavRoutes.SELLER_DASHBOARD && currentRoute != NavRoutes.ADMIN_DASHBOARD) {
        when (currentUser?.role) {
            UserRoles.WHOLESALER, UserRoles.SHOPKEEPER -> currentRoute = NavRoutes.SELLER_DASHBOARD
            UserRoles.ADMIN -> currentRoute = NavRoutes.ADMIN_DASHBOARD
            else -> currentRoute = NavRoutes.HOME
        }
    }

    val currentRole = currentUser?.role ?: UserRoles.CUSTOMER
    val platformMarginPercentage = platformSettings.platformMarginPercentage

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Quick Role Switcher Bar at the top of the entire app
            RoleSwitcherBar(
                currentRole = currentRole,
                onRoleSelected = { newRole ->
                    viewModel.switchRole(newRole)
                }
            )
        },
        bottomBar = {
            // Role-adaptive Bottom Navigation Bar
            val navItems = when (currentRole) {
                UserRoles.WHOLESALER, UserRoles.SHOPKEEPER -> listOf(
                    BottomNavItem(NavRoutes.SELLER_DASHBOARD, "Dashboard", Icons.Default.Dashboard, sellerOrders.size),
                    BottomNavItem(NavRoutes.SELLER_ADD_PRODUCT, "Add Uniform", Icons.Default.AddBusiness),
                    BottomNavItem(NavRoutes.CATALOG, "Marketplace", Icons.Default.Checkroom),
                    BottomNavItem(NavRoutes.ACCOUNT, "Shop Profile", Icons.Default.Person)
                )
                UserRoles.ADMIN -> listOf(
                    BottomNavItem(NavRoutes.ADMIN_DASHBOARD, "Admin Panel", Icons.Default.AdminPanelSettings, pendingSellers.size),
                    BottomNavItem(NavRoutes.CATALOG, "All Uniforms", Icons.Default.Checkroom),
                    BottomNavItem(NavRoutes.ACCOUNT, "Account", Icons.Default.Person)
                )
                else -> listOf(
                    BottomNavItem(NavRoutes.HOME, "Home", Icons.Default.Home),
                    BottomNavItem(NavRoutes.CATALOG, "Uniforms", Icons.Default.Checkroom),
                    BottomNavItem(NavRoutes.CART, "Cart", Icons.Default.ShoppingCart, cartItems.size),
                    BottomNavItem(NavRoutes.ORDERS, "Orders", Icons.Default.ReceiptLong),
                    BottomNavItem(NavRoutes.ACCOUNT, "Account", Icons.Default.Person)
                )
            }

            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentRoute = item.route },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            if (item.badgeCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = SchoolAmber) {
                                            Text(
                                                "${item.badgeCount}",
                                                color = Slate900,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                ) {
                                    Icon(item.icon, contentDescription = item.label)
                                }
                            } else {
                                Icon(item.icon, contentDescription = item.label)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SchoolNavy,
                            selectedTextColor = SchoolNavy,
                            indicatorColor = Color(0xFFEFF6FF)
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                // --- Customer Flows ---
                NavRoutes.HOME -> {
                    HomeScreen(
                        currentLocation = currentLocation,
                        schools = schools,
                        categories = categories,
                        products = allProducts,
                        sellers = allSellers,
                        cartItemCount = cartItems.size,
                        notificationCount = notifications.count { !it.isRead },
                        platformMarginPercentage = platformMarginPercentage,
                        onSearchClick = { currentRoute = NavRoutes.CATALOG },
                        onSchoolSelect = { school ->
                            viewModel.selectSchoolFilter(school.id)
                            currentRoute = NavRoutes.CATALOG
                        },
                        onCategorySelect = { category ->
                            viewModel.selectCategoryFilter(category)
                            currentRoute = NavRoutes.CATALOG
                        },
                        onProductClick = { product ->
                            viewModel.selectProduct(product)
                            currentRoute = NavRoutes.PRODUCT_DETAIL
                        },
                        onCompareClick = { product ->
                            comparisonTargetProduct = product
                            viewModel.selectProduct(product)
                            showComparisonDialog = true
                        },
                        onCartClick = { currentRoute = NavRoutes.CART },
                        onNotificationClick = { showNotificationsDialog = true },
                        onLocationClick = { showLocationDialog = true },
                        onViewAllProducts = { currentRoute = NavRoutes.CATALOG }
                    )
                }

                NavRoutes.CATALOG -> {
                    ProductCatalogScreen(
                        searchQuery = searchQuery,
                        selectedSchoolId = selectedSchoolId,
                        selectedCategory = selectedCategory,
                        selectedGender = selectedGender,
                        selectedSortOption = selectedSortOption,
                        schools = schools,
                        categories = categories,
                        products = filteredProducts,
                        platformMarginPercentage = platformMarginPercentage,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onSchoolSelect = { viewModel.selectSchoolFilter(it) },
                        onCategorySelect = { viewModel.selectCategoryFilter(it) },
                        onGenderSelect = { viewModel.selectGenderFilter(it) },
                        onSortSelect = { viewModel.setSortOption(it) },
                        onClearFilters = { viewModel.clearFilters() },
                        onProductClick = { product ->
                            viewModel.selectProduct(product)
                            currentRoute = NavRoutes.PRODUCT_DETAIL
                        },
                        onCompareClick = { product ->
                            comparisonTargetProduct = product
                            viewModel.selectProduct(product)
                            showComparisonDialog = true
                        }
                    )
                }

                NavRoutes.PRODUCT_DETAIL -> {
                    selectedProduct?.let { product ->
                        ProductDetailScreen(
                            product = product,
                            reviews = productReviews,
                            platformMarginPercentage = platformMarginPercentage,
                            onBackClick = { currentRoute = NavRoutes.CATALOG },
                            onCompareSellersClick = {
                                comparisonTargetProduct = product
                                showComparisonDialog = true
                            },
                            onAddToCart = { prod, size, qty ->
                                viewModel.addToCart(prod, size, qty)
                            },
                            onBuyNow = { prod, size, qty ->
                                viewModel.addToCart(prod, size, qty)
                                currentRoute = NavRoutes.CART
                            },
                            onSubmitReview = { prodId, rating, comment ->
                                viewModel.submitReview(prodId, rating, comment)
                            }
                        )
                    } ?: run {
                        currentRoute = NavRoutes.CATALOG
                    }
                }

                NavRoutes.CART -> {
                    CartScreen(
                        cartItems = cartItems,
                        platformMarginPercentage = platformMarginPercentage,
                        deliveryFee = platformSettings.defaultDeliveryFee,
                        freeDeliveryThreshold = platformSettings.freeDeliveryThreshold,
                        couponCode = couponCode,
                        discountAmount = discountAmount,
                        couponMessage = couponMessage,
                        onQuantityChange = { itemId, qty -> viewModel.updateCartQuantity(itemId, qty) },
                        onRemoveItem = { itemId -> viewModel.removeFromCart(itemId) },
                        onApplyCoupon = { code -> viewModel.applyCoupon(code) },
                        onRemoveCoupon = { viewModel.removeCoupon() },
                        onProceedToCheckout = { currentRoute = NavRoutes.CHECKOUT },
                        onContinueShopping = { currentRoute = NavRoutes.CATALOG }
                    )
                }

                NavRoutes.CHECKOUT -> {
                    currentUser?.let { user ->
                        CheckoutScreen(
                            user = user,
                            cartItems = cartItems,
                            platformMarginPercentage = platformMarginPercentage,
                            deliveryFee = platformSettings.defaultDeliveryFee,
                            freeDeliveryThreshold = platformSettings.freeDeliveryThreshold,
                            discountAmount = discountAmount,
                            couponCode = couponCode,
                            onApplyCoupon = { code -> viewModel.applyCoupon(code) },
                            onRemoveCoupon = { viewModel.removeCoupon() },
                            onBackClick = { currentRoute = NavRoutes.CART },
                            onPlaceOrder = { address, city, pincode, deliveryType, paymentMethod, onlineDiscount, voucherDiscount, voucherCode ->
                                viewModel.placeOrder(
                                    deliveryAddress = address,
                                    deliveryCity = city,
                                    deliveryPincode = pincode,
                                    deliveryType = deliveryType,
                                    paymentMethod = paymentMethod,
                                    onlineDiscount = onlineDiscount,
                                    voucherDiscount = voucherDiscount,
                                    voucherCode = voucherCode,
                                    onSuccess = {
                                        currentRoute = NavRoutes.ORDERS
                                    }
                                )
                            }
                        )
                    }
                }

                NavRoutes.ORDERS -> {
                    OrderHistoryScreen(
                        orders = customerOrders,
                        onBackClick = { currentRoute = NavRoutes.HOME }
                    )
                }

                // --- Seller Flows ---
                NavRoutes.SELLER_DASHBOARD -> {
                    currentUser?.let { user ->
                        SellerDashboardScreen(
                            user = user,
                            products = sellerProducts,
                            ordersWithItems = sellerOrders,
                            payouts = sellerPayouts,
                            platformMarginPercentage = platformMarginPercentage,
                            onAddNewProduct = { currentRoute = NavRoutes.SELLER_ADD_PRODUCT },
                            onUpdateStock = { prod, stock -> viewModel.updateProductStock(prod, stock) },
                            onToggleActive = { prod -> viewModel.toggleProductActive(prod) },
                            onUpdateOrderStatus = { orderId, status, custId ->
                                viewModel.updateOrderStatus(orderId, status, custId)
                            },
                            onRequestPayout = { amt, acc, ifsc ->
                                viewModel.requestSellerPayout(amt, acc, ifsc)
                            }
                        )
                    }
                }

                NavRoutes.SELLER_ADD_PRODUCT -> {
                    SellerAddProductScreen(
                        schools = schools,
                        categories = categories,
                        platformMarginPercentage = platformMarginPercentage,
                        onBackClick = { currentRoute = NavRoutes.SELLER_DASHBOARD },
                        onPublishProduct = { school, name, cat, desc, brand, gender, classGroup, color, sizes, mat, basePrice, stock, days ->
                            viewModel.addSellerProduct(
                                school = school,
                                name = name,
                                category = cat,
                                description = desc,
                                brand = brand,
                                gender = gender,
                                classGroup = classGroup,
                                color = color,
                                sizes = sizes,
                                material = mat,
                                basePrice = basePrice,
                                stock = stock,
                                deliveryDays = days,
                                onSuccess = {
                                    currentRoute = NavRoutes.SELLER_DASHBOARD
                                }
                            )
                        }
                    )
                }

                // --- Admin Flows ---
                NavRoutes.ADMIN_DASHBOARD -> {
                    AdminDashboardScreen(
                        settings = platformSettings,
                        allOrders = allOrders,
                        allSellers = allSellers,
                        pendingSellers = pendingSellers,
                        pendingProducts = pendingProducts,
                        allProducts = allProducts,
                        payouts = allPayouts,
                        onUpdatePlatformMargin = { newMargin ->
                            viewModel.updatePlatformMargin(newMargin)
                        },
                        onUpdateDeliveryFeeSettings = { fee, threshold ->
                            viewModel.updateDeliveryFeeSettings(fee, threshold)
                        },
                        onVerifySeller = { sellerId, approve ->
                            viewModel.verifySeller(sellerId, approve)
                        },
                        onApproveProduct = { prodId, approve ->
                            viewModel.approveProduct(prodId, approve)
                        },
                        onProcessPayout = { payoutId, sellerId, approve ->
                            viewModel.processPayout(payoutId, sellerId, approve)
                        }
                    )
                }

                // --- Common Account Screen ---
                NavRoutes.ACCOUNT -> {
                    currentUser?.let { user ->
                        AccountScreen(
                            user = user,
                            platformMarginPercentage = platformMarginPercentage,
                            onSwitchRole = { role ->
                                viewModel.switchRole(role)
                            },
                            onViewMyOrders = { currentRoute = NavRoutes.ORDERS }
                        )
                    }
                }
            }
        }
    }

    // Compare Sellers Dialog
    if (showComparisonDialog) {
        val target = comparisonTargetProduct ?: selectedProduct
        if (target != null) {
            PriceComparisonDialog(
                selectedProduct = target,
                comparisonSellers = if (comparisonProducts.isNotEmpty()) comparisonProducts else listOf(target),
                platformMarginPercentage = platformMarginPercentage,
                onSelectSellerProduct = { selectedSellerProduct ->
                    viewModel.selectProduct(selectedSellerProduct)
                },
                onDismiss = {
                    showComparisonDialog = false
                    comparisonTargetProduct = null
                }
            )
        }
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        NotificationsDialog(
            notifications = notifications,
            onMarkAllRead = { viewModel.markNotificationsRead() },
            onDismiss = { showNotificationsDialog = false }
        )
    }

    // Location Dialog
    if (showLocationDialog) {
        LocationDialog(
            currentLocation = currentLocation,
            onLocationSelected = { loc -> viewModel.setLocation(loc) },
            onDismiss = { showLocationDialog = false }
        )
    }
}
