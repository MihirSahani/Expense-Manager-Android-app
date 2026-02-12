package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.localstorage.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val userManager = Graph.userManager

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            if (userManager.user == null) {
                userManager.getUser()
            }
            _userName.value = userManager.user?.firstName ?: "Guest"
        }
    }
}