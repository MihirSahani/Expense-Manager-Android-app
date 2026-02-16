package com.example.financemanager.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payee-category-mapper")
class PayeeCategoryMapper(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "payee")
    var payee: String,
    @ColumnInfo(name = "category_id")
    var categoryId: Int
)