package com.example.financemanager.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.example.financemanager.database.DummyData
import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import com.example.financemanager.repository.data.Keys
import com.example.financemanager.repository.data.Timeframe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope

class OnboardingRepo(private val db: ExpenseManagementDatabase) {
    val user = db.userDao().get()

    suspend fun createUser(firstName: String, lastName: String) {
        db.userDao().create(User(firstName = firstName, lastName = lastName))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun initialize() {
        val isInitialized = db.appSettingDao().getByKey(Keys.IS_INITIALIZATION_DONE.ordinal)
        if (isInitialized == null) {
            coroutineScope {
                // ----------------------------------------------------- Load dummy data
                DummyData.accounts.forEach { account ->
                    db.accountDao().create(account)
                }
                DummyData.categories.forEach { category ->
                    db.categoryDao().create(category)
                }
                db.appSettingDao()
                    .insert(AppSetting(Keys.BUDGET_TIMEFRAME.ordinal, Timeframe.MONTHLY.ordinal.toLong()))
                db.appSettingDao()
                    .insert(AppSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal, 0L))
                db.appSettingDao()
                    .insert(AppSetting(Keys.SALARY_CREDIT_TIME.ordinal, 0L))
                db.appSettingDao()
                    .insert(AppSetting(Keys.LAST_SMS_TIMESTAMP.ordinal, 0L))
                db.appSettingDao()
                    .insert(AppSetting(Keys.IS_INITIALIZATION_DONE.ordinal, 1L))
            }
        }
    }

    fun hasMandatoryPermission(context: Context): Boolean {
        return context.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                &&
                context.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
    }

    fun hasOptionalPermission(context: Context): Boolean {
        return context.checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
    }
}
