package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financemanager.database.entity.PayeeCategoryMapper

@Dao
abstract class PayeeCategoryMapperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(mapping: PayeeCategoryMapper)

    @Query("SELECT `category_id` FROM `payee-category-mapper` WHERE payee = :payee")
    abstract suspend fun get(payee: String): Int?

    @Query("DELETE FROM `payee-category-mapper` WHERE `category_id` = :categoryId")
    abstract suspend fun delete(categoryId: Int)

    @Query("SELECT `payee` FROM `payee-category-mapper` WHERE `category_id` = :categoryId")
    abstract suspend fun getPayee(categoryId: Int): String

    @Query("UPDATE `payee-category-mapper` SET `category_id` = :categoryId WHERE `payee` = :payee")
    abstract suspend fun update(payee: String, categoryId: Int)

}