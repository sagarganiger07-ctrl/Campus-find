package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun getItemByIdFlow(id: String): Flow<ItemEntity?>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: String): ItemEntity?

    @Query("SELECT * FROM items WHERE postedByUserId = :userId ORDER BY createdAt DESC")
    fun getItemsByUser(userId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE postedByUserId = :userId AND type = :type ORDER BY createdAt DESC")
    fun getItemsByUserAndType(userId: String, type: String): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItem(id: String)

    @Query("UPDATE items SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: String)

    @Query("UPDATE items SET status = 'RETURNED', returnedAt = :returnedAt WHERE id = :id")
    suspend fun markAsReturned(id: String, returnedAt: Long = System.currentTimeMillis())

    @Query("UPDATE items SET isReportedByUsers = :reported, reportedReason = :reason WHERE id = :id")
    suspend fun reportItem(id: String, reported: Boolean, reason: String?)
}
