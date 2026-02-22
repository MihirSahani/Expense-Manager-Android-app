package com.example.financemanager.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.financemanager.MainActivity
import com.example.financemanager.R
import com.example.financemanager.database.entity.Category
import com.example.financemanager.database.entity.Transaction
import com.example.financemanager.ui.composable.utils.MyText.toIndianFormat
import java.util.Locale

class NotificationManager(
    private val context: Context
) {

    private val transactionChannel = "payment_notification_channel"
    private val notificationManger = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    fun createPaymentNotificationChannel() {
        val channelName = "Payment Notification"
        val channelDescription = "Notifications for transactions captured by the application"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(transactionChannel, channelName, importance).apply {
            description = channelDescription
            setShowBadge(true)
            enableLights(true)
            enableVibration(true)
        }
        notificationManger.createNotificationChannel(channel)
    }

    fun showTransactionNotification(transaction: Transaction, category: Category?) {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_EDIT
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("transaction_id", transaction.id)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 
            transaction.id, 
            intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val isIncome = transaction.type.equals("income", ignoreCase = true)
        val formattedAmount = transaction.amount.toIndianFormat()
        
        val contentText = buildString {
            append(transaction.payee)
            if (category != null) {
                append(" • ")
                append(category.name)
            }
        }

        val bigText = buildString {
            append(formattedAmount)
            if(isIncome) append(" credited from ") else append(" debited to ")
            append(transaction.payee)
            if (category != null) {
                append("\nCategory: ")
                append(category.name)
            }
            append("\nAccount: ")
            append(transaction.rawAccountIdName)
        }

        val notification = NotificationCompat.Builder(context, transactionChannel)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(formattedAmount)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManger.notify(transaction.id, notification)
    }
}
