package com.example.financemanager.database.localstorage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financemanager.database.entity.Lending
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LendingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(lending: Lending)

    @Query("SELECT * FROM `lending`")
    abstract fun getLendingsFlow(): Flow<List<Lending>>

    @Query("SELECT * FROM `lending`")
    abstract suspend fun getLendings(): List<Lending>

    @Update
    abstract suspend fun update(lending: Lending)

    @Delete
    abstract suspend fun delete(lending: Lending)

    @Query("UPDATE `lending` SET `is_paid` = :isPaid WHERE `id` = :id")
    abstract suspend fun updateIsPaid(id: Int, isPaid: Boolean)

    @Query("UPDATE `lending` SET `return_date` = :returnDate WHERE `id` = :id")
    abstract suspend fun updateReturnDate(id: Int, returnDate: Long)
}