package com.example.financemanager.internal

import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.localstorage.dao.AccountDao

class AccountManager(
    private val accountDao: AccountDao
) {
    var accounts: MutableList<Account> = mutableListOf()

    suspend fun addAccount(account: Account) {
        accountDao.create(account)
        accounts.add(account)
    }

    suspend fun removeAccount(account: Account) {
        accountDao.delete(account)
        accounts.remove(account)
    }

    suspend fun updateAccount(account: Account) {
        accountDao.update(account)
        val index = accounts.indexOfFirst { it.id == account.id }
        if (index != -1) {
            accounts[index] = account
        }
        else {
            accounts = accountDao.getAll() as MutableList<Account>
        }
    }

    suspend fun getAllAccounts() {
        accounts = accountDao.getAll() as MutableList<Account>
    }
}