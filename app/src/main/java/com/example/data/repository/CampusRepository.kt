package com.example.data.repository

import android.content.Context
import com.example.data.local.CampusDatabase
import com.example.data.model.ClaimEntity
import com.example.data.model.ClaimStatus
import com.example.data.model.ItemEntity
import com.example.data.model.ItemStatus
import com.example.data.model.ItemType
import com.example.data.model.ListingReportEntity
import com.example.data.model.MessageEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CampusRepository(private val db: CampusDatabase) {

    private val userDao = db.userDao()
    private val itemDao = db.itemDao()
    private val claimDao = db.claimDao()
    private val messageDao = db.messageDao()
    private val notificationDao = db.notificationDao()
    private val reportDao = db.listingReportDao()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val allItems: Flow<List<ItemEntity>> = itemDao.getAllItems()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allClaims: Flow<List<ClaimEntity>> = claimDao.getAllClaims()
    val allReports: Flow<List<ListingReportEntity>> = reportDao.getAllReports()

    fun getItem(id: String): Flow<ItemEntity?> = itemDao.getItemByIdFlow(id)
    suspend fun getItemDirect(id: String): ItemEntity? = itemDao.getItemById(id)

    fun getItemsByUser(userId: String): Flow<List<ItemEntity>> = itemDao.getItemsByUser(userId)
    fun getItemsByUserAndType(userId: String, type: String): Flow<List<ItemEntity>> =
        itemDao.getItemsByUserAndType(userId, type)

    fun getClaimsForItem(itemId: String): Flow<List<ClaimEntity>> = claimDao.getClaimsForItem(itemId)
    fun getClaimsByClaimer(userId: String): Flow<List<ClaimEntity>> = claimDao.getClaimsByClaimer(userId)
    fun getClaimsForOwnerFinder(userId: String): Flow<List<ClaimEntity>> = claimDao.getClaimsForOwnerFinder(userId)
    fun getClaim(claimId: String): Flow<ClaimEntity?> = claimDao.getClaimByIdFlow(claimId)

    fun getMessagesForClaim(claimId: String): Flow<List<MessageEntity>> = messageDao.getMessagesForClaim(claimId)

    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId)

    fun getUnreadNotificationCount(userId: String): Flow<Int> =
        notificationDao.getUnreadCount(userId)

    suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingAdmin = userDao.getUserByEmail("admin@campus.edu")
        if (existingAdmin != null) {
            // Already seeded, if no user is currently logged in, log in Alex by default
            if (_currentUser.value == null) {
                val alex = userDao.getUserByEmail("alex@campus.edu")
                if (alex != null && !alex.isSuspended) {
                    _currentUser.value = alex
                }
            }
            return@withContext
        }

        // Seed Users
        val adminUser = UserEntity(
            id = "user_admin_001",
            email = "admin@campus.edu",
            passwordHash = "admin123",
            fullName = "Campus Safety & Admin",
            studentOrStaffId = "STAFF-ADM-01",
            department = "Campus Security & Student Affairs",
            phone = "555-0199",
            role = UserRole.ADMIN.name,
            avatarInitials = "AD"
        )
        val studentAlex = UserEntity(
            id = "user_student_alex",
            email = "alex@campus.edu",
            passwordHash = "student123",
            fullName = "Alex Rivera",
            studentOrStaffId = "STU-2024-8891",
            department = "Computer Science",
            phone = "555-0142",
            role = UserRole.STUDENT.name,
            avatarInitials = "AR"
        )
        val studentMaya = UserEntity(
            id = "user_student_maya",
            email = "maya@campus.edu",
            passwordHash = "student123",
            fullName = "Maya Chen",
            studentOrStaffId = "STU-2023-4412",
            department = "Mechanical Engineering",
            phone = "555-0178",
            role = UserRole.STUDENT.name,
            avatarInitials = "MC"
        )

        userDao.insertUser(adminUser)
        userDao.insertUser(studentAlex)
        userDao.insertUser(studentMaya)

        // Seed Items
        val item1 = ItemEntity(
            id = "item_001",
            type = ItemType.LOST.name,
            title = "Blue Hydro Flask (32 oz) with Campus Stickers",
            category = "Bottles & Lunch",
            description = "Dark blue Hydro Flask with a KLE Tech sticker and a white mountain silhouette sticker. Small dent near bottom rim.",
            location = "Main Library - 3rd Floor Quiet Study",
            eventDate = "2026-09-24",
            identifyingDetails = "Contains lemon slice residue and scratch on base",
            imageUri = null,
            postedByUserId = studentAlex.id,
            postedByName = studentAlex.fullName,
            status = ItemStatus.ACTIVE.name
        )

        val item2 = ItemEntity(
            id = "item_002",
            type = ItemType.FOUND.name,
            title = "AirPods Pro 2 in Matte Black Spigen Case",
            category = "Electronics",
            description = "Found sitting on a table near the microwave corner of the cafeteria. White AirPods inside black protective case.",
            location = "Student Center Cafeteria",
            eventDate = "2026-09-24",
            identifyingDetails = "Laser engraved name initial 'M' on the case inner lid",
            imageUri = null,
            postedByUserId = studentMaya.id,
            postedByName = studentMaya.fullName,
            status = ItemStatus.CLAIM_REQUESTED.name
        )

        val item3 = ItemEntity(
            id = "item_003",
            type = ItemType.FOUND.name,
            title = "Set of 3 Dorm Keys on Red KLE Lanyard",
            category = "Keys",
            description = "Found on a concrete bench facing the Science Quad. Has 2 silver keys and 1 gold brass key plus a gym token.",
            location = "Science Quad Benches",
            eventDate = "2026-09-23",
            identifyingDetails = "Room number stamped on one key ending in 14",
            imageUri = null,
            postedByUserId = studentAlex.id,
            postedByName = studentAlex.fullName,
            status = ItemStatus.ACTIVE.name
        )

        val item4 = ItemEntity(
            id = "item_004",
            type = ItemType.LOST.name,
            title = "Graphing Calculator TI-84 Plus CE",
            category = "Books & Study",
            description = "Black TI-84 Plus CE calculator. Has a small yellow smiley face sticker on the sliding back cover.",
            location = "Engineering Block Hall 302",
            eventDate = "2026-09-22",
            identifyingDetails = "Battery compartment has battery dated AUG 2026",
            imageUri = null,
            postedByUserId = studentMaya.id,
            postedByName = studentMaya.fullName,
            status = ItemStatus.ACTIVE.name
        )

        val item5 = ItemEntity(
            id = "item_005",
            type = ItemType.FOUND.name,
            title = "Black North Face Recon Backpack",
            category = "Bags & Wallets",
            description = "Found on the second row of the gymnasium bleachers after evening intramural basketball. Turned into Campus Security.",
            location = "Campus Sports Arena / Gym",
            eventDate = "2026-09-21",
            identifyingDetails = "Contains a pair of gray running shoes inside",
            imageUri = null,
            postedByUserId = adminUser.id,
            postedByName = adminUser.fullName,
            status = ItemStatus.RETURNED.name,
            returnedAt = System.currentTimeMillis() - 86400000L
        )

        itemDao.insertItem(item1)
        itemDao.insertItem(item2)
        itemDao.insertItem(item3)
        itemDao.insertItem(item4)
        itemDao.insertItem(item5)

        // Seed Sample Claim on item2 (Alex claiming Maya's found AirPods)
        val claim1 = ClaimEntity(
            id = "claim_001",
            itemId = item2.id,
            itemTitle = item2.title,
            claimerUserId = studentAlex.id,
            claimerName = studentAlex.fullName,
            claimerStudentId = studentAlex.studentOrStaffId,
            ownerFinderUserId = studentMaya.id,
            proofDetails = "The AirPods are paired to an iPhone named 'Alex Phone'. The serial number ends in 49J, and inside is engraved with 'M'.",
            status = ClaimStatus.PENDING.name,
            createdAt = System.currentTimeMillis() - 7200000L
        )
        claimDao.insertClaim(claim1)

        // Seed Safe Message
        val msg1 = MessageEntity(
            id = "msg_001",
            claimId = claim1.id,
            itemId = item2.id,
            senderUserId = studentAlex.id,
            senderName = studentAlex.fullName,
            messageText = "Hi Maya, thanks so much for picking these up! I can meet you at the library front desk or student center to verify."
        )
        messageDao.insertMessage(msg1)

        // Seed Notifications for Maya and Alex
        val notif1 = NotificationEntity(
            id = "notif_001",
            userId = studentMaya.id,
            title = "New Claim Request",
            message = "Alex Rivera requested to claim your found item: AirPods Pro 2.",
            type = "CLAIM_REQUEST",
            relatedItemId = item2.id,
            relatedClaimId = claim1.id,
            isRead = false
        )
        val notif2 = NotificationEntity(
            id = "notif_002",
            userId = studentAlex.id,
            title = "Campus Safety Tip",
            message = "Welcome to CampusFind! Always meet in well-lit public campus locations to inspect and return items.",
            type = "STATUS_CHANGE",
            isRead = false
        )
        notificationDao.insertNotification(notif1)
        notificationDao.insertNotification(notif2)

        // Default login: Alex
        _currentUser.value = studentAlex
    }

    suspend fun login(email: String, password: String):Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email.trim())
        if (user == null) {
            return@withContext Result.failure(Exception("No account found with this email."))
        }
        if (user.passwordHash != password.trim()) {
            return@withContext Result.failure(Exception("Incorrect password."))
        }
        if (user.isSuspended) {
            return@withContext Result.failure(Exception("This student/staff account has been suspended by campus administration."))
        }
        _currentUser.value = user
        Result.success(user)
    }

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        studentOrStaffId: String,
        department: String,
        phone: String,
        role: String = UserRole.STUDENT.name
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return@withContext Result.failure(Exception("An account with this email already exists."))
        }

        val initials = fullName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")

        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            email = cleanEmail,
            passwordHash = password.trim(),
            fullName = fullName.trim(),
            studentOrStaffId = studentOrStaffId.trim(),
            department = department.trim(),
            phone = phone.trim(),
            role = role,
            avatarInitials = if (initials.isNotBlank()) initials else "ST"
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        Result.success(newUser)
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        val rows = userDao.updatePasswordByEmail(email.trim(), newPassword.trim())
        if (rows > 0) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("No registered account found with email $email"))
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
    }

    suspend fun createItem(
        type: ItemType,
        title: String,
        category: String,
        description: String,
        location: String,
        eventDate: String,
        identifyingDetails: String,
        imageUri: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("You must be logged in"))
        val newItem = ItemEntity(
            id = UUID.randomUUID().toString(),
            type = type.name,
            title = title.trim(),
            category = category.trim(),
            description = description.trim(),
            location = location.trim(),
            eventDate = eventDate.trim(),
            identifyingDetails = identifyingDetails.trim(),
            imageUri = imageUri,
            postedByUserId = user.id,
            postedByName = user.fullName,
            status = ItemStatus.ACTIVE.name
        )
        itemDao.insertItem(newItem)

        // Check for potential match alert (simple keyword category & title match)
        val oppositeType = if (type == ItemType.LOST) ItemType.FOUND.name else ItemType.LOST.name
        // Create an alert notification for user confirming post
        notificationDao.insertNotification(
            NotificationEntity(
                userId = user.id,
                title = "Listing Published",
                message = "Your ${type.name.lowercase()} report for '${title}' is now live on CampusFind.",
                type = "STATUS_CHANGE",
                relatedItemId = newItem.id
            )
        )
        Result.success(newItem.id)
    }

    suspend fun updateItem(item: ItemEntity): Result<Unit> = withContext(Dispatchers.IO) {
        itemDao.updateItem(item)
        Result.success(Unit)
    }

    suspend fun deleteItem(itemId: String): Result<Unit> = withContext(Dispatchers.IO) {
        itemDao.deleteItem(itemId)
        Result.success(Unit)
    }

    suspend fun markItemAsReturned(itemId: String): Result<Unit> = withContext(Dispatchers.IO) {
        itemDao.markAsReturned(itemId)
        val item = itemDao.getItemById(itemId)
        if (item != null) {
            notificationDao.insertNotification(
                NotificationEntity(
                    userId = item.postedByUserId,
                    title = "Item Marked as Returned",
                    message = "'${item.title}' has been marked as returned and resolved. Thank you for making our campus better!",
                    type = "STATUS_CHANGE",
                    relatedItemId = item.id
                )
            )
        }
        Result.success(Unit)
    }

    suspend fun requestClaim(
        itemId: String,
        proofDetails: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("User must be logged in"))
        val item = itemDao.getItemById(itemId) ?: return@withContext Result.failure(Exception("Item not found"))

        if (item.postedByUserId == user.id) {
            return@withContext Result.failure(Exception("You cannot claim an item you reported."))
        }

        val claim = ClaimEntity(
            id = UUID.randomUUID().toString(),
            itemId = item.id,
            itemTitle = item.title,
            claimerUserId = user.id,
            claimerName = user.fullName,
            claimerStudentId = user.studentOrStaffId,
            ownerFinderUserId = item.postedByUserId,
            proofDetails = proofDetails.trim(),
            status = ClaimStatus.PENDING.name
        )
        claimDao.insertClaim(claim)
        itemDao.updateStatus(itemId, ItemStatus.CLAIM_REQUESTED.name)

        // Notify finder/owner
        notificationDao.insertNotification(
            NotificationEntity(
                userId = item.postedByUserId,
                title = "New Claim Request",
                message = "${user.fullName} (${user.studentOrStaffId}) submitted a claim for '${item.title}'.",
                type = "CLAIM_REQUEST",
                relatedItemId = item.id,
                relatedClaimId = claim.id
            )
        )
        Result.success(claim.id)
    }

    suspend fun reviewClaim(
        claimId: String,
        accept: Boolean,
        responseNotes: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val claim = claimDao.getClaimById(claimId) ?: return@withContext Result.failure(Exception("Claim not found"))
        val newStatus = if (accept) ClaimStatus.ACCEPTED.name else ClaimStatus.REJECTED.name
        claimDao.updateClaimStatus(claimId, newStatus, responseNotes)

        if (accept) {
            itemDao.updateStatus(claim.itemId, ItemStatus.CLAIM_APPROVED.name)
        } else {
            itemDao.updateStatus(claim.itemId, ItemStatus.ACTIVE.name)
        }

        val statusText = if (accept) "Approved! You can now coordinate handover." else "Declined."
        notificationDao.insertNotification(
            NotificationEntity(
                userId = claim.claimerUserId,
                title = if (accept) "Claim Approved!" else "Claim Not Accepted",
                message = "Your claim for '${claim.itemTitle}' was $statusText",
                type = if (accept) "CLAIM_ACCEPTED" else "CLAIM_REJECTED",
                relatedItemId = claim.itemId,
                relatedClaimId = claim.id
            )
        )
        Result.success(Unit)
    }

    suspend fun sendMessage(
        claimId: String,
        itemId: String,
        messageText: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("User must be logged in"))
        val claim = claimDao.getClaimById(claimId) ?: return@withContext Result.failure(Exception("Claim not found"))

        val message = MessageEntity(
            claimId = claimId,
            itemId = itemId,
            senderUserId = user.id,
            senderName = user.fullName,
            messageText = messageText.trim()
        )
        messageDao.insertMessage(message)

        // Determine recipient
        val recipientId = if (claim.claimerUserId == user.id) claim.ownerFinderUserId else claim.claimerUserId
        notificationDao.insertNotification(
            NotificationEntity(
                userId = recipientId,
                title = "New Message from ${user.fullName}",
                message = messageText.take(60),
                type = "MESSAGE",
                relatedItemId = itemId,
                relatedClaimId = claimId
            )
        )
        Result.success(Unit)
    }

    suspend fun reportListing(
        itemId: String,
        reason: String,
        details: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("User must be logged in"))
        val item = itemDao.getItemById(itemId) ?: return@withContext Result.failure(Exception("Item not found"))

        itemDao.reportItem(itemId, true, reason)
        reportDao.insertReport(
            ListingReportEntity(
                itemId = itemId,
                itemTitle = item.title,
                reportedByUserId = user.id,
                reason = reason,
                details = details
            )
        )
        Result.success(Unit)
    }

    suspend fun markNotificationAsRead(id: String) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead(userId: String) = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead(userId)
    }

    suspend fun adminToggleUserSuspension(userId: String, suspend: Boolean) = withContext(Dispatchers.IO) {
        userDao.setSuspendedStatus(userId, suspend)
    }

    suspend fun adminResolveReport(reportId: String, actionDismiss: Boolean) = withContext(Dispatchers.IO) {
        reportDao.updateStatus(reportId, if (actionDismiss) "DISMISSED" else "RESOLVED")
    }

    companion object {
        @Volatile
        private var INSTANCE: CampusRepository? = null

        fun getInstance(context: Context): CampusRepository {
            return INSTANCE ?: synchronized(this) {
                val db = CampusDatabase.getDatabase(context)
                val repo = CampusRepository(db)
                INSTANCE = repo
                // Seed data on background
                CoroutineScope(Dispatchers.IO).launch {
                    repo.seedInitialDataIfNeeded()
                }
                repo
            }
        }
    }
}
