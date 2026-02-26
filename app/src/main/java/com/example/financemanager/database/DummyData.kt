package com.example.financemanager.database

import com.example.financemanager.BuildConfig
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Category

object DummyData {
    val accounts = listOf(
        Account(name = "Savings", type = "Bank", currency = "INR", currentBalance = 0.0, bankName = "HDFC", accountNo = BuildConfig.ACCOUNT_NUMBER),
        Account(name = "Credit Card", type = "Credit", currency = "INR", currentBalance = 0.0, bankName = "AMEX", accountNo = BuildConfig.ACCOUNT_NUMBER),
        Account(name = "Cash", type = "Cash", currency = "INR", currentBalance = 0.0)
    )

    val categories = listOf(
        Category(name = "Food", description = "Dining and groceries", type = "Expense", color = "#FF7043", createdAt = "", updatedAt = ""),
        Category(name = "Transport", description = "Fuel and public transport", type = "Expense", color = "#42A5F5", createdAt = "", updatedAt = ""),
        Category(name = "Salary", description = "Monthly pay", type = "Income", color = "#66BB6A", createdAt = "", updatedAt = ""),
        Category(name = "Groceries", description = "Groceries", type = "Expense", color = "#FF7043", createdAt = "", updatedAt = ""),
        Category(name = "Utilities", description = "Electricity, water, gas", type = "Expense", color = "#29B6F6", createdAt = "", updatedAt = ""),
        Category(name = "Family and Friends", description = "Gifts, vacations", type = "Expense", color = "#FFCA28", createdAt = "", updatedAt = ""),
        Category(name = "Shopping", description = "Clothing, shoes, accessories", type = "Expense", color = "#66BB6A", createdAt = "", updatedAt = "")
    )

}
