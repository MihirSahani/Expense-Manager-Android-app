package com.example.financemanager.repository

import android.content.Context
import androidx.core.net.toUri
import androidx.room.withTransaction
import com.example.financemanager.database.entity.AppSetting
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.PayeeCategoryMapper
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.database.entity.TransactionSummary
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import com.example.financemanager.repository.data.Keys
import com.example.financemanager.repository.data.Timeframe
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.sync.Mutex
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

class TransactionRepo(private val db: ExpenseManagementDatabase) {
    private var transactions = db.transactionDao().getTransactionsFlow()
    private val parseMutex = Mutex()

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
        if (!parseMutex.tryLock()) return
        try {
            val lastProcessedTimestamp = db.appSettingDao().getByKey(Keys.LAST_SMS_TIMESTAMP.ordinal) ?: 0L
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
                        tasks.add(async { parseSingleMessage(body, sender, smsDateLong, false) })
                    }
                    tasks.awaitAll()
                }
            }
            db.appSettingDao().insert(AppSetting(Keys.LAST_SMS_TIMESTAMP.ordinal, latestTimestamp))
            updateSalaryCreditTime()
        } finally {
            parseMutex.unlock()
        }
    }

    private suspend fun updateSalaryCreditTime() {
        val transactions = db.transactionDao().getTransactionWithIncomeCategory()
        if (transactions.isNotEmpty()) {
            db.appSettingDao().insert(AppSetting(Keys.SALARY_CREDIT_TIME.ordinal, transactions[0]?.transactionDate ?: 0L))
        } else {
            db.appSettingDao().insert(AppSetting(Keys.SALARY_CREDIT_TIME.ordinal, 0L))
        }
        if (transactions.size == 2) {
            db.appSettingDao().insert(AppSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal, transactions[1]?.transactionDate ?: 0L))
        } else {
            db.appSettingDao().insert(AppSetting(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal, 0L))
        }
    }

    suspend fun parseSingleMessage(
        body: String, sender: String, smsDateLong: Long, isSalaryCreditTimeUpdateRequired: Boolean = true
    ): Pair<Transaction, Category?>? {
        val transactionAndCategory = coroutineScope {
            if (hdfcSenderRegex.matches(sender)) {
                val d1 = async { processUpiMessage(body, smsDateLong) }
                val d2 = async { processSalaryMessage(body, smsDateLong) }
                val d3 = async { processHdfcCreditMessage(body, smsDateLong) }
                awaitAll(d1, d2, d3).firstOrNull { it != null }
            } else if (amexSenderRegex.matches(sender)) {
                processAmexDebitMessage(body, smsDateLong)
            } else if (bobSenderRegex.matches(sender) || sbiSenderRegex.matches(sender)) {
                val tasks = listOf(
                    async { processSbiUpiMessage(body, smsDateLong) },
                    async { processSbiCreditMessage(body, smsDateLong) },
                    async { processBobCredit1Message(body, smsDateLong) },
                    async { processBobDebit1Message(body, smsDateLong) },
                    async { processBobCredit2Message(body, smsDateLong) },
                    async { processBobDebit2Message(body, smsDateLong) }
                )
                tasks.awaitAll().firstOrNull { it != null }
            } else {
                null
            }
        }

        transactionAndCategory?.let { (transaction, _) ->
            db.transactionDao().create(transaction)
            if (isSalaryCreditTimeUpdateRequired) {
                updateSalaryCreditTime()
            }
        }
        return transactionAndCategory
    }

    private suspend fun processUpiMessage(body: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val matchResult = hdfcDebitRegex.find(body)
        return matchResult?.let {
            val (amountStr, accountNo, payee, _) = it.destructured
            createTransaction(amountStr, accountNo, payee, "Expense", body, smsDateLong)
        }
    }

    private suspend fun processSalaryMessage(body: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val matchResult = hdfcSalaryRegex.find(body)
        return matchResult?.let {
            val (amountStr, accountNo, companyName) = it.destructured
            createTransaction(amountStr, accountNo, companyName.trim(), "Income", body, smsDateLong)
        }
    }

    private suspend fun processHdfcCreditMessage(body: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val matchResult = hdfcCreditRegex.find(body)
        return matchResult?.let {
            val (amountStr, accountNo, payee) = it.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Income", body, smsDateLong)
        }
    }

    private suspend fun processSbiUpiMessage(body: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val matchResult = sbiDebitRegex.find(body)
        return matchResult?.let {
            val (accountNo, amountStr, payee) = it.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong)
        }
    }

    private suspend fun processSbiCreditMessage(body: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val matchResult = sbiCreditRegex.find(body)
        return matchResult?.let {
            val (accountNo, amountStr, payee) = it.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Income", body, smsDateLong)
        }
    }

    private suspend fun processAmexDebitMessage(body: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val matchResult = amexDebitRegex.find(body)
        return matchResult?.let {
            val (amountStr, accountNo, payee) = it.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong)
        }
    }

    private suspend fun processBobCredit1Message(body: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val matchResult = bobCredit1Regex.find(body)
        return matchResult?.let {
            val (amountStr, accountNo, payee) = it.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Income", body, smsDateLong)
        }
    }

    private suspend fun processBobDebit1Message(body: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val matchResult = bobDebit1Regex.find(body)
        return matchResult?.let {
            val (amountStr, accountNo, payee) = it.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong)
        }
    }

    private suspend fun processBobCredit2Message(body: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val matchResult = bobCredit2Regex.find(body)
        return matchResult?.let {
            val (amountStr, payee) = it.destructured
            createTransaction(amountStr, "BOB-UPI", payee.trim(), "Income", body, smsDateLong)
        }
    }

    private suspend fun processBobDebit2Message(body: String, smsDateLong: Long): Pair<Transaction, Category?>? {
        val matchResult = bobDebit2Regex.find(body)
        return matchResult?.let {
            val (amountStr, accountNo, payee) = it.destructured
            createTransaction(amountStr, accountNo, payee.trim(), "Expense", body, smsDateLong)
        }
    }

    private suspend fun createTransaction(
        amountStr: String, accountNo: String, payee: String, type: String, body: String,
        smsDateLong: Long
    ): Pair<Transaction, Category?> {
        val amount = amountStr.replace(",", "").toDoubleOrNull() ?: 0.0
        val timestamp = System.currentTimeMillis()
        val dbPayee = db.payeeDisplayDao().get(payee) ?: payee.beautify()
        val categoryId = db.PayeeCategoryMapperDao().get(dbPayee)
        val transaction = Transaction(
            accountId = db.accountIdMapperDao().get(accountNo),
            type = type,
            amount = amount,
            categoryId = categoryId,
            payee = dbPayee,
            currency = "INR",
            transactionDate = smsDateLong,
            description = body,
            receiptURL = "",
            location = "",
            createdAt = timestamp,
            updatedAt = timestamp,
            rawAccountIdName = accountNo
        )
        return Pair(transaction, if (categoryId != null) db.categoryDao().getById(categoryId) else null)
    }

    fun getTransactionFlow(id: Int) = db.transactionDao().getFlow(id)

    suspend fun getTransaction(id: Int) = db.transactionDao().get(id)

    private fun getTimeMillis(month: Int, year: Int): Pair<Long, Long> {
        val yearMonth = YearMonth.of(year, month)
        val zoneId = ZoneId.systemDefault()
        val startMillis = yearMonth.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = yearMonth.atEndOfMonth().atTime(23, 59, 59, 999_000_000).atZone(zoneId).toInstant().toEpochMilli()
        return Pair(startMillis, endMillis)
    }

    private suspend fun updateAccountBalance(t: Transaction, addingTransaction: Boolean = true) {
        t.accountId?.let {
            val isExpense = t.type.equals("expense", ignoreCase = true)
            val balanceChange = if (isExpense xor addingTransaction) -t.amount else t.amount
            db.accountDao().updateBalance(it, balanceChange)
        }
    }

    suspend fun addTransaction(t: Transaction) {
        db.withTransaction {
            db.transactionDao().create(t)
            updateAccountBalance(t)
            updateSalaryCreditTime()
        }
    }

    suspend fun deleteTransaction(t: Transaction) {
        db.withTransaction {
            db.transactionDao().delete(t)
            updateAccountBalance(t, false)
            updateSalaryCreditTime()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTransactionsCurrentCycle(): Flow<List<Transaction>> {
        return db.appSettingDao().getByKeyFlow(Keys.BUDGET_TIMEFRAME.ordinal)
            .flatMapLatest { timeframe ->
                when (timeframe) {
                    Timeframe.SALARY_DATE.ordinal.toLong() -> {
                        db.appSettingDao().getByKeyFlow(Keys.SALARY_CREDIT_TIME.ordinal)
                            .flatMapLatest { timestamp -> db.transactionDao().getTransactionsFlow(from = timestamp ?: 0L) }
                    }
                    Timeframe.MONTHLY.ordinal.toLong() -> {
                        val date = LocalDate.now()
                        val (from, to) = getTimeMillis(date.monthValue, date.year)
                        db.transactionDao().getTransactionsFlow(from = from, to = to)
                    }
                    else -> throw IllegalArgumentException("Invalid timeframe")
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTransactionsArchivedCycle(): Flow<List<Transaction>> {
        return db.appSettingDao().getByKeyFlow(Keys.BUDGET_TIMEFRAME.ordinal)
            .flatMapLatest { timeframe ->
                when (timeframe) {
                    Timeframe.SALARY_DATE.ordinal.toLong() -> {
                        db.appSettingDao().getByKeyFlow(Keys.SALARY_CREDIT_TIME.ordinal)
                            .flatMapLatest { timestamp -> db.transactionDao().getTransactionsFlow(to = (timestamp ?: 1L) - 1) }
                    }
                    Timeframe.MONTHLY.ordinal.toLong() -> {
                        val date = LocalDate.now().minusMonths(1)
                        val (_, to) = getTimeMillis(date.monthValue, date.year)
                        db.transactionDao().getTransactionsFlow(to = to)
                    }
                    else -> throw IllegalArgumentException("Invalid timeframe")
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTransactionsByCategoryCurrentCycle(id: Int?): Flow<List<Transaction>> {
        return db.appSettingDao().getByKeyFlow(Keys.BUDGET_TIMEFRAME.ordinal)
            .flatMapLatest { timeframe ->
                when (timeframe) {
                    Timeframe.SALARY_DATE.ordinal.toLong() -> {
                        db.appSettingDao().getByKeyFlow(Keys.SALARY_CREDIT_TIME.ordinal)
                            .flatMapLatest { timestamp -> db.transactionDao().getTransactionsByCategoryFlow(id, timestamp ?: 0L) }
                    }
                    Timeframe.MONTHLY.ordinal.toLong() -> {
                        val date = LocalDate.now()
                        val (from, to) = getTimeMillis(date.monthValue, date.year)
                        db.transactionDao().getTransactionsByCategoryFlow(id, from, to)
                    }
                    else -> throw IllegalArgumentException("Invalid timeframe")
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSumOfTransactionsByCategoryCurrentCycle(): Flow<List<TransactionSummary>> {
        return db.appSettingDao().getByKeyFlow(Keys.BUDGET_TIMEFRAME.ordinal)
            .flatMapLatest { timeframe ->
                when (timeframe) {
                    Timeframe.SALARY_DATE.ordinal.toLong() -> {
                        db.appSettingDao().getByKeyFlow(Keys.SALARY_CREDIT_TIME.ordinal)
                            .flatMapLatest { time -> db.transactionDao().getSumOfTransactionsByCategoryFlow(time ?: 0L) }
                    }
                    Timeframe.MONTHLY.ordinal.toLong() -> {
                        val date = LocalDate.now()
                        val (from, to) = getTimeMillis(date.monthValue, date.year)
                        db.transactionDao().getSumOfTransactionsByCategoryFlow(from, to)
                    }
                    else -> throw IllegalArgumentException("Invalid timeframe")
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSumOfTransactionsPreviousCycle(): Flow<Double> {
        return db.appSettingDao().getByKeyFlow(Keys.BUDGET_TIMEFRAME.ordinal)
            .flatMapLatest { timeframe ->
                when (timeframe) {
                    Timeframe.SALARY_DATE.ordinal.toLong() -> {
                        db.appSettingDao().getByKeyFlow(Keys.SALARY_CREDIT_TIME.ordinal)
                            .flatMapLatest { to ->
                                db.appSettingDao().getByKeyFlow(Keys.PREVIOUS_SALARY_CREDIT_TIME.ordinal)
                                    .flatMapLatest { from -> db.transactionDao().getSumOfTransactionsFlow(from ?: 0L, (to ?: 1L) - 1) }
                            }
                    }
                    Timeframe.MONTHLY.ordinal.toLong() -> {
                        val previousMonth = LocalDate.now().minusMonths(1)
                        val (from, to) = getTimeMillis(previousMonth.monthValue, previousMonth.year)
                        db.transactionDao().getSumOfTransactionsFlow(from, to)
                    }
                    else -> throw IllegalArgumentException("Invalid timeframe")
                }
            }
    }

    suspend fun updateAccount(transaction: Transaction) {
        val newAccountId = transaction.accountId
        val t = db.transactionDao().get(transaction.id) ?: return
        db.withTransaction {
            val amount = db.transactionDao().getTotalAmountForRawAccount(t.rawAccountIdName)
            if (amount != null) {
                t.accountId?.let { db.accountDao().updateBalance(it, -amount) }
                newAccountId?.let { db.accountDao().updateBalance(it, amount) }
                db.transactionDao().updateAccountForTransactionsWithRawAccount(t.rawAccountIdName, newAccountId)
                updateSalaryCreditTime()
            }
        }
    }

    suspend fun updateTransactionCategory(t: Transaction, forAll: Boolean) {
        db.withTransaction {
            t.categoryId?.let { catId ->
                if (forAll) {
                    db.PayeeCategoryMapperDao().insert(PayeeCategoryMapper(categoryId = catId, payee = t.payee))
                    db.transactionDao().updateCategoryForTransactionsWithPayee(t.payee, catId)
                }
                else {
                    db.transactionDao().update(t)
                }
            }
        }
    }

    fun String.beautify(): String {
        return this.split(" ").map { word ->
            word.lowercase().replaceFirstChar { it.titlecaseChar() }
        }.joinToString(" ").replace("  ", " ")
    }
}
