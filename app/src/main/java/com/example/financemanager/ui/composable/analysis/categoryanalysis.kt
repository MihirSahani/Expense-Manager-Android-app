package com.example.financemanager.ui.composable.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.transaction.DateHeader
import com.example.financemanager.ui.composable.transaction.TransactionItem
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.CategoryAnalysisVM
import com.example.financemanager.viewmodel.TransactionVM
import java.util.*

@Composable
fun ViewTransactionByCategoryScreen(navController: NavController, viewModel: CategoryAnalysisVM, transactionVM: TransactionVM) {
    val transactions by viewModel.transactionForCategoryCurrentTimeframe.collectAsState()
    val categories by transactionVM.categories.collectAsState()

    CategoryAnalysisScreenContent(
        transactions = transactions,
        categories = categories,
        onClick = { transaction ->
            transactionVM.selectTransaction(transaction)
            navController.navigate(Screen.ViewTransaction.route)
        },
        dateToString = transactionVM::dateToString
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAnalysisScreenContent(
    transactions: List<Transaction>,
    categories: List<com.example.financemanager.database.entity.Category>,
    onClick: (Transaction) -> Unit = {},
    dateToString: (Long) -> String = { it.toString() }
) {
    val groupedTransactions = remember(transactions) {
        transactions
            .sortedByDescending { it.transactionDate }
            .groupBy { dateToString(it.transactionDate).substring(0, 10) }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MyText.ScreenHeader("Transactions")

        ListOfItems(
            items = groupedTransactions,
            headerContent = { date ->
                DateHeader(date)
            },
            itemContent = { transaction ->
                val category = categories.find { it.id == transaction.categoryId }
                TransactionItem(transaction, category) {
                    onClick(transaction)
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryAnalysisScreenPreview() {
    val calendar = Calendar.getInstance()
    val sampleTransactions = listOf(
        Transaction(id = 1, payee = "Starbucks", amount = -15.50, transactionDate = calendar.timeInMillis, type = "Expense", currency = "USD", description = "", receiptURL = "", location = "", createdAt = 0L, updatedAt = 0L, rawAccountIdName = ""),
        Transaction(id = 2, payee = "Walmart", amount = -120.00, transactionDate = calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis, type = "Expense", currency = "USD", description = "", receiptURL = "", location = "", createdAt = 0L, updatedAt = 0L, rawAccountIdName = ""),
        Transaction(id = 3, payee = "Apple Music", amount = -9.99, transactionDate = calendar.apply { add(Calendar.DAY_OF_YEAR, -2) }.timeInMillis, type = "Expense", currency = "USD", description = "", receiptURL = "", location = "", createdAt = 0L, updatedAt = 0L, rawAccountIdName = "")
    )
    FinanceManagerTheme {
        CategoryAnalysisScreenContent(
            transactions = sampleTransactions,
            categories = emptyList()
        )
    }
}
