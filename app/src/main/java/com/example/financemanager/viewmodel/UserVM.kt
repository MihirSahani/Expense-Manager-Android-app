package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.User
import com.example.financemanager.repository.UserRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserVM(private val userRepo: UserRepo) : ViewModel() {
    val user = userRepo.user
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    fun updateUserDetails(updatedUser: User) {
        viewModelScope.launch {
            userRepo.updateUser(updatedUser)
       }
    }
}
