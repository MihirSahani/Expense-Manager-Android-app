package com.example.financemanager.ui.composable.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun BottomBar(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column (
            modifier = Modifier.weight(1F).fillMaxWidth().clickable {},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Groups,  contentDescription = "Menu")
            Text("Groups")
        }
        Column (
            modifier = Modifier.weight(1F).clickable {},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Person,  contentDescription = "Account")
            Text("Individual")
        }
        Column (
            modifier = Modifier.weight(1F).fillMaxWidth().clickable {},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Balance,  contentDescription = "Balance Sheet")
            Text("Balance Sheet")
        }
        Column (
            modifier = Modifier.weight(1F).fillMaxWidth().clickable {},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Logout,  contentDescription = "Logout")
            Text("Logout")
        }
    }
}
