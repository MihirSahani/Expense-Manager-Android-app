package com.example.financemanager.ui.composable.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.ui.composable.utils.MyInput
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
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

    AddEditCategoryScreenContent(
        isEditing = viewModel.categoryId != null,
        name = name,
        onNameChange = { name = it },
        description = description,
        onDescriptionChange = { description = it },
        type = type,
        onTypeChange = { type = it },
        color = color,
        onColorChange = { color = it },
        monthlyBudget = monthlyBudget,
        onMonthlyBudgetChange = { monthlyBudget = it },
        enableMonthlyBudget = enableMonthlyBudget,
        onEnableMonthlyBudgetChange = { enableMonthlyBudget = it },
        onSaveClick = {
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
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreenContent(
    isEditing: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    type: String,
    onTypeChange: (String) -> Unit,
    color: String,
    onColorChange: (String) -> Unit,
    monthlyBudget: String,
    onMonthlyBudgetChange: (String) -> Unit,
    enableMonthlyBudget: Boolean,
    onEnableMonthlyBudgetChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MyInput.TextField(
                value = name,
                onValueChange = onNameChange,
                label = ("Name"),
                modifier = Modifier.fillMaxWidth()
            )
            MyInput.TextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = ("Description"),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp, 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Monthly Budget")
                Spacer(modifier = Modifier.width(16.dp))
                MyInput.Switch(
                    checked = enableMonthlyBudget,
                    onCheckedChange = onEnableMonthlyBudgetChange,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            if (enableMonthlyBudget) {
                MyInput.TextField(
                    value = monthlyBudget,
                    onValueChange = onMonthlyBudgetChange,
                    label = "Monthly Budget Amount",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp, 1.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Type", fontWeight = FontWeight.Bold)
                RadioButton(selected = type == "Expense", onClick = { onTypeChange("Expense") })
                Text("Expense")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = type == "Income", onClick = { onTypeChange("Income") })
                Text("Income")
            }

            MyInput.TextField(
                value = color,
                onValueChange = onColorChange,
                label = ("Color (Hex)"),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save")
            }
        }
    }
}

@Preview(showBackground = true, name = "Add Category")
@Composable
fun AddCategoryScreenPreview() {
    FinanceManagerTheme {
        AddEditCategoryScreenContent(
            isEditing = false,
            name = "",
            onNameChange = {},
            description = "",
            onDescriptionChange = {},
            type = "Expense",
            onTypeChange = {},
            color = "#FF0000",
            onColorChange = {},
            monthlyBudget = "",
            onMonthlyBudgetChange = {},
            enableMonthlyBudget = false,
            onEnableMonthlyBudgetChange = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Edit Category")
@Composable
fun EditCategoryScreenPreview() {
    FinanceManagerTheme {
        AddEditCategoryScreenContent(
            isEditing = true,
            name = "Groceries",
            onNameChange = {},
            description = "Weekly grocery shopping",
            onDescriptionChange = {},
            type = "Expense",
            onTypeChange = {},
            color = "#4CAF50",
            onColorChange = {},
            monthlyBudget = "20000",
            onMonthlyBudgetChange = {},
            enableMonthlyBudget = true,
            onEnableMonthlyBudgetChange = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
