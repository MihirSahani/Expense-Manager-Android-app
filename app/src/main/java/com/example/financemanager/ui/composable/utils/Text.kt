package com.example.financemanager.ui.composable.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.abs

object MyText {

    @Composable
    fun ScreenHeader(text: String) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 32.dp, bottom = 16.dp, top = 16.dp)
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
    fun Header1(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier
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

    @Composable
    fun TransactionAmount(amount: Double, modifier: Modifier = Modifier) {
        Text(
            text = String.format(Locale.getDefault(), "%.2f", abs(amount)),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (amount < 0)
                MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary,
            modifier = modifier
        )
    }

}