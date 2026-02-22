package com.example.financemanager.internal

import android.content.Context
import androidx.core.net.toUri
import com.example.financemanager.database.entity.Category
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
    private val sbiSenderRegex = """[A-Z]{2}-SBIUPI-[ST]""".toRegex()
    private val amexSenderRegex = """[A-Z]{2}-AMEXIN-[ST]""".toRegex()
    private val bobSenderRegex = """[A-Z]{2}-BOB(TXN|SMS)-[ST]""".toRegex()

    private val hdfcDebitRegex = 
        """Sent Rs\.\s*([\d,.]+).*?A/C\s*\*(\d{4}).*?To\s+(.*?)\s+On\s+(\d{2}/\d{2}/\d{2})"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    private val hdfcSalaryRegex = 
        """Update!\s+INR\s+([\d,.]+)\s+deposited in HDFC Bank A/c\s+xx(\d{4}).*?for.*?Cr-[^-]+-([^-]+(?:-[^-]+)?)"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    private val hdfcCreditRegex = 
        """Credit Alert!\s+Rs\.?\s*([\d,.]+)\s+credited to HDFC Bank A/c XX(\d{4}) on .*? from VPA\s+([^(\s]+)"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    private val sbiDebitRegex = 
        """Dear UPI user A/C X(\d{4}) debited by ([\d,.]+) on date .*? trf to (.*?) (?:BAL|Ref ?No)"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    private val sbiCreditRegex = 
        """Dear SBI User, your A/c X(\d{4})-credited by Rs\.([\d,.]+) on .*? transfer from (.*?) Ref ?No"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    private val amexDebitRegex = 
        """You've spent INR ([\d,.]+) on your AMEX Corp Card \*\* (\d{5}) at (.*?) on"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    private val bobCredit1Regex = 
        """Rs\.([\d,.]+) Credited to A/c \.\.\.(\d{4}) .*? by (.*?)\. Total Bal"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    private val bobDebit1Regex = 
        """Rs\.([\d,.]+) transferred from A/c \.\.\.(\d{4}) to:(.*?)\. Total Bal"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    private val bobCredit2Regex = 
        """Your account is credited with INR ([\d,.]+) on .*? by (.*?); AvlBal"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    private val bobDebit2Regex = 
        """Rs\.([\d,.]+) Dr\. from A/C X+(\d{4}) and Cr\. to (.*?)\. Ref"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    suspend fun parseMessages(
        context: Context, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager,
        categoryManager: CategoryManager, lastProcessedTimestamp: Long = 0L
    ): Long {
        var latestTimestamp = lastProcessedTimestamp
        
        coroutineScope {
            val selection = if (lastProcessedTimestamp > 0) "date > ?" else null
            val selectionArgs = if (lastProcessedTimestamp > 0) arrayOf(lastProcessedTimestamp.toString()) else null

            val cursor = context.contentResolver.query(
                "content://sms/inbox".toUri(),
                null, selection, selectionArgs, "date ASC"
            )

            cursor?.use {
                val bodyIndex = it.getColumnIndexOrThrow("body")
                val addressIndex = it.getColumnIndexOrThrow("address")
                val dateIndex = it.getColumnIndexOrThrow("date")

                val tasks = mutableListOf<Deferred<Pair<Transaction, Category?>?>>()

                while (it.moveToNext()) {
                    val body = it.getString(bodyIndex)
                    val sender = it.getString(addressIndex)
                    val smsDateLong = it.getLong(dateIndex)
                    
                    if (smsDateLong > latestTimestamp) {
                        latestTimestamp = smsDateLong
                    }

                    tasks.add(async {
                        parseSingleMessage(body, sender, smsDateLong, transactionManager,
                            payeeCategoryMapperManager, accountIdMapperManager, accountManager, categoryManager)
                    })
                }
                tasks.awaitAll()
            }
        }
        return latestTimestamp
    }

    suspend fun parseSingleMessage(
        body: String, sender: String, smsDateLong: Long,
        transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        return if (hdfcSenderRegex.matches(sender)) {
            coroutineScope {
                val d1 = async {
                    processUpiMessage(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                        accountIdMapperManager, accountManager, categoryManager)
                }
                val d2 = async {
                    processSalaryMessage(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                        accountIdMapperManager, accountManager, categoryManager)
                }
                val d3 = async {
                    processHdfcCreditMessage(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                        accountIdMapperManager, accountManager, categoryManager)
                }
                awaitAll(d1, d2, d3).firstOrNull { it != null }
            }
        } else if (amexSenderRegex.matches(sender)) {
            processAmexDebitMessage(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                accountIdMapperManager, accountManager, categoryManager)
        } else if (bobSenderRegex.matches(sender) || sbiSenderRegex.matches(sender)) {
            coroutineScope {
                val tasks = listOf(
                    async {
                        processSbiUpiMessage(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                            accountIdMapperManager, accountManager, categoryManager)
                    },
                    async {
                        processSbiCreditMessage(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                            accountIdMapperManager, accountManager, categoryManager)
                    },
                    async {
                        processBobCredit1Message(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                            accountIdMapperManager, accountManager, categoryManager)
                    },
                    async {
                        processBobDebit1Message(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                            accountIdMapperManager, accountManager, categoryManager)
                    },
                    async {
                        processBobCredit2Message(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                            accountIdMapperManager, accountManager, categoryManager)
                    },
                    async {
                        processBobDebit2Message(body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                            accountIdMapperManager, accountManager, categoryManager)
                    }
                )
                tasks.awaitAll().firstOrNull { it != null }
            }
        } else {
            null
        }
    }

    private suspend fun processUpiMessage(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        val matchResult = hdfcDebitRegex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee, _) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee, "Expense", body, smsDateLong,
                transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
                accountManager, categoryManager)
        } else null
    }

    private suspend fun processSalaryMessage(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager,
        accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        val matchResult = hdfcSalaryRegex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, companyName) = matchResult.destructured
            createTransaction(amountStr, accountNo, companyName.trim(), "Income",
                body, smsDateLong, transactionManager, payeeCategoryMapperManager,
                accountIdMapperManager, accountManager, categoryManager)
        } else null
    }

    private suspend fun processHdfcCreditMessage(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager,
        accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        val matchResult = hdfcCreditRegex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Income", body, smsDateLong,
                transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
                accountManager, categoryManager)
        } else null
    }

    private suspend fun processSbiUpiMessage(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        val matchResult = sbiDebitRegex.find(body)
        return if (matchResult != null) {
            val (accountNo, amountStr, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong,
                transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
                accountManager, categoryManager)
        } else null
    }

    private suspend fun processSbiCreditMessage(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        val matchResult = sbiCreditRegex.find(body)
        return if (matchResult != null) {
            val (accountNo, amountStr, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Income", body, smsDateLong,
                transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
                accountManager, categoryManager)
        } else null
    }

    private suspend fun processAmexDebitMessage(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        val matchResult = amexDebitRegex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong,
                transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
                accountManager, categoryManager)
        } else null
    }

    private suspend fun processBobCredit1Message(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        val matchResult = bobCredit1Regex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Income", body, smsDateLong,
                transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
                accountManager, categoryManager)
        } else null
    }

    private suspend fun processBobDebit1Message(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        val matchResult = bobDebit1Regex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong,
                transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
                accountManager, categoryManager)
        } else null
    }

    private suspend fun processBobCredit2Message(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        val matchResult = bobCredit2Regex.find(body)
        return if (matchResult != null) {
            val (amountStr, payee) = matchResult.destructured
            // Account number is not in this format, using a placeholder or common identifier if possible
            createTransaction(amountStr, "BOB-UPI", payee.trim(), "Income", body, smsDateLong,
                transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
                accountManager, categoryManager)
        } else null
    }

    private suspend fun processBobDebit2Message(
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager,
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?>? {
        val matchResult = bobDebit2Regex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong,
                transactionManager, payeeCategoryMapperManager, accountIdMapperManager,
                accountManager, categoryManager)
        } else null
    }

    private suspend fun createTransaction(
        amountStr: String, accountNo: String, payee: String, type: String,
        body: String, smsDateLong: Long, transactionManager: TransactionManager,
        payeeCategoryMapperManager: PayeeCategoryMapperManager,
        accountIdMapperManager: AccountIdMapperManager, accountManager: AccountManager, 
        categoryManager: CategoryManager
    ): Pair<Transaction, Category?> {
        val amount = amountStr.replace(",", "").toDoubleOrNull() ?: 0.0
        val timestamp = System.currentTimeMillis()
        val categoryId = payeeCategoryMapperManager.getMapping(payee)
        val transaction = Transaction(
            accountId = accountIdMapperManager.getAccountId(accountNo),
            type = type,
            amount = amount,
            categoryId = categoryId,
            payee = payee,
            currency = "INR",
            transactionDate = smsDateLong,
            description = body,
            receiptURL = "",
            location = "",
            createdAt = timestamp,
            updatedAt = timestamp,
            rawAccountIdName = accountNo
        )

        transactionManager.addTransaction(
            transaction,
            accountManager
        )
        return Pair(transaction, if (categoryId != null) categoryManager.getCategory(categoryId) else null)
    }

    private fun getFormattedTimestamp(timeMillis: Long): String {
        return SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault())
            .format(Date(timeMillis))
    }
}
