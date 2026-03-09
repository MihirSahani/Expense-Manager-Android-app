package com.example.financemanager.database

import com.example.financemanager.BuildConfig
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.PayeeCategoryMapper
import com.example.financemanager.database.entity.PayeeDisplayNames
import com.example.financemanager.database.entity.User
import com.example.financemanager.repository.data.colors

object PrepopulationData {
    val accounts = listOf(
        Account(id = 1, name = "Savings", type = "Bank", currency = "INR", currentBalance = 0.0, bankName = "HDFC", rawAccountNo = BuildConfig.SAVINGS_ACCOUNT_NO),
        Account(id = 2, name = "Credit Card", type = "Credit", currency = "INR", currentBalance = 0.0, bankName = "American Express", rawAccountNo = BuildConfig.CREDIT_ACCOUNT_NO),
        Account(id = 3, name = "Cash", type = "Cash", currency = "INR", currentBalance = 0.0)
    )

    val categories = listOf(
        Category(id = 1, name = "Food", description = "Dining and groceries", type = "Expense", color = colors[0], createdAt = "", updatedAt = "", monthlyBudget = 10_000.0),
        Category(id = 2, name = "Transport", description = "Fuel and public transport", type = "Expense", color = colors[5], createdAt = "", updatedAt = "", monthlyBudget = 2_000.0),
        Category(id = 3, name = "Salary", description = "Monthly pay", type = "Income", color = colors[9], createdAt = "", updatedAt = ""),
        Category(id = 4, name = "Groceries", description = "Groceries", type = "Expense", color = colors[14], createdAt = "", updatedAt = ""),
        Category(id = 5, name = "Utilities", description = "Electricity, water, gas", type = "Expense", color = colors[2], createdAt = "", updatedAt = "", 2_000.0),
        Category(id = 6, name = "Family and Friends", description = "Gifts, vacations", type = "Expense", color = colors[1], createdAt = "", updatedAt = ""),
        Category(id = 7, name = "Shopping", description = "Clothing, shoes, accessories", type = "Expense", color = colors[7], createdAt = "", updatedAt = "", 8_000.0),
        Category(id = 8, name = "Investment", description = "Mutual Funds, stocks, etc", type = "Expense", color = colors[13], createdAt = "", updatedAt = "", 30_000.0)
    )

    val payeeCategoryMapping = listOf(
        // Food
        PayeeCategoryMapper(payee = "Swiggy", categoryId = 1),
        PayeeCategoryMapper(payee = "Zomato", categoryId = 1),
        PayeeCategoryMapper(payee = "Jubilant FoodWorks", categoryId = 1),
        // Transport
        PayeeCategoryMapper(payee = "Uber", categoryId = 2),
        PayeeCategoryMapper(payee = "OLA", categoryId = 2),
        PayeeCategoryMapper(payee = "Rapido", categoryId = 2),
        PayeeCategoryMapper(payee = "IATA Pay Account", categoryId = 2),
        // Salary
        PayeeCategoryMapper(payee = "Deloitte Consulting India", categoryId = 3),
        // Groceries
        PayeeCategoryMapper(payee = "Zepto", categoryId = 4),
        PayeeCategoryMapper(payee = "Blinkit", categoryId = 4),
        // Utilities
        PayeeCategoryMapper(payee = "PayU", categoryId = 5),
        PayeeCategoryMapper(payee = "American Express", categoryId = 5),
        PayeeCategoryMapper(payee = "Samsung Pay", categoryId = 5),
        PayeeCategoryMapper(payee = "Airtel", categoryId = 5),
        // Friends and Families
        // Shopping
        PayeeCategoryMapper(payee = "Amazon India", categoryId = 7),
        // Investment
        PayeeCategoryMapper(payee = "Upstox", categoryId = 8)
   )

    val payeeDisplayMapping = listOf(
        PayeeDisplayNames(payee = "UPSTOX SECURITIES PVT LTD", displayName = "Upstox"),
        PayeeDisplayNames(payee = "ZOMATO LTD", displayName = "Zomato"),
        PayeeDisplayNames(payee = "PAYU COMMUNI", displayName = "PayU"),
        PayeeDisplayNames(payee = "JUBILANT FOODWORKS LIMITE", displayName = "Jubilant FoodWorks"),
        PayeeDisplayNames(payee = "SAMSUNG BILL PAY", displayName = "Samsung Pay"),
        PayeeDisplayNames(payee = "Zomatofood", displayName = "Zomato"),
        PayeeDisplayNames(payee = "UBER INDIA SYSTEMS PRIVAT", displayName = "Uber"),
        PayeeDisplayNames(payee = "ZOMATO LIMITED", displayName = "Zomato"),
        PayeeDisplayNames(payee = "ETERNAL LIMITED", displayName = "Zomato"),
        PayeeDisplayNames(payee = "DOMINOS PIZZA", displayName = "Jubilant FoodWorks"),
        PayeeDisplayNames(payee = "ZOMATO", displayName = "Zomato"),
        PayeeDisplayNames(payee = "Swiggy Ltd", displayName = "Swiggy"),
        PayeeDisplayNames(payee = "ZEPTO MARKETPLACE PRIVATE", displayName = "Zepto"),
        PayeeDisplayNames(payee = "Dominos Pizza", displayName = "Jubilant FoodWorks"),
        PayeeDisplayNames(payee = "payzomato@hdfcbank", displayName = "Zomato"),
        PayeeDisplayNames(payee = "PPSL COMMUNI", displayName = "Airtel"),
        PayeeDisplayNames(payee = "Phonepemerchant@yesbank", displayName = "PhonePe Merchant"),
        PayeeDisplayNames(payee = "INTERNATIONAL AIR TRANSPO", displayName = "IATA Pay Account")
    )

    val user = User(firstName = "Mihir", lastName = "Sahani")
}
