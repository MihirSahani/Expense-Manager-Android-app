package com.example.financemanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import com.example.financemanager.internal.ExpenseManagementInternal
import com.example.financemanager.internal.NotificationManager
import com.example.financemanager.ui.composable.initial.Navigate
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.InitialVM
import com.example.financemanager.viewmodel.TransactionVM
import com.example.financemanager.viewmodel.ViewModelFactory
import com.example.financemanager.viewmodel.ViewModelName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object Graph {
    lateinit var database: ExpenseManagementDatabase
    lateinit var expenseManagementInternal: ExpenseManagementInternal

    lateinit var viewModelFactory: ViewModelFactory
    @SuppressLint("StaticFieldLeak")
    lateinit var notificationManager: NotificationManager


    fun provide(context: Context) {
        database = Room.databaseBuilder(
            context,
            ExpenseManagementDatabase::class.java,
            "expense_management.db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
        // Initialize ExpenseManagementInternal after the database is ready
        expenseManagementInternal = ExpenseManagementInternal(database)
        viewModelFactory = ViewModelFactory(expenseManagementInternal)
        notificationManager = NotificationManager(context)
        notificationManager.createPaymentNotificationChannel()
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Graph.provide(this)
        handleIntents(intent)
        setContent {
            FinanceManagerTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Navigate(Graph.viewModelFactory.getViewModel(ViewModelName.LOGIN) as InitialVM)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntents(intent)
    }

    private fun handleIntents(intent: Intent) {
        lifecycleScope.launch {
            when(intent.action) {
                Intent.ACTION_EDIT -> {
                    val transactionId = intent.getIntExtra("transaction_id", -1)
                    if (transactionId != -1) {
                        val transaction = Graph.expenseManagementInternal.getTransaction(transactionId)
                        val transactionVM = Graph.viewModelFactory.getViewModel(ViewModelName.TRANSACTION) as TransactionVM
                        transactionVM.selectTransaction(transaction)
                        val initialVM = Graph.viewModelFactory.getViewModel(ViewModelName.LOGIN) as InitialVM
                        if (transaction != null) {
                            initialVM.triggerNavigateToEditTransaction(transaction)
                        }
                    }
                }
            }
        }

    }
}