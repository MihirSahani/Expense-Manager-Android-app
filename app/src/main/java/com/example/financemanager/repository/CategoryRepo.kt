package com.example.financemanager.repository

import androidx.room.withTransaction
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import kotlinx.coroutines.coroutineScope

class CategoryRepo(private val db: ExpenseManagementDatabase) {
    val categories = db.categoryDao().getCategoriesFlow()

    fun get(id: Int) = db.categoryDao().getFlow(id)

    suspend fun update(category: Category) {
        db.categoryDao().update(category)
    }

    suspend fun add(category: Category) {
        db.categoryDao().create(category)
    }

    suspend fun delete(category: Category) {
        coroutineScope {
            db.withTransaction {
                db.categoryDao().delete(category)
                val payee = db.PayeeCategoryMapperDao().getPayee(category.id)
                db.transactionDao().updateCategoryForTransactionsWithPayee(payee, null)
            }
        }
    }
}