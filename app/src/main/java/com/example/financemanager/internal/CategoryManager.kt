package com.example.financemanager.internal

import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.localstorage.dao.CategoryDao

class CategoryManager(
    private val categoryDao: CategoryDao
) {

    suspend fun addCategory(category: Category) {
        categoryDao.create(category)
    }

    suspend fun removeCategory(category: Category) {
        categoryDao.delete(category)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.update(category)
    }

    suspend fun getAllCategories(): MutableList<Category> {
        return categoryDao.getAll()
    }
}