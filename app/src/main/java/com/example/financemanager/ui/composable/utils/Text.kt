package com.example.financemanager.ui.composable.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object MyText {

    @Composable
    fun ScreenHeader(text: String) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }

    @Composable
    fun ScreenTitle(text: String) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }

    @Composable
    fun Header1(text: String) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }

    @Composable
    fun Body(text: String) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    @Composable
    fun Subtitle(text: String) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

}