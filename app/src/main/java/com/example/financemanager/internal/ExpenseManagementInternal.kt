package com.example.financemanager.internal

import androidx.room.Database
import com.example.financemanager.Graph
import com.example.financemanager.database.DummyData
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import kotlinx.coroutines.flow.Flow

class ExpenseManagementInternal(database: ExpenseManagementDatabase) {
    val accountManager: AccountManager = AccountManager(database.accountDao())
    val categoryManager: CategoryManager = CategoryManager(database.categoryDao())
    val transactionManager: TransactionManager = TransactionManager(database.transactionDao())
    val userManager: UserManager = UserManager(database.userDao())

    suspend fun getUser(): User? {
        return userManager.getUser()
    }

    suspend fun getTransactions(): MutableList<Transaction> {
        return transactionManager.getAllTransactions()
    }

    suspend fun loadDummyData() {
        DummyData.accounts.forEach { accountManager.addAccount(it) }
        DummyData.categories.forEach { categoryManager.addCategory(it) }
        DummyData.transactions.forEach { transactionManager.addTransaction(it) }
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
}