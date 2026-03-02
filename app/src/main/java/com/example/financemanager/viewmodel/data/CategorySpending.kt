package com.example.financemanager.viewmodel.data

import com.example.financemanager.database.entity.Category

data class CategorySpending(
    val category: Category,
    val totalSpending: Double,
    val budget: Double?
)