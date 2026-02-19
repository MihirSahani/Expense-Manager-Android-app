package com.example.financemanager.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.User
import com.example.financemanager.internal.ExpenseManagementInternal
import com.example.financemanager.internal.Keys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InitialVM(private val expenseManagementInternal: ExpenseManagementInternal): ViewModel() {

    private val _isUserLoaded = MutableStateFlow(false)
    val isUserLoaded: StateFlow<Boolean> = _isUserLoaded.asStateFlow()

    private val _user = expenseManagementInternal.getUser()
        .onEach { _isUserLoaded.value = true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val user: StateFlow<User?> = _user

    val userName: StateFlow<String> = _user.map { it?.firstName ?: "Guest" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Guest")

    fun parseSMS(context: Context) {
        viewModelScope.launch {
            expenseManagementInternal.parseMessagesToTransactions(context)
        }
    }

    init {
        viewModelScope.launch {
            if (expenseManagementInternal.appSettingManager.getAppSetting(
                Keys.IS_INITIALIZATION_DONE
            ) == null) {
                expenseManagementInternal.appSettingManager.updateAppSetting(
                    Keys.IS_INITIALIZATION_DONE, 0
                )
                expenseManagementInternal.loadDummyData()
            }
        }
    }


    fun signUp(firstName: String, lastName: String) {
        viewModelScope.launch {
            expenseManagementInternal.createUser(firstName, lastName)
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
