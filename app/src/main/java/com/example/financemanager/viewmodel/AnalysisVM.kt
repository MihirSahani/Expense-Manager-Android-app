package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.TransactionSummary
import com.example.financemanager.internal.ExpenseManagementInternal
import com.example.financemanager.internal.Keys
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import kotlin.math.abs

data class CategorySpending(
    val category: Category,
    val totalSpending: Double,
    val budget: Double?
)

class AnalysisVM(private var em: ExpenseManagementInternal): ViewModel() {

    private val now = LocalDate.now()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val categorySpendingList: StateFlow<List<CategorySpending>> = em.getSettingFlow(Keys.BUDGET_TIMEFRAME)
        .flatMapLatest { budgetTimeframe ->
            val transactionSumFlow = if (budgetTimeframe == 1L) {
                em.getTransactionSumBySalaryDateFlow()
            } else {
                em.getTransactionSumByMonthFlow(now.year, now.monthValue)
            }
            combine(
                em.getCategoriesFlow(),
                transactionSumFlow
            ) { categories, transactionSum ->
                transformToSpendingList(categories, transactionSum)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun transformToSpendingList(categories: List<Category>, transactionSum: List<TransactionSummary>): List<CategorySpending> {
        val spendingList = categories
            .filter { it.type == "Expense" }
            .map { category ->
                val sumForCategory = transactionSum.find { it.categoryId == category.id }?.totalAmount ?: 0.0
                CategorySpending(
                    category = category,
                    totalSpending = abs(sumForCategory),
                    budget = category.monthlyBudget,
                )
            }.toMutableList()

        // Handle Uncategorized transactions
        val uncategorizedSum = transactionSum.find { it.categoryId == null }?.totalAmount ?: 0.0
        if (abs(uncategorizedSum) > 0) {
            spendingList.add(
                CategorySpending(
                    category = Category(
                        id = -1, // Virtual ID for uncategorized
                        name = "Uncategorized",
                        description = "Transactions without a category",
                        type = "Expense",
                        color = "#808080", // Gray,
                        updatedAt = "",
                        createdAt = "",
                    ),
                    totalSpending = abs(uncategorizedSum),
                    budget = null
                )
            )
        }

        return spendingList.sortedByDescending { it.totalSpending }
    }

    val isCategoryTransactionLoaded: StateFlow<Boolean> = categorySpendingList
        .map { true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}
