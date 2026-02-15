package com.example.financemanager.database

import com.example.financemanager.BuildConfig
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Category

object DummyData {
    val accounts = listOf(
        Account(name = "Savings", type = "Bank", currency = "INR", currentBalance = 5000.0, bankName = "HDFC", accountNumber = BuildConfig.ACCOUNT_NUMBER),
        Account(name = "Checking", type = "Bank", currency = "INR", currentBalance = 0.0),
        Account(name = "Credit Card", type = "Credit", currency = "INR", currentBalance = 0.0)
    )

    val categories = listOf(
        Category(name = "Food", description = "Dining and groceries", type = "Expense", color = "#FF5733", createdAt = "", updatedAt = ""),
        Category(name = "Transport", description = "Fuel and public transport", type = "Expense", color = "#33FF57", createdAt = "", updatedAt = ""),
        Category(name = "Salary", description = "Monthly pay", type = "Income", color = "#3357FF", createdAt = "", updatedAt = "")
    )

}
