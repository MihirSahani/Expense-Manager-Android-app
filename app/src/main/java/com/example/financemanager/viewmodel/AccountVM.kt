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

    var selectedAccountId: Int? = null

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

    fun loadAccounts() {
        viewModelScope.launch {
            _areAccountsLoaded.value = false
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

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            expenseManagementInternal.accountManager.updateAccount(account)
            loadAccounts()
        }
    }

    fun getAccountById(id: Int?): Account? {
        if (id == null) return Account()
        return _accounts.value.find { it.id == id }
    }
}
