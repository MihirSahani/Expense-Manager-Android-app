package com.example.financemanager.ui.composable.transaction

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Account
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.ui.composable.utils.MyInput
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.viewmodel.TransactionVM
import java.util.*

@Composable
fun AddTransactionScreen(navController: NavController, viewModel: TransactionVM) {

    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    AddTransactionContent(
        onAddTransaction = { transaction ->
            viewModel.addTransaction(transaction)
            navController.popBackStack()
        },
        categories = categories,
        accounts = accounts,
        dateToString = viewModel::dateToString,
        navigateUp = { navController.navigateUp() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    onAddTransaction: (Transaction) -> Unit,
    categories: List<Category>,
    accounts: List<Account>,
    dateToString: (Long) -> String,
    navigateUp: () -> Unit
) {
    var payee by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("Expense") }
    
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var showAccountDialog by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    var transactionDate by remember { mutableLongStateOf(calendar.timeInMillis) }
    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            transactionDate = calendar.timeInMillis
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        MyText.ScreenHeader("Add Transaction")

        MyInput.TextField(
            value = payee,
            onValueChange = { payee = it },
            label = "Payee",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyInput.TextField(
            value = amount,
            onValueChange = { amount = it },
            label = "Amount",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            MyText.Header1("Type:", modifier = Modifier.padding(end = 16.dp))
            RadioButton(selected = transactionType == "Expense", onClick = {
                transactionType = "Expense"
                selectedCategory = null
            })
            MyText.Body("Expense")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = transactionType == "Income", onClick = {
                transactionType = "Income"
                selectedCategory = null
            })
            MyText.Body("Income")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            MyInput.TextField(
                value = selectedCategory?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = "Category",
                placeholder = "Select Category",
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showCategoryDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            MyInput.TextField(
                value = selectedAccount?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = "Account",
                placeholder = "Select Account",
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showAccountDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            MyInput.TextField(
                value = dateToString(transactionDate),
                onValueChange = {},
                readOnly = true,
                label = "Transaction Date",
                trailingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { datePickerDialog.show() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        MyInput.TextField(
            value = description,
            onValueChange = { description = it },
            label = "Description",
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val newTransaction = Transaction(
                    payee = payee,
                    amount = if(transactionType == "Expense") -(amount.toDoubleOrNull() ?: 0.0) else (amount.toDoubleOrNull() ?: 0.0),
                    type = transactionType,
                    transactionDate = transactionDate,
                    categoryId = selectedCategory?.id,
                    accountId = selectedAccount?.id ?: 0,
                    description = description,
                    currency = selectedAccount?.currency ?: "USD",
                    receiptURL = "",
                    location = "",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    rawAccountIdName = selectedAccount?.name ?: ""
                )
                onAddTransaction(newTransaction)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = payee.isNotBlank() && amount.isNotBlank() && selectedCategory != null && selectedAccount != null,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Save Transaction")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showCategoryDialog) {
            val filteredCategories = categories.filter { it.type.equals(transactionType, ignoreCase = true) }
            SimpleDialog(
                title = "Select Category",
                items = filteredCategories.map { it.name },
                onItemSelected = { index ->
                    selectedCategory = filteredCategories[index]
                    showCategoryDialog = false
                },
                onDismiss = { showCategoryDialog = false }
            )
        }

        if (showAccountDialog) {
            SimpleDialog(
                title = "Select Account",
                items = accounts.map { it.name },
                onItemSelected = { index ->
                    selectedAccount = accounts[index]
                    showAccountDialog = false
                },
                onDismiss = { showAccountDialog = false }
            )
        }
    }
}

@Composable
private fun SimpleDialog(
    title: String,
    items: List<String>,
    onItemSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(horizontal = 16.dp),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        title = { MyText.Header1(title) },
        text = {
            ListOfItems(
                items
            ) { item ->
                MyText.Header1(
                    item,
                    modifier = Modifier
                        .clickable { onItemSelected(items.indexOf(item)) }
                        .padding(16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                MyText.Header1("Cancel")
            }
        }
    )
}
