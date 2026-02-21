package com.example.financemanager.ui.composable.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financemanager.database.entity.Transaction
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

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
    fun Header1(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurface) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier,
            color = color
        )
    }

    @Composable
    fun Body(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = color,
            modifier = modifier
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
    fun TransactionAmount(
        amount: Double,
        modifier: Modifier = Modifier,
        fontSize: TextUnit = 16.sp,
        color: Color? = null,
        type: String = "income") {
        Text(
            text = amount.toIndianFormat(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = color
                ?: if (type.equals("expense", ignoreCase = true) || amount < 0) Color(0xFF9B2600)
                else Color(0xFF02AF34),
            modifier = modifier
        )
    }

    @Composable
    fun Date(date: Long, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
        val format = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val formattedDate = format.format(date)
        Body(text = formattedDate, color)
    }

    fun Double.toIndianFormat(): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2

        return formatter.format(this)
    }
}