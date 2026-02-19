package com.example.financemanager.ui.composable.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.category.parseColor
import com.example.financemanager.viewmodel.AnalysisVM
import com.example.financemanager.viewmodel.CategorySpending
import java.util.Locale

@Composable
fun AnalysisScreen(navController: NavController, viewModel: AnalysisVM) {
    val categorySpendingList by viewModel.categorySpendingList.collectAsState()

    Scaffold(
        topBar = {
            Text(
                text = "Analysis of Spending",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerPadding ->
        LazyColumn(
           modifier = Modifier
               .fillMaxSize()
               .padding(innerPadding)
               .padding(16.dp),
           verticalArrangement = Arrangement.spacedBy(8.dp)
       ) {
           items(categorySpendingList) { spending ->
               CategoryAnalysisItem(spending, navController)
           }
       }
    }
}

@Composable
fun CategoryAnalysisItem(spending: CategorySpending, navController: NavController) {
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // ------------------------------------------------------Icon
            if (spending.category.id == -1) { // Uncategorized
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Uncategorized",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(parseColor(spending.category.color))
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ----------------------------------------------------- Name,desc of card
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = spending.category.name,
                    style = MaterialTheme.typography.titleLarge
                )
                if (spending.category.description.isNotBlank()) {
                    Text(
                        text = spending.category.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // ------------------------------------------------------- Cat
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
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (spending.budget != null) {
                        Text(
                            text = "Budget: ${
                                String.format(
                                    Locale.getDefault(), "%.2f",
                                    spending.budget
                                )
                            }"
                        )
                    }
                }
                if (spending.budget != null) {

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