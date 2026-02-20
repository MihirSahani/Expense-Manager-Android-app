package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import com.example.financemanager.internal.ExpenseManagementInternal

enum class ViewModelName {
    LOGIN,
    HOME,
    ACCOUNTS,
    TRANSACTION,
    SETTINGS,
    USER_DETAILS_UPDATE,
    CATEGORY,
    ANALYSIS,
    CATEGORY_ANALYSIS
}

class ViewModelFactory(private val expenseManagementInternal: ExpenseManagementInternal) {

    val initialVM: InitialVM by lazy { InitialVM(expenseManagementInternal) }
    val homeVM: InitialVM by lazy { InitialVM(expenseManagementInternal) }
    val accountVM: AccountVM by lazy { AccountVM(expenseManagementInternal) }
    val transactionVM: TransactionVM by lazy { TransactionVM(expenseManagementInternal) }
    val userVM: UserVM by lazy { UserVM(expenseManagementInternal, initialVM.user) }
    val categoryVM: CategoryVM by lazy { CategoryVM(expenseManagementInternal) }
    val analysisVM: AnalysisVM by lazy { AnalysisVM(expenseManagementInternal) }
    val settingsVM: SettingsVM by lazy { SettingsVM(expenseManagementInternal) }
    val categoryAnalysisVM: CategoryAnalysisVM by lazy { CategoryAnalysisVM(expenseManagementInternal) }

    fun getViewModel(name: ViewModelName): ViewModel {
        return when (name) {
            ViewModelName.LOGIN -> initialVM
            ViewModelName.HOME -> homeVM
            ViewModelName.ACCOUNTS -> accountVM
            ViewModelName.TRANSACTION -> transactionVM
            ViewModelName.USER_DETAILS_UPDATE -> userVM
            ViewModelName.CATEGORY -> categoryVM
            ViewModelName.ANALYSIS -> analysisVM
            ViewModelName.SETTINGS -> settingsVM
            ViewModelName.CATEGORY_ANALYSIS -> categoryAnalysisVM
        }
    }
}
