package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class ItemType {
    LOST, FOUND
}

enum class ItemStatus {
    ACTIVE,
    CLAIM_REQUESTED,
    CLAIM_APPROVED,
    RETURNED,
    CLOSED
}

enum class ClaimStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    RETURNED
}

enum class UserRole {
    STUDENT,
    STAFF,
    ADMIN
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val studentOrStaffId: String,
    val department: String,
    val phone: String = "",
    val role: String = UserRole.STUDENT.name,
    val avatarInitials: String = "",
    val isSuspended: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String, // "LOST" or "FOUND"
    val title: String,
    val category: String,
    val description: String,
    val location: String,
    val eventDate: String,
    val identifyingDetails: String = "",
    val imageUri: String? = null,
    val postedByUserId: String,
    val postedByName: String,
    val status: String = ItemStatus.ACTIVE.name,
    val isReportedByUsers: Boolean = false,
    val reportedReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val returnedAt: Long? = null
)

@Entity(tableName = "claims")
data class ClaimEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val itemId: String,
    val itemTitle: String,
    val claimerUserId: String,
    val claimerName: String,
    val claimerStudentId: String,
    val ownerFinderUserId: String,
    val proofDetails: String,
    val status: String = ClaimStatus.PENDING.name,
    val createdAt: Long = System.currentTimeMillis(),
    val responseNotes: String? = null
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val claimId: String,
    val itemId: String,
    val senderUserId: String,
    val senderName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // "CLAIM_REQUEST", "CLAIM_ACCEPTED", "CLAIM_REJECTED", "MESSAGE", "STATUS_CHANGE", "MATCH_ALERT"
    val relatedItemId: String? = null,
    val relatedClaimId: String? = null,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "listing_reports")
data class ListingReportEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val itemId: String,
    val itemTitle: String,
    val reportedByUserId: String,
    val reason: String,
    val details: String,
    val status: String = "OPEN", // "OPEN", "RESOLVED", "DISMISSED"
    val timestamp: Long = System.currentTimeMillis()
)
