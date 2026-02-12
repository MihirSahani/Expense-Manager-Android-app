package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.financemanager.database.entity.Category

@Dao
abstract class CategoryDao {
    @Query("SELECT * FROM `categories`")
    abstract suspend fun getAll(): List<Category>

    @Update
    abstract suspend fun update(category: Category)

    @Delete
    abstract suspend fun delete(category: Category)

    @Insert
    abstract suspend fun create(category: Category)
}