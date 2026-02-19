package com.example.financemanager.ui.composable.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.ui.composable.utils.MyInput
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.AccountVM
import com.example.financemanager.viewmodel.CategoryVM
import com.example.financemanager.viewmodel.TransactionVM

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

    ViewTransactionContent(
        transaction = transaction,
        categories = categories,
        accounts = accounts,
        onUpdateCategory = { updatedTransaction, updateAll ->
            transactionVM.updateTransactionCategory(updatedTransaction, updateAll)
        },
        onUpdateAccount = { updatedTransaction ->
            transactionVM.updateTransactionAccount(updatedTransaction)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTransactionContent(
    transaction: Transaction?,
    categories: List<Category>,
    accounts: List<Account>,
    onUpdateCategory: (Transaction, Boolean) -> Unit,
    onUpdateAccount: (Transaction) -> Unit
) {
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }


    if (transaction == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MyText.Header1("Transaction not found")
        }
    } else {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            MyText.ScreenHeader("Transaction Details")

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DetailItem("Payee", transaction.payee)
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                DetailItem("Amount", "${transaction.amount} ${transaction.currency}")
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                DetailItem("Date", transaction.transactionDate)
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                DetailItem("Type", transaction.type)
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))


                if (transaction.location.isNotEmpty()) {
                    DetailItem("Location", transaction.location)
                }

            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val currentCategory = categories.find { it.id == transaction.categoryId }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MyText.Body("Category")
                        MyText.Header1(
                            currentCategory?.name ?: "Not Assigned",
                            modifier = Modifier,
                            color = if (currentCategory == null) Color.Red
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                val currentAccount = accounts.find { it.id == transaction.accountId }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAccountDialog = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MyText.Body("Account")
                    MyText.Header1(
                        text = currentAccount?.name ?:
                        "Not Assigned (${transaction.rawAccountIdName})",
                        color = if (currentAccount == null) Color.Red
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (transaction.description.isNotEmpty()) {
                    Column(
                        Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        MyText.Body("Description")
                        MyText.Header1(text = transaction.description)
                    }
                }
            }
        }
    }

    if (showCategoryDialog && transaction != null) {
        var updateCategoryForAllTransactionsWithPayee by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { MyText.Header1("Select Category") },
            text = {
                Column(
                    Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 16.dp)
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MyText.Header1(
                            "Update all for this payee",
                            modifier = Modifier.weight(1f)
                        )
                        MyInput.Switch(
                            checked = updateCategoryForAllTransactionsWithPayee,
                            onCheckedChange = {updateCategoryForAllTransactionsWithPayee = it}
                        )
                    }

                    Spacer(Modifier.padding(8.dp))

                    ListOfItems(categories) { category ->
                        TextButton(
                            onClick = {
                                val updatedTransaction = transaction.copy(
                                    categoryId = category.id
                                )
                                onUpdateCategory(updatedTransaction, updateCategoryForAllTransactionsWithPayee)
                                showCategoryDialog = false
                            },
                            modifier = Modifier.fillMaxWidth().padding( horizontal = 8.dp)
                        ) {
                            MyText.Header1(category.name, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    MyText.Header1("Cancel")
                }
            }
        )
    }

    if (showAccountDialog && transaction != null) {

        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            title = { MyText.Header1("Select Account") },
            text = {
                Column {
                    ListOfItems(accounts) { account ->
                        TextButton(
                            onClick = {
                                val updatedTransaction = transaction.copy(
                                    accountId = account.id
                                )
                                onUpdateAccount(updatedTransaction)
                                showAccountDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MyText.Header1(account.name, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    MyText.Header1("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MyText.Body(label)
        MyText.Header1(value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Transaction Details") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ViewTransactionPreview() {
    val sampleTransaction = Transaction(
        id = 1,
        payee = "Starbucks",
        amount = 15.5,
        currency = "USD",
        type = "Expense",
        transactionDate = "2023-10-27 08:30",
        categoryId = 1,
        accountId = 1,
        description = "Morning coffee",
        receiptURL = "",
        location = "Seattle, WA",
        createdAt = "",
        updatedAt = "",
        rawAccountIdName = "Visa 1234"
    )
    val sampleCategories = listOf(
        Category(id = 1, name = "Food", description = "Groceries", type = "Expense", color = "#FF5733", createdAt = "", updatedAt = "")
    )
    val sampleAccounts = listOf(
        Account(id = 1, name = "Debit Card", type = "Bank", currency = "USD", currentBalance = 1000.0, bankName = "Chase", accountNumber = "1234", isIncludedInTotal = true, createdAt = "", updatedAt = "")
    )
    FinanceManagerTheme {
        ViewTransactionContent(
            transaction = sampleTransaction,
            categories = sampleCategories,
            accounts = sampleAccounts,
            onUpdateCategory = { _, _ -> },
            onUpdateAccount = {}
        )
    }
}
