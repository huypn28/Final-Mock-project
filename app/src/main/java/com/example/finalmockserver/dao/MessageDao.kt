package com.example.finalmockserver.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalmockserver.model.Message

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Message)

    @Query("SELECT * FROM message")
    fun getAllMessages(): List<Message>

    @Query("SELECT * FROM message WHERE senderId = :userId OR receiverId = :userId")
    fun getMessagesForUser(userId: Int): List<Message>

    @Query("SELECT * FROM message WHERE messageId = :messageId")
    fun getMessageById(messageId: Int): Message
}