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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Category
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.category.parseColor
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.AnalysisVM
import com.example.financemanager.viewmodel.CategorySpending
import java.util.Locale

@Composable
fun AnalysisScreen(navController: NavController, viewModel: AnalysisVM) {
    val categorySpendingList by viewModel.categorySpendingList.collectAsState()

    AnalysisScreenContent(
        categorySpendingList = categorySpendingList,
        onCategoryClick = { categoryId ->
            navController.navigate(
                Screen.ViewTransactionByCategory.createRoute(categoryId)
            )
        }
    )
}

@Composable
fun AnalysisScreenContent(
    categorySpendingList: List<CategorySpending>,
    onCategoryClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MyText.ScreenHeader("Category")
        ListOfItems(categorySpendingList, Modifier.padding(16.dp)) { spending ->
            CategoryAnalysisItem(spending, onCategoryClick)
        }
    }
}
@Composable
fun CategoryAnalysisItem(spending: CategorySpending, onCategoryClick: (Int) -> Unit) {
       Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCategoryClick(spending.category.id) }
                .padding(16.dp),
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
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MyText.Header1(spending.category.name)
                    if (spending.category.description.isNotBlank()) {
                        MyText.Body(text = spending.category.description)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // ------------------------------------------------------- Cat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MyText.Subtitle(text ="Spent: ${
                        String.format(
                            Locale.getDefault(), "%.2f",
                            spending.totalSpending
                        )}"
                    )
                    if (spending.budget != null) {
                        MyText.Subtitle(
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
                        MyText.Body(text = "Over Budget!")
                    }
                }
            }
        }
}

@Preview(showBackground = true)
@Composable
fun AnalysisScreenPreview() {
    val sampleSpending = listOf(
        CategorySpending(
            category = Category(id = 1, name = "Food", description = "Groceries", type = "Expense", color = "#FF5733", createdAt = "", updatedAt = "", monthlyBudget = 5000.0),
            totalSpending = 3500.0,
            budget = 5000.0
        ),
        CategorySpending(
            category = Category(id = 2, name = "Rent", description = "Monthly rent", type = "Expense", color = "#3357FF", createdAt = "", updatedAt = "", monthlyBudget = 15000.0),
            totalSpending = 15000.0,
            budget = 15000.0
        ),
        CategorySpending(
            category = Category(id = 3, name = "Entertainment", description = "Movies, etc", type = "Expense", color = "#F033FF", createdAt = "", updatedAt = "", monthlyBudget = 2000.0),
            totalSpending = 2500.0,
            budget = 2000.0
        )
    )
    FinanceManagerTheme {
        AnalysisScreenContent(
            categorySpendingList = sampleSpending,
            onCategoryClick = {}
        )
    }
}
