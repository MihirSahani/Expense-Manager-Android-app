package com.example.financemanager.database.localstorage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.dao.AccountDao
import com.example.financemanager.database.localstorage.dao.CategoryDao
import com.example.financemanager.database.localstorage.dao.TransactionDao
import com.example.financemanager.database.localstorage.dao.UserDao
import com.example.financemanager.internal.AccountManager
import com.example.financemanager.internal.CategoryManager
import com.example.financemanager.internal.TransactionManager
import com.example.financemanager.internal.UserManager

@Database(entities = [User::class, Account::class, Category::class, Transaction::class], version = 1)
abstract class ExpenseManagementDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
}

object Graph {
    lateinit var database: ExpenseManagementDatabase
    lateinit var userManager: UserManager
    lateinit var accountManager: AccountManager
    lateinit var categoryManager: CategoryManager
    lateinit var transactionManager: TransactionManager

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, ExpenseManagementDatabase::class.java, "expense_management.db").build()
        userManager = UserManager(database.userDao())
        accountManager = AccountManager(database.accountDao())
        categoryManager = CategoryManager(database.categoryDao())
        transactionManager = TransactionManager(database.transactionDao())
    }
}