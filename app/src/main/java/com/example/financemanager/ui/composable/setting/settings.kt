package com.example.financemanager.ui.composable.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.composable.utils.MyInput
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.SettingsVM

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsVM) {
    val budgetTimeframe by viewModel.budgetTimeframe.collectAsState()
    val salaryCreditTime by viewModel.salaryCreditTime.collectAsState()


    SettingsScreenContent(
        budgetTimeframe = budgetTimeframe ?: 0L,
        salaryCreditTime = salaryCreditTime ,
        onUpdateUserDetailsClick = { navController.navigate(Screen.UpdateUserDetails.route) },
        onBudgetTimeframeChange = { isChecked ->
            viewModel.updateBudgetTimeframe(if (isChecked) 1L else 0L)
        }
    )
}

@Composable
fun SettingsScreenContent(
    budgetTimeframe: Long,
    salaryCreditTime: Long?,
    onUpdateUserDetailsClick: () -> Unit,
    onBudgetTimeframeChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MyText.ScreenHeader("Settings")

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            SettingsItem(
                title = "Update User Details", icon = Icons.Default.Person, onClick = onUpdateUserDetailsClick
            )
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    MyText.Header1("Budget Timeframe")
                    Spacer(modifier = Modifier.width(16.dp))
                    MyText.Body(text = if (budgetTimeframe == 1L) "Salary Based" else "Monthly")
                }
                MyInput.Switch(
                    modifier = Modifier.size(height = 24.dp, width = 48.dp),
                    checked = budgetTimeframe == 1L,
                    onCheckedChange = onBudgetTimeframeChange
                )
            }
            if (salaryCreditTime != null &&  salaryCreditTime != 0L) {
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Money,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        MyText.Header1(text = "Salary Credit Time")
                    }
                    MyText.Date(salaryCreditTime)
                }
            }

        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            MyText.Header1(text = title)
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    FinanceManagerTheme {
        SettingsScreenContent(
            budgetTimeframe = 1L,
            salaryCreditTime = 0L,
            onUpdateUserDetailsClick = {},
            onBudgetTimeframeChange = {}
        )
    }
}
