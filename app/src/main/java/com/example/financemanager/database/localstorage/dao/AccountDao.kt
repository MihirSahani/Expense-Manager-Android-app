package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financemanager.database.entity.Account
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao {
    @Query("SELECT * FROM `accounts`")
    abstract fun getAccountsFlow(): Flow<List<Account>>

    @Query("SELECT * FROM `accounts`")
    abstract suspend fun getAll(): List<Account>
    
    @Update
    abstract suspend fun update(account: Account)

    @Delete
    abstract suspend fun delete(account: Account)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun create(account: Account)

    @Query("SELECT * FROM `accounts` WHERE id = :id")
    abstract fun getFlow(id: Int): Flow<Account?>

    @Query("SELECT * FROM `accounts` WHERE id = :id")
    abstract suspend fun get(id: Int): Account?

    @Query("UPDATE `accounts` SET `balance` = `balance` + :diff WHERE id = :accountId")
    abstract suspend fun updateBalance(accountId: Int, diff: Double)
}
