package com.example.financemanager.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account-id-mapper")
class AccountIdMapper(
    @PrimaryKey
    @ColumnInfo(name = "account_name")
    var accountName: String,
    @ColumnInfo(name = "account_id")
    var accountId: Int
)