package com.example.financemanager.ui.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val iconSelected: ImageVector) {
    object Permissions : Screen("permissions", "Permissions", Icons.Outlined.Security, Icons.Default.Security)
    object Home : Screen("home", "Home", Icons.Outlined.Home, Icons.Default.Home)
    object Accounts : Screen("accounts", "Accounts", Icons.Outlined.AccountBalanceWallet, Icons.Default.AccountBalanceWallet)
    object AddEditAccount : Screen("add_edit_account", "Add/Edit Account", Icons.Outlined.Add, Icons.Default.Add)
    object Analysis : Screen("analysis", "Analysis", Icons.Outlined.PieChart, Icons.Default.PieChart)
    object TransactionHistory : Screen("history", "History", Icons.Outlined.Assignment, Icons.Default.Assignment)
    object Settings : Screen("settings", "Settings", Icons.Outlined.Settings, Icons.Default.Settings)
    object UpdateUserDetails : Screen("update_user_details", "Update Details", Icons.Outlined.Person, Icons.Default.Person)
    object Login : Screen("login", "Login", Icons.Outlined.Person, Icons.Default.Person)
    object Categories : Screen("categories", "Categories", Icons.Outlined.Category, Icons.Default.Category)
    object AddEditCategory : Screen("add_edit_category", "Add/Edit Category", Icons.Outlined.Category, Icons.Default.Category)
    object ViewTransaction : Screen("view_transaction", "View Transaction", Icons.Outlined.Assignment, Icons.Default.Assignment)
    object ViewTransactionByCategory : Screen("view_transaction_by_category","View Transaction By Category", Icons.Outlined.Assignment, Icons.Default.Assignment)
    object AddTransaction : Screen("add_transaction", "Add Transaction", Icons.Outlined.Add, Icons.Default.Add)
}
