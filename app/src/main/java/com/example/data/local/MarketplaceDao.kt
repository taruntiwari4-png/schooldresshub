package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.PayoutEntity
import com.example.data.model.PlatformSettingsEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.SchoolEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketplaceDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserByIdOnce(id: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role IN ('WHOLESALER', 'SHOPKEEPER')")
    fun getAllSellers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role IN ('WHOLESALER', 'SHOPKEEPER') AND isVerified = 0")
    fun getPendingSellers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isVerified = :isVerified WHERE id = :userId")
    suspend fun setSellerVerified(userId: String, isVerified: Boolean)

    // --- Schools ---
    @Query("SELECT * FROM schools ORDER BY name ASC")
    fun getAllSchools(): Flow<List<SchoolEntity>>

    @Query("SELECT * FROM schools WHERE id = :id")
    suspend fun getSchoolById(id: String): SchoolEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchools(schools: List<SchoolEntity>)

    // --- Categories ---
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    // --- Products ---
    @Query("SELECT * FROM products WHERE isApproved = 1 AND isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductByIdOnce(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE schoolId = :schoolId AND isApproved = 1 AND isActive = 1")
    fun getProductsBySchool(schoolId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category AND isApproved = 1 AND isActive = 1")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isApproved = 0 ORDER BY createdAt DESC")
    fun getPendingProducts(): Flow<List<ProductEntity>>

    @Query("""
        SELECT * FROM products 
        WHERE schoolName = :schoolName 
          AND category = :category 
          AND isApproved = 1 
          AND isActive = 1
        ORDER BY basePrice ASC
    """)
    fun getSimilarProductsForComparison(schoolName: String, category: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET isApproved = :approved WHERE id = :productId")
    suspend fun setProductApproval(productId: String, approved: Boolean)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: String)

    // --- Cart ---
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getCartItems(userId: String): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId AND selectedSize = :size LIMIT 1")
    suspend fun findCartItem(userId: String, productId: String, size: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateCartItemQuantity(id: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: String)

    // --- Orders ---
    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getCustomerOrders(customerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderByIdOnce(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsOnce(orderId: String): List<OrderItemEntity>

    @Query("SELECT * FROM order_items WHERE sellerId = :sellerId ORDER BY id DESC")
    fun getOrderItemsForSeller(sellerId: String): Flow<List<OrderItemEntity>>

    // --- Reviews ---
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
    fun getReviewsForProduct(productId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    // --- Platform Settings ---
    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    fun getPlatformSettings(): Flow<PlatformSettingsEntity?>

    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    suspend fun getPlatformSettingsOnce(): PlatformSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatformSettings(settings: PlatformSettingsEntity)

    @Query("UPDATE platform_settings SET platformMarginPercentage = :margin WHERE id = 1")
    suspend fun updatePlatformMargin(margin: Double)

    @Query("UPDATE platform_settings SET defaultDeliveryFee = :deliveryFee, freeDeliveryThreshold = :threshold WHERE id = 1")
    suspend fun updateDeliverySettings(deliveryFee: Double, threshold: Double)

    // --- Payouts ---
    @Query("SELECT * FROM payouts WHERE sellerId = :sellerId ORDER BY requestDate DESC")
    fun getPayoutsBySeller(sellerId: String): Flow<List<PayoutEntity>>

    @Query("SELECT * FROM payouts ORDER BY requestDate DESC")
    fun getAllPayouts(): Flow<List<PayoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutEntity)

    @Query("UPDATE payouts SET status = :status, processedDate = :processedDate WHERE id = :id")
    suspend fun updatePayoutStatus(id: Long, status: String, processedDate: Long)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotifications(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markNotificationsAsRead(userId: String)
}
