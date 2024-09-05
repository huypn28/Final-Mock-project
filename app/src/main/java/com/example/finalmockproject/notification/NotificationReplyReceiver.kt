package com.example.finalmockproject.notification

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.finalmockproject.service.MessageService

class NotificationReplyReceiver : BroadcastReceiver() {
    @SuppressLint("LogNotTimber")
    override fun onReceive(context: Context, intent: Intent) {
        val receiverId = intent.getIntExtra("receiverId", -1)
        val senderId = intent.getIntExtra("senderId", -1)
        val replyText = getMessageText(intent)

        Log.d("NotificationReplyReceiver", "onReceive: receiverId=$receiverId, senderId=$senderId, replyText=$replyText")

        if (!replyText.isNullOrEmpty() && receiverId != -1 && senderId != -1) {
            val serviceIntent = Intent(context, MessageService::class.java).apply {
                putExtra("senderId", senderId)
                putExtra("receiverId", receiverId)
                putExtra("replyText", replyText.toString())
            }
            context.startService(serviceIntent)
            NotificationManagerCompat.from(context).cancelAll()
        } else {
            Log.e("NotificationReplyReceiver", "Invalid input or message text is empty")
        }
    }

    private fun getMessageText(intent: Intent): CharSequence? {
        return RemoteInput.getResultsFromIntent(intent)?.getCharSequence("KEY_TEXT_REPLY")
    }
}
