package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.emptyList

class TransactionVM(private val expenseManagementInternal: ExpenseManagementInternal) : ViewModel() {

    private val _transactions = expenseManagementInternal.getTransactionsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _categories = expenseManagementInternal.getCategoriesFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _accounts = expenseManagementInternal.getAccountsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val accounts: StateFlow<List<Account>> = _accounts

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction

    fun selectTransaction(transaction: Transaction?) {
        _selectedTransaction.value = transaction
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            expenseManagementInternal.addTransaction(transaction)
        }
    }

    fun updateTransactionCategory(
        transaction: Transaction, updateCategoryForAllTransactionsWithPayee: Boolean
    ) {
        viewModelScope.launch {
            expenseManagementInternal.updateTransactionCategory(
                transaction, updateCategoryForAllTransactionsWithPayee
            )
            _selectedTransaction.value = transaction
        }
    }

    fun updateTransactionAccount(transaction: Transaction) {
        viewModelScope.launch {
            expenseManagementInternal.updateTransactionAccount(transaction)
            _selectedTransaction.value = transaction
        }
    }

    fun dateToString(date: Long): String {
        return SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(date)
    }
}
