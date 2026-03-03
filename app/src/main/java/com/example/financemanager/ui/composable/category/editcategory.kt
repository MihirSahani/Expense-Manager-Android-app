package com.example.financemanager.ui.composable.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.financemanager.repository.data.colors
import com.example.financemanager.ui.composable.utils.ListOfGrids
import com.example.financemanager.ui.composable.utils.ListOfItems
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
    var showColorPicker by remember { mutableStateOf(false) }

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
        showColorPicker = showColorPicker,
        onColorPickerChange = { showColorPicker = it },
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
    showColorPicker: Boolean,
    onColorPickerChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
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
                MyText.Header2("Monthly Budget")
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
                MyText.Header2("Type")

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = type == "Expense", onClick = { onTypeChange("Expense") })
                    MyText.Header2("Expense")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = type == "Income", onClick = { onTypeChange("Income") })
                    MyText.Header2("Income")
                }
            }

            // MyInput.TextField(
            //     value = color,
            //     onValueChange = onColorChange,
            //     label = ("Color (Hex)"),
            //     modifier = Modifier.fillMaxWidth()
            // )

            Row(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .clickable {
                        onColorPickerChange(true)
                    }
                    .padding(8.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            )  {
                MyText.Header1("Color")
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(parseColor(color))
                )
            }

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save")
            }
        }

        if(showColorPicker) {
            AlertDialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = { onColorPickerChange(false) },
                title = { MyText.Header1("Select Color") },
                text = {
                    ListOfGrids(colors) { color ->
                        TextButton(
                            onClick = {
                                onColorChange(color)
                                onColorPickerChange(false)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(parseColor(color))
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { onColorPickerChange(false) }) {
                        MyText.Header1("Cancel")
                    }
                }
            )
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
            showColorPicker = false,
            onColorPickerChange = {},
            onSaveClick = {},
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
            showColorPicker = true,
            onColorPickerChange = {},
            onSaveClick = {}
        )
    }
}
