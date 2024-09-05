@file:Suppress("DEPRECATION")

package com.example.finalmockproject.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MessageService : IntentService("MessageService") {

    @Deprecated("Deprecated in Java")
    @SuppressLint("LogNotTimber")
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val senderId = intent.getIntExtra("senderId", -1)
            val receiverId = intent.getIntExtra("receiverId", -1)
            val replyText = intent.getStringExtra("replyText")

            Log.d("MessageService", "Handling intent: senderId=$senderId, receiverId=$receiverId, replyText=$replyText")

            if (!replyText.isNullOrEmpty() && receiverId != -1 && senderId != -1) {
                Log.d("MessageService", "Sending message from $senderId to $receiverId: $replyText")

                sendMessageBroadcast(senderId, receiverId, replyText)
            }
        }
    }

    private fun sendMessageBroadcast(senderId: Int, receiverId: Int, message: String) {
        val broadcastIntent = Intent("SEND_MESSAGE_ACTION").apply {
            putExtra("senderId", senderId)
            putExtra("receiverId", receiverId)
            putExtra("message", message)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }
}