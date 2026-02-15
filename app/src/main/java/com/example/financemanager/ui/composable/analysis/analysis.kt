package com.example.financemanager.ui.composable.analysis

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.financemanager.viewmodel.LoginVM

@Composable
fun AnalysisScreen(navController: NavController, viewModel: LoginVM) {
    Text(text = "Analysis of Spending")
}