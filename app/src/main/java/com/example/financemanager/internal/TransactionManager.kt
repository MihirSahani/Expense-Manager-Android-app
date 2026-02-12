package com.example.financemanager.internal

import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.localstorage.dao.TransactionDao

class TransactionManager(val transactionDao: TransactionDao) {
    var transactions: MutableList<Transaction> = mutableListOf()

    suspend fun getAllTransactions() {
        transactions = transactionDao.getAll()
    }

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.create(transaction)
        transactions.add(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            transactions[index] = transaction
        }
        else {
            transactions = transactionDao.getAll()
        }
    }

    suspend fun removeTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
        transactions.remove(transaction)
    }
}