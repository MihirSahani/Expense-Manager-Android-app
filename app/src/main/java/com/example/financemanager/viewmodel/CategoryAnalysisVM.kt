package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CategoryAnalysisVM(
    private val expenseManagementInternal: ExpenseManagementInternal, categoryId: Int
) : ViewModel() {
    private var _transactionForMonth = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionForMonth: MutableStateFlow<List<Transaction>> get() = _transactionForMonth

    private var _isTransactionLoaded = MutableStateFlow(false)
    val isTransactionLoaded: MutableStateFlow<Boolean> get() = _isTransactionLoaded

    private var _selectedCategory = MutableStateFlow<Int?>(0)
    val selectedCategory: MutableStateFlow<Int?> get() = _selectedCategory

    init {
        _selectedCategory.value = categoryId
    }

    fun loadTransaction() {
        _isTransactionLoaded.value = false
        viewModelScope.launch {
            _transactionForMonth.value = expenseManagementInternal.getTransactionsByCategory(
                _selectedCategory.value, LocalDate.now().year, LocalDate.now().monthValue
            )
            _isTransactionLoaded.value = true
        }
    }
}