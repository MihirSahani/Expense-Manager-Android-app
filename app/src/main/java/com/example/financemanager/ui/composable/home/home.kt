package com.example.financemanager.ui.composable.home

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.InitialVM

@Composable
fun HomeScreen(navController: NavController, viewModel: InitialVM) {
    val userName by viewModel.userName.collectAsState()

    viewModel.parseSMS(LocalContext.current)

    HomeScreenContent(
        userName = userName,
        onAccountsClick = { navController.navigate(Screen.Accounts.route) },
        onCategoriesClick = { navController.navigate(Screen.Categories.route) }
    )
}

@Composable
fun HomeScreenContent(
    userName: String,
    onAccountsClick: () -> Unit,
    onCategoriesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        MyText.ScreenHeader("Hello $userName")

        Spacer(modifier = Modifier.height(32.dp))

        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeWidget(
                title = "Accounts",
                icon = Icons.Default.AccountBalanceWallet,
                onClick = onAccountsClick
            )
            HomeWidget(
                title = "Categories",
                icon = Icons.Default.Category,
                onClick = onCategoriesClick
            )
        }
    }
}

@Composable
fun HomeWidget(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyText.Header1(title)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FinanceManagerTheme {
        HomeScreenContent(
            userName = "Munshi",
            onAccountsClick = {},
            onCategoriesClick = {}
        )
    }
}
