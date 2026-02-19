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

    @Query("SELECT * FROM `transactions`")
    abstract fun getAllFlow(): Flow<List<Transaction>>

    @Query("SELECT * FROM `transactions`")
    abstract suspend fun getAll(): List<Transaction>

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
    abstract suspend fun getByRawAccountName(accountName: String): List<Transaction>




    @Query(
        "SELECT SUM(CASE WHEN type = 'Expense' THEN -ABS(`amount`) ELSE ABS(`amount`) END) " +
                "AS `totalAmount`, `category_id` AS `categoryId` FROM `transactions` " +
                "WHERE transaction_date >= printf('%04d-%02d-01', :year, :month) AND " +
                "transaction_date <= date(printf('%04d-%02d-01', :year, :month), '+1 month', '-1 day') " +
                "GROUP BY `category_id`"
    )
    abstract fun getSumOfTransactionsByCategoryFlow(
        year: Int,
        month: Int
    ): Flow<List<TransactionSummary>>

    @Query(
        "SELECT SUM(CASE WHEN type = 'Expense' THEN -ABS(`amount`) ELSE ABS(`amount`) END)" +
                " AS `totalAmount` FROM `transactions` " +
                "WHERE transaction_date >= :timeMills"
    )
    abstract fun getSumOfTransactionsBySalaryDateFlow(
        timeMills: Long
    ): Flow<List<TransactionSummary>>

    @Query(
        "SELECT * FROM `transactions` " +
                "LEFT JOIN `categories` ON `transactions`.`category_id` = `categories`.`id` " +
                "WHERE `categories`.`type` = 'Income' ORDER BY `transactions`.`transaction_date` DESC"
    )
    abstract suspend fun getTransactionWithIncomeCategory(): Transaction?

    @Query(
        "SELECT * FROM `transactions` WHERE `category_id` = :categoryId AND " +
                "transaction_date >= :timeMills"
    )
    abstract fun getTransactionsByCategoryAndSalaryFlow(
        categoryId: Int?,
        timeMills: Long?
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM `transactions` WHERE `category_id` = :categoryId AND " +
                "transaction_date >= printf('%04d-%02d-01', :year, :month) AND " +
                "transaction_date <= date(printf('%04d-%02d-01', :year, :month), '+1 month', '-1 day'); "
    )
    abstract fun getTransactionsByCategoryAndMonthFlow(
        categoryId: Int?,
        year: Int,
        month: Int
    ): Flow<List<Transaction>>

    @Query(
        "SELECT * FROM `transactions` " +
                "WHERE transaction_date >= printf('%04d-%02d-01', :year, :month) AND " +
                "transaction_date <= date(printf('%04d-%02d-01', :year, :month), '+1 month', '-1 day');"
    )
    abstract fun getTransactionsByMonthFlow(year: Int, month: Int): Flow<List<Transaction>>
}