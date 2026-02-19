package com.example.financemanager.ui.composable.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.Category
import com.example.financemanager.viewmodel.CategoryVM
import androidx.core.graphics.toColorInt
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme

@Composable
fun CategoryScreen(navController: NavController, viewModel: CategoryVM) {
    val categories by viewModel.categories.collectAsState()

    CategoryScreenContent(
        categories = categories,
        onAddCategoryClick = {
            viewModel.categoryId = null
            navController.navigate(Screen.AddEditCategory.route)
        },
        onCategoryClick = { category ->
            viewModel.categoryId = category.id
            navController.navigate(Screen.AddEditCategory.route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreenContent(
    categories: List<Category>,
    onAddCategoryClick: () -> Unit,
    onCategoryClick: (Category) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCategoryClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { padding ->
        Column {
            MyText.ScreenHeader("Categories")
            ListOfItems(
                categories,
                modifier = Modifier.padding(16.dp)
            ) { category ->
                CategoryItem(category) {
                    onCategoryClick(category)
                }
            }
        }

    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
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

fun parseColor(hex: String): Color {
    return try {
        Color(hex.toColorInt())
    } catch (e: Exception) {
        Color.Gray
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    val sampleCategories = listOf(
        Category(
            id = 1,
            name = "Food",
            description = "Groceries and dining",
            type = "Expense",
            color = "#FF5733",
            createdAt = "",
            updatedAt = "",
            monthlyBudget = null
        ),
        Category(
            id = 2,
            name = "Salary",
            description = "Monthly income",
            type = "Income",
            color = "#33FF57",
            createdAt = "",
            updatedAt = "",
            monthlyBudget = null
        ),
        Category(
            id = 3, name = "Rent", description = "House rent", type = "Expense", color = "#3357FF",
            createdAt = "",
            updatedAt = "",
            monthlyBudget = null
        )
    )
    FinanceManagerTheme {
        CategoryScreenContent(
            categories = sampleCategories,
            onAddCategoryClick = {},
            onCategoryClick = {}
        )
    }
}
