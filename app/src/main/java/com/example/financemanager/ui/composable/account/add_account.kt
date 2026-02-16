package com.example.financemanager.ui.composable.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Account
import com.example.financemanager.viewmodel.AccountVM
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountScreen(
    navController: NavController,
    accountVM: AccountVM
) {
    val existingAccount = remember { accountVM.getAccountById(accountVM.selectedAccountId) }
    if (existingAccount == null) {
        navController.popBackStack()
        return
    }

    var name by remember { mutableStateOf(existingAccount.name)}
    var type by remember { mutableStateOf(existingAccount.type) }
    var currency by remember { mutableStateOf(existingAccount.currency) }
    var balanceText by remember { mutableStateOf(existingAccount.currentBalance.toString()) }
    var bankName by remember { mutableStateOf(existingAccount.bankName) }
    var accountNumber by remember { mutableStateOf(existingAccount.accountNumber)}
    var isIncludedInTotal by remember { mutableStateOf(existingAccount.isIncludedInTotal) }

    val accountTypes = listOf("Bank", "Credit", "Cash", "Investment")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (accountVM.selectedAccountId == null) "Add Account" else "Edit Account") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account Name") },
                modifier = Modifier.fillMaxWidth()
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Account Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    accountTypes.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                type = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = balanceText,
                onValueChange = { balanceText = it },
                label = { Text("Initial Balance") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Currency") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Bank Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = { Text("Account Number (Last 4 digits)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Include in Total Net Worth")
                Switch(checked = isIncludedInTotal, onCheckedChange = { isIncludedInTotal = it })
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                    val accountToSave = existingAccount.copy(
                        name = name,
                        type = type,
                        currency = currency,
                        currentBalance = balanceText.toDoubleOrNull() ?: 0.0,
                        bankName = bankName,
                        accountNumber = accountNumber,
                        isIncludedInTotal = isIncludedInTotal,
                        updatedAt = currentDateTime,
                        createdAt = if (existingAccount.createdAt.isEmpty()) currentDateTime else existingAccount.createdAt
                    )
                    
                    if (accountVM.selectedAccountId == null) {
                        accountVM.addAccount(accountToSave)
                    } else {
                        accountVM.updateAccount(accountToSave)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Save Account")
            }
        }
    }
}
