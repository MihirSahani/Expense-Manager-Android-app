package com.example.financemanager.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app-settings")
class AppSetting(
    @ColumnInfo(name = "key")
    var key: String = "",
    @ColumnInfo(name = "value")
    var value: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}