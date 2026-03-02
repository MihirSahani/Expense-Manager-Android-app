package com.example.financemanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.example.financemanager.Graph
import com.example.financemanager.repository.TransactionRepo
import com.example.financemanager.repository.data.RepoName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SMSReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val body = message.messageBody
                val sender = message.originatingAddress ?: continue
                val timestamp = message.timestampMillis

                scope.launch {
                    val result = (Graph.repoFactory.get(RepoName.TRANSACTION) as TransactionRepo)
                        .parseSingleMessage(body, sender, timestamp)
                    if (result != null) {
                        Graph.notificationManager
                            .showTransactionNotification(
                                result.first,
                                result.second
                            )
                    }
                }
            }
        }
    }
}