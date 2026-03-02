package com.example.financemanager.repository

import com.example.financemanager.database.entity.PayeeCategoryMapper
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase

class PayeeCategoryRepo(private val db: ExpenseManagementDatabase) {

    suspend fun get(payee: String) = db.PayeeCategoryMapperDao().get(payee)

    suspend fun insert(mapping: PayeeCategoryMapper) {
        db.PayeeCategoryMapperDao().insert(mapping)
    }

    suspend fun update(payee: String, categoryId: Int) {
        db.PayeeCategoryMapperDao().update(payee, categoryId)
    }
}