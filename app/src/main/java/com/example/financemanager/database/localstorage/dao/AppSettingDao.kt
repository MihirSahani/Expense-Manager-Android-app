package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financemanager.database.entity.AppSetting

@Dao
abstract class AppSettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(appSetting: AppSetting)

    @Query("SELECT * FROM `app-settings` WHERE `key` = :key")
    abstract suspend fun getByKey(key: String): AppSetting
}