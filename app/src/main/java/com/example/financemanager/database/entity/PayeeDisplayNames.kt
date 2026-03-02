package com.example.financemanager.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payee-display-mapper")
class PayeeDisplayNames(
    @PrimaryKey
    @ColumnInfo(name = "payee")
    val payee: String,
    @ColumnInfo(name = "display_name")
    val displayName: String
)