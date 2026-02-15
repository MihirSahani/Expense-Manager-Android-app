package com.example.financemanager.internal

import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.localstorage.dao.AppSettingDao

class AppSettingManager(
    private val appSettingDao: AppSettingDao
) {
    suspend fun getAppSetting(key: String): AppSetting {
        return appSettingDao.getByKey(key)
    }

    suspend fun updateAppSetting(key: String, value: Int) {
        return appSettingDao.insert(AppSetting(key, value))
    }
}