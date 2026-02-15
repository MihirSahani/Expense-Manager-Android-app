package com.example.financemanager.ui.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Permissions : Screen("permissions", "Permissions", Icons.Default.Security)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Accounts : Screen("accounts", "Accounts", Icons.Default.AccountBalanceWallet)
    object AddEditAccount : Screen("add_edit_account", "Add/Edit Account", Icons.Default.Add)
    object Analysis : Screen("analysis", "Analysis", Icons.Default.PieChart)
    object TransactionHistory : Screen("history", "History", Icons.Default.History)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object UpdateUserDetails : Screen("update_user_details", "Update Details", Icons.Default.Person)
    object Login : Screen("login", "Login", Icons.Default.Person)
    object Categories : Screen("categories", "Categories", Icons.Default.Category)
    object AddEditCategory : Screen("add_edit_category", "Add/Edit Category", Icons.Default.Category)
    object ViewTransaction : Screen("view_transaction", "View Transaction", Icons.Default.History)
}
