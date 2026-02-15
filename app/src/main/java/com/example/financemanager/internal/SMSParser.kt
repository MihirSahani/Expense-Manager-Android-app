package com.example.financemanager.internal

import android.content.Context
import androidx.core.net.toUri
import com.example.financemanager.database.entity.Transaction
import kotlinx.coroutines.coroutineScope
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

    suspend fun parseMessagesToTransactions(context: Context, transactionManager: TransactionManager, payeeCategoryMapperManager: PayeeCategoryMapperManager, accountIdMapperManager: AccountIdMapperManager) {
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
                val smsDateLong = it.getLong(dateIndex)

                if (sender in bankSenders) {
                    coroutineScope {
                        processUpiMessage(body,
                            smsDateLong, transactionManager, payeeCategoryMapperManager, accountIdMapperManager)
                        processSalaryMessage(body,
                            smsDateLong, transactionManager, payeeCategoryMapperManager, accountIdMapperManager)
                    }
                }
            }
        }
    }

    private suspend fun processUpiMessage(body: String, smsDateLong: Long, transactionManager: TransactionManager, payeeCategoryMapperManager: PayeeCategoryMapperManager, accountIdMapperManager: AccountIdMapperManager) {
        val matchResult = hdfcUpiRegex.find(body)
        if (matchResult != null) {
            val (amountStr, accountNo, payee, _) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee, "Expense", body, smsDateLong, transactionManager, payeeCategoryMapperManager, accountIdMapperManager)
        }
    }

    private suspend fun processSalaryMessage(body: String, smsDateLong: Long, transactionManager: TransactionManager, payeeCategoryMapperManager: PayeeCategoryMapperManager, accountIdMapperManager: AccountIdMapperManager) {
        val matchResult = hdfcSalaryRegex.find(body)
        if (matchResult != null) {
            val (amountStr, accountNo, companyName) = matchResult.destructured
            createTransaction(amountStr, accountNo, companyName.trim(), "Income", body, smsDateLong, transactionManager, payeeCategoryMapperManager, accountIdMapperManager)
        }
    }

    private suspend fun createTransaction(
        amountStr: String,
        accountNo: String,
        payee: String,
        type: String,
        body: String,
        smsDateLong: Long,
        transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager
    ) {
        val amount = amountStr.replace(",", "").toDoubleOrNull() ?: 0.0
        val timestamp = getFormattedTimestamp(System.currentTimeMillis())
        val transactionDate = getFormattedTimestamp(smsDateLong)

        transactionManager.addTransaction(
            Transaction(
                accountId = accountIdMapperManager.getAccountId(accountNo),
                type = type,
                amount = amount,
                categoryId = payeeCategoryMapperManager.getCategoryId(payee),
                payee = payee,
                currency = "INR",
                transactionDate = transactionDate,
                description = body,
                receiptURL = "",
                location = "",
                createdAt = timestamp,
                updatedAt = timestamp,
                rawAccountIdName = accountNo
            )
        )
    }

    private fun getFormattedTimestamp(timeMillis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timeMillis))
    }
}
