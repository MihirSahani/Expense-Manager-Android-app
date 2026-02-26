package com.example.financemanager.internal

import com.example.financemanager.database.entity.AccountIdMapper
import com.example.financemanager.database.localstorage.dao.AccountIdMapperDao

class AccountIdMapperManager(
    private val accountIdMapperDao: AccountIdMapperDao
) {
    suspend fun getAccountId(accountName: String): Int? {
        return accountIdMapperDao.get(accountName)
    }

    suspend fun insert(accountIdMapper: AccountIdMapper) {
        accountIdMapperDao.insert(accountIdMapper)
    }
}