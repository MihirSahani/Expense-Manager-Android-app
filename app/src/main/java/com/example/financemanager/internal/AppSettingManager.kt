package com.example.financemanager.internal

import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.localstorage.dao.AppSettingDao
import kotlinx.coroutines.flow.Flow

enum class Keys {
    IS_INITIALIZATION_DONE,
    LAST_SMS_TIMESTAMP,
    SALARY_CREDIT_TIME,
    BUDGET_TIMEFRAME,
    PREVIOUS_SALARY_CREDIT_TIME,
}

enum class BudgetTimeframe {
    MONTHLY,
    SALARY_DATE,
}

class AppSettingManager(
    private val appSettingDao: AppSettingDao
) {
    suspend fun getAppSetting(key: Keys): Long? {
        return appSettingDao.getByKey(key.ordinal)
    }

    fun getAppSettingFlow(key: Keys): Flow<Long?> {
        return appSettingDao.getByKeyFlow(key.ordinal)
    }

    suspend fun updateAppSetting(key: Keys, value: Long?) {
        return appSettingDao.insert(AppSetting(key.ordinal, value))
    }
}