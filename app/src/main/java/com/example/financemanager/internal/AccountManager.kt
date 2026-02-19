package com.example.financemanager.internal

import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.localstorage.dao.AccountDao
import kotlinx.coroutines.flow.Flow

class AccountManager(
    private val accountDao: AccountDao
) {

    suspend fun addAccount(account: Account) {
        accountDao.create(account)
    }

    suspend fun removeAccount(account: Account) {
        accountDao.delete(account)
    }

    suspend fun updateAccount(account: Account) {
        accountDao.update(account)
    }

    fun getAllAccountsFlow(): Flow<List<Account>> {
        return accountDao.getAllFlow()
    }
    suspend fun getAllAccounts(): List<Account> {
        return accountDao.getAll()
    }

    suspend fun getAccount(id: Int): Account? {
        return accountDao.get(id)
    }

    fun getAccountFlow(id: Int): Flow<Account?> {
        return accountDao.getFlow(id)
    }

    suspend fun updateAccountBalance(accountId: Int, diff: Double) {
        return accountDao.updateBalance(accountId, diff)
    }

    @androidx.room.Transaction
    suspend fun updateAccountBalanceForTransactionAccountUpdate(oldAccountId: Int?,
                                                                newAccountId: Int?, diff: Double) {
        accountDao.removeBalanceFromOldAccount(oldAccountId, diff)
        accountDao.addBalanceToNewAccount(newAccountId, diff)
    }
}