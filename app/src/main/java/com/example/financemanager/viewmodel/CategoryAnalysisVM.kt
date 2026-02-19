package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class CategoryAnalysisVM(
    private val expenseManagementInternal: ExpenseManagementInternal, categoryId: Int
) : ViewModel() {
    private var _selectedCategory = MutableStateFlow<Int?>(null)
    val selectedCategory: MutableStateFlow<Int?> get() = _selectedCategory

    private var _transactionForMonth = expenseManagementInternal.getTransactionsByCategoryFlow(
        _selectedCategory.value, LocalDate.now().year, LocalDate.now().monthValue
    ).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val transactionForMonth: StateFlow<List<Transaction>> get() = _transactionForMonth

    init {
        _selectedCategory.value = categoryId
    }
}