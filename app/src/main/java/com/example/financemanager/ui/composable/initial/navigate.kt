package com.example.financemanager.ui.composable.initial

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.financemanager.ui.composable.account.AddEditAccountScreen
import com.example.financemanager.ui.composable.analysis.AnalysisScreen
import com.example.financemanager.ui.composable.analysis.ViewTransactionByCategoryScreen
import com.example.financemanager.ui.composable.category.AddEditCategoryScreen
import com.example.financemanager.ui.composable.category.CategoryScreen
import com.example.financemanager.ui.composable.home.HomeScreen
import com.example.financemanager.ui.composable.transaction.AddTransactionScreen
import com.example.financemanager.ui.composable.transaction.TransactionHistoryScreen
import com.example.financemanager.ui.composable.transaction.ViewTransactionScreen
import com.example.financemanager.viewmodel.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigate(viewModel: InitialVM) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        viewModel.navigateToEditTransaction.collect {
            navController.navigate(Screen.ViewTransaction.route)
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute == Screen.Home.route ||
                currentRoute == Screen.Analysis.route ||
                currentRoute == Screen.TransactionHistory.route ||
                currentRoute == Screen.Settings.route
            ) {
                NavigationBar(navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Home.route ||
                currentRoute == Screen.TransactionHistory.route
            ) {
                FloatingActionButton(onClick = {
                    navController.navigate(Screen.AddTransaction.route)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
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
                    Graph.viewModelFactory.getViewModel(ViewModelName.LOGIN)
                            as InitialVM
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.LOGIN)
                            as InitialVM
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.HOME)
                            as InitialVM
                )
            }
            composable(Screen.Accounts.route) {
                AccountsScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.ACCOUNTS)
                            as AccountVM
                )
            }
            composable(Screen.AddEditAccount.route) {
                AddEditAccountScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.ACCOUNTS)
                            as AccountVM
                )
            }
            composable(Screen.Analysis.route) {
                AnalysisScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.ANALYSIS)
                            as AnalysisVM,
                    Graph.viewModelFactory.getViewModel(ViewModelName.CATEGORY_ANALYSIS)
                            as CategoryAnalysisVM
                )
            }
            composable(Screen.TransactionHistory.route) {
                TransactionHistoryScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.TRANSACTION)
                            as TransactionVM
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.SETTINGS) as SettingsVM
                )
            }
            composable(Screen.UpdateUserDetails.route) {
                UpdateUserDetailsScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.USER_DETAILS_UPDATE)
                            as UserVM
                )
            }
            composable(Screen.Categories.route) {
                CategoryScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.CATEGORY)
                            as CategoryVM
                )
            }
            composable(Screen.AddEditCategory.route) {
                AddEditCategoryScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.CATEGORY)
                            as CategoryVM
                )
            }
            composable(Screen.ViewTransaction.route) {
                ViewTransactionScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.TRANSACTION)
                            as TransactionVM,
                    Graph.viewModelFactory.getViewModel(ViewModelName.CATEGORY) as CategoryVM,
                    Graph.viewModelFactory.getViewModel(ViewModelName.ACCOUNTS) as AccountVM
                )
            }

            composable(route = Screen.ViewTransactionByCategory.route) {
                ViewTransactionByCategoryScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.CATEGORY_ANALYSIS)
                            as CategoryAnalysisVM,
                    Graph.viewModelFactory.getViewModel(ViewModelName.TRANSACTION)
                            as TransactionVM
                )
            }

            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    navController,
                    Graph.viewModelFactory.getViewModel(ViewModelName.TRANSACTION)
                            as TransactionVM
                )
            }
        }
    }
}