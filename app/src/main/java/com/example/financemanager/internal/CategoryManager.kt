package com.example.financemanager.internal

import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.localstorage.dao.CategoryDao
import kotlinx.coroutines.flow.Flow

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

    fun getCategoriesFlow(): Flow<List<Category>> {
        return categoryDao.getAllFlow()
    }
    suspend fun getCategories(): List<Category> {
        return categoryDao.getAll()
    }

    suspend fun getCategory(id: Int): Category? {
        return categoryDao.getById(id)
    }
}