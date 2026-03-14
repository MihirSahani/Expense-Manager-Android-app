package com.example.financemanager.repository

import com.example.financemanager.database.entity.Lending
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import kotlinx.coroutines.flow.Flow

class LendingRepo(private val db: ExpenseManagementDatabase) {
    val lendings: Flow<List<Lending>> = db.lendingDao().getLendingsFlow()

    suspend fun create(lending: Lending) {
        db.lendingDao().insert(lending)
    }

    suspend fun update(lending: Lending) {
        db.lendingDao().update(lending)
    }

    suspend fun delete(lending: Lending) {
        db.lendingDao().delete(lending)
    }

    suspend fun updateIsPaid(id: Int, isPaid: Boolean = true) {
        db.lendingDao().updateIsPaid(id, isPaid)
    }

    suspend fun updateReturnDate(id: Int) {
        db.lendingDao().updateReturnDate(id, System.currentTimeMillis())
    }
}