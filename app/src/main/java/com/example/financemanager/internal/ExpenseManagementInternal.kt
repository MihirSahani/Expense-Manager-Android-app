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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    suspend fun getUser(): User? {
        return userManager.getUser()
    }

    suspend fun getTransactions(): MutableList<Transaction> {
        return transactionManager.getAllTransactions()
    }

    suspend fun addTransaction(transaction: Transaction) {
        transactionManager.addTransaction(transaction, accountManager)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionManager.updateTransaction(transaction)
    }


    suspend fun updateTransactionCategory(transaction: Transaction,
                                          updateCategoryForAllTransactionsWithPayee: Boolean) {

        if (transaction.categoryId != null && (updateCategoryForAllTransactionsWithPayee ||
                    payeeCategoryMapperManager.getMapping(transaction.payee) == null)) {
            payeeCategoryMapperManager.addMapping(transaction.payee,
                transaction.categoryId!!
            )
            transactionManager.updateCategoryForAllTransactionsWithPayee(transaction.payee,
                transaction.categoryId!!
            )
        }
    }

    suspend fun updateTransactionAccount(newTransaction: Transaction) {

        val transactions = transactionManager.getTransactionsWithRawAccountId(
            newTransaction.rawAccountIdName
        )

        coroutineScope {
            transactions.map { transaction ->
                async {
                    accountManager.updateAccountBalanceForTransactionAccountUpdate(
                        transaction.accountId,
                        newTransaction.accountId, transaction.amount
                    )
                }
            }
        }.awaitAll()

        transactionManager.updateAccountForAllTransactionsWithRawAccount(
            newTransaction.rawAccountIdName, newTransaction.accountId!!)
    }

    suspend fun loadDummyData() {
        coroutineScope {
            val accountsJob = async {
                val accounts = accountManager.getAllAccounts()
                if (accounts.isEmpty()) {
                    DummyData.accounts.map { account ->
                        async {
                            accountManager.addAccount(account)
                        }
                    }.awaitAll()
                }
            }
            val categoriesJob = async {
                val categories = categoryManager.getAllCategories()
                if (categories.isEmpty()) {
                    DummyData.categories.map { category ->
                        async {
                            categoryManager.addCategory(category)
                        }
                    }.awaitAll()
                }
            }
            accountsJob.await()
            categoriesJob.await()
        }
    }

    suspend fun parseMessagesToTransactions(context: Context) {
        if (transactionManager.getAllTransactions().isEmpty()) {
            smsParser.parseMessagesToTransactions(context, transactionManager,
                payeeCategoryMapperManager, accountIdMapperManager, accountManager)
        }
    }

    suspend fun addAccount(account: Account) {
        accountManager.addAccount(account)
    }

    suspend fun createUser(firstName: String, lastName: String) {
        userManager.addUser(firstName, lastName)
    }

    suspend fun updateUser(user: User) {
        userManager.updateUser(user)
    }

    suspend fun getAccounts(): MutableList<Account> {
        return accountManager.getAllAccounts()
    }

    suspend fun getCategories(): MutableList<Category> {
        return categoryManager.getAllCategories()
    }

    suspend fun getPayeeCategory(payee: String): Int? {
        return payeeCategoryMapperManager.getMapping(payee)
    }

    suspend fun updatePayeeCategory(payee: String, categoryId: Int) {
        payeeCategoryMapperManager.addMapping(payee, categoryId)
    }

    suspend fun mapAccountToId(accountIdMapper: AccountIdMapper) {
        accountIdMapperManager.insert(accountIdMapper)
    }

    suspend fun getAccountIdMapping(accountName: String): Int? {
        return accountIdMapperManager.getAccountId(accountName)
    }

    suspend fun getTransactionsByCategory(categoryId: Int?, year: Int, month: Int): List<Transaction> {
        return transactionManager.getTransactionsByCategory(categoryId, year, month)
    }

    suspend fun getTransactionSumByMonth(year: Int, month: Int): List<TransactionSummary> {
        return transactionManager.getSumOfTransactionsByCategory(year, month)
    }
}
