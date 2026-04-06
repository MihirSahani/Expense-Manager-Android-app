package com.example.financemanager.ui.composable.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.PhonePaused
import androidx.compose.material.icons.filled.Savings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.financemanager.repository.data.CategoryIcons

val iconMapper: Map<Int?, ImageVector> = mapOf(
    CategoryIcons.FOOD.ordinal to Icons.Filled.LocalDining,
    CategoryIcons.TRANSPORT.ordinal to Icons.Filled.LocalTaxi,
    CategoryIcons.SALARY.ordinal to Icons.Filled.Badge,
    CategoryIcons.GROCERIES.ordinal to Icons.Filled.EmojiFoodBeverage,
    CategoryIcons.UTILITIES.ordinal to Icons.Filled.PhonePaused,
    CategoryIcons.FRIENDS_AND_FAMILIES.ordinal to Icons.Filled.Groups,
    CategoryIcons.SHOPPING.ordinal to Icons.Filled.LocalGroceryStore,
    CategoryIcons.INVESTMENT.ordinal to Icons.Filled.Savings,
)