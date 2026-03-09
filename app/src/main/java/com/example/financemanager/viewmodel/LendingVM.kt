package com.example.financemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Lending
import com.example.financemanager.repository.LendingRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LendingVM(private val lendingRepo: LendingRepo): ViewModel() {
    val lendings: StateFlow<List<Lending>> = lendingRepo.lendings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun create(lending: Lending) {
        viewModelScope.launch {
            lendingRepo.create(lending)
        }
    }

    fun update(lending: Lending) {
        viewModelScope.launch {
            lendingRepo.update(lending)
        }
    }

    fun delete(lending: Lending) {
        viewModelScope.launch {
            lendingRepo.delete(lending)
        }
    }

    fun markPaid(id: Int, value: Boolean = true) {
        viewModelScope.launch {
            lendingRepo.updateIsPaid(id, value)
        }
    }
}