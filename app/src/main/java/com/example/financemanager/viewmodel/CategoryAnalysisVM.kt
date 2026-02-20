package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class CategoryAnalysisVM(
    private val em: ExpenseManagementInternal
) : ViewModel() {
    private var _selectedCategory = MutableStateFlow<Int?>(null)
    val selectedCategory: MutableStateFlow<Int?> get() = _selectedCategory

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionForCategoryCurrentTimeframe: StateFlow<List<Transaction>> = selectedCategory
        .flatMapLatest { id ->
            em.getTransactionsByCategoryCurrentTimeframe(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}