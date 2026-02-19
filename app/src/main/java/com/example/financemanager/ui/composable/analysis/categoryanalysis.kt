package com.example.financemanager.ui.composable.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.CategoryAnalysisVM

@Composable
fun ViewTransactionByCategoryScreen(navController: NavController, viewModel: CategoryAnalysisVM) {
    val transactions by viewModel.transactionForCategoryCurrentTimeframe.collectAsState()

    CategoryAnalysisScreenContent(
        transactions = transactions,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAnalysisScreenContent(
    transactions: List<Transaction>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MyText.ScreenHeader("Transactions")

        ListOfItems(transactions, Modifier.padding(16.dp)) { transaction ->
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    MyText.Header1(text = transaction.payee)
                    MyText.Body(text = transaction.transactionDate)
                }
                MyText.TransactionAmount(transaction)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryAnalysisScreenPreview() {
    val sampleTransactions = listOf(
        Transaction(id = 1, payee = "Starbucks", amount = -15.50, transactionDate = "2023-10-27", type = "Expense", currency = "USD", description = "", receiptURL = "", location = "", createdAt = "", updatedAt = "", rawAccountIdName = ""),
        Transaction(id = 2, payee = "Walmart", amount = -120.00, transactionDate = "2023-10-26", type = "Expense", currency = "USD", description = "", receiptURL = "", location = "", createdAt = "", updatedAt = "", rawAccountIdName = ""),
        Transaction(id = 3, payee = "Apple Music", amount = -9.99, transactionDate = "2023-10-25", type = "Expense", currency = "USD", description = "", receiptURL = "", location = "", createdAt = "", updatedAt = "", rawAccountIdName = "")
    )
    FinanceManagerTheme {
        CategoryAnalysisScreenContent(
            transactions = sampleTransactions,
        )
    }
}
