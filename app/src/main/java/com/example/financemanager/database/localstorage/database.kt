package com.example.financemanager.database.localstorage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.PayeeCategoryMapper
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.dao.AccountDao
import com.example.financemanager.database.localstorage.dao.AppSettingDao
import com.example.financemanager.database.localstorage.dao.CategoryDao
import com.example.financemanager.database.localstorage.dao.PayeeCategoryMapperDao
import com.example.financemanager.database.localstorage.dao.TransactionDao
import com.example.financemanager.database.localstorage.dao.UserDao

@Database(entities = [User::class, Account::class, Category::class, Transaction::class, AppSetting::class, PayeeCategoryMapper::class], version = 3)
abstract class ExpenseManagementDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun appSettingDao(): AppSettingDao
    abstract fun PayeeCategoryMapperDao(): PayeeCategoryMapperDao
}
