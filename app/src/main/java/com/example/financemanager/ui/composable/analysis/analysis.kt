package com.example.financemanager.ui.composable.analysis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.viewmodel.AnalysisVM
import java.util.Locale

@Composable
fun AnalysisScreen(navController: NavController, viewModel: AnalysisVM) {
    viewModel.loadCategoryTransaction()
    val categorySpendingList by viewModel.categorySpendingList.collectAsState()
    val isLoaded by viewModel.isCategoryTransactionLoaded.collectAsState()

    Scaffold(
        topBar = {
            Text(
                text = "Analysis of Spending (Current Month)",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerPadding ->
        if (!isLoaded) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorySpendingList) { spending ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    Screen.ViewTransactionByCategory
                                        .createRoute(spending.category.id)
                                )
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = spending.category.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            if (spending.budget != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Spent: ${
                                            String.format(
                                                Locale.getDefault(), "%.2f",
                                                spending.totalSpending
                                            )
                                        }"
                                    )
                                    Text(
                                        text = "Budget: ${
                                            String.format(
                                                Locale.getDefault(), "%.2f",
                                                spending.budget
                                            )
                                        }"
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                val progress = if (spending.budget > 0) {
                                    (spending.totalSpending / spending.budget)
                                        .coerceAtMost(1.0).toFloat()
                                } else {
                                    0f
                                }
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = if (spending.totalSpending > spending.budget)
                                        MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.primary
                                )
                                if (spending.totalSpending > spending.budget && spending.budget > 0) {
                                    Text(
                                        text = "Over Budget!",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}