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
import com.example.financemanager.Graph
import com.example.financemanager.viewmodel.*

const val LOGIN_ROUTE = "login_screen"

@Composable
fun Navigate() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != LOGIN_ROUTE && currentRoute != Screen.UpdateUserDetails.route) {
                NavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LOGIN_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(LOGIN_ROUTE) { 
                LoginScreen(navController, Graph.viewModelFactory.getViewModel(ViewModelName.LOGIN) as LoginVM)
            }
            composable(Screen.Home.route) { 
                HomeScreen(navController, Graph.viewModelFactory.getViewModel(ViewModelName.HOME) as HomeVM) 
            }
            composable(Screen.Accounts.route) { 
                AccountsScreen(navController, Graph.viewModelFactory.getViewModel(ViewModelName.ACCOUNTS) as AccountVM) 
            }
            composable(Screen.Analysis.route) { 
                AnalysisScreen(navController, Graph.viewModelFactory.getViewModel(ViewModelName.HOME) as HomeVM) 
            }
            composable(Screen.TransactionHistory.route) { 
                TransactionHistoryScreen(navController, Graph.viewModelFactory.getViewModel(ViewModelName.TRANSACTION) as TransactionVM) 
            }
            composable(Screen.Settings.route) { 
                SettingsScreen(navController) 
            }
            composable(Screen.UpdateUserDetails.route) {
                UpdateUserDetailsScreen(navController, Graph.viewModelFactory.getViewModel(ViewModelName.USER_DETAILS_UPDATE) as UserVM)
            }
        }
    }
}