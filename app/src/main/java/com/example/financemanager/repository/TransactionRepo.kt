package com.example.financemanager.repository

import android.icu.util.Calendar
import androidx.room.withTransaction
import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.entity.PayeeCategoryMapper
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.TransactionSummary
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import com.example.financemanager.internal.AppSettingManager
import com.example.financemanager.internal.BudgetTimeframe
import com.example.financemanager.internal.Keys
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

class TransactionRepo(private val db: ExpenseManagementDatabase) {
    private var _transactions = db.transactionDao().getTransactionsFlow()
    val transactions get() = _transactions

    fun getTransactionFlow(id: Int) = db.transactionDao().getFlow(id)

    private fun getTimeMillis(month: Int, year: Int): Pair<Long, Long> {
        val yearMonth = YearMonth.of(year, month)
        val zoneId = ZoneId.systemDefault()

        val startMillis = yearMonth
            .atDay(1)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()

        val endMillis = yearMonth
            .atEndOfMonth()
            .atTime(23, 59, 59, 999_000_000)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        return Pair(startMillis, endMillis)
    }

    private suspend fun updateSalaryCreditTime() {
        db.withTransaction {
            val transactions = db.transactionDao().getTransactionWithIncomeCategory()
            if (transactions.isNotEmpty()) {
                try {
                    db.appSettingDao()
                        .insert(
                            AppSetting(Keys.SALARY_CREDIT_TIME.ordinal, transactions[0]!!.transactionDate)
                        )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            else {
                db.appSettingDao().insert(AppSetting(Keys.SALARY_CREDIT_TIME.ordinal, 0L))
            }
            if(transactions.size == 2) {
                try {
                    db.appSettingDao()
                        .insert(AppSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal, transactions[1]!!.transactionDate))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else {
                db.appSettingDao().insert(AppSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal, 0L))
            }
        }
    }

    private suspend fun updateAccountBalance(t: Transaction, addingTransaction: Boolean = true) {
        if(t.accountId != null) {
            when(t.type.equals("expense", ignoreCase = true).xor(addingTransaction)) {
                true -> db.accountDao().updateBalance(t.accountId!!, -t.amount)
                false -> db.accountDao().updateBalance(t.accountId!!, t.amount)
            }
        }
    }

    suspend fun addTransaction(t: Transaction) {
        db.withTransaction {
            db.transactionDao().create(t)
            updateAccountBalance(t)
            updateSalaryCreditTime()
        }
    }

    suspend fun deleteTransaction(t: Transaction) {
        db.withTransaction {
            db.transactionDao().delete(t)
            updateAccountBalance(t)
            updateSalaryCreditTime()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTransactionsCurrentCycle(): Flow<List<Transaction>> {
        return db.appSettingDao().getByKeyFlow(Keys.BUDGET_TIMEFRAME.ordinal)
            .flatMapLatest { timeframe ->
                when(timeframe) {
                    BudgetTimeframe.MONTHLY.ordinal.toLong()-> {
                        val date = LocalDate.now()
                        val (from, to) = getTimeMillis(date.monthValue, date.year)
                        db.transactionDao().getTransactionsFlow(from = from, to = to)
                    }
                    BudgetTimeframe.SALARY_DATE.ordinal.toLong() -> {
                        db.appSettingDao().getByKeyFlow(Keys.SALARY_CREDIT_TIME.ordinal)
                            .flatMapLatest { timestamp ->
                                db.transactionDao().getTransactionsFlow(from = timestamp?:0L)
                            }
                    }
                    else -> throw Exception("Invalid timeframe")
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTransactionsArchivedCycle(): Flow<List<Transaction>> {
        return db.appSettingDao().getByKeyFlow(Keys.BUDGET_TIMEFRAME.ordinal)
            .flatMapLatest { timeframe ->
                when(timeframe) {
                    BudgetTimeframe.MONTHLY.ordinal.toLong()-> {
                        val date = LocalDate.now().minusMonths(1)
                        val (_, to) = getTimeMillis(date.monthValue, date.year)
                        db.transactionDao().getTransactionsFlow(to = to)
                    }
                    BudgetTimeframe.SALARY_DATE.ordinal.toLong() -> {
                        db.appSettingDao().getByKeyFlow(Keys.SALARY_CREDIT_TIME.ordinal)
                            .flatMapLatest { timestamp ->
                                db.transactionDao().getTransactionsFlow(to = timestamp?:0L)
                            }
                    }
                    else -> throw Exception("Invalid timeframe")
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSumOfTransactionsByCategoryCurrentCycle(): Flow<List<TransactionSummary>> {
        return db.appSettingDao().getByKeyFlow(Keys.BUDGET_TIMEFRAME.ordinal)
            .flatMapLatest { timeframe ->
                when(timeframe) {
                    BudgetTimeframe.MONTHLY.ordinal.toLong() -> {
                        val date = LocalDate.now()
                        val (from, to) = getTimeMillis(date.monthValue, date.year)
                        db.transactionDao().getSumOfTransactionsByCategoryFlow(from, to)
                    }

                    BudgetTimeframe.SALARY_DATE.ordinal.toLong() -> {
                        db.appSettingDao().getByKeyFlow(Keys.SALARY_CREDIT_TIME.ordinal)
                            .flatMapLatest { timeframe ->
                                db.transactionDao().getSumOfTransactionsByCategoryFlow(timeframe?:0L)
                            }
                    }
                    else -> throw Exception("Invalid timeframe")
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSumOfTransactionsPreviousCycle(): Flow<Double> {
        return db.appSettingDao().getByKeyFlow(Keys.BUDGET_TIMEFRAME.ordinal)
            .flatMapLatest { timeframe ->
                when(timeframe) {
                    BudgetTimeframe.MONTHLY.ordinal.toLong()-> {
                        val previousMonth = LocalDate.now().minusMonths(1)
                        val (from, to) = getTimeMillis(previousMonth.monthValue, previousMonth.year)
                        db.transactionDao().getSumOfTransactionsFlow(from, to)
                    }
                    BudgetTimeframe.SALARY_DATE.ordinal.toLong()-> {
                        db.appSettingDao().getByKeyFlow(Keys.SALARY_CREDIT_TIME.ordinal)
                            .flatMapLatest { to ->
                                db.appSettingDao().getByKeyFlow(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal)
                                    .flatMapLatest { from ->
                                        db.transactionDao().getSumOfTransactionsFlow(from?:0L, (to?:1L)-1)
                                    }
                            }
                    }
                    else -> throw Exception("Invalid timeframe")
                }
            }
    }

    suspend fun updateAccount(t: Transaction, newAccountId: Int) {
        db.withTransaction {
            val amount = db.transactionDao()
                .getTotalAmountForRawAccount(t.rawAccountIdName)
            coroutineScope {
                if (amount != null) {
                    val waitGroup = mutableListOf<Job>()
                    waitGroup.add( launch {
                        if (t.accountId != null) {
                            db.accountDao().updateBalance(t.accountId!!, -amount)
                        }
                    })
                    waitGroup.add( launch {
                        db.accountDao().updateBalance(newAccountId, amount)
                    })
                    waitGroup.add( launch {
                        db.transactionDao()
                            .updateAccountForTransactionsWithRawAccount(t.rawAccountIdName, newAccountId)
                    })
                    waitGroup.joinAll()
                }
            }
        }
    }

    suspend fun updateTransactionCategory(t: Transaction, forAll: Boolean) {
        db.withTransaction {
            coroutineScope {
                val waitGroup = mutableListOf<Job>()
                if(t.categoryId != null) {
                    when (forAll) {
                        true -> {
                            waitGroup.add( launch {
                                db.PayeeCategoryMapperDao()
                                    .addMapping(PayeeCategoryMapper(categoryId = t.categoryId!!, payee = t.payee))
                            })
                            waitGroup.add( launch {
                                db.transactionDao()
                                    .updateCategoryForTransactionsWithPayee(t.payee, t.categoryId!!)
                            })
                        }
                        false -> {
                            waitGroup.add( launch {
                                db.transactionDao()
                                    .update(t)
                            })
                        }
                    }
                }
                waitGroup.joinAll()
            }
        }
    }
}