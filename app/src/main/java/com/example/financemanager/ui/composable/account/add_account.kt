package com.example.financemanager.ui.composable.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Account
import com.example.financemanager.ui.composable.utils.MyInput
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.AccountVM
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddEditAccountScreen(
    navController: NavController,
    accountVM: AccountVM
) {
    val existingAccount by accountVM.account.collectAsState()
    val isEditing = accountVM.selectedAccountId.collectAsState().value != null

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Bank") }
    var currency by remember { mutableStateOf("INR") }
    var balanceText by remember { mutableStateOf("0.0") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var isIncludedInTotal by remember { mutableStateOf(true) }

    LaunchedEffect(existingAccount) {
        existingAccount?.let {
            name = it.name
            type = it.type
            currency = it.currency
            balanceText = it.currentBalance.toString()
            bankName = it.bankName
            accountNumber = it.accountNo
            isIncludedInTotal = it.isIncludedInTotal
        }
    }

    AddEditAccountScreenContent(
        isEditing = isEditing,
        name = name,
        onNameChange = { name = it },
        type = type,
        onTypeChange = { type = it },
        currency = currency,
        onCurrencyChange = { currency = it },
        balanceText = balanceText,
        onBalanceTextChange = { balanceText = it },
        bankName = bankName,
        onBankNameChange = { bankName = it },
        accountNumber = accountNumber,
        onAccountNumberChange = { accountNumber = it },
        isIncludedInTotal = isIncludedInTotal,
        onIsIncludedInTotalChange = { isIncludedInTotal = it },
        onSaveClick = {
            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            val accountToSave = existingAccount?.copy(
                name = name,
                type = type,
                currency = currency,
                currentBalance = balanceText.toDoubleOrNull() ?: 0.0,
                bankName = bankName,
                accountNo = accountNumber,
                isIncludedInTotal = isIncludedInTotal,
                updatedAt = currentDateTime
            ) ?: Account(
                name = name,
                type = type,
                currency = currency,
                currentBalance = balanceText.toDoubleOrNull() ?: 0.0,
                bankName = bankName,
                accountNo = accountNumber,
                isIncludedInTotal = isIncludedInTotal,
                createdAt = currentDateTime,
                updatedAt = currentDateTime
            )
            
            if (!isEditing) {
                accountVM.addAccount(accountToSave)
            } else {
                accountVM.updateAccount(accountToSave)
            }
            navController.popBackStack()
        },
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountScreenContent(
    isEditing: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    type: String,
    onTypeChange: (String) -> Unit,
    currency: String,
    onCurrencyChange: (String) -> Unit,
    balanceText: String,
    onBalanceTextChange: (String) -> Unit,
    bankName: String,
    onBankNameChange: (String) -> Unit,
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    isIncludedInTotal: Boolean,
    onIsIncludedInTotalChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val accountTypes = listOf("Bank", "Credit", "Cash", "Investment")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { MyText.ScreenHeader(if (!isEditing) "Add Account" else "Edit Account") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MyInput.TextField(name, onNameChange, "Account Name")

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                MyInput.TextField(type, {}, "Account Type",
                    trailingIcon = null, readOnly = true, modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    accountTypes.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { MyText.Body(selectionOption) },
                            onClick = {
                                onTypeChange(selectionOption)
                                expanded = false
                            }
                        )
                    }
                }
            }

            MyInput.TextField(
                balanceText,
                onBalanceTextChange,
                "Initial Balance",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )

            MyInput.TextField(
                value = currency,
                onValueChange = onCurrencyChange,
                label = "Currency",
                modifier = Modifier.fillMaxWidth()
            )

            MyInput.TextField(
                value = bankName,
                onValueChange = onBankNameChange,
                label = "Bank Name",
                modifier = Modifier.fillMaxWidth()
            )

            MyInput.TextField(
                value = accountNumber,
                onValueChange = onAccountNumberChange,
                label = "Account Number (Last 4 digits)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyText.Body("Include in Total Net Worth")
                MyInput.Switch(checked = isIncludedInTotal, onCheckedChange = onIsIncludedInTotalChange)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Account")
            }
        }
    }
}

@Preview(showBackground = true, name = "Add Account")
@Composable
fun AddAccountScreenPreview() {
    FinanceManagerTheme {
        AddEditAccountScreenContent(
            isEditing = false,
            name = "",
            onNameChange = {},
            type = "Bank",
            onTypeChange = {},
            currency = "INR",
            onCurrencyChange = {},
            balanceText = "0.0",
            onBalanceTextChange = {},
            bankName = "",
            onBankNameChange = {},
            accountNumber = "",
            onAccountNumberChange = {},
            isIncludedInTotal = true,
            onIsIncludedInTotalChange = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Edit Account")
@Composable
fun EditAccountScreenPreview() {
    FinanceManagerTheme {
        AddEditAccountScreenContent(
            isEditing = true,
            name = "Salary Account",
            onNameChange = {},
            type = "Bank",
            onTypeChange = {},
            currency = "INR",
            onCurrencyChange = {},
            balanceText = "25000.0",
            onBalanceTextChange = {},
            bankName = "HDFC Bank",
            onBankNameChange = {},
            accountNumber = "5678",
            onAccountNumberChange = {},
            isIncludedInTotal = true,
            onIsIncludedInTotalChange = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}