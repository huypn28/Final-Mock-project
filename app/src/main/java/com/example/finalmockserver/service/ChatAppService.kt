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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("NAME_SHADOWING")
@AndroidEntryPoint
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

        override fun checkAndAddUser(username: String?): User {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    val existingUser = userDao.getUserByUsername(username)
                    if (existingUser != null) {
                        existingUser
                    } else {
                        val newUser = User(username = username)
                        val userId = userDao.insertUser(newUser).toInt()
                        val insertedUser = userDao.getUserById(userId)

                        val existingUsers = userDao.getAllUsersExcept(userId)
                        val recentBoxes = existingUsers.map { existingUser ->
                            RecentBox(
                                user1Id = insertedUser.userId,
                                user2Id = existingUser.userId,
                                lastMessageId = 0
                            )
                        }
                        recentBoxDao.insertRecentBoxes(recentBoxes)

                        insertedUser
                    }
                }
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}