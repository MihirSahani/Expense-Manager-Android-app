package com.example.financemanager.internal

import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.TransactionSummary
import com.example.financemanager.database.localstorage.dao.TransactionDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset

class TransactionManager(val transactionDao: TransactionDao) {

    fun getAllTransactionsFlow(): Flow<List<Transaction>> {
        return transactionDao.getAllFlow()
    }

    @androidx.room.Transaction
    suspend fun addTransaction(transaction: Transaction, accountManager: AccountManager) {
        coroutineScope {
            launch {
                if (transaction.accountId != null) {
                    val account = accountManager.getAccount(transaction.accountId!!)
                    val adjustedAmount = if (transaction.type.equals("expense", ignoreCase = true)) {
                        -transaction.amount
                    } else {
                        transaction.amount
                    }

                    account?.let {
                        accountManager.updateAccountBalance(transaction.accountId!!, adjustedAmount)
                    }
                }
            }
            launch {
                transactionDao.create(transaction)
            }
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun removeTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    suspend fun updateCategoryForTransactionsWithPayee(payee: String, categoryId: Int) {
        transactionDao.updateCategoryForTransactionsWithPayee(payee, categoryId)
    }

    suspend fun updateAccountForTransactionsWithRawAccount(accountName: String, accountId: Int) {
        transactionDao.updateAccountForTransactionsWithRawAccount(accountName, accountId)
    }

    suspend fun getTransactionsWithRawAccountId(accountName: String): List<Transaction> {
        return transactionDao.getByRawAccountName(accountName)
    }


    fun getSumOfTransactionsByCategoryAndMonthFlow(year: Int, month: Int): Flow<List<TransactionSummary>> {
        val (start, end) = getMonthRange(year, month)
        return transactionDao.getSumOfTransactionsByCategoryAndMonthFlow(start, end)
    }

    fun getSumOfTransactionsByCategoryAndSalaryDateFlow(timeMills: Long?): Flow<List<TransactionSummary>> {
        return transactionDao.getSumOfTransactionsByCategoryAndSalaryDateFlow(timeMills)
    }

    suspend fun getTransactionWithIncomeCategory(): List<Transaction?> {
        return transactionDao.getTransactionWithIncomeCategory()
    }

    fun getTransactionsByCategoryAndSalaryFlow(categoryId: Int?, timeMills: Long?): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByCategoryAndSalaryFlow(categoryId, timeMills)
    }

    fun getTransactionsByCategoryAndMonthFlow(categoryId: Int?, year: Int, month: Int): Flow<List<Transaction>> {
        val (start, end) = getMonthRange(year, month)
        return transactionDao.getTransactionsByCategoryAndMonthFlow(categoryId, start, end)
    }

    private fun getMonthRange(year: Int, month: Int): Pair<Long, Long> {
        val start = LocalDateTime.of(year, month, 1, 0, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val end = LocalDateTime.of(year, month, 1, 0, 0)
            .plusMonths(1)
            .minusNanos(1)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return Pair(start, end)
    }
    fun getAmountSavedMonth(month: Int, year: Int): Flow<Double> {
        val first = YearMonth.of(year, month).atDay(1).atStartOfDay(ZoneOffset.systemDefault())
        val end = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59, 999).atZone(ZoneOffset.systemDefault())

        val startTime = first.toInstant().toEpochMilli()
        val endTime = end.toInstant().toEpochMilli()

        return transactionDao.getSumOfTransactionsBetweenTime(startTime, endTime)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAmountSavedSalary(appSettingManager: AppSettingManager): Flow<Double> {
        return appSettingManager.getAppSettingFlow(Keys.SALARY_CREDIT_TIME)
            .flatMapLatest { salaryDate ->
                appSettingManager.getAppSettingFlow(Keys.PREVIOUS_SALARY_CREDIT_TIME)
                    .flatMapLatest { previousSalaryDate ->
                        transactionDao.getSumOfTransactionsBetweenTime(previousSalaryDate!!, salaryDate!!)
                }
            }
    }
}
