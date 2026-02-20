package com.example.financemanager.database

import com.example.financemanager.BuildConfig
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Category

object DummyData {
    val accounts = listOf(
        Account(name = "Savings", type = "Bank", currency = "INR", currentBalance = 0.0, bankName = "HDFC", accountNumber = BuildConfig.ACCOUNT_NUMBER),
        Account(name = "Credit Card", type = "Credit", currency = "INR", currentBalance = 0.0, bankName = "AMEX", accountNumber = BuildConfig.ACCOUNT_NUMBER),
        Account(name = "Cash", type = "Cash", currency = "INR", currentBalance = 0.0)
    )

    val categories = listOf(
        Category(name = "Food", description = "Dining and groceries", type = "Expense", color = "#FF7043", createdAt = "", updatedAt = ""),
        Category(name = "Transport", description = "Fuel and public transport", type = "Expense", color = "#42A5F5", createdAt = "", updatedAt = ""),
        Category(name = "Salary", description = "Monthly pay", type = "Income", color = "#66BB6A", createdAt = "", updatedAt = ""),
        Category(name = "Groceries", description = "Groceries", type = "Expense", color = "#FF7043", createdAt = "", updatedAt = ""),
        Category(name = "Utilities", description = "Electricity, water, gas", type = "Expense", color = "#29B6F6", createdAt = "", updatedAt = ""),
        Category(name = "Entertainment", description = "Movies, music, hobbies", type = "Expense", color = "#AB47BC", createdAt = "", updatedAt = ""),
        Category(name = "Health", description = "Health and wellness", type = "Expense", color = "#EC407A", createdAt = "", updatedAt = ""),
        Category(name = "Education", description = "Tuition, books, courses", type = "Expense", color = "#8D6E63", createdAt = "", updatedAt = ""),
        Category(name = "Family", description = "Gifts, vacations", type = "Expense", color = "#FFCA28", createdAt = "", updatedAt = ""),
    )

}
