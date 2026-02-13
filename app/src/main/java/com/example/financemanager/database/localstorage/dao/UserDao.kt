package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financemanager.database.entity.User

@Dao
abstract class UserDao {
    @Query("SELECT * FROM `user`")
    abstract suspend fun get(): User?

    @Update
    abstract suspend fun update(user: User)

    @Delete
    abstract suspend fun delete(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun create(user: User)
}