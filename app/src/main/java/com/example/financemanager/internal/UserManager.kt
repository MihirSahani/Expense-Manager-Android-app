package com.example.financemanager.internal

import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.dao.UserDao

class UserManager(
    private val userDao: UserDao
) {
    var user: User? = null

    suspend fun addUser(user: User) {
        userDao.create(user)
        this.user = user
    }

    suspend fun removeUser(user: User) {
        userDao.delete(user)
        this.user = null
    }

    suspend fun updateUser(user: User) {
        userDao.update(user)
        this.user = user
    }

    suspend fun getUser() {
        user = userDao.get()
    }
}