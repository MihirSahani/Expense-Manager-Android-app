package com.example.financemanager.ui.composable.analysis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.CategoryAnalysisVM
import com.example.financemanager.viewmodel.TransactionVM
import java.util.Calendar

@Composable
fun ViewTransactionByCategoryScreen(navController: NavController, viewModel: CategoryAnalysisVM, transactionVM: TransactionVM) {
    val transactions by viewModel.transactionForCategoryCurrentTimeframe.collectAsState()

    CategoryAnalysisScreenContent(
        transactions = transactions,
        onClick = { transaction ->
            transactionVM.selectTransaction(transaction)
            navController.navigate(Screen.ViewTransaction.route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAnalysisScreenContent(
    transactions: List<Transaction>,
    onClick: (Transaction) -> Unit = {}
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
                    .clickable { onClick(transaction) }
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    MyText.Header1(text = transaction.payee)
                    MyText.Date(transaction.transactionDate)
                }
                MyText.TransactionAmount(transaction)
            }
        }
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
        )
    }
}
