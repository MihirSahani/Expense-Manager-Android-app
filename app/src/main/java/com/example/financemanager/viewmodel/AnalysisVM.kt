package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.TransactionSummary
import com.example.financemanager.repository.CategoryRepo
import com.example.financemanager.repository.TransactionRepo
import com.example.financemanager.viewmodel.data.CategorySpending
import kotlinx.coroutines.flow.*
import kotlin.math.abs

class AnalysisVM(
    private val transactionRepo: TransactionRepo,
    private val categoryRepo: CategoryRepo
): ViewModel() {

    val _amountSavedLastTimeframe: StateFlow<Double> = transactionRepo.getSumOfTransactionsPreviousCycle()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val amountSavedLastTimeFrame: StateFlow<Double> = _amountSavedLastTimeframe



    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val categorySpendingList: StateFlow<List<CategorySpending>> =
        transactionRepo.getSumOfTransactionsByCategoryCurrentCycle()
        .combine(categoryRepo.categories) { transactionSum, categories ->
            transformToSpendingList(categories, transactionSum)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun transformToSpendingList(
        categories: List<Category>, transactionSum: List<TransactionSummary>
    ): List<CategorySpending> {
        val spendingList = categories
            .filter { it.type == "Expense" }
            .map { category ->
                val sumForCategory = transactionSum
                    .find { it.categoryId == category.id }?.totalAmount ?: 0.0
                CategorySpending(
                    category = category,
                    totalSpending = abs(sumForCategory),
                    budget = category.monthlyBudget,
                )
            }.toMutableList()

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
}
