package com.example.financemanager.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app-settings")
class AppSetting(
    @PrimaryKey
    @ColumnInfo(name = "key")
    var key: Int?,
    @ColumnInfo(name = "value")
    var value: Long? = 0
)
