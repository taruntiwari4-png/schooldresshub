package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        UserEntity::class,
        SchoolEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        ReviewEntity::class,
        PlatformSettingsEntity::class,
        PayoutEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun marketplaceDao(): MarketplaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "schooldresshub_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
