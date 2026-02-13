package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.User
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginVM(private val expenseManagementInternal: ExpenseManagementInternal): ViewModel() {
    var _isUserDetailsLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUserDetailsLoaded: StateFlow<Boolean> = _isUserDetailsLoaded.asStateFlow()

    var user: User? = null

    init {
        viewModelScope.launch {
            launch {
                user = expenseManagementInternal.getUser()
                _isUserDetailsLoaded.value = true
            }
            launch { expenseManagementInternal.loadDummyData() }
        }
    }

    fun signUp(firstName: String, lastName: String) {
        viewModelScope.launch {
            expenseManagementInternal.createUser(firstName, lastName)
        }
    }
}