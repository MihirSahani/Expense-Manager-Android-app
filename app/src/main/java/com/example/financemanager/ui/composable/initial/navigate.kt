package com.example.financemanager.ui.composable.initial

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financemanager.Graph
import com.example.financemanager.ui.composable.NavigationBar
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.setting.SettingsScreen
import com.example.financemanager.ui.composable.setting.UpdateUserDetailsScreen
import com.example.financemanager.ui.composable.account.AccountsScreen
import com.example.financemanager.ui.composable.analysis.AnalysisScreen
import com.example.financemanager.ui.composable.category.AddEditCategoryScreen
import com.example.financemanager.ui.composable.category.CategoryScreen
import com.example.financemanager.ui.composable.home.HomeScreen
import com.example.financemanager.ui.composable.transaction.TransactionHistoryScreen
import com.example.financemanager.viewmodel.*

@Composable
fun Navigate() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Login.route &&
                currentRoute != Screen.Permissions.route &&
                currentRoute != Screen.UpdateUserDetails.route) {
                NavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Permissions.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Permissions.route) {
                Permission(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.LOGIN) as LoginVM
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.LOGIN) as LoginVM
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.HOME) as LoginVM
                )
            }
            composable(Screen.Accounts.route) {
                AccountsScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.ACCOUNTS) as AccountVM
                )
            }
            composable(Screen.Analysis.route) {
                AnalysisScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.HOME) as LoginVM
                )
            }
            composable(Screen.TransactionHistory.route) {
                TransactionHistoryScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.TRANSACTION) as TransactionVM
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }
            composable(Screen.UpdateUserDetails.route) {
                UpdateUserDetailsScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.USER_DETAILS_UPDATE) as UserVM
                )
            }
            composable(Screen.Categories.route) {
                CategoryScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.CATEGORY) as CategoryVM
                )
            }
            composable(Screen.AddEditCategory.route) {
                val categoryId = it.arguments?.getString("categoryId")?.toIntOrNull() ?: -1
                AddEditCategoryScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.CATEGORY) as CategoryVM
                )
            }
        }
    }
}