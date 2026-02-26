package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financemanager.database.entity.AccountIdMapper

@Dao
abstract class AccountIdMapperDao {
    @Query("SELECT `account_id` FROM `account-id-mapper` WHERE `account_name` = :accountName")
    abstract suspend fun get(accountName: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(accountIdMapper: AccountIdMapper)

    @Query("DELETE FROM `account-id-mapper` WHERE `account_id` = :id")
    abstract suspend fun delete(id: Int)
}