package com.example.financemanager.repository

import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase

class UserRepo(private val db: ExpenseManagementDatabase) {
    val user = db.userDao().get()

    suspend fun addUser(firstName: String, lastName: String) {
        db.userDao().create(User(firstName = firstName, lastName = lastName))
    }

    suspend fun updateUser(user: User) {
        db.userDao().update(user)
    }
}