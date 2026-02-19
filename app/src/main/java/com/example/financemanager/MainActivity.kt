package com.example.financemanager

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.financemanager.database.localstorage.ExpenseManagementDatabase
import com.example.financemanager.internal.ExpenseManagementInternal
import com.example.financemanager.ui.composable.initial.Navigate
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.ViewModelFactory

object Graph {
    lateinit var database: ExpenseManagementDatabase
    lateinit var expenseManagementInternal: ExpenseManagementInternal

    lateinit var viewModelFactory: ViewModelFactory


    fun provide(context: Context) {
        database = Room.databaseBuilder(context, ExpenseManagementDatabase::class.java, "expense_management.db")
            .fallbackToDestructiveMigration()
            .build()
        // Initialize ExpenseManagementInternal after the database is ready
        expenseManagementInternal = ExpenseManagementInternal(database)
        viewModelFactory = ViewModelFactory(expenseManagementInternal)
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Graph.provide(this)

        setContent {
            FinanceManagerTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Navigate()
                }
            }
        }
    }
}