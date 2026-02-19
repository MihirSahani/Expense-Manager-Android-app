package com.example.financemanager.ui.composable.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.database.entity.Account
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.viewmodel.AccountVM
import java.util.Locale

@Composable
fun AccountsScreen(navController: NavController, viewModel: AccountVM) {
    val accounts by viewModel.accounts.collectAsState(emptyList())
    val netWorth by viewModel.netWorth.collectAsState(0.0)

    AccountsContent(
        accounts = accounts,
        netWorth = netWorth,
        onAccountClick = { account ->
            viewModel.selectedAccountId.value = account.id
            navController.navigate(Screen.AddEditAccount.route)
        },
        onAddAccountClick = {
            viewModel.selectedAccountId.value = null
            navController.navigate(Screen.AddEditAccount.route)
        }
    )
}

@Composable
fun AccountsContent(
    accounts: List<Account>,
    netWorth: Double,
    onAccountClick: (Account) -> Unit,
    onAddAccountClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAccountClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Account")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            MyText.ScreenHeader("Accounts")

            NetWorthCard(netWorth)

            Spacer(modifier = Modifier.height(24.dp))

            MyText.Header1("Your Accounts", Modifier.padding(start=32.dp))

            ListOfItems(accounts, Modifier.padding(16.dp)) { account ->
                AccountItem(account) {
                    onAccountClick(account)
                }
            }
        }
    }
}

@Composable
fun NetWorthCard(netWorth: Double) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            MyText.Header1("Net Worth")
            MyText.ScreenTitle("₹ " + String.format(Locale.getDefault(), "%.2f", netWorth))
        }
    }
}

@Composable
fun AccountItem(account: Account, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            MyText.Header1(account.name)
            MyText.Body(account.type)

        }
        Text(
            text = "₹ " + String.format(
                Locale.getDefault(), "%.2f", account.currentBalance
            ),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = if (account.currentBalance >= 0) Color.Unspecified else Color.Red
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountsScreenPreview() {
    val mockAccounts = listOf(
        Account(id = 1, name = "Savings Account", type = "Bank", currentBalance = 12500.0),
        Account(id = 2, name = "Wallet", type = "Cash", currentBalance = 1500.0),
        Account(id = 3, name = "Credit Card", type = "Card", currentBalance = -500.0)
    )
    AccountsContent(
        accounts = mockAccounts,
        netWorth = 13500.0,
        onAccountClick = {},
        onAddAccountClick = {}
    )
}
