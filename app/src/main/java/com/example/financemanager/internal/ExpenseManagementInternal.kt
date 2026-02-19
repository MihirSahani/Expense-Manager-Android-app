package com.example.financemanager.internal

import android.content.Context
import com.example.financemanager.database.DummyData
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.AccountIdMapper
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.TransactionSummary
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

    fun getTransactionsFlow(): Flow<List<Transaction>> {
        return transactionManager.getAllTransactionsFlow()
    }

    suspend fun addTransaction(transaction: Transaction) {
        transactionManager.addTransaction(transaction, accountManager)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionManager.updateTransaction(transaction)
    }

    suspend fun updateTransactionCategory(
        transaction: Transaction, updateCategoryForAllTransactionsWithPayee: Boolean
    ) {

        if (transaction.categoryId != null && (updateCategoryForAllTransactionsWithPayee ||
                    payeeCategoryMapperManager.getMapping(transaction.payee) == null)) {
            coroutineScope {
                val waitGroup = mutableListOf<Job>()

                waitGroup.add(launch {
                    payeeCategoryMapperManager.addMapping(transaction.payee,
                        transaction.categoryId!!
                    )
                })
                waitGroup.add(launch {
                    transactionManager.updateCategoryForTransactionsWithPayee(transaction.payee,
                        transaction.categoryId!!
                    )
                })
                waitGroup.joinAll()
            }

        } else {
            transactionManager.updateTransaction(transaction)
        }
    }

    suspend fun updateTransactionAccount(newTransaction: Transaction) {

        val transactions = transactionManager.getTransactionsWithRawAccountId(
            newTransaction.rawAccountIdName
        )

        coroutineScope {
            val waitGroup = mutableListOf<Job>()
            transactions.map { transaction ->
                launch {
                    // Normalize the type comparison to handle "Expense" vs "expense"
                    val isExpense = transaction.type.equals("expense", ignoreCase = true)
                    val signedAmount = if (isExpense) -transaction.amount else transaction.amount
                    
                    try {
                        accountManager.updateAccountBalanceForTransactionAccountUpdate(
                            transaction.accountId,
                            newTransaction.accountId,
                            signedAmount
                        )
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.joinAll()

            waitGroup.add(launch {
                accountIdMapperManager.insert(AccountIdMapper(newTransaction.rawAccountIdName, newTransaction.accountId!!))
            })
            waitGroup.add(launch {
                transactionManager.updateAccountForTransactionsWithRawAccount(
                    newTransaction.rawAccountIdName, newTransaction.accountId!!
                )
            })
            waitGroup.joinAll()
        }
    }

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
        val timestamp = smsParser.parseMessages(
            context, transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
            accountManager, appSettingManager.getAppSetting(Keys.LAST_SMS_TIMESTAMP) ?: 0L)
        appSettingManager.updateAppSetting(Keys.LAST_SMS_TIMESTAMP, timestamp)
    }

    suspend fun parseSingleMessage(body: String, sender: String, smsDateLong: Long) {
        smsParser.parseSingleMessage(body, sender, smsDateLong, transactionManager,
            payeeCategoryMapperManager, accountIdMapperManager, accountManager)
    }

    suspend fun addAccount(account: Account) {
        accountManager.addAccount(account)
    }
    suspend fun updateAccount(account: Account) {
        accountManager.updateAccount(account)
    }

    suspend fun getAccount(id: Int): Account? {
        return accountManager.getAccount(id)
    }

    suspend fun getAccountFlow(id: Int): Flow<Account?> {
        return accountManager.getAccountFlow(id)
    }
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

    fun getTransactionsByCategoryFlow(categoryId: Int?, year: Int, month: Int): Flow<List<Transaction>> {
        return transactionManager.getTransactionsByCategoryFlow(categoryId, year, month)
    }

    fun getTransactionSumByMonthFlow(year: Int, month: Int): Flow<List<TransactionSummary>> {
        return transactionManager.getSumOfTransactionsByCategoryFlow(year, month)
    }
}
