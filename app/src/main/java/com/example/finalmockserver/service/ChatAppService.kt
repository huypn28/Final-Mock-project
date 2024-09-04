package com.example.finalmockserver.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.finalmockserver.IMyAidlInterface
import com.example.finalmockserver.dao.MessageDao
import com.example.finalmockserver.dao.RecentBoxDao
import com.example.finalmockserver.dao.UserDao
import com.example.finalmockserver.model.Message
import com.example.finalmockserver.model.RecentBox
import com.example.finalmockserver.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatAppService : Service() {
    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var messageDao: MessageDao

    @Inject
    lateinit var recentBoxDao: RecentBoxDao

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
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}