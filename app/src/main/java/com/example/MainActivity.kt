package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ItemType
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ClaimDetailScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ItemDetailScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MyReportsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.ReportItemScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CampusViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CampusFindApp()
                }
            }
        }
    }
}

@Composable
fun CampusFindApp(viewModel: CampusViewModel = viewModel()) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val items by viewModel.allItems.collectAsState()
    val users by viewModel.allUsers.collectAsState()
    val claims by viewModel.allClaims.collectAsState()
    val reports by viewModel.allReports.collectAsState()
    val unreadCount by viewModel.unreadNotifCount.collectAsState()
    val notifications by viewModel.userNotifications.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    when (val screen = currentScreen) {
        is Screen.Splash -> {
            SplashScreen(
                currentUser = currentUser,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        is Screen.Login -> {
            LoginScreen(
                onLogin = { email, pass, onSuccess, onError ->
                    viewModel.login(email, pass, onSuccess, onError)
                },
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        is Screen.Register -> {
            RegisterScreen(
                onRegister = { name, email, pass, id, dept, phone, role, onSuccess, onError ->
                    viewModel.register(name, email, pass, id, dept, phone, role, onSuccess, onError)
                },
                onNavigateBack = { viewModel.navigateBack() }
            )
        }

        is Screen.ForgotPassword -> {
            ForgotPasswordScreen(
                onReset = { email, pass, onSuccess, onError ->
                    viewModel.resetPassword(email, pass, onSuccess, onError)
                },
                onNavigateBack = { viewModel.navigateBack() }
            )
        }

        is Screen.Home -> {
            HomeScreen(
                currentUser = currentUser,
                items = items,
                unreadCount = unreadCount,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }

        is Screen.Search -> {
            SearchScreen(
                items = items,
                initialType = screen.initialType,
                onNavigateBack = { viewModel.navigateBack() },
                onNavigateToItem = { viewModel.navigateTo(Screen.ItemDetail(it)) }
            )
        }

        is Screen.ReportItem -> {
            ReportItemScreen(
                type = screen.type,
                onNavigateBack = { viewModel.navigateBack() },
                onSubmitItem = { type, title, cat, desc, loc, date, proof, img, onSuccess, onError ->
                    viewModel.createItem(type, title, cat, desc, loc, date, proof, img, onSuccess, onError)
                },
                onSuccessNav = { itemId ->
                    viewModel.navigateBack()
                    viewModel.navigateTo(Screen.ItemDetail(itemId))
                }
            )
        }

        is Screen.ItemDetail -> {
            ItemDetailScreen(
                itemFlow = viewModel.getItemFlow(screen.itemId),
                claimsFlow = viewModel.getClaimsForItem(screen.itemId),
                currentUser = currentUser,
                onNavigateBack = { viewModel.navigateBack() },
                onRequestClaim = { itemId, proof, onSuccess, onError ->
                    viewModel.requestClaim(itemId, proof, onSuccess, onError)
                },
                onReportListing = { itemId, reason, details, onComplete ->
                    viewModel.reportListing(itemId, reason, details, onComplete)
                },
                onMarkAsReturned = { itemId ->
                    viewModel.markItemAsReturned(itemId)
                },
                onDeleteItem = { itemId, onSuccess ->
                    viewModel.deleteItem(itemId, onSuccess)
                },
                onNavigateToClaim = { claimId ->
                    viewModel.navigateTo(Screen.ClaimDetail(claimId))
                }
            )
        }

        is Screen.ClaimDetail -> {
            ClaimDetailScreen(
                claimFlow = viewModel.getClaimFlow(screen.claimId),
                messagesFlow = viewModel.getMessagesForClaim(screen.claimId),
                currentUser = currentUser,
                onNavigateBack = { viewModel.navigateBack() },
                onReviewClaim = { claimId, accept, notes, onComplete ->
                    viewModel.reviewClaim(claimId, accept, notes, onComplete)
                },
                onSendMessage = { claimId, itemId, text ->
                    viewModel.sendMessage(claimId, itemId, text)
                },
                onMarkAsReturned = { itemId ->
                    viewModel.markItemAsReturned(itemId)
                }
            )
        }

        is Screen.MyReports -> {
            MyReportsScreen(
                items = items,
                claims = claims,
                currentUser = currentUser,
                initialTabIndex = screen.initialTabIndex,
                onNavigateBack = { viewModel.navigateBack() },
                onNavigateToItem = { viewModel.navigateTo(Screen.ItemDetail(it)) },
                onNavigateToClaim = { viewModel.navigateTo(Screen.ClaimDetail(it)) },
                onDeleteItem = { itemId, onSuccess ->
                    viewModel.deleteItem(itemId, onSuccess)
                },
                onUpdateItem = { item, onSuccess ->
                    viewModel.updateItem(item, onSuccess)
                }
            )
        }

        is Screen.Notifications -> {
            NotificationsScreen(
                notifications = notifications,
                onNavigateBack = { viewModel.navigateBack() },
                onMarkAsRead = { viewModel.markNotificationAsRead(it) },
                onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
                onNavigateToItem = { viewModel.navigateTo(Screen.ItemDetail(it)) },
                onNavigateToClaim = { viewModel.navigateTo(Screen.ClaimDetail(it)) }
            )
        }

        is Screen.Admin -> {
            AdminScreen(
                currentUser = currentUser,
                users = users,
                items = items,
                claims = claims,
                reports = reports,
                onNavigateBack = { viewModel.navigateBack() },
                onNavigateToItem = { viewModel.navigateTo(Screen.ItemDetail(it)) },
                onNavigateToClaim = { viewModel.navigateTo(Screen.ClaimDetail(it)) },
                onDeleteItem = { itemId, onSuccess ->
                    viewModel.deleteItem(itemId, onSuccess)
                },
                onToggleUserSuspension = { userId, suspend ->
                    viewModel.adminToggleUserSuspension(userId, suspend)
                },
                onResolveReport = { reportId, dismiss ->
                    viewModel.adminResolveReport(reportId, dismiss)
                }
            )
        }

        is Screen.Profile -> {
            ProfileScreen(
                currentUser = currentUser,
                allUsers = users,
                onNavigateBack = { viewModel.navigateBack() },
                onNavigateToAdmin = { viewModel.navigateTo(Screen.Admin) },
                onSwitchUser = { viewModel.switchUser(it) },
                onLogout = { viewModel.logout() }
            )
        }
    }
}
