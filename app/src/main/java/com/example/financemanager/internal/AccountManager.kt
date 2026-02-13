package com.example.financemanager.internal

import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.localstorage.dao.AccountDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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

    suspend fun getAllAccounts(): MutableList<Account> {
        return accountDao.getAll()
    }
}