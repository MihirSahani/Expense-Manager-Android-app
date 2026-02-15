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
    CATEGORY
}

class ViewModelFactory(private val expenseManagementInternal: ExpenseManagementInternal) {

    val loginVM: LoginVM by lazy { LoginVM(expenseManagementInternal) }
    val homeVM: LoginVM by lazy { LoginVM(expenseManagementInternal) }
    val accountVM: AccountVM by lazy { AccountVM(expenseManagementInternal) }
    val transactionVM: TransactionVM by lazy { TransactionVM(expenseManagementInternal) }
    val userVM: UserVM by lazy { UserVM(expenseManagementInternal, loginVM.user) }
    val categoryVM: CategoryVM by lazy { CategoryVM(expenseManagementInternal) }

    fun getViewModel(name: ViewModelName): ViewModel {
        return when (name) {
            ViewModelName.LOGIN -> loginVM
            ViewModelName.HOME -> homeVM
            ViewModelName.ACCOUNTS -> accountVM
            ViewModelName.TRANSACTION -> transactionVM
            ViewModelName.USER_DETAILS_UPDATE -> userVM
            ViewModelName.CATEGORY -> categoryVM
            else -> throw IllegalArgumentException("Unknown ViewModelName: $name")
        }
    }
}
