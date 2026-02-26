package com.example.financemanager.repository

import androidx.room.withTransaction
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.AccountIdMapper
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class AccountRepo(private val db: ExpenseManagementDatabase) {
    val accounts = db.accountDao().getAccountsFlow()

    private suspend fun updateMapperAndTransaction(a: Account) {
        db.accountIdMapperDao()
            .insert(AccountIdMapper(a.accountNo, a.id))
        db.transactionDao().updateAccountForTransactionsWithRawAccount(
            a.accountNo, a.id
        )
    }

    suspend fun addAccount(account: Account) {
        db.withTransaction {
            db.accountDao().create(account)
            updateMapperAndTransaction(account)
        }
    }

    suspend fun updateAccount(account: Account) {
        db.withTransaction {
            db.accountDao().update(account)
            updateMapperAndTransaction(account)
        }
    }

    suspend fun deleteAccount(account: Account) {
        db.withTransaction {
            db.accountDao().delete(account)
            db.accountIdMapperDao().delete(account.id)
            db.transactionDao().updateAccountForTransactionsWithRawAccount(
                account.accountNo, null
            )
        }
    }
}