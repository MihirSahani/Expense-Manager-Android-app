package com.example.financemanager.internal

import android.content.Context
import com.example.financemanager.database.DummyData
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.AccountIdMapper
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class ExpenseManagementInternal(database: ExpenseManagementDatabase) {
    val accountManager: AccountManager = AccountManager(database.accountDao())
    val categoryManager: CategoryManager = CategoryManager(database.categoryDao())
    val transactionManager: TransactionManager = TransactionManager(
        database.transactionDao()
    )
    val userManager: UserManager = UserManager(database.userDao())
    val appSettingManager: AppSettingManager = AppSettingManager(
        database.appSettingDao()
    )
    val payeeCategoryMapperManager: PayeeCategoryMapperManager = PayeeCategoryMapperManager(
        database.PayeeCategoryMapperDao()
    )
    val accountIdMapperManager: AccountIdMapperManager = AccountIdMapperManager(
        database.accountIdMapperDao()
    )
    val smsParser: SMSParser = SMSParser()

    fun getUser(): Flow<User?> {
        return userManager.getUser()
    }

    // fun getTransactionsFlow(): Flow<List<Transaction>> {
    //     return transactionManager.getAllTransactionsFlow()
    // }

    // suspend fun getTransaction(id: Int): Transaction? {
    //     return transactionManager.getTransaction(id)
    // }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // fun getTransactionCurrentCycle(): Flow<List<Transaction>> {
    //     return appSettingManager.getAppSettingFlow(Keys.BUDGET_TIMEFRAME)
    //         .flatMapLatest { timeframe ->
    //             when(timeframe) {
    //                 BudgetTimeframe.MONTHLY.ordinal.toLong()-> {
    //                     val currMonth = LocalDate.now()
    //                     transactionManager.getTransactionsByMonthCurrentCycleFlow(
    //                         currMonth.monthValue,
    //                         currMonth.year
    //                     )
    //                 }
    //                 BudgetTimeframe.SALARY_DATE.ordinal.toLong() -> {
    //                     appSettingManager.getAppSettingFlow(Keys.SALARY_CREDIT_TIME).flatMapLatest { timestamp ->
    //                         transactionManager.getTransactionsBySalaryCurrentCycleFlow(timestamp?:0L)
    //                     }
    //                 }
    //                 else -> {
    //                     throw Exception("Invalid timeframe")
    //                 }
    //             }
    //         }
    // }
    //
    // @OptIn(ExperimentalCoroutinesApi::class)
    // fun getTransactionArchived(): Flow<List<Transaction>> {
    //     return appSettingManager.getAppSettingFlow(Keys.BUDGET_TIMEFRAME).flatMapLatest { timeframe ->
    //         when(timeframe) {
    //             BudgetTimeframe.MONTHLY.ordinal.toLong() -> transactionManager.getArchivedTransactionMonth()
    //             BudgetTimeframe.SALARY_DATE.ordinal.toLong() -> {
    //                 appSettingManager.getAppSettingFlow(Keys.SALARY_CREDIT_TIME).flatMapLatest { timestamp ->
    //                     transactionManager.getArchivedTransactionsSalary(timestamp?:1L)
    //                 }
    //             }
    //             else -> throw Exception("Invalid timeframe")
    //         }
    //     }
    // }

    // suspend fun addTransaction(transaction: Transaction) {
    //     transactionManager.addTransaction(transaction, accountManager)
    //     if (transaction.type.equals("income", ignoreCase = true)) {
    //         updateSalaryCreditTime()
    //     }
    // }

    // suspend fun updateTransaction(transaction: Transaction) {
    //     transactionManager.updateTransaction(transaction)
    //     if (transaction.type.equals("income", ignoreCase = true)) {
    //         updateSalaryCreditTime()
    //     }
    // }

    // suspend fun updateTransactionCategory(
    //     transaction: Transaction, updateCategoryForAllTransactionsWithPayee: Boolean
    // ) {

    //     if (transaction.categoryId != null && (updateCategoryForAllTransactionsWithPayee ||
    //                 payeeCategoryMapperManager.getMapping(transaction.payee) == null)) {
    //         coroutineScope {
    //             val waitGroup = mutableListOf<Job>()

    //             waitGroup.add(launch {
    //                 payeeCategoryMapperManager.addMapping(transaction.payee,
    //                     transaction.categoryId!!
    //                 )
    //             })
    //             waitGroup.add(launch {
    //                 transactionManager.updateCategoryForTransactionsWithPayee(transaction.payee,
    //                     transaction.categoryId!!
    //                 )
    //             })
    //             waitGroup.joinAll()
    //         }

    //     } else {
    //         transactionManager.updateTransaction(transaction)
    //     }
    //     if (transaction.type.equals("income", ignoreCase = true)) {
    //         updateSalaryCreditTime()
    //     }
    // }

    // suspend fun updateTransactionAccount(newTransaction: Transaction) {

    //     val transactions = transactionManager.getTransactionsWithRawAccountId(
    //         newTransaction.rawAccountIdName
    //     )

    //     coroutineScope {
    //         val waitGroup = mutableListOf<Job>()
    //         transactions.map { transaction ->
    //             launch {
    //                 // Normalize the type comparison to handle "Expense" vs "expense"
    //                 val isExpense = transaction.type.equals("expense", ignoreCase = true)
    //                 val signedAmount = if (isExpense) -transaction.amount else transaction.amount
    //
    //                 try {
    //                     accountManager.updateAccountBalanceForTransactionAccountUpdate(
    //                         transaction.accountId,
    //                         newTransaction.accountId,
    //                         signedAmount
    //                     )
    //                 }
    //                 catch (e: Exception) {
    //                     e.printStackTrace()
    //                 }
    //             }
    //         }.joinAll()

    //         waitGroup.add(launch {
    //             accountIdMapperManager.insert(AccountIdMapper(newTransaction.rawAccountIdName, newTransaction.accountId!!))
    //         })
    //         waitGroup.add(launch {
    //             transactionManager.updateAccountForTransactionsWithRawAccount(
    //                 newTransaction.rawAccountIdName, newTransaction.accountId!!
    //             )
    //         })
    //         waitGroup.joinAll()
    //     }
    // }

    suspend fun loadDummyData() {
        coroutineScope {
            val waitGroup = mutableListOf<Job>()

            waitGroup.add( launch {
                val accounts = accountManager.getAllAccounts()
                if (accounts.isEmpty()) {
                    DummyData.accounts.map { account ->
                        launch {
                            accountManager.addAccount(account)
                        }
                    }.joinAll()
                }
            })
            waitGroup.add( launch {
                val categories = categoryManager.getCategories()
                if (categories.isEmpty()) {
                    DummyData.categories.map { category ->
                        launch {
                            categoryManager.addCategory(category)
                        }
                    }.joinAll()
                }
            })
            waitGroup.joinAll()
        }
    }

    suspend fun parseMessagesToTransactions(context: Context) {
        val lastTimestamp = appSettingManager.getAppSetting(Keys.LAST_SMS_TIMESTAMP) ?: 0L
        val timestamp = smsParser.parseMessages(
            context, transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
            accountManager, categoryManager, lastTimestamp)
        updateSMSParseTime(timestamp)
        updateSalaryCreditTime()
    }

    suspend fun updateSMSParseTime(timestamp: Long) {
        appSettingManager.updateAppSetting(Keys.LAST_SMS_TIMESTAMP, timestamp)
    }

    suspend fun parseSingleMessage(body: String, sender: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val result = smsParser.parseSingleMessage(body, sender, smsDateLong, transactionManager,
            payeeCategoryMapperManager, accountIdMapperManager, accountManager, categoryManager)

        updateSMSParseTime(System.currentTimeMillis())
        updateSalaryCreditTime()
        return result
    }

    // suspend fun addAccount(account: Account) {
    //     accountManager.addAccount(account)
    //     accountIdMapperManager.insert(AccountIdMapper(account.accountNo, account.id))
    //     transactionManager.updateAccountForTransactionsWithRawAccount(account.accountNo, account.id)
    // }
    // suspend fun updateAccount(account: Account) {
    //     accountManager.updateAccount(account)
    //     accountIdMapperManager.insert(AccountIdMapper(account.accountNo, account.id))
    //     transactionManager.updateAccountForTransactionsWithRawAccount(account.accountNo, account.id)
    // }

    // suspend fun getAccount(id: Int): Account? {
    //     return accountManager.getAccount(id)
    // }

    // fun getAccountFlow(id: Int): Flow<Account?> {
    //     return accountManager.getAccountFlow(id)
    // }
    suspend fun createUser(firstName: String, lastName: String) {
        userManager.addUser(firstName, lastName)
    }

    suspend fun updateUser(user: User) {
        userManager.updateUser(user)
    }

    fun getAccountsFlow(): Flow<List<Account>> {
        return accountManager.getAllAccountsFlow()
    }

    suspend fun getAccounts(): List<Account> {
        return accountManager.getAllAccounts()
    }

    fun getCategoriesFlow(): Flow<List<Category>> {
        return categoryManager.getCategoriesFlow()
    }

    suspend fun getCategories(): List<Category> {
        return categoryManager.getCategories()
    }

    suspend fun getPayeeCategory(payee: String): Int? {
        return payeeCategoryMapperManager.getMapping(payee)
    }

    suspend fun updatePayeeCategory(payee: String, categoryId: Int) {
        payeeCategoryMapperManager.addMapping(payee, categoryId)
    }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // fun getTransactionSumBySalaryDateFlow(): Flow<List<TransactionSummary>> {
    //     return appSettingManager.getAppSettingFlow(Keys.SALARY_CREDIT_TIME)
    //         .flatMapLatest { timestamp ->
    //             transactionManager.getSumOfTransactionsByCategoryAndSalaryDateFlow(timestamp ?: 0L)
    //         }
    // }

    fun getSettingFlow(key: Keys): Flow<Long?> {
        return appSettingManager.getAppSettingFlow(key)
    }
    
    suspend fun getAppSetting(key: Keys): Long? {
        return appSettingManager.getAppSetting(key)
    }

    suspend fun updateSetting(key: Keys, value: Long?) {
        appSettingManager.updateAppSetting(key, value)
    }

    // suspend fun updateSalaryCreditTime() {
    //     val transactions = transactionManager.getTransactionWithIncomeCategory()
    //     if (transactions.isNotEmpty()) {
    //         try {
    //             updateSetting(Keys.SALARY_CREDIT_TIME, transactions[0]!!.transactionDate)
    //         } catch (e: Exception) {
    //             e.printStackTrace()
    //         }
    //     } else {
    //         updateSetting(Keys.SALARY_CREDIT_TIME, 0L)
    //     }
    //     if (transactions.size==2) {
    //         try {
    //             updateSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME, transactions[1]!!.transactionDate)
    //         } catch (e: Exception) {
    //             e.printStackTrace()
    //         }
    //     } else {
    //         updateSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME, 0L)
    //     }
    // }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // fun getTransactionsByCategoryCurrentTimeframe(categoryId: Int?): Flow<List<Transaction>> {
    //     return appSettingManager.getAppSettingFlow(Keys.BUDGET_TIMEFRAME)
    //         .flatMapLatest { timeframe ->
    //             if (timeframe == BudgetTimeframe.SALARY_DATE.ordinal.toLong()) {
    //                 appSettingManager.getAppSettingFlow(Keys.SALARY_CREDIT_TIME)
    //                     .flatMapLatest { timestamp ->
    //                         transactionManager.getTransactionsByCategoryAndSalaryFlow(categoryId, timestamp)
    //                     }
    //             } else {
    //                 val now = LocalDate.now()
    //                 transactionManager.getTransactionsByCategoryAndMonthFlow(categoryId, now.year, now.monthValue)
    //             }
    //         }
    // }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // fun getTransactionSumByCategoryCurrentTimeframe(): Flow<List<TransactionSummary>> {
    //     return appSettingManager.getAppSettingFlow(Keys.BUDGET_TIMEFRAME)
    //         .flatMapLatest { timeframe ->
    //             if (timeframe == 1L) {
    //                 appSettingManager.getAppSettingFlow(Keys.SALARY_CREDIT_TIME)
    //                     .flatMapLatest { timestamp ->
    //                         transactionManager.getSumOfTransactionsByCategoryAndSalaryDateFlow(timestamp)
    //                     }
    //             } else {
    //                 val now = LocalDate.now()
    //                 transactionManager.getSumOfTransactionsByCategoryAndMonthFlow(now.year, now.monthValue)
    //             }
    //         }
    // }

    // @OptIn(ExperimentalCoroutinesApi::class)
    // fun getSumOfTransactionsPreviousCycle(): Flow<Double> {
    //     return appSettingManager.getAppSettingFlow(Keys.BUDGET_TIMEFRAME)
    //         .flatMapLatest { timeframe ->
    //             when(timeframe) {
    //                 BudgetTimeframe.MONTHLY.ordinal.toLong()-> {
    //                     val previousMonth = LocalDate.now().minusMonths(1)
    //                     transactionManager.getAmountSavedMonth(previousMonth.monthValue, previousMonth.year)
    //                 }
    //                 BudgetTimeframe.SALARY_DATE.ordinal.toLong()-> transactionManager.getAmountSavedSalary(appSettingManager)
    //                 else -> throw Exception("Invalid timeframe")
    //             }
    //         }
    // }

}
