package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClaimEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClaimDao {
    @Query("SELECT * FROM claims ORDER BY createdAt DESC")
    fun getAllClaims(): Flow<List<ClaimEntity>>

    @Query("SELECT * FROM claims WHERE id = :id LIMIT 1")
    fun getClaimByIdFlow(id: String): Flow<ClaimEntity?>

    @Query("SELECT * FROM claims WHERE id = :id LIMIT 1")
    suspend fun getClaimById(id: String): ClaimEntity?

    @Query("SELECT * FROM claims WHERE itemId = :itemId ORDER BY createdAt DESC")
    fun getClaimsForItem(itemId: String): Flow<List<ClaimEntity>>

    @Query("SELECT * FROM claims WHERE claimerUserId = :userId ORDER BY createdAt DESC")
    fun getClaimsByClaimer(userId: String): Flow<List<ClaimEntity>>

    @Query("SELECT * FROM claims WHERE ownerFinderUserId = :userId ORDER BY createdAt DESC")
    fun getClaimsForOwnerFinder(userId: String): Flow<List<ClaimEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClaim(claim: ClaimEntity)

    @Update
    suspend fun updateClaim(claim: ClaimEntity)

    @Query("UPDATE claims SET status = :status, responseNotes = :notes WHERE id = :claimId")
    suspend fun updateClaimStatus(claimId: String, status: String, notes: String?)
}
