package com.example.financemanager.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "account_id")
    var accountId: Int? = null,
    @ColumnInfo(name = "category_id")
    var categoryId: Int? = null,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "amount")
    var amount: Double,
    @ColumnInfo(name = "payee")
    var payee: String,
    @ColumnInfo(name = "currency")
    var currency: String,
    @ColumnInfo(name = "transaction_date")
    var transactionDate: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "receipt_url")
    var receiptURL: String,
    @ColumnInfo(name = "location")
    var location: String,
    @ColumnInfo(name = "created_at")
    var createdAt: String,
    @ColumnInfo(name = "updated_at")
    var updatedAt: String,
    @ColumnInfo(name = "raw_account_id_name")
    var rawAccountIdName: String,
)
