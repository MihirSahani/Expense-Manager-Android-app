package com.example.financemanager.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lending")
class Lending(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "payee")
    val payee: String,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name =  "is_paid")
    val isPaid: Boolean,
    @ColumnInfo(name = "is_given")
    val isGiven: Boolean,

    @ColumnInfo(name = "transaction_date")
    val transactionDate: Long,
    @ColumnInfo(name = "return_date")
    val returnDate: Long,
)