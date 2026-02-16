package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs

data class CategorySpending(
    val category: Category,
    val totalSpending: Double,
    val budget: Double?
)

class AnalysisVM(private var expenseManagementInternal: ExpenseManagementInternal): ViewModel() {
    private val _isCategoryTransactionLoaded = MutableStateFlow(false)
    val isCategoryTransactionLoaded: StateFlow<Boolean> get() = _isCategoryTransactionLoaded

    private val _categorySpendingList = MutableStateFlow<List<CategorySpending>>(emptyList())
    val categorySpendingList: StateFlow<List<CategorySpending>> get() = _categorySpendingList

    init {
        loadCategoryTransaction()
    }

    fun loadCategoryTransaction() {
        _isCategoryTransactionLoaded.value = false
        viewModelScope.launch {
            val categories = expenseManagementInternal.getCategories()
            val now = LocalDate.now()
            val transactionSum = expenseManagementInternal.getTransactionSumByMonth(
                now.year, now.monthValue
            )


            val spendingList = categories.filter { it.type == "Expense" }.map { category ->
                val categoryTransactions = transactionSum.filter { it.categoryId == category.id }
                CategorySpending(
                    category = category,
                    totalSpending = abs(categoryTransactions.find {
                        it.categoryId == category.id
                    }?.totalAmount ?: 0.0),
                    budget = category.monthlyBudget,
                )
            }

            _categorySpendingList.value = spendingList
            _isCategoryTransactionLoaded.value = true
        }
    }
}