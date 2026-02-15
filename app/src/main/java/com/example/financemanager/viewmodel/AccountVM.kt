package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Account
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountVM(private val expenseManagementInternal: ExpenseManagementInternal): ViewModel() {
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> get() = _accounts

    private val _areAccountsLoaded = MutableStateFlow(false)
    val areAccountsLoaded: StateFlow<Boolean> get() = _areAccountsLoaded

    val netWorth: StateFlow<Double> = _accounts
        .map { accountList ->
            accountList
                .filter { it.isIncludedInTotal }
                .sumOf { it.currentBalance }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        viewModelScope.launch {
            _accounts.value = expenseManagementInternal.getAccounts()
            _areAccountsLoaded.value = true
        }
    }

    fun addAccount(account: Account) {
        viewModelScope.launch {
            expenseManagementInternal.addAccount(account)
            loadAccounts()
        }
    }
}
