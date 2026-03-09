package com.example.financemanager.database.localstorage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.financemanager.database.PrepopulationData
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Lending
import com.example.financemanager.database.entity.PayeeCategoryMapper
import com.example.financemanager.database.entity.PayeeDisplayNames
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.dao.AccountDao
import com.example.financemanager.database.localstorage.dao.AppSettingDao
import com.example.financemanager.database.localstorage.dao.CategoryDao
import com.example.financemanager.database.localstorage.dao.LendingDao
import com.example.financemanager.database.localstorage.dao.PayeeCategoryMapperDao
import com.example.financemanager.database.localstorage.dao.PayeeDisplayDao
import com.example.financemanager.database.localstorage.dao.TransactionDao
import com.example.financemanager.database.localstorage.dao.UserDao
import com.example.financemanager.repository.data.Keys
import com.example.financemanager.repository.data.Timeframe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [
        User::class, Account::class, Category::class, Transaction::class, AppSetting::class,
        PayeeCategoryMapper::class, PayeeDisplayNames::class, Lending::class
], version = 14)
abstract class ExpenseManagementDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun appSettingDao(): AppSettingDao
    abstract fun PayeeCategoryMapperDao(): PayeeCategoryMapperDao
    abstract fun payeeDisplayDao(): PayeeDisplayDao
    abstract fun lendingDao(): LendingDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseManagementDatabase? = null

        fun buildDatabase(context: Context): ExpenseManagementDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseManagementDatabase::class.java,
                    "expense_management.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                prepopulateDatabase(database)
                            }
                        }
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                prepopulateDatabase(database)
                            }
                        }
                    }
                })
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(db: ExpenseManagementDatabase) {
            PrepopulationData.accounts.forEach { account ->
                db.accountDao().create(account)
            }
            PrepopulationData.categories.forEach { category -> db.categoryDao().create(category) }
            PrepopulationData.payeeDisplayMapping.forEach { mapper -> db.payeeDisplayDao().insert(mapper) }
            PrepopulationData.payeeCategoryMapping.forEach { mapper -> db.PayeeCategoryMapperDao().insert(mapper) }
            PrepopulationData.lendings.forEach { lending -> db.lendingDao().insert(lending) }
            db.userDao().create(PrepopulationData.user)

            db.appSettingDao().insert(AppSetting(Keys.BUDGET_TIMEFRAME.ordinal, Timeframe.MONTHLY.ordinal.toLong()))
            db.appSettingDao().insert(AppSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal, 0L))
            db.appSettingDao().insert(AppSetting(Keys.SALARY_CREDIT_TIME.ordinal, 0L))
            db.appSettingDao().insert(AppSetting(Keys.LAST_SMS_TIMESTAMP.ordinal, 0L))
            db.appSettingDao().insert(AppSetting(Keys.IS_INITIALIZATION_DONE.ordinal, 0L))
        }
    }
}
