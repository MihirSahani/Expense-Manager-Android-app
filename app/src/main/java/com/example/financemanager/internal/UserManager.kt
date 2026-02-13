package com.example.financemanager.internal

import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.dao.UserDao

class UserManager(
    private val userDao: UserDao
) {

    suspend fun addUser(user: User) {
        userDao.create(user)
    }

    suspend fun removeUser(user: User) {
        userDao.delete(user)
    }

    suspend fun updateUser(user: User) {
        userDao.update(user)
    }

    suspend fun getUser(): User? {
        return userDao.get()
    }
}