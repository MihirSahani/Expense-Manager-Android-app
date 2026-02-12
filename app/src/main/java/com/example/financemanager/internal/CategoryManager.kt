package com.example.financemanager.internal

import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.localstorage.dao.CategoryDao

class CategoryManager(
    private val categoryDao: CategoryDao
) {
    var categories: MutableList<Category> = mutableListOf()

    suspend fun addCategory(category: Category) {
        categoryDao.create(category)
        categories.add(category)
    }

    suspend fun removeCategory(category: Category) {
        categoryDao.delete(category)
        categories.remove(category)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.update(category)
        val index = categories.indexOfFirst { it.id == category.id }
        if (index != -1) {
            categories[index] = category
        }
        else {
            categories = categoryDao.getAll() as MutableList<Category>
        }
    }

    suspend fun getAllCategories() {
        categories = categoryDao.getAll() as MutableList<Category>
    }
}