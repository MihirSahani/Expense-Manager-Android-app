package com.example.financemanager.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

const val LOGIN_ROUTE = "login_screen"

@Composable
fun Navigate() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != LOGIN_ROUTE) {
                NavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LOGIN_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(LOGIN_ROUTE) { LoginScreen(navController) }
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Accounts.route) { AccountsScreen() }
            composable(Screen.Analysis.route) { AnalysisScreen() }
            composable(Screen.TransactionHistory.route) { TransactionHistoryScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}