package com.example.financemanager.internal

import android.content.Context
import androidx.room.Database
import com.example.financemanager.Graph
import com.example.financemanager.database.DummyData
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.AccountIdMapper
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExpenseManagementInternal(database: ExpenseManagementDatabase) {
    val accountManager: AccountManager = AccountManager(database.accountDao())
    val categoryManager: CategoryManager = CategoryManager(database.categoryDao())
    val transactionManager: TransactionManager = TransactionManager(database.transactionDao())
    val userManager: UserManager = UserManager(database.userDao())
    val appSettingManager: AppSettingManager = AppSettingManager(database.appSettingDao())
    val payeeCategoryMapperManager: PayeeCategoryMapperManager = PayeeCategoryMapperManager(database.PayeeCategoryMapperDao())
    val accountIdMapperManager: AccountIdMapperManager = AccountIdMapperManager(database.accountIdMapperDao())
    val smsParser: SMSParser = SMSParser()

    suspend fun getUser(): User? {
        return userManager.getUser()
    }

    suspend fun getTransactions(): MutableList<Transaction> {
        return transactionManager.getAllTransactions()
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionManager.updateTransaction(transaction)
    }

    suspend fun updateTransactionCategory(transaction: Transaction, updateCategoryForAllTransactionsWithPayee: Boolean) {
        updateTransaction(transaction)
        if (transaction.categoryId != null && (updateCategoryForAllTransactionsWithPayee || payeeCategoryMapperManager.getCategoryId(transaction.payee) == null)) {
            payeeCategoryMapperManager.addMapping(transaction.payee, transaction.categoryId!!)
            transactionManager.updateCategoryForAllTransactionsWithPayee(transaction.payee, transaction.categoryId!!)
        }
    }

    suspend fun updateTransactionAccount(transaction: Transaction) {
        updateTransaction(transaction)
        if (transaction.accountId != null) {
            accountIdMapperManager.insert(AccountIdMapper(transaction.rawAccountIdName, transaction.accountId!!))
            transactionManager.updateAccountForAllTransactionsWithAccount(transaction.rawAccountIdName, transaction.accountId!!)
        }
    }

    suspend fun loadDummyData() {
        coroutineScope {
            val accounts = accountManager.getAllAccounts()
            if (accounts.isEmpty()) {
                launch { DummyData.accounts.forEach { accountManager.addAccount(it) } }
            }
            val categories = categoryManager.getAllCategories()
            if (categories.isEmpty()) {
                launch { DummyData.categories.forEach { categoryManager.addCategory(it) } }
            }
        }
    }

    suspend fun parseMessagesToTransactions(context: Context) {
        if (transactionManager.getAllTransactions().isEmpty()) {
            smsParser.parseMessagesToTransactions(context, transactionManager, payeeCategoryMapperManager, accountIdMapperManager)
        }
    }

    suspend fun addAccount(account: Account) {
        accountManager.addAccount(account)
    }

    suspend fun createUser(firstName: String, lastName: String) {
        val user = User(firstName = firstName, lastName = lastName, token = "dummy_token")
        userManager.addUser(user)
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
        return payeeCategoryMapperManager.getCategoryId(payee)
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
}
