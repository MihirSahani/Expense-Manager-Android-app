package com.example.financemanager.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase

class OnboardingRepo(private val db: ExpenseManagementDatabase) {
    val user = db.userDao().get()

    suspend fun createUser(firstName: String, lastName: String) {
        db.userDao().create(User(firstName = firstName, lastName = lastName))
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
