package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.DummyData
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.localstorage.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {
    private val transactionManager = Graph.transactionManager
    private val accountManager = Graph.accountManager
    private val categoryManager = Graph.categoryManager

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    init {
        viewModelScope.launch {
            prepopulateDatabase()
            loadAllTransactions()
        }
    }

    private suspend fun prepopulateDatabase() {
        transactionManager.getAllTransactions()
        if (transactionManager.transactions.isEmpty()) {
            DummyData.accounts.forEach { accountManager.addAccount(it) }
            DummyData.categories.forEach { categoryManager.addCategory(it) }
            DummyData.transactions.forEach { transactionManager.addTransaction(it) }
        }
    }

    fun loadAllTransactions() {
        viewModelScope.launch {
            transactionManager.getAllTransactions()
            _transactions.value = transactionManager.transactions
        }
    }
}