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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import java.time.Instant
import java.time.Period

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
    fun Header2(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurface) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = modifier,
            color = color
        )
    }

    @Composable
    fun Body(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier, fontSize: TextUnit = 14.sp) {
        Text(
            text = text,
            fontSize = fontSize,
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
    fun Long.toStringDate(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, pattern: String = "dd MMM yyyy HH:mm"): String {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return format.format(this)
    }

    @Composable
    fun String.toLongDate(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, pattern: String = "dd MMM yyyy HH:mm"): Long {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return format.parse(this)?.time ?: 0L
    }

    @OptIn(ExperimentalTime::class)
    fun Long.timeRemaining(): String {

        val returnInstant = Instant.ofEpochMilli(this)
        val returnDate: LocalDate = returnInstant.atZone(ZoneId.systemDefault()).toLocalDate()
        val currDate: LocalDate = LocalDate.now()

        val output = StringBuilder()
        var diff: Period
        var counter: UInt = 0U

        if (returnDate.isBefore(currDate)) {
            output.append("Overdue: ")
            diff = Period.between(returnDate, currDate)
        } else {
            output.append("Due in: ")
            diff = Period.between(currDate, returnDate)
        }

        val years = diff.years
        val months = diff.months
        val days = diff.days

        if (years == 0 && months == 0 && days == 0) return "Due: Today"

        if (years > 0) {
            counter++
            output.append("$years ${if (years == 1) "year" else "years"} ")
        }
        if (months > 0) {
            counter++
            output.append("$months ${if (months == 1) "month" else "months"} ")
        }
        if (days > 0 && counter != 2U) output.append("$days ${if (days == 1) "day" else "days"} ")

        return output.toString().trim()
    }

    fun Double.toIndianFormat(): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2

        return formatter.format(this)
    }
}
