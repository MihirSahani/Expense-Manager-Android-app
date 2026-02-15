package com.example.financemanager.ui.composable.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.viewmodel.TransactionVM

@Composable
fun TransactionHistoryScreen(navController: NavController, viewModel: TransactionVM) {
    val transactions by viewModel.transactions.collectAsState()

    // Group and sort transactions by date (latest first)
    val groupedTransactions = remember(transactions) {
        transactions
            .sortedByDescending { it.transactionDate }
            .groupBy { it.transactionDate.split(" ")[0] }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Transaction History",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            groupedTransactions.forEach { (date, transactionsInDate) ->
                item {
                    DateHeader(date)
                }
                items(transactionsInDate) { transaction ->
                    TransactionItem(transaction) {
                        viewModel.selectTransaction(transaction)
                        navController.navigate(Screen.ViewTransaction.route)
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            modifier = Modifier.padding(end = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = transaction.payee, fontWeight = FontWeight.Bold)
                Text(text = transaction.transactionDate, fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = "${if (transaction.type == "Expense") "-" else "+"}${transaction.amount} ${transaction.currency}",
                color = if (transaction.type == "Expense") Color.Red else Color.Green,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
