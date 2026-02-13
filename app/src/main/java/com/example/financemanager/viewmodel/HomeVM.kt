package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import com.example.financemanager.database.entity.User
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeVM(private val expenseManagementInternal: ExpenseManagementInternal, var user: User?) : ViewModel() {

    private val _userName = MutableStateFlow(user?.firstName ?: "Something went horribly wrong, anyways Guest")
    val userName: StateFlow<String> = _userName

}