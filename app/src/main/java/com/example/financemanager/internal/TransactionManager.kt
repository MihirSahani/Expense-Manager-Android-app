package com.example.financemanager.internal

import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.localstorage.dao.TransactionDao
import kotlinx.coroutines.flow.Flow

class TransactionManager(val transactionDao: TransactionDao) {

    suspend fun getAllTransactions(): MutableList<Transaction> {
        return transactionDao.getAll()
    }

    suspend fun addTransaction(transaction: Transaction, accountManager: AccountManager) {
        if (transaction.type == "Expense") {
            transaction.amount = -transaction.amount
        }
        if (transaction.accountId != null ) {
            val account = accountManager.getAccount(transaction.accountId!!)
            if (account != null) {
                accountManager.updateAccount(account.copy(
                    currentBalance = account.currentBalance + transaction.amount
                ))
            }
        }
        transactionDao.create(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun removeTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    suspend fun updateCategoryForAllTransactionsWithPayee(payee: String, categoryId: Int) {
        transactionDao.updateCategoryForAllTransactionsWithPayee(payee, categoryId)
    }

    suspend fun updateAccountForAllTransactionsWithRawAccount(accountName: String, accountId: Int) {
        transactionDao.updateAccountForAllTransactionsWithRawAccount(accountName, accountId)
    }

    suspend fun getTransactionsWithRawAccountId(accountName: String): List<Transaction> {
        return transactionDao.getByRawAccountName(accountName)
    }
}