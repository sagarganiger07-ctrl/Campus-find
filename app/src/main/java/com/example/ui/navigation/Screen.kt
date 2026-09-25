package com.example.ui.navigation

import com.example.data.model.ItemType

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Register : Screen()
    object ForgotPassword : Screen()
    object Home : Screen()
    data class Search(val initialType: String? = null) : Screen()
    data class ReportItem(val type: ItemType, val editItemId: String? = null) : Screen()
    data class ItemDetail(val itemId: String) : Screen()
    data class MyReports(val initialTabIndex: Int = 0) : Screen()
    data class ClaimDetail(val claimId: String) : Screen()
    object Notifications : Screen()
    object Admin : Screen()
    object Profile : Screen()
}
