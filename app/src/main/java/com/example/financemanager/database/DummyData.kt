package com.example.financemanager.database

import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DummyData {
    val accounts = listOf(
        Account(id = 1, name = "Savings", type = "Bank", currency = "USD", currentBalance = 5000.0),
        Account(id = 2, name = "Checking", type = "Bank", currency = "USD", currentBalance = 1500.0),
        Account(id = 3, name = "Credit Card", type = "Credit", currency = "USD", currentBalance = -250.0)
    )

    val categories = listOf(
        Category(id = 1, name = "Food", description = "Dining and groceries", type = "Expense", color = "#FF5733", createdAt = "", updatedAt = ""),
        Category(id = 2, name = "Transport", description = "Fuel and public transport", type = "Expense", color = "#33FF57", createdAt = "", updatedAt = ""),
        Category(id = 3, name = "Salary", description = "Monthly pay", type = "Income", color = "#3357FF", createdAt = "", updatedAt = "")
    )

    val transactions = listOf(
        Transaction(
            id = 1, accountId = 1, categoryId = 1, type = "Expense", amount = 45.5,
            payee = "Grocery Store", currency = "USD", transactionDate = "2024-03-20",
            description = "Weekly groceries", receiptURL = "", location = "New York",
            createdAt = "", updatedAt = ""
        ),
        Transaction(
            id = 2, accountId = 2, categoryId = 2, type = "Expense", amount = 30.0,
            payee = "Gas Station", currency = "USD", transactionDate = "2024-03-21",
            description = "Fuel refill", receiptURL = "", location = "New York",
            createdAt = "", updatedAt = ""
        ),
        Transaction(
            id = 3, accountId = 1, categoryId = 3, type = "Income", amount = 3000.0,
            payee = "Workplace", currency = "USD", transactionDate = "2024-03-01",
            description = "Salary for March", receiptURL = "", location = "Remote",
            createdAt = "", updatedAt = ""
        )
    )
}