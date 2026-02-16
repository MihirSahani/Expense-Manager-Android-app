package com.example.financemanager.internal

import android.content.Context
import androidx.core.net.toUri
import com.example.financemanager.database.entity.Transaction
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SMSParser {
    private val hdfcSenderRegex = """[A-Z]{2}-HDFCBK-[ST]""".toRegex()

    private val hdfcUpiRegex =
        """Sent Rs\.\s*([\d,.]+).*?A/C\s*\*(\d{4}).*?To\s+(.*?)\s+On\s+(\d{2}/\d{2}/\d{2})"""
            .toRegex(RegexOption.DOT_MATCHES_ALL)

    private val hdfcSalaryRegex =
        """Update!\s+INR\s+([\d,.]+)\s+deposited in HDFC Bank A/c\s+xx(\d{4}).*?for.*?Cr-[^-]+-([^-]+(?:-[^-]+)?)"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    suspend fun parseMessagesToTransactions(
        context: Context, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager
    ) {
        coroutineScope {
            val cursor = context.contentResolver.query(
                "content://sms/inbox".toUri(),
                null, null, null, null
            )

            cursor?.use {
                val bodyIndex = it.getColumnIndexOrThrow("body")
                val addressIndex = it.getColumnIndexOrThrow("address")
                val dateIndex = it.getColumnIndexOrThrow("date")

                val tasks = mutableListOf<Deferred<Unit>>()

                while (it.moveToNext()) {
                    tasks.add(async {
                        val body = it.getString(bodyIndex)
                        val sender = it.getString(addressIndex)
                        val smsDateLong = it.getLong(dateIndex)

                        parseSingleMessage(body, sender, smsDateLong, transactionManager,
                            payeeCategoryMapperManager, accountIdMapperManager, accountManager)
                    })
                }
                tasks.awaitAll()
            }
        }
    }

    suspend fun parseSingleMessage(
        body: String, sender: String, smsDateLong: Long,
        transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager
    ) {
        if (hdfcSenderRegex.matches(sender)) {
            processUpiMessage(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                accountIdMapperManager, accountManager)
            processSalaryMessage(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                accountIdMapperManager, accountManager)
        }
    }

    private suspend fun processUpiMessage(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager
    ) {
        val matchResult = hdfcUpiRegex.find(body)
        if (matchResult != null) {
            val (amountStr, accountNo, payee, _) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee, "Expense", body, smsDateLong,
                transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
                accountManager)
        }
    }

    private suspend fun processSalaryMessage(body: String, smsDateLong: Long,
                                             transactionManager: TransactionManager,
                                             payeeCategoryMapperManager: PayeeCategoryMapperManager,
                                             accountIdMapperManager: AccountIdMapperManager,
                                             accountManager: AccountManager) {
        val matchResult = hdfcSalaryRegex.find(body)
        if (matchResult != null) {
            val (amountStr, accountNo, companyName) = matchResult.destructured
            createTransaction(amountStr, accountNo, companyName.trim(), "Income",
                body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                accountIdMapperManager, accountManager)
        }
    }

    private suspend fun createTransaction(
        amountStr: String, accountNo: String, payee: String, type: String,
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager
    ) {
        val amount = amountStr.replace(",", "").toDoubleOrNull() ?: 0.0
        val timestamp = getFormattedTimestamp(System.currentTimeMillis())
        val transactionDate = getFormattedTimestamp(smsDateLong)

        transactionManager.addTransaction(
            Transaction(
                accountId = accountIdMapperManager.getAccountId(accountNo),
                type = type,
                amount = amount,
                categoryId = payeeCategoryMapperManager.getMapping(payee),
                payee = payee,
                currency = "INR",
                transactionDate = transactionDate,
                description = body,
                receiptURL = "",
                location = "",
                createdAt = timestamp,
                updatedAt = timestamp,
                rawAccountIdName = accountNo
            ),
            accountManager
        )
    }

    private fun getFormattedTimestamp(timeMillis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Date(timeMillis))
    }
}
