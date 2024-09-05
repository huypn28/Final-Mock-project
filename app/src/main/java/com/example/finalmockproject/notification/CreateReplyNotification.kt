package com.example.finalmockproject.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleOwner
import com.example.finalmockproject.viewmodel.ChatAppViewModel
import com.example.finalmockproject.R
import com.example.finalmockserver.model.Message

class CreateReplyNotification {
    companion object {
        fun observeMessagesForNotifications(
            lifecycleOwner: LifecycleOwner,
            context: Context,
            viewModel: ChatAppViewModel,
            currentUserId: Int,
            displayedMessageIds: MutableSet<Int>,
            getSenderName: (Int) -> String
        ) {
            viewModel.messages.observe(lifecycleOwner) { messages ->
                if (messages.isNotEmpty()) {
                    val lastMessage = messages.last()
                    if (!displayedMessageIds.contains(lastMessage.messageId) &&
                        lastMessage.deletedByUserId?.contains(currentUserId.toString()) != true) {
                        showNotification(context, lastMessage, currentUserId, getSenderName)
                        displayedMessageIds.add(lastMessage.messageId)
                    }
                }
            }
        }

        @SuppressLint("ObsoleteSdkInt")
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Chat Notifications"
                val descriptionText = "Notifications for new chat messages"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel("chat_notifications", name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                    context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        @SuppressLint("LogNotTimber")
        private fun showNotification(
            context: Context,
            message: Message,
            currentUserId: Int,
            getSenderName: (Int) -> String
        ) {
            if (message.senderId == currentUserId ||
                message.deletedByUserId?.contains(currentUserId.toString()) == true) {
                return
            }

            val senderName = getSenderName(message.senderId)

            val replyLabel = "Reply"
            val remoteInput = androidx.core.app.RemoteInput.Builder("KEY_TEXT_REPLY")
                .setLabel(replyLabel)
                .build()

            val replyIntent = Intent(context, NotificationReplyReceiver::class.java).apply {
                Log.d("NotificationReplyReceiver", "onReceive: receiverId=${message.senderId}, senderId=$currentUserId")
                putExtra("receiverId", message.senderId)
                putExtra("senderId", currentUserId)
                putExtra("notificationId", message.messageId)
            }

            val replyPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val replyAction = NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_send,
                "Reply", replyPendingIntent
            ).addRemoteInput(remoteInput).build()

            val notification = NotificationCompat.Builder(context, "chat_notifications")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("New message from $senderName")
                .setContentText(message.message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(replyAction)
                .setAutoCancel(true)
                .build()

            with(NotificationManagerCompat.from(context)) {
                if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notify(message.messageId, notification)
                }
            }
        }
    }
}
