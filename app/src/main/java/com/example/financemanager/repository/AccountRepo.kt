package com.example.financemanager.repository

import androidx.room.withTransaction
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase

class AccountRepo(private val db: ExpenseManagementDatabase) {
    val accounts = db.accountDao().getAccountsFlow()

    private suspend fun updateAccountsOfTransactions(rawAccountNo: String, toAccountId: Int?) {
        db.transactionDao().updateAccountForTransactionsWithRawAccount(rawAccountNo, toAccountId)
    }

    fun get(id: Int) = db.accountDao().getFlow(id)

    suspend fun addAccount(account: Account) {
        db.withTransaction {
            db.accountDao().create(account)
            updateAccountsOfTransactions(account.rawAccountNo, account.id)
        }
    }

    suspend fun updateAccount(account: Account) {
        db.withTransaction {
            db.accountDao().update(account)
            updateAccountsOfTransactions(account.rawAccountNo, account.id)
        }
    }

    suspend fun deleteAccount(account: Account) {
        db.withTransaction {
            db.accountDao().delete(account)
            updateAccountsOfTransactions(account.rawAccountNo, null)
        }
    }
}