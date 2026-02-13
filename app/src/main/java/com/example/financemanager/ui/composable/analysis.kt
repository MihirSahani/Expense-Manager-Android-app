package com.example.financemanager.ui.composable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.financemanager.viewmodel.HomeVM

@Composable
fun AnalysisScreen(navController: NavController, viewModel: HomeVM) {
    Text(text = "Analysis of Spending")
}