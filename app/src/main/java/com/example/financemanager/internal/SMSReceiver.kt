package com.example.financemanager.internal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.example.financemanager.Graph
import com.example.financemanager.viewmodel.ViewModelName
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
                    Graph.expenseManagementInternal.parseSingleMessage(body, sender, timestamp)
                }
            }
        }
    }
}
