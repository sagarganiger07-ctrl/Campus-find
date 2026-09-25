package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ClaimEntity
import com.example.data.model.ItemEntity
import com.example.data.model.ItemType
import com.example.data.model.ListingReportEntity
import com.example.data.model.MessageEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.data.repository.CampusRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CampusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CampusRepository.getInstance(application)

    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Splash))
    val screenStack: StateFlow<List<Screen>> = _screenStack.asStateFlow()

    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Splash).apply {
        viewModelScope.launch {
            _screenStack.collect { stack ->
                value = stack.lastOrNull() ?: Screen.Home
            }
        }
    }

    val currentUser: StateFlow<UserEntity?> = repository.currentUser

    val allItems: StateFlow<List<ItemEntity>> = repository.allItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allClaims: StateFlow<List<ClaimEntity>> = repository.allClaims
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<ListingReportEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userNotifications: StateFlow<List<NotificationEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getNotificationsForUser(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getUnreadNotificationCount(user.id) else flowOf(0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    // Navigation methods
    fun navigateTo(screen: Screen) {
        val current = _screenStack.value.toMutableList()
        // If navigating to Home after Auth/Splash, reset stack
        if (screen is Screen.Home) {
            _screenStack.value = listOf(Screen.Home)
        } else {
            current.add(screen)
            _screenStack.value = current
        }
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value.toMutableList()
        return if (current.size > 1) {
            current.removeAt(current.size - 1)
            _screenStack.value = current
            true
        } else {
            false
        }
    }

    // Auth actions
    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            result.onSuccess {
                showToast("Welcome back, ${it.fullName}!")
                _screenStack.value = listOf(Screen.Home)
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Login failed")
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        pass: String,
        studentId: String,
        dept: String,
        phone: String,
        role: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.register(fullName, email, pass, studentId, dept, phone, role)
            result.onSuccess {
                showToast("Registration successful! Welcome to CampusFind.")
                _screenStack.value = listOf(Screen.Home)
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Registration failed")
            }
        }
    }

    fun resetPassword(email: String, newPass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val res = repository.resetPassword(email, newPass)
            res.onSuccess {
                showToast("Password updated successfully. You can now login.")
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Failed to reset password")
            }
        }
    }

    fun logout() {
        repository.logout()
        _screenStack.value = listOf(Screen.Login)
        showToast("Logged out successfully.")
    }

    fun switchUser(user: UserEntity) {
        repository.switchUser(user)
        showToast("Switched active profile to ${user.fullName} (${user.role})")
    }

    // Item actions
    fun getItemFlow(itemId: String): Flow<ItemEntity?> = repository.getItem(itemId)

    fun createItem(
        type: ItemType,
        title: String,
        category: String,
        description: String,
        location: String,
        eventDate: String,
        identifyingDetails: String,
        imageUri: String?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.createItem(
                type, title, category, description, location, eventDate, identifyingDetails, imageUri
            )
            res.onSuccess { id ->
                showToast("${if (type == ItemType.LOST) "Lost" else "Found"} report posted successfully!")
                onSuccess(id)
            }.onFailure {
                onError(it.message ?: "Failed to post report")
            }
        }
    }

    fun updateItem(item: ItemEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateItem(item)
            showToast("Listing updated")
            onSuccess()
        }
    }

    fun deleteItem(itemId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
            showToast("Item listing removed")
            onSuccess()
        }
    }

    fun markItemAsReturned(itemId: String) {
        viewModelScope.launch {
            repository.markItemAsReturned(itemId)
            showToast("Success! Item marked as Returned & Reunited.")
        }
    }

    // Claim actions
    fun getClaimsForItem(itemId: String): Flow<List<ClaimEntity>> = repository.getClaimsForItem(itemId)
    fun getClaimFlow(claimId: String): Flow<ClaimEntity?> = repository.getClaim(claimId)
    fun getClaimsByClaimer(userId: String): Flow<List<ClaimEntity>> = repository.getClaimsByClaimer(userId)
    fun getClaimsForOwnerFinder(userId: String): Flow<List<ClaimEntity>> = repository.getClaimsForOwnerFinder(userId)

    fun requestClaim(itemId: String, proofDetails: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val res = repository.requestClaim(itemId, proofDetails)
            res.onSuccess {
                showToast("Claim request sent to finder! They will review your proof.")
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Failed to submit claim")
            }
        }
    }

    fun reviewClaim(claimId: String, accept: Boolean, notes: String?, onComplete: () -> Unit) {
        viewModelScope.launch {
            val res = repository.reviewClaim(claimId, accept, notes)
            res.onSuccess {
                showToast(if (accept) "Claim approved! You can now coordinate handover." else "Claim declined.")
                onComplete()
            }
        }
    }

    // Messaging actions
    fun getMessagesForClaim(claimId: String): Flow<List<MessageEntity>> = repository.getMessagesForClaim(claimId)

    fun sendMessage(claimId: String, itemId: String, messageText: String) {
        viewModelScope.launch {
            if (messageText.isBlank()) return@launch
            repository.sendMessage(claimId, itemId, messageText)
        }
    }

    // Listing report (Flagging inappropriate items)
    fun reportListing(itemId: String, reason: String, details: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.reportListing(itemId, reason, details)
            showToast("Thank you. Listing has been flagged for Campus Admin review.")
            onComplete()
        }
    }

    // Notifications
    fun markNotificationAsRead(id: String) {
        viewModelScope.launch { repository.markNotificationAsRead(id) }
    }

    fun markAllNotificationsAsRead() {
        val user = currentUser.value ?: return
        viewModelScope.launch { repository.markAllNotificationsAsRead(user.id) }
    }

    // Admin operations
    fun adminToggleUserSuspension(userId: String, suspend: Boolean) {
        viewModelScope.launch {
            repository.adminToggleUserSuspension(userId, suspend)
            showToast(if (suspend) "User suspended from campus platform" else "User suspension lifted")
        }
    }

    fun adminResolveReport(reportId: String, dismiss: Boolean) {
        viewModelScope.launch {
            repository.adminResolveReport(reportId, dismiss)
            showToast(if (dismiss) "Report dismissed" else "Report resolved")
        }
    }
}
