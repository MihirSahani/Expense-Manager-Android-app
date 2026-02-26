package com.example.financemanager.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.User
import com.example.financemanager.internal.BudgetTimeframe
import com.example.financemanager.internal.ExpenseManagementInternal
import com.example.financemanager.internal.Keys
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class InitialVM(private val em: ExpenseManagementInternal): ViewModel() {

    private val _isUserLoaded = MutableStateFlow(false)
    val isUserLoaded: StateFlow<Boolean> = _isUserLoaded.asStateFlow()

    private val _user = em.getUser()
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

    private val _modalEditTransaction = MutableSharedFlow<Transaction>(replay = 1)
    val modalEditTransaction = _modalEditTransaction.asSharedFlow()

    fun triggerModalEditTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _modalEditTransaction.emit(transaction)
        }
    }

    fun initialize() {
        viewModelScope.launch {
            if (em.appSettingManager.getAppSetting(Keys.IS_INITIALIZATION_DONE) == null) {
                val waitGroup = mutableListOf<Job>()

                waitGroup.add(launch {
                    em.loadDummyData()
                })
                waitGroup.add(launch {
                    em.updateSetting(Keys.BUDGET_TIMEFRAME, BudgetTimeframe.MONTHLY.ordinal.toLong())
                })
                waitGroup.add(launch {
                    em.updateSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME, 0L)
                })
                waitGroup.add(launch {
                    em.updateSetting(Keys.SALARY_CREDIT_TIME, 0L)
                })
                waitGroup.add(launch {
                    em.updateSetting(Keys.LAST_SMS_TIMESTAMP, 0L)
                    // expenseManagementInternal.updateSetting(Keys.LAST_SMS_TIMESTAMP, System.currentTimeMillis())
                })
                waitGroup.add(launch {
                    em.updateSetting(Keys.IS_INITIALIZATION_DONE, 1L)
                })
                waitGroup.joinAll()
            }
        }
    }

    fun parseMessages(context: Context) {
        viewModelScope.launch {
            em.parseMessagesToTransactions(context)
        }
    }


    fun signUp(firstName: String, lastName: String) {
        viewModelScope.launch {
            em.createUser(firstName, lastName)
        }
    }

    fun hasMandatoryPermissions(context: Context): Boolean {
        return context.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                &&
                context.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
    }

    fun hasOptionalPermission(context: Context): Boolean {
        return context.checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
    }
}
