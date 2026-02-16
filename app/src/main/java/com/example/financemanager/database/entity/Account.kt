package com.example.financemanager.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "type")
    var type: String = "",

    @ColumnInfo(name = "currency")
    var currency: String = "",

    @ColumnInfo(name = "balance")
    var currentBalance: Double = 0.0,

    @ColumnInfo(name = "bank_name")
    var bankName: String = "",

    @ColumnInfo(name = "account_number")
    var accountNumber: String = "",

    @ColumnInfo(name = "is_included_in_total")
    var isIncludedInTotal: Boolean = true,

    @ColumnInfo(name = "is_active")
    var isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    var createdAt: String = "",

    @ColumnInfo(name = "updated_at")
    var updatedAt: String = "",
)
