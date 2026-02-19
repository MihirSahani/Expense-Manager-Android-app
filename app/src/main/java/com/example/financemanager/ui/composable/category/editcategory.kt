package com.example.financemanager.ui.composable.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.viewmodel.CategoryVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreen(navController: NavController, viewModel: CategoryVM) {
    val categories by viewModel.categories.collectAsState()
    val category = categories.find { it.id == viewModel.categoryId }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") }
    var color by remember { mutableStateOf("#FF0000") }
    var monthlyBudget by remember { mutableStateOf("") }
    var enableMonthlyBudget by remember { mutableStateOf(false) }

    LaunchedEffect(category) {
        category?.let {
            name = it.name
            description = it.description
            type = it.type
            color = it.color
            monthlyBudget = it.monthlyBudget?.toString() ?: ""
            enableMonthlyBudget = it.monthlyBudget != null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.categoryId == null) "Add Category" else "Edit Category") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Monthly Budget")
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = enableMonthlyBudget,
                    onCheckedChange = { enableMonthlyBudget = it },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            if (enableMonthlyBudget) {
                OutlinedTextField(
                    value = monthlyBudget,
                    onValueChange = { monthlyBudget = it },
                    label = { Text("Monthly Budget Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            Text("Type", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = type == "Expense", onClick = { type = "Expense" })
                Text("Expense")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = type == "Income", onClick = { type = "Income" })
                Text("Income")
            }

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color (Hex)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (viewModel.categoryId == null) {
                        viewModel.addCategory(name, description, type, color)
                    } else {
                        category?.let {
                            val updatedCategory = it.copy(
                                name = name,
                                description = description,
                                type = type,
                                color = color,
                                monthlyBudget = if (enableMonthlyBudget) monthlyBudget.toDoubleOrNull() else null
                            )
                            viewModel.updateCategory(updatedCategory)
                        }
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}