package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.localstorage.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {

    private val accountManager = Graph.accountManager

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts

    private val _netWorth = MutableStateFlow(0.0)
    val netWorth: StateFlow<Double> = _netWorth

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountManager.getAllAccounts()
            _accounts.value = accountManager.accounts
            calculateNetWorth()
        }
    }

    private fun calculateNetWorth() {
        val total = _accounts.value
            .filter { it.isIncludedInTotal }
            .sumOf { it.currentBalance }
        _netWorth.value = total
    }
}