package com.example.financemanager.internal

import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.localstorage.dao.TransactionDao
import kotlinx.coroutines.flow.Flow

class TransactionManager(val transactionDao: TransactionDao) {

    suspend fun getAllTransactions(): MutableList<Transaction> {
        return transactionDao.getAll()
    }

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.create(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun removeTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }
}