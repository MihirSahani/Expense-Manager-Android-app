package com.example.financemanager.ui.composable.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Category
import com.example.financemanager.viewmodel.CategoryVM
import androidx.core.graphics.toColorInt
import com.example.financemanager.ui.composable.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController: NavController, viewModel: CategoryVM) {
    val categories by viewModel.categories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Categories") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.categoryId = null
                navController.navigate(Screen.AddEditCategory.route)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryItem(category) {
                    viewModel.categoryId = category.id
                    navController.navigate(Screen.AddEditCategory.route)
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(parseColor(category.color))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = category.name, fontWeight = FontWeight.Bold)
                    Text(text = category.description, fontSize = 12.sp, color = Color.Gray)
                }
                Text(text = category.type, fontSize = 12.sp, color = Color.Gray)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreen(navController: NavController, viewModel: CategoryVM) {
    val category = remember(viewModel.categoryId) {
        if (viewModel.categoryId != null) viewModel.getCategoryById(viewModel.categoryId!!) else null
    }

    var name by remember { mutableStateOf(category?.name ?: "") }
    var description by remember { mutableStateOf(category?.description ?: "") }
    var type by remember { mutableStateOf(category?.type ?: "Expense") }
    var color by remember { mutableStateOf(category?.color ?: "#FF0000") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.categoryId == null) "Add Category" else "Edit Category") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                            it.name = name
                            it.description = description
                            it.type = type
                            it.color = color
                            viewModel.updateCategory(it)
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

fun parseColor(hex: String): Color {
    return try {
        Color(hex.toColorInt())
    } catch (e: Exception) {
        Color.Gray
    }
}
