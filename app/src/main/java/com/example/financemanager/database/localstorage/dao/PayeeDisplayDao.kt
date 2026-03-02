package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financemanager.database.entity.PayeeDisplayNames

@Dao
abstract class PayeeDisplayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(mapping: PayeeDisplayNames)

    @Query("SELECT `display_name` FROM `payee-display-mapper` WHERE `payee` = :payee")
    abstract suspend fun get(payee: String): String?

    @Query("SELECT `display_name` FROM `payee-display-mapper` WHERE `payee` = :payee")
    abstract fun getFlow(payee: String): String?
}