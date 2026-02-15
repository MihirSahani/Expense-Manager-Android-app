package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financemanager.database.entity.Transaction

@Dao
abstract class TransactionDao {
    @Query("SELECT * FROM `transactions`")
    abstract suspend fun getAll(): MutableList<Transaction>

    @Update
    abstract suspend fun update(transaction: Transaction)

    @Delete
    abstract suspend fun delete(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun create(transaction: Transaction)

    @Query("UPDATE `transactions` SET `category_id` = :categoryId WHERE payee = :payee")
    abstract suspend fun updateCategoryForAllTransactionsWithPayee(payee: String, categoryId: Int)

    @Query("UPDATE `transactions` SET `account_id` = :accountId WHERE `raw_account_id_name` = :accountName")
    abstract suspend fun updateAccountForAllTransactionsWithAccount(accountName: String, accountId: Int)
}