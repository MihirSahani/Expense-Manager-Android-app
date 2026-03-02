package com.example.financemanager.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.PayeeCategoryMapper
import com.example.financemanager.repository.CategoryRepo
import com.example.financemanager.repository.PayeeCategoryRepo
import com.example.financemanager.repository.TransactionRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CategoryVM(
    private val categoryRepo: CategoryRepo,
    private val payeeCategoryRepo: PayeeCategoryRepo
): ViewModel() {
    private val _categories = categoryRepo.categories
    val categories: StateFlow<List<Category>> = _categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var categoryId: Int? = null

    fun addCategory(name: String, description: String, type: String, color: String) {
        viewModelScope.launch {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val category = Category(
                name = name,
                description = description,
                type = type,
                color = color,
                createdAt = timestamp,
                updatedAt = timestamp
            )
            categoryRepo.add(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            category.updatedAt = timestamp
            categoryRepo.update(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepo.delete(category)
        }
    }

    fun getPayeeCategory(payee: String, categoryId: MutableState<Int?>) {
        viewModelScope.launch {
            categoryId.value = payeeCategoryRepo.get(payee)
        }
    }

    fun updatePayeeCategory(payee: String, categoryId: Int) {
        viewModelScope.launch {
            payeeCategoryRepo.update(payee, categoryId)
        }
    }
}