package com.example.financemanager.repository

import android.content.Context
import androidx.core.net.toUri
import androidx.room.withTransaction
import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import com.example.financemanager.internal.Keys
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SMSParserRepo(private val db: ExpenseManagementDatabase) {

    private val hdfcSenderRegex = """[A-Z]{2}-HDFCBK-[ST]""".toRegex()
    private val sbiSenderRegex = """[A-Z]{2}-SBIUPI-[ST]""".toRegex()
    private val amexSenderRegex = """[A-Z]{2}-AMEXIN-[ST]""".toRegex()
    private val bobSenderRegex = """[A-Z]{2}-BOB(TXN|SMS)-[ST]""".toRegex()

    private val hdfcDebitRegex = """Sent Rs\.\s*([\d,.]+).*?A/C\s*\*(\d{4}).*?To\s+(.*?)\s+On\s+(\d{2}/\d{2}/\d{2})""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val hdfcSalaryRegex = """Update!\s+INR\s+([\d,.]+)\s+deposited in HDFC Bank A/c\s+xx(\d{4}).*?for.*?Cr-[^-]+-([^-]+(?:-[^-]+)?)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val hdfcCreditRegex = """Credit Alert!\s+Rs\.?\s*([\d,.]+)\s+credited to HDFC Bank A/c XX(\d{4}) on .*? from VPA\s+([^(\s]+)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val sbiDebitRegex = """Dear UPI user A/C X(\d{4}) debited by ([\d,.]+) on date .*? trf to (.*?) (?:BAL|Ref ?No)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val sbiCreditRegex = """Dear SBI User, your A/c X(\d{4})-credited by Rs\.([\d,.]+) on .*? transfer from (.*?) Ref ?No""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val amexDebitRegex = """You've spent INR ([\d,.]+) on your AMEX Corp Card \*\* (\d{5}) at (.*?) on""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val bobCredit1Regex = """Rs\.([\d,.]+) Credited to A/c \.\.\.(\d{4}) .*? by (.*?)\. Total Bal""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val bobDebit1Regex = """Rs\.([\d,.]+) transferred from A/c \.\.\.(\d{4}) to:(.*?)\. Total Bal""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val bobCredit2Regex = """Your account is credited with INR ([\d,.]+) on .*? by (.*?); AvlBal""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val bobDebit2Regex = """Rs\.([\d,.]+) Dr\. from A/C X+(\d{4}) and Cr\. to (.*?)\. Ref""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    suspend fun parseMessages(context: Context) {
        val lastProcessedTimestamp = db.appSettingDao().getByKey(Keys.LAST_SMS_TIMESTAMP.ordinal)?:0L
        var latestTimestamp = lastProcessedTimestamp
        coroutineScope {
            val selection = if (lastProcessedTimestamp > 0) "date > ?" else null
            val selectionArgs =
                if (lastProcessedTimestamp > 0) arrayOf(lastProcessedTimestamp.toString())
                else null

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
                        parseSingleMessage(body, sender, smsDateLong, false)
                    })
                }
                tasks.awaitAll()
            }
        }
        db.appSettingDao().insert(AppSetting(Keys.LAST_SMS_TIMESTAMP.ordinal, latestTimestamp))
        updateSalaryCreditTime()
    }

    private suspend fun updateSalaryCreditTime() {
        db.withTransaction {
            val transactions = db.transactionDao().getTransactionWithIncomeCategory()
            if (transactions.isNotEmpty()) {
                try {
                    db.appSettingDao()
                        .insert(
                            AppSetting(Keys.SALARY_CREDIT_TIME.ordinal, transactions[0]!!.transactionDate)
                        )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            else {
                db.appSettingDao().insert(AppSetting(Keys.SALARY_CREDIT_TIME.ordinal, 0L))
            }
            if(transactions.size == 2) {
                try {
                    db.appSettingDao()
                        .insert(AppSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal, transactions[1]!!.transactionDate))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else {
                db.appSettingDao().insert(AppSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal, 0L))
            }
        }
    }

    suspend fun parseSingleMessage(
        body: String, sender: String, smsDateLong: Long, isSalaryCreditTimeUpdateRequired: Boolean = true
    ): Pair<Transaction, Category?>? {
        val transactionAndCategory = if (hdfcSenderRegex.matches(sender)) {
            coroutineScope {
                val d1 = async { processUpiMessage(body, smsDateLong) }
                val d2 = async { processSalaryMessage(body, smsDateLong) }
                val d3 = async { processHdfcCreditMessage(body, smsDateLong) }
                awaitAll(d1, d2, d3).firstOrNull { it != null }
            }
        } else if (amexSenderRegex.matches(sender)) {
            processAmexDebitMessage(body, smsDateLong)
        } else if (bobSenderRegex.matches(sender) || sbiSenderRegex.matches(sender)) {
            coroutineScope {
                val tasks = listOf(
                    async { processSbiUpiMessage(body, smsDateLong) },
                    async { processSbiCreditMessage(body, smsDateLong) },
                    async { processBobCredit1Message(body, smsDateLong) },
                    async { processBobDebit1Message(body, smsDateLong) },
                    async { processBobCredit2Message(body, smsDateLong) },
                    async { processBobDebit2Message(body, smsDateLong) }
                )
                tasks.awaitAll().firstOrNull { it != null }
            }
        } else {
            null
        }
        if(isSalaryCreditTimeUpdateRequired) {
            updateSalaryCreditTime()
        }
        return transactionAndCategory
    }

    private suspend fun processUpiMessage(
        body: String, smsDateLong: Long
    ): Pair<Transaction, Category?>? {
        val matchResult = hdfcDebitRegex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee, _) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee, "Expense", body, smsDateLong)
        } else null
    }

    private suspend fun processSalaryMessage(
        body: String, smsDateLong: Long
    ): Pair<Transaction, Category?>? {
        val matchResult = hdfcSalaryRegex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, companyName) = matchResult.destructured
            createTransaction(
                amountStr, accountNo, companyName.trim(), "Income", body, smsDateLong
            )
        } else null
    }

    private suspend fun processHdfcCreditMessage(
        body: String, smsDateLong: Long
    ): Pair<Transaction, Category?>? {
        val matchResult = hdfcCreditRegex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Income", body, smsDateLong)
        } else null
    }

    private suspend fun processSbiUpiMessage(
        body: String, smsDateLong: Long
    ): Pair<Transaction, Category?>? {
        val matchResult = sbiDebitRegex.find(body)
        return if (matchResult != null) {
            val (accountNo, amountStr, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong)
        } else null
    }

    private suspend fun processSbiCreditMessage(
        body: String, smsDateLong: Long
    ): Pair<Transaction, Category?>? {
        val matchResult = sbiCreditRegex.find(body)
        return if (matchResult != null) {
            val (accountNo, amountStr, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Income", body, smsDateLong)
        } else null
    }

    private suspend fun processAmexDebitMessage(
        body: String, smsDateLong: Long
    ): Pair<Transaction, Category?>? {
        val matchResult = amexDebitRegex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong)
        } else null
    }

    private suspend fun processBobCredit1Message(
        body: String, smsDateLong: Long
    ): Pair<Transaction, Category?>? {
        val matchResult = bobCredit1Regex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Income", body, smsDateLong)
        } else null
    }

    private suspend fun processBobDebit1Message(
        body: String, smsDateLong: Long
    ): Pair<Transaction, Category?>? {
        val matchResult = bobDebit1Regex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong)
        } else null
    }

    private suspend fun processBobCredit2Message(
        body: String, smsDateLong: Long
    ): Pair<Transaction, Category?>? {
        val matchResult = bobCredit2Regex.find(body)
        return if (matchResult != null) {
            val (amountStr, payee) = matchResult.destructured
            // Account number is not in this format, using a placeholder or common identifier if possible
            createTransaction(amountStr, "BOB-UPI", payee.trim(), "Income", body, smsDateLong)
        } else null
    }

    private suspend fun processBobDebit2Message(
        body: String, smsDateLong: Long
    ): Pair<Transaction, Category?>? {
        val matchResult = bobDebit2Regex.find(body)
        return if (matchResult != null) {
            val (amountStr, accountNo, payee) = matchResult.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong)
        } else null
    }

    private suspend fun createTransaction(
        amountStr: String, accountNo: String, payee: String, type: String, body: String,
        smsDateLong: Long
    ): Pair<Transaction, Category?> {
        val amount = amountStr.replace(",", "").toDoubleOrNull() ?: 0.0
        val timestamp = System.currentTimeMillis()
        val categoryId = db.PayeeCategoryMapperDao().getCategoryId(payee)
        val transaction = Transaction(
            accountId = db.accountIdMapperDao().get(accountNo),
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

        db.transactionDao().create(transaction)
        return Pair(transaction, if (categoryId != null) db.categoryDao().getById(categoryId) else null)
    }
}