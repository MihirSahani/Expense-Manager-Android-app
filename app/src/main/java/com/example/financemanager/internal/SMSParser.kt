package com.example.financemanager.internal

import android.content.Context
import androidx.core.net.toUri
import com.example.financemanager.database.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SMSParser {
    private val bankSenders = setOf(
        "VD-HDFCBK-S", "VD-HDFCBK-T", "VM-HDFCBK-S", "VM-HDFCBK-T",
        "JM-HDFCBK-S", "JM-HDFCBK-T", "JD-HDFCBK-S", "JD-HDFCBK-T",
        "AX-HDFCBK-S", "AX-HDFCBK-T"
    )

    // Regex to match HDFC UPI transaction messages
    // Format: Sent Rs. 20 \nFrom HDFC Bank A/C *1234\nTo Apple\nOn dd/mm/yy
    private val hdfcUpiRegex = """Sent Rs\.\s*([\d,.]+).*?A/C\s*\*(\d{4}).*?To\s+(.*?)\s+On\s+(\d{2}/\d{2}/\d{2})""".toRegex(RegexOption.DOT_MATCHES_ALL)

    // Regex to match HDFC Salary Credit messages
    // Updated Format: Update! INR 100.00 deposited in HDFC Bank A/c xx1234 on 30-DEC-24 for NEFT Cr-CITI00000006-DELOITTE-INDIA P LTD-Ajmal-Kasab-CITIN12355.
    private val hdfcSalaryRegex = """Update!\s+INR\s+([\d,.]+)\s+deposited in HDFC Bank A/c\s+xx(\d{4}).*?for.*?Cr-[^-]+-([^-]+(?:-[^-]+)?)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    fun parseMessagesToTransactions(context: Context, transactionManager: TransactionManager) {
        val cursor = context.contentResolver.query(
            "content://sms/inbox".toUri(),
            null, null, null, null
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndexOrThrow("body")
            val addressIndex = it.getColumnIndexOrThrow("address")
            val idIndex = it.getColumnIndexOrThrow("_id")
            val dateIndex = it.getColumnIndexOrThrow("date")

            while (it.moveToNext()) {
                val body = it.getString(bodyIndex)
                val sender = it.getString(addressIndex)
                val smsId = it.getString(idIndex)
                val smsDateLong = it.getLong(dateIndex)

                if (sender in bankSenders) {
                    processUpiMessage(body, smsId, smsDateLong, transactionManager)
                    processSalaryMessage(body, smsId, smsDateLong, transactionManager)
                }
            }
        }
    }

    private fun processUpiMessage(body: String, smsId: String, smsDateLong: Long, transactionManager: TransactionManager) {
        val matchResult = hdfcUpiRegex.find(body)
        if (matchResult != null) {
            val (amountStr, accountNo, payee, _) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee, "Expense", smsId, smsDateLong, transactionManager)
        }
    }

    private fun processSalaryMessage(body: String, smsId: String, smsDateLong: Long, transactionManager: TransactionManager) {
        val matchResult = hdfcSalaryRegex.find(body)
        if (matchResult != null) {
            val (amountStr, accountNo, companyName) = matchResult.destructured
            createTransaction(amountStr, accountNo, companyName.trim(), "Income", smsId, smsDateLong, transactionManager)
        }
    }

    private fun createTransaction(
        amountStr: String,
        accountNo: String,
        payee: String,
        type: String,
        smsId: String,
        smsDateLong: Long,
        transactionManager: TransactionManager
    ) {
        val amount = amountStr.replace(",", "").toDoubleOrNull() ?: 0.0
        val timestamp = getFormattedTimestamp(System.currentTimeMillis())
        val transactionDate = getFormattedTimestamp(smsDateLong)

        CoroutineScope(Dispatchers.IO).launch {
            transactionManager.addTransaction(
                Transaction(
                    accountId = 1, // Default account
                    categoryId = if (type == "Income") 3 else 1, // Salary (3) or General Expense (1)
                    type = type,
                    amount = amount,
                    payee = payee,
                    currency = "INR",
                    transactionDate = transactionDate,
                    description = "A/C: *$accountNo | SMS ID: $smsId",
                    receiptURL = "",
                    location = "",
                    createdAt = timestamp,
                    updatedAt = timestamp,
                )
            )
        }
    }

    private fun getFormattedTimestamp(timeMillis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timeMillis))
    }
}
