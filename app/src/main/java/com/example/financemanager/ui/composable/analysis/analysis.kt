package com.example.financemanager.ui.composable.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Category
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.category.parseColor
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.composable.utils.MyText.toIndianFormat
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.AnalysisVM
import com.example.financemanager.viewmodel.CategoryAnalysisVM
import com.example.financemanager.viewmodel.data.CategorySpending
import java.util.Locale
import kotlin.math.abs

@Composable
fun AnalysisScreen(navController: NavController, viewModel: AnalysisVM, categoryAnalysisVM: CategoryAnalysisVM) {
    val categorySpendingList by viewModel.categorySpendingList.collectAsState()
    val netTransactionPrevCycle by viewModel.netTransactionPreviousCycle.collectAsState()
    val netTransactionCurrCycle by viewModel.netTransactionCurrentCycle.collectAsState()



    AnalysisScreenContent(
        categorySpendingList = categorySpendingList,
        netTransactionPrevMonth = netTransactionPrevCycle,
        netTransactionCurrMonth = netTransactionCurrCycle,
        onCategoryClick = { categoryId ->
            categoryAnalysisVM.selectedCategory.value = categoryId
            navController.navigate(Screen.ViewTransactionByCategory.route)
        }
    )
}

@Composable
fun AnalysisScreenContent(
    categorySpendingList: List<CategorySpending>,
    netTransactionPrevMonth: Double = 0.0,
    netTransactionCurrMonth: Double = 0.0,
    onCategoryClick: (Int?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MyText.ScreenHeader("Analysis")
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val label = if (netTransactionPrevMonth >= 0) "you saved" else "you overspent"
                val color = if (netTransactionPrevMonth >= 0) Color(0xFF02AF34) else Color(0xFF9B2600)

                MyText.Body(text = "Last month $label", color = MaterialTheme.colorScheme.onSurfaceVariant)
                MyText.Header1(abs(netTransactionPrevMonth).toIndianFormat(), color = color)
            }

            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val label = if (netTransactionCurrMonth>= 0) "balance remaining" else "you overspent"
                val color = if (netTransactionCurrMonth>= 0) Color(0xFF02AF34) else Color(0xFF9B2600)

                MyText.Body(text = "This month's $label ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                MyText.Header1(abs(netTransactionCurrMonth).toIndianFormat(), color = color)
            }
        }


        ListOfItems(categorySpendingList, Modifier.padding(16.dp)) { spending ->
            CategoryAnalysisItem(spending, onCategoryClick)
        }
    }
}
@Composable
fun CategoryAnalysisItem(spending: CategorySpending, onCategoryClick: (Int?) -> Unit) {
       Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCategoryClick(if(spending.category.id != -1) spending.category.id else null) }
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
                    MyText.Subtitle(text ="Spent: ${spending.totalSpending.toIndianFormat()}"
                    )
                    if (spending.budget != null) {
                        MyText.Subtitle(
                            text = "Budget: ${spending.budget.toIndianFormat()}"
                        )
                    }
                }
                if (spending.budget != null) {

                    Spacer(modifier = Modifier.height(8.dp))

                    val progress = when(spending.totalSpending < spending.budget) {
                        true -> (spending.totalSpending / spending.budget)
                            .coerceAtMost(1.0).toFloat()
                        false -> (spending.budget / spending.totalSpending)
                            .coerceAtMost(1.0).toFloat()
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        gapSize = (-10).dp,
                        strokeCap = StrokeCap.Round,
                        drawStopIndicator = {},
                        trackColor = if (spending.totalSpending > spending.budget) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.surfaceContainer,
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
            netTransactionPrevMonth = 1250.50,
            netTransactionCurrMonth = -500.00,
            onCategoryClick = {}
        )
    }
}
