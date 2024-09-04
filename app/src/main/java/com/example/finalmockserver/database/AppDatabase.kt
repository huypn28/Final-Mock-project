package com.example.finalmockserver.database

import androidx.room.RoomDatabase
import com.example.finalmockserver.dao.MessageDao
import com.example.finalmockserver.dao.RecentBoxDao
import com.example.finalmockserver.dao.UserDao

abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun recentBoxDao(): RecentBoxDao
}