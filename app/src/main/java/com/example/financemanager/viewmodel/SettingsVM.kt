package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.Graph
import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.repository.AppSettingRepo
import com.example.financemanager.repository.data.Keys
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SettingsVM(private val appSettingRepo: AppSettingRepo) : ViewModel() {

    val budgetTimeframe: StateFlow<Long?> = appSettingRepo.getFlow(Keys.BUDGET_TIMEFRAME)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val salaryCreditTime: StateFlow<Long?> = appSettingRepo.getFlow(Keys.SALARY_CREDIT_TIME)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun updateBudgetTimeframe(timeframe: Long) {
        viewModelScope.launch {
            appSettingRepo.insert(AppSetting(Keys.BUDGET_TIMEFRAME.ordinal, timeframe))
        }
    }

    fun sendTestNotification() {
        val testTransaction = Transaction(
            id = 1,
            payee = "Test Merchant",
            amount = 1234.56,
            currency = "INR",
            type = "Expense",
            transactionDate = System.currentTimeMillis(),
            categoryId = null,
            description = "This is a test notification from settings.",
            receiptURL = "",
            location = "",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            rawAccountIdName = "Test Account"
        )
        val testCategory = Category(
            id = 999,
            name = "Test Category",
            description = "Test Category Description",
            type = "Expense",
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = System.currentTimeMillis().toString(),
            color = "#FF0000",
        )
        Graph.notificationManager.showTransactionNotification(testTransaction, testCategory)
    }
}
