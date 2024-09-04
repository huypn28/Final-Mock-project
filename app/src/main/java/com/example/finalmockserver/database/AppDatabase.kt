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

@Database(entities = [User::class, Message::class, RecentBox::class], version = 11)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun recentBoxDao(): RecentBoxDao
}