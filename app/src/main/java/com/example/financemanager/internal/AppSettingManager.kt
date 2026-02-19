package com.example.financemanager.internal

import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.localstorage.dao.AppSettingDao

enum class Keys {
    IS_INITIALIZATION_DONE,
    LAST_SMS_TIMESTAMP
}

class AppSettingManager(
    private val appSettingDao: AppSettingDao
) {
    suspend fun getAppSetting(key: Keys): Long? {
        return appSettingDao.getByKey(key.ordinal)
    }

    suspend fun updateAppSetting(key: Keys, value: Long?) {
        return appSettingDao.insert(AppSetting(key.ordinal, value))
    }
}