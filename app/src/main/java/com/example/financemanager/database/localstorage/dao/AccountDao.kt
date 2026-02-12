package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financemanager.database.entity.Account

@Dao
abstract class AccountDao {
    @Query("SELECT * FROM `accounts`")
    abstract suspend fun getAll(): List<Account>
    
    @Update
    abstract suspend fun update(account: Account)

    @Delete
    abstract suspend fun delete(account: Account)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun create(account: Account)
}
