package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ClaimEntity
import com.example.data.model.ItemEntity
import com.example.data.model.ListingReportEntity
import com.example.data.model.MessageEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ItemEntity::class,
        ClaimEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        ListingReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
    abstract fun claimDao(): ClaimDao
    abstract fun messageDao(): MessageDao
    abstract fun notificationDao(): NotificationDao
    abstract fun listingReportDao(): ListingReportDao

    companion object {
        @Volatile
        private var INSTANCE: CampusDatabase? = null

        fun getDatabase(context: Context): CampusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    "campus_find_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
