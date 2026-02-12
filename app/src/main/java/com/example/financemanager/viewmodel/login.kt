package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.Graph
import com.example.financemanager.internal.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(): ViewModel() {
    val userManager = UserManager(Graph.database.userDao())
    private val _isUserLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUserLoaded: StateFlow<Boolean> = _isUserLoaded.asStateFlow()


    init {
        viewModelScope.launch {
            userManager.getUser()
            _isUserLoaded.value = true
        }
    }

    fun signUp(firstName: String, lastName: String) {
        viewModelScope.launch {
            val user = User(firstName = firstName, lastName = lastName, token = "dummy_token")
            userManager.addUser(user)
        }
    }
}