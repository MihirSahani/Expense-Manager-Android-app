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
            accountNumber = it.accountNumber
            isIncludedInTotal = it.isIncludedInTotal
        }
    }

    val accountTypes = listOf("Bank", "Credit", "Cash", "Investment")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (!isEditing) "Add Account" else "Edit Account") },
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
                    val accountToSave = existingAccount?.copy(
                        name = name,
                        type = type,
                        currency = currency,
                        currentBalance = balanceText.toDoubleOrNull() ?: 0.0,
                        bankName = bankName,
                        accountNumber = accountNumber,
                        isIncludedInTotal = isIncludedInTotal,
                        updatedAt = currentDateTime
                    ) ?: Account(
                        name = name,
                        type = type,
                        currency = currency,
                        currentBalance = balanceText.toDoubleOrNull() ?: 0.0,
                        bankName = bankName,
                        accountNumber = accountNumber,
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
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Save Account")
            }
        }
    }
}