package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ListingReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingReportDao {
    @Query("SELECT * FROM listing_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ListingReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ListingReportEntity)

    @Update
    suspend fun updateReport(report: ListingReportEntity)

    @Query("UPDATE listing_reports SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)
}
