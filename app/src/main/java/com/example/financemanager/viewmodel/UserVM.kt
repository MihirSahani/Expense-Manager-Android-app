package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.User
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserVM(private val expenseManagementInternal: ExpenseManagementInternal, val user: StateFlow<User?>) : ViewModel() {
    fun updateUserDetails(updatedUser: User) {
        viewModelScope.launch {
            expenseManagementInternal.updateUser(updatedUser)
       }
    }
}
