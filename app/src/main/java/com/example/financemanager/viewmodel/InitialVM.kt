package com.example.financemanager.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.User
import com.example.financemanager.repository.OnboardingRepo
import com.example.financemanager.repository.TransactionRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InitialVM(
    private val onboardingRepo: OnboardingRepo,
    private val transactionRepo: TransactionRepo
): ViewModel() {

    private val _isUserLoaded = MutableStateFlow(false)
    val isUserLoaded: StateFlow<Boolean> = _isUserLoaded.asStateFlow()

    private val _user = onboardingRepo.user
        .onEach { _isUserLoaded.value = true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val user: StateFlow<User?> = _user

    val userName: StateFlow<String> = _user.map { it?.firstName ?: "Guest" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Guest")

   private val _navigateToEditTransaction = MutableSharedFlow<Transaction>(replay = 1)
    val navigateToEditTransaction = _navigateToEditTransaction.asSharedFlow()

    fun triggerNavigateToEditTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _navigateToEditTransaction.emit(transaction)
        }
    }

    fun initialize() {
        viewModelScope.launch {
            onboardingRepo.initialize()
        }
    }

    fun parseMessages(context: Context) {
        viewModelScope.launch {
            transactionRepo.parseMessages(context)
        }
    }


    fun signUp(firstName: String, lastName: String) {
        viewModelScope.launch {
            onboardingRepo.createUser(firstName, lastName)
        }
    }

    fun hasMandatoryPermissions(context: Context): Boolean {
        return onboardingRepo.hasMandatoryPermission(context)
    }

    fun hasOptionalPermission(context: Context): Boolean {
        return onboardingRepo.hasOptionalPermission(context)
    }
}
