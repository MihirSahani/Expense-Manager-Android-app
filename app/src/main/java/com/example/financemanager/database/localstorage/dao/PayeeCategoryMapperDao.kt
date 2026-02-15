package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financemanager.database.entity.PayeeCategoryMapper

@Dao
abstract class PayeeCategoryMapperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addMapping(mapping: PayeeCategoryMapper)

    @Query("SELECT `category_id` FROM `payee-category-mapper` WHERE payee = :payee")
    abstract suspend fun getCategoryId(payee: String): Int?
}