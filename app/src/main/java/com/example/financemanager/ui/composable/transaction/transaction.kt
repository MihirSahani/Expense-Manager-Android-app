package com.example.financemanager.ui.composable.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.category.parseColor
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.TransactionVM
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionHistoryScreen(navController: NavController, viewModel: TransactionVM) {
    viewModel.selectTransaction(null)
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()

    TransactionHistoryContent(
        transactions = transactions,
        categories = categories,
        onTransactionClick = { transaction ->
            viewModel.selectTransaction(transaction)
            navController.navigate(Screen.ViewTransaction.route)
        }
    )
}

@Composable
fun TransactionHistoryContent(
    transactions: List<Transaction>,
    categories: List<Category>,
    onTransactionClick: (Transaction) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault()) }

    val groupedTransactions = remember(transactions) {
        transactions
            .sortedByDescending { transaction ->
                try {
                    dateFormat.parse(transaction.transactionDate)?.time ?: 0L
                } catch (e: Exception) {
                    0L
                }
            }
            .groupBy { it.transactionDate.split(" ")[0] }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        MyText.ScreenHeader("My Transactions")
        
        Spacer(modifier = Modifier.height(16.dp))

        ListOfItems(
            items = groupedTransactions,
            headerContent = { date ->
                DateHeader(date)
            },
            itemContent = { transaction ->
                val category = categories.find { it.id == transaction.categoryId }
                TransactionItem(transaction, category) {
                    onTransactionClick(transaction)
                }
            }
        )
    }
}

@Composable
fun DateHeader(date: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyText.Body(text = date)

        HorizontalDivider(
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            thickness = 1.dp,
        )
    }
}

@Composable
fun TransactionItem(transaction: Transaction, category: Category?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (category != null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(parseColor(category.color))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Uncategorized",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            MyText.Header1(text = transaction.payee)
            MyText.Body(
                text = category?.name ?: "Uncategorized", 
                color = if (category == null) MaterialTheme.colorScheme.error else Color.Gray
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            MyText.TransactionAmount(transaction)
            MyText.Body(transaction.transactionDate.split(" ").last())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionHistoryPreview() {
    val sampleCategories = listOf(
        Category(id = 1, name = "Food", description = "Groceries", type = "Expense", color = "#FF5733", createdAt = "", updatedAt = ""),
        Category(id = 2, name = "Salary", description = "Monthly Income", type = "Income", color = "#33FF57", createdAt = "", updatedAt = "")
    )
    val sampleTransactions = listOf(
        Transaction(id = 1, payee = "Starbucks", amount = 15.5, currency = "USD", type = "Expense", transactionDate = "27-Oct-2023 08:30", categoryId = 1, description = "", receiptURL = "", location = "", createdAt = "", updatedAt = "", rawAccountIdName = ""),
        Transaction(id = 2, payee = "Employer", amount = 5000.0, currency = "USD", type = "Income", transactionDate = "26-Oct-2023 10:00", categoryId = 2, description = "", receiptURL = "", location = "", createdAt = "", updatedAt = "", rawAccountIdName = ""),
        Transaction(id = 3, payee = "Unknown", amount = 20.0, currency = "USD", type = "Expense", transactionDate = "26-Oct-2023 15:45", categoryId = null, description = "", receiptURL = "", location = "", createdAt = "", updatedAt = "", rawAccountIdName = "")
    )
    FinanceManagerTheme {
        TransactionHistoryContent(
            transactions = sampleTransactions,
            categories = sampleCategories,
            onTransactionClick = {}
        )
    }
}
