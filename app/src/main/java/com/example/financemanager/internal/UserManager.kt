package com.example.financemanager.internal

import com.example.financemanager.database.entity.User
import com.example.financemanager.database.localstorage.dao.UserDao
import kotlinx.coroutines.flow.Flow

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

    fun getUser(): Flow<User?> {
        return userDao.get()
    }
}