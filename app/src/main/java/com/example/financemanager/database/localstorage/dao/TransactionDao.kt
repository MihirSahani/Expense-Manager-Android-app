package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.TransactionSummary
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TransactionDao {
    @Query("SELECT * FROM `transactions` WHERE id = :id")
    abstract fun getFlow(id: Int): Flow<Transaction?>

    @Query("SELECT * FROM `transactions` WHERE id = :id")
    abstract suspend fun get(id: Int): Transaction?

    @Update
    abstract suspend fun update(transaction: Transaction)

    @Delete
    abstract suspend fun delete(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun create(transaction: Transaction)

    @Query("UPDATE `transactions` SET `category_id` = :categoryId WHERE payee = :payee")
    abstract suspend fun updateCategoryForTransactionsWithPayee(payee: String, categoryId: Int)

    @Query(
        "UPDATE `transactions` SET `account_id` = :accountId " +
                "WHERE `raw_account_id_name` = :accountName"
    )
    abstract suspend fun updateAccountForTransactionsWithRawAccount(
        accountName: String, accountId: Int
    )

    @Query("SELECT * FROM `transactions` WHERE `raw_account_id_name` = :accountName")
    abstract suspend fun getTransactionsByRawAccountName(accountName: String): List<Transaction>

    @Query(
        "SELECT * FROM `transactions` " +
                "LEFT JOIN `categories` ON `transactions`.`category_id` = `categories`.`id` " +
                "WHERE `categories`.`type` = 'Income' ORDER BY `transactions`.`transaction_date` DESC LIMIT 2"
    )
    abstract suspend fun getTransactionWithIncomeCategory(): List<Transaction?>

    @Query(
        "SELECT SUM(CASE WHEN type = 'Expense' THEN -ABS(`amount`) ELSE ABS(`amount`) END) " +
                "AS `totalAmount`, `category_id` AS `categoryId` FROM `transactions` " +
                "WHERE transaction_date >= :from AND transaction_date <= :to " +
                "GROUP BY `category_id`"
    )
    abstract fun getSumOfTransactionsByCategoryFlow(
        from: Long = 0L,
        to: Long = Long.MAX_VALUE
    ): Flow<List<TransactionSummary>>


    @Query(
        "SELECT * FROM `transactions` WHERE " +
                "((:categoryId IS NULL AND `category_id` IS NULL) OR (`category_id` = :categoryId)) AND " +
                "transaction_date >= :startTime AND transaction_date <= :endTime"
    )
    abstract fun getTransactionsByCategoryFlow(
        categoryId: Int?,
        startTime: Long = 0L,
        endTime: Long = Long.MAX_VALUE
    ): Flow<List<Transaction>>


    @Query(
        "SELECT SUM(CASE WHEN type = 'Expense' THEN -ABS(`amount`) ELSE ABS(`amount`) END) " +
                "AS `totalAmount` FROM `transactions` " +
                "WHERE transaction_date >= :from AND transaction_date < :to"
    )
    abstract fun getSumOfTransactionsFlow(
        from: Long = 0,
        to: Long = Long.MAX_VALUE
    ): Flow<Double>

    @Query(
        "SELECT * FROM `transactions` " +
                "WHERE transaction_date >= :from AND transaction_date <= :to"
    )
    abstract fun getTransactionsFlow(
        from: Long = 0,
        to: Long = Long.MAX_VALUE
    ): Flow<List<Transaction>>
}