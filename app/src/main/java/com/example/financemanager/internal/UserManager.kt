package com.example.financemanager.internal

import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.dao.UserDao

class UserManager(
    private val userDao: UserDao
) {

    suspend fun addUser(firstName: String, lastName: String, token: String = "") {
        userDao.create(User(firstName=firstName, lastName = lastName, token = token))
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