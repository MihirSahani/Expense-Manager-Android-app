package com.example.financemanager.repository

import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase

class CategoryRepo(private val db: ExpenseManagementDatabase) {
    val categories = db.categoryDao().getCategoriesFlow()

    suspend fun addCategory(c: Category) {
        db.categoryDao().create(c)
    }

    suspend fun updateCategory(c: Category) {
        db.categoryDao().update(c)
    }

    suspend fun deleteCategory(c: Category) {
        db.categoryDao().delete(c)
    }
}