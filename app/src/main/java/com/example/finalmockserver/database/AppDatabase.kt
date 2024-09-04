package com.example.finalmockserver.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.finalmockserver.dao.MessageDao
import com.example.finalmockserver.dao.RecentBoxDao
import com.example.finalmockserver.dao.UserDao
import com.example.finalmockserver.di.Converters
import com.example.finalmockserver.model.Message
import com.example.finalmockserver.model.RecentBox
import com.example.finalmockserver.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, Message::class, RecentBox::class], version = 11)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun recentBoxDao(): RecentBoxDao

    fun populateInitialData() {
        CoroutineScope(Dispatchers.IO).launch {
            val userDao = userDao()
            val messageDao = messageDao()
            val recentBoxDao = recentBoxDao()

            if (userDao.getAllUsers().isEmpty()) {
                userDao.insertUser(
                    User(
                        status = "Offline",
                        imageUrl = "https://tse1.mm.bing.net/th?id=OIP.QT7nFVax4CigU2Xr5tyx3gHaES&pid=Api&P=0&h=220",
                        username = "Nguyen Van A"
                    )
                )
                userDao.insertUser(
                    User(
                        status = "Offline",
                        imageUrl = "https://cdn-cashy-static-assets.lucidchart.com/marketing/blog/2020Q3/sales/how-to-do-a-product-demo.png",
                        username = "Nguyen Van B"
                    )
                )
                userDao.insertUser(
                    User(
                        status = "Offline",
                        imageUrl = "https://st4.depositphotos.com/14431644/21644/i/450/depositphotos_216442552-stock-photo-text-sign-showing-demo-conceptual.jpg",
                        username = "Nguyen Van C"
                    )
                )
                userDao.insertUser(
                    User(
                        status = "Offline",
                        imageUrl = "https://tse2.mm.bing.net/th?id=OIP.bMWHLFuoYBlIIuVDmqooyQHaEK&pid=Api&P=0&h=220",
                        username = "Nguyen Van D"
                    )
                )
                userDao.insertUser(
                    User(
                        status = "Offline",
                        imageUrl = "https://cdn-1.timesmedia.co.id/images/2022/09/05/Kader-PMII-Demo.jpg",
                        username = "Nguyen Van E"
                    )
                )
            }

            if (messageDao.getAllMessages().isEmpty()) {
                messageDao.insertMessage(
                    Message(
                        senderId = 1,
                        receiverId = 2,
                        message = "Hello",
                        time = "1693485600000",
                        deletedByUserId = emptyList()
                    )
                )

                messageDao.insertMessage(
                    Message(
                        senderId = 2,
                        receiverId = 1,
                        message = "Hi",
                        time = "1693485660000",
                        deletedByUserId = emptyList()
                    )
                )

                messageDao.insertMessage(
                    Message(
                        senderId = 1,
                        receiverId = 2,
                        message = "How are you?",
                        time = "1693485720000",
                        deletedByUserId = emptyList()
                    )
                )

                messageDao.insertMessage(
                    Message(
                        senderId = 1,
                        receiverId = 3,
                        message = "Hey!",
                        time = "1693485780000",
                        deletedByUserId = emptyList()
                    )
                )

                messageDao.insertMessage(
                    Message(
                        senderId = 3,
                        receiverId = 1,
                        message = "Hello there!",
                        time = "1693485840000",
                        deletedByUserId = emptyList()
                    )
                )

                messageDao.insertMessage(
                    Message(
                        senderId = 1,
                        receiverId = 4,
                        message = "Are you free today?",
                        time = "1693485900000",
                        deletedByUserId = emptyList()
                    )
                )

                messageDao.insertMessage(
                    Message(
                        senderId = 4,
                        receiverId = 1,
                        message = "Yes, I am.",
                        time = "1693485960000",
                        deletedByUserId = emptyList()
                    )
                )

                messageDao.insertMessage(
                    Message(
                        senderId = 2,
                        receiverId = 5,
                        message = "Meeting at 5?",
                        time = "1693486020000",
                        deletedByUserId = emptyList()
                    )
                )

                messageDao.insertMessage(
                    Message(
                        senderId = 5,
                        receiverId = 2,
                        message = "Sure, see you then.",
                        time = "1693486080000",
                        deletedByUserId = emptyList()
                    )
                )
            }

            if (recentBoxDao.getAllRecentBox().isEmpty()) {
                recentBoxDao.insertRecentBox(
                    RecentBox(
                        user1Id = 1,
                        user2Id = 2,
                        lastMessageId = 3
                    )
                )
                recentBoxDao.insertRecentBox(
                    RecentBox(
                        user1Id = 1,
                        user2Id = 3,
                        lastMessageId = 4
                    )
                )
                recentBoxDao.insertRecentBox(
                    RecentBox(
                        user1Id = 1,
                        user2Id = 4,
                        lastMessageId = 6
                    )
                )
                recentBoxDao.insertRecentBox(
                    RecentBox(
                        user1Id = 5,
                        user2Id = 1,
                        lastMessageId = 0
                    )
                )
                recentBoxDao.insertRecentBox(
                    RecentBox(
                        user1Id = 2,
                        user2Id = 3,
                        lastMessageId = 0
                    )
                )
                recentBoxDao.insertRecentBox(
                    RecentBox(
                        user1Id = 2,
                        user2Id = 4,
                        lastMessageId = 0
                    )
                )
                recentBoxDao.insertRecentBox(
                    RecentBox(
                        user1Id = 2,
                        user2Id = 5,
                        lastMessageId = 8
                    )
                )
                recentBoxDao.insertRecentBox(
                    RecentBox(
                        user1Id = 3,
                        user2Id = 4,
                        lastMessageId = 7
                    )
                )
                recentBoxDao.insertRecentBox(
                    RecentBox(
                        user1Id = 3,
                        user2Id = 5,
                        lastMessageId = 9
                    )
                )
                recentBoxDao.insertRecentBox(
                    RecentBox(
                        user1Id = 4,
                        user2Id = 5,
                        lastMessageId = 0
                    )
                )

            }
        }
    }
}