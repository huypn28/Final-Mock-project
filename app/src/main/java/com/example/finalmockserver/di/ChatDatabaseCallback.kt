package com.example.finalmockserver.di

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finalmockserver.dao.MessageDao
import com.example.finalmockserver.dao.RecentBoxDao
import com.example.finalmockserver.dao.UserDao
import com.example.finalmockserver.model.Message
import com.example.finalmockserver.model.RecentBox
import com.example.finalmockserver.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class ChatDatabaseCallback(
    private val userDao: UserDao,
    private val messageDao: MessageDao,
    private val recentBoxDao: RecentBoxDao
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

//        // Thêm dữ liệu trong background thread
//        Executors.newSingleThreadExecutor().execute {
//            // Thêm dữ liệu cố định cho bảng User
//            userDao.insertUser(User(status = "Online", imageUrl = "url1", username = "Nguyen Van A"))
//            userDao.insertUser(User(status = "Offline", imageUrl = "url2", username = "Nguyen Van B"))
//
//            // Thêm dữ liệu cố định cho bảng Message
//            messageDao.insertMessage(Message(senderId = 1, receiverId = 2, message = "Hello", time = "7:16 PM", deleted_by_user_id = listOf()))
//            messageDao.insertMessage(Message(senderId = 2, receiverId = 1, message = "Hi", time = "7:17 PM", deleted_by_user_id = listOf()))
//
//            // Thêm dữ liệu cố định cho bảng RecentBox
//            recentBoxDao.insertRecentBox(RecentBox(receiverId = 2, receiverImage = "url2", time = "7:16 PM", name = "Nguyen Van B", senderId = 1, lastestmessage = "Hello", lastChattingPerson = "Nguyen Van A", receiverStatus = "Offline"))
//            recentBoxDao.insertRecentBox(RecentBox(receiverId = 1, receiverImage = "url1", time = "7:17 PM", name = "Nguyen Van A", senderId = 2, lastestmessage = "Hi", lastChattingPerson = "Nguyen Van B", receiverStatus = "Online"))
//        }

//        // Sử dụng CoroutineScope để thực hiện các thao tác trên background thread
//        CoroutineScope(Dispatchers.IO).launch {
//            // Thêm dữ liệu cố định cho bảng User
//            userDao.insertUser(User(status = "Online", imageUrl = "url1", username = "Nguyen Van A"))
//            userDao.insertUser(User(status = "Offline", imageUrl = "url2", username = "Nguyen Van B"))
//
//            // Thêm dữ liệu cố định cho bảng Message
//            messageDao.insertMessage(Message(senderId = 1, receiverId = 2, message = "Hello", time = "7:16 PM", deleted_by_user_id = listOf()))
//            messageDao.insertMessage(Message(senderId = 2, receiverId = 1, message = "Hi", time = "7:17 PM", deleted_by_user_id = listOf()))
//
//            // Thêm dữ liệu cố định cho bảng RecentBox
//            recentBoxDao.insertRecentBox(RecentBox(receiverId = 2, receiverImage = "url2", time = "7:16 PM", name = "Nguyen Van B", senderId = 1, lastestmessage = "Hello", lastChattingPerson = "Nguyen Van A", receiverStatus = "Offline"))
//            recentBoxDao.insertRecentBox(RecentBox(receiverId = 1, receiverImage = "url1", time = "7:17 PM", name = "Nguyen Van A", senderId = 2, lastestmessage = "Hi", lastChattingPerson = "Nguyen Van B", receiverStatus = "Online"))
//        }

        // Thực hiện các thao tác trên cơ sở dữ liệu trong một coroutine
        runBlocking {
            // Thêm dữ liệu cố định cho bảng User
            userDao.insertUser(User(status = "Online", imageUrl = "url1", username = "Nguyen Van A"))
            userDao.insertUser(User(status = "Offline", imageUrl = "url2", username = "Nguyen Van B"))

            // Thêm dữ liệu cố định cho bảng Message
            messageDao.insertMessage(Message(senderId = 1, receiverId = 2, message = "Hello", time = "7:16 PM", deleted_by_user_id = listOf()))
            messageDao.insertMessage(Message(senderId = 2, receiverId = 1, message = "Hi", time = "7:17 PM", deleted_by_user_id = listOf()))

            // Thêm dữ liệu cố định cho bảng RecentBox
            recentBoxDao.insertRecentBox(RecentBox(receiverId = 2, receiverImage = "url2", time = "7:16 PM", name = "Nguyen Van B", senderId = 1, lastestmessage = "Hello", lastChattingPerson = "Nguyen Van A", receiverStatus = "Offline"))
            recentBoxDao.insertRecentBox(RecentBox(receiverId = 1, receiverImage = "url1", time = "7:17 PM", name = "Nguyen Van A", senderId = 2, lastestmessage = "Hi", lastChattingPerson = "Nguyen Van B", receiverStatus = "Online"))
        }
    }
}
