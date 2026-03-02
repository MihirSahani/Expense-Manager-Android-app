package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Account
import com.example.financemanager.repository.AccountRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountVM(private val accountRepo: AccountRepo): ViewModel() {

    var selectedAccountId: MutableStateFlow<Int?> = MutableStateFlow(null)

    val accounts: StateFlow<List<Account>> = accountRepo.accounts
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val account: StateFlow<Account?> = selectedAccountId.flatMapLatest { id ->
        id?.let { accountRepo.get(it) } ?: flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val netWorth: Flow<Double> = accounts
        .map { accountList ->
            accountList
                .filter { it.isIncludedInTotal }
                .sumOf { it.currentBalance }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    fun addAccount(account: Account) {
        viewModelScope.launch { accountRepo.addAccount(account) }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch { accountRepo.updateAccount(account) }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch { accountRepo.deleteAccount(account) }
    }
}
