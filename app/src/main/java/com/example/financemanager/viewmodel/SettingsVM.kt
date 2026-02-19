package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.internal.ExpenseManagementInternal
import com.example.financemanager.internal.Keys
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsVM(private val expenseManagementInternal: ExpenseManagementInternal) : ViewModel() {

    val budgetTimeframe: StateFlow<Long?> = expenseManagementInternal.getSettingFlow(Keys.BUDGET_TIMEFRAME)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val salaryCreditTime: StateFlow<Long?> = expenseManagementInternal.getSettingFlow(Keys.SALARY_CREDIT_TIME)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun updateBudgetTimeframe(timeframe: Long) {
        viewModelScope.launch {
            expenseManagementInternal.updateSetting(Keys.BUDGET_TIMEFRAME, timeframe)
        }
    }

    fun updateSalaryCreditTime(timestamp: Long) {
        viewModelScope.launch {
            expenseManagementInternal.updateSetting(Keys.SALARY_CREDIT_TIME, timestamp)
        }
    }
}
