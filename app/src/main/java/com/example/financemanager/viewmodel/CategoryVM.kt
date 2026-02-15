package com.example.financemanager.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.entity.Category
import com.example.financemanager.internal.ExpenseManagementInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CategoryVM(private val expenseManagementInternal: ExpenseManagementInternal): ViewModel() {
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories
    var categoryId: Int? = null

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = expenseManagementInternal.getCategories()
        }
    }

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
            expenseManagementInternal.categoryManager.addCategory(category)
            loadCategories()
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            category.updatedAt = timestamp
            expenseManagementInternal.categoryManager.updateCategory(category)
            loadCategories()
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            expenseManagementInternal.categoryManager.removeCategory(category)
            loadCategories()
        }
    }

    fun getCategoryById(id: Int): Category? {
        return _categories.value.find { it.id == id }
    }

    fun getPayeeCategory(payee: String, categoryId: MutableState<Int?>) {
        viewModelScope.launch {
            categoryId.value = expenseManagementInternal.getPayeeCategory(payee)
        }
    }

    fun updatePayeeCategory(payee: String, categoryId: Int) {
        viewModelScope.launch {
            expenseManagementInternal.updatePayeeCategory(payee, categoryId)
        }
    }
}