package com.example.financemanager.database.localstorage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.financemanager.database.DummyData
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.AccountIdMapper
import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.PayeeCategoryMapper
import com.example.financemanager.database.entity.PayeeDisplayNames
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.dao.AccountDao
import com.example.financemanager.database.localstorage.dao.AccountIdMapperDao
import com.example.financemanager.database.localstorage.dao.AppSettingDao
import com.example.financemanager.database.localstorage.dao.CategoryDao
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
        PayeeCategoryMapper::class, AccountIdMapper::class, PayeeDisplayNames::class
], version = 10)
abstract class ExpenseManagementDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun appSettingDao(): AppSettingDao
    abstract fun PayeeCategoryMapperDao(): PayeeCategoryMapperDao
    abstract fun accountIdMapperDao(): AccountIdMapperDao
    abstract fun payeeDisplayDao(): PayeeDisplayDao

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
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = buildDatabase(context)
                            prepopulateDatabase(database)
                        }
                    }
                })
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(db: ExpenseManagementDatabase) {
            DummyData.accounts.forEach { account ->
                val id = db.accountDao().create(account)
                if (account.accountNo.isNotBlank()) {
                    db.accountIdMapperDao().insert(AccountIdMapper(account.accountNo, id.toInt()))
                }
            }
            DummyData.categories.forEach { category -> db.categoryDao().create(category) }
            DummyData.payeeDisplayMapping.forEach { mapper -> db.payeeDisplayDao().insert(mapper) }
            DummyData.payeeCategoryMapping.forEach { mapper -> db.PayeeCategoryMapperDao().insert(mapper) }
            db.userDao().create(DummyData.user)

            db.appSettingDao().insert(AppSetting(Keys.BUDGET_TIMEFRAME.ordinal, Timeframe.MONTHLY.ordinal.toLong()))
            db.appSettingDao().insert(AppSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal, 0L))
            db.appSettingDao().insert(AppSetting(Keys.SALARY_CREDIT_TIME.ordinal, 0L))
            db.appSettingDao().insert(AppSetting(Keys.LAST_SMS_TIMESTAMP.ordinal, 0L))
            db.appSettingDao().insert(AppSetting(Keys.IS_INITIALIZATION_DONE.ordinal, 1L))
        }
    }
}
