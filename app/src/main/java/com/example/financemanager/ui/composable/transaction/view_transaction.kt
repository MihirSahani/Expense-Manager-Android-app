package com.example.financemanager.ui.composable.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financemanager.viewmodel.AccountVM
import com.example.financemanager.viewmodel.CategoryVM
import com.example.financemanager.viewmodel.TransactionVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTransactionScreen(
    navController: NavController,
    transactionVM: TransactionVM,
    categoryVM: CategoryVM,
    accountVM: AccountVM
) {
    val transaction by transactionVM.selectedTransaction.collectAsState()
    val categories by categoryVM.categories.collectAsState()
    val accounts by accountVM.accounts.collectAsState()

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (transaction == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Transaction not found")
            }
        } else {
            val currentTransaction = transaction!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailItem("Payee", currentTransaction.payee)
                DetailItem("Amount", "${currentTransaction.amount} ${currentTransaction.currency}")
                DetailItem("Date", currentTransaction.transactionDate)
                DetailItem("Type", currentTransaction.type)
                
                val currentCategory = categories.find { it.id == currentTransaction.categoryId }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Category", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = currentCategory?.name ?: "Not Assigned",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (currentCategory == null) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Button(onClick = { showCategoryDialog = true }) {
                        Text(if (currentCategory == null) "Assign" else "Change")
                    }
                }

                val currentAccount = accounts.find { it.id == currentTransaction.accountId }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Account", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = currentAccount?.name ?: "Not Assigned (${currentTransaction.rawAccountIdName})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (currentAccount == null) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Button(onClick = { showAccountDialog = true }) {
                        Text(if (currentAccount == null) "Assign" else "Change")
                    }
                }

                if (currentTransaction.description.isNotEmpty()) {
                    DetailItem("Description", currentTransaction.description)
                }
                
                if (currentTransaction.location.isNotEmpty()) {
                    DetailItem("Location", currentTransaction.location)
                }
            }
        }
    }

    if (showCategoryDialog && transaction != null) {
        val currentTransaction = transaction!!
        var updateCategoryForAllTransactionsWithPayee by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Select Category") },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Update all for this payee", modifier = Modifier.weight(1f))
                        Switch(checked = updateCategoryForAllTransactionsWithPayee, onCheckedChange = {updateCategoryForAllTransactionsWithPayee = it}, )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    categories.forEach { category ->
                        TextButton(
                            onClick = {
                                val updatedTransaction = currentTransaction.copy(categoryId = category.id)
                                transactionVM.updateTransactionCategory(updatedTransaction, updateCategoryForAllTransactionsWithPayee)
                                showCategoryDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(category.name, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAccountDialog && transaction != null) {
        val currentTransaction = transaction!!
        var updateAccountForAllTransactions by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            title = { Text("Select Account") },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    accounts.forEach { account ->
                        TextButton(
                            onClick = {
                                val updatedTransaction = currentTransaction.copy(accountId = account.id)
                                transactionVM.updateTransactionAccount(updatedTransaction)
                                showAccountDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(account.name, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp)
    }
}
