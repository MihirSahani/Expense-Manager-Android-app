package com.example.financemanager.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.User
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InitialVM(private val expenseManagementInternal: ExpenseManagementInternal): ViewModel() {
    private val _isUserDetailsLoaded = MutableStateFlow(false)
    val isUserDetailsLoaded: StateFlow<Boolean> = _isUserDetailsLoaded.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _userName = MutableStateFlow("Guest")
    val userName: StateFlow<String> = _userName.asStateFlow()

    fun parseSMS(context: Context) {
        viewModelScope.launch {
            expenseManagementInternal.parseMessagesToTransactions(context)
        }
    }

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val fetchedUser = expenseManagementInternal.getUser()
            _user.value = fetchedUser
            _userName.value = fetchedUser?.firstName ?: "Guest"
            _isUserDetailsLoaded.value = true
            
            // Load dummy data only once or as needed
            expenseManagementInternal.loadDummyData()
        }
    }

    fun signUp(firstName: String, lastName: String) {
        viewModelScope.launch {
            expenseManagementInternal.createUser(firstName, lastName)
            loadUserData()
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
