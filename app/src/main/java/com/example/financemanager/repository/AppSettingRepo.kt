package com.example.financemanager.repository

import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import com.example.financemanager.repository.data.Keys
import kotlinx.coroutines.flow.Flow

class AppSettingRepo(private val db: ExpenseManagementDatabase) {

    suspend fun get(key: Keys): Long? {
        return db.appSettingDao().getByKey(key.ordinal)
    }

    fun getFlow(key: Keys): Flow<Long?> {
        return db.appSettingDao().getByKeyFlow(key.ordinal)
    }

    suspend fun insert(appSetting: AppSetting) {
        db.appSettingDao().insert(appSetting)
    }
}