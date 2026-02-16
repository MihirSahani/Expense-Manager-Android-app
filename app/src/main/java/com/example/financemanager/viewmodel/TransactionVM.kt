package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionVM(private val expenseManagementInternal: ExpenseManagementInternal) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _transactions.value = expenseManagementInternal.getTransactions()
        }
    }

    fun selectTransaction(transaction: Transaction?) {
        _selectedTransaction.value = transaction
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            expenseManagementInternal.addTransaction(transaction)
            loadTransactions()
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
            loadTransactions()
        }
    }

    fun updateTransactionAccount(transaction: Transaction) {
        viewModelScope.launch {
            expenseManagementInternal.updateTransactionAccount(transaction)
            _selectedTransaction.value = transaction
            loadTransactions()
        }
    }

}
