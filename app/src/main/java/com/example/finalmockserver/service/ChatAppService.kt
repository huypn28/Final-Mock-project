package com.example.finalmockserver.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.RemoteException
import com.example.finalmockserver.IMessageReceivedCallback
import com.example.finalmockserver.IMyAidlInterface
import com.example.finalmockserver.IRecentBoxUpdateCallback
import com.example.finalmockserver.IUserStatusCallback
import com.example.finalmockserver.dao.MessageDao
import com.example.finalmockserver.dao.RecentBoxDao
import com.example.finalmockserver.dao.UserDao
import com.example.finalmockserver.model.Message
import com.example.finalmockserver.model.RecentBox
import com.example.finalmockserver.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ChatAppService : Service() {
    @Inject
    lateinit var userDao: UserDao

    @Inject     
    lateinit var messageDao: MessageDao

    @Inject
    lateinit var recentBoxDao: RecentBoxDao

    private val userStatusCallbacks = RemoteCallbackList<IUserStatusCallback>()
    private val recentBoxUpdateCallbacks = RemoteCallbackList<IRecentBoxUpdateCallback>()
    private val messageReceivedCallbacks = RemoteCallbackList<IMessageReceivedCallback>()

    private val binder = object : IMyAidlInterface.Stub() {

        override fun getMessagesForUser(userId: Int): List<Message> {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    messageDao.getMessagesForUser(userId)
                }
            }
        }

        override fun getRecentBoxesForUser(userId: Int): List<RecentBox> {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    recentBoxDao.getRecentBoxesForUser(userId)
                }
            }
        }


        override fun getUserById(userId: Int): User {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    userDao.getUserById(userId)
                }
            }
        }

        override fun getAllUsers(): List<User> {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    userDao.getAllUsers()
                }
            }
        }

        override fun getAllRecentBox(): List<RecentBox> {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    recentBoxDao.getAllRecentBox()
                }
            }
        }

        override fun getAllMessage(): List<Message> {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    messageDao.getAllMessages()
                }
            }
        }

        override fun getMessageById(messageId: Int): Message {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    messageDao.getMessageById(messageId)
                }
            }
        }

        override fun getMessagesBetweenUsers(senderId: Int, receiverId: Int): List<Message> {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    messageDao.getMessagesBetweenUsers(senderId, receiverId)
                }
            }
        }

        override fun sendMessage(message: Message) {
            CoroutineScope(Dispatchers.IO).launch {
                messageDao.insertMessage(message)
                notifyMessageReceived(message)
            }
        }

        override fun updateMessage(message: Message) {
            CoroutineScope(Dispatchers.IO).launch {
                messageDao.updateMessage(message)
            }
        }

        override fun addUser(user: User?): Int {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    if (user != null) {
                        userDao.insertUser(user).toInt()
                    } else {
                        -1
                    }
                }
            }
        }

        override fun addRecentBox(recentBox: RecentBox?) {
            recentBox?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    recentBoxDao.insertRecentBox(it)
                }
            }
        }

        override fun updateUserStatus(userId: Int, status: String) {
            CoroutineScope(Dispatchers.IO).launch {
                userDao.updateUserStatus(userId, status)
                notifyUserStatusChanged(userId, status)
            }
        }

        override fun registerUserStatusCallback(callback: IUserStatusCallback) {
            userStatusCallbacks.register(callback)
        }

        override fun unregisterUserStatusCallback(callback: IUserStatusCallback) {
            userStatusCallbacks.unregister(callback)
        }

        override fun registerRecentBoxUpdateCallbacks(callback: IRecentBoxUpdateCallback) {
            recentBoxUpdateCallbacks.register(callback)
        }

        override fun unregisterRecentBoxUpdateCallbacks(callback: IRecentBoxUpdateCallback) {
            recentBoxUpdateCallbacks.unregister(callback)
        }

        override fun updateLastMessageForRecentBox(recentBoxId: Int, lastMessageId: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                recentBoxDao.updateLastMessageId(recentBoxId, lastMessageId)
                notifyRecentBoxUpdated(recentBoxId, lastMessageId)
            }
        }

        override fun registerMessageReceivedCallback(callback: IMessageReceivedCallback) {
            messageReceivedCallbacks.register(callback)
        }

        override fun unregisterMessageReceivedCallback(callback: IMessageReceivedCallback) {
            messageReceivedCallbacks.unregister(callback)
        }
    }

    private fun notifyUserStatusChanged(userId: Int, status: String) {
        val n = userStatusCallbacks.beginBroadcast()
        for (i in 0 until n) {
            try {
                userStatusCallbacks.getBroadcastItem(i).onUserStatusChanged(userId, status)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        userStatusCallbacks.finishBroadcast()
    }

    private fun notifyRecentBoxUpdated(recentBoxId: Int, lastMessageId: Int) {
        val n = recentBoxUpdateCallbacks.beginBroadcast()
        for (i in 0 until n) {
            try {
                recentBoxUpdateCallbacks.getBroadcastItem(i).onRecentBoxUpdated(recentBoxId, lastMessageId)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        recentBoxUpdateCallbacks.finishBroadcast()
    }

    private fun notifyMessageReceived(message: Message) {
        val n = messageReceivedCallbacks.beginBroadcast()
        for (i in 0 until n) {
            try {
                messageReceivedCallbacks.getBroadcastItem(i).onMessageReceived(message)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        messageReceivedCallbacks.finishBroadcast()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}