package com.example.finalmockserver.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT * FROM message WHERE (senderId = :senderId AND receiverId = :receiverId) OR (senderId = :receiverId AND receiverId = :senderId) ORDER BY time ASC")
     fun getMessagesBetweenUsers(senderId: Int, receiverId: Int): List<Message>

    @Update
     fun updateMessage(message: Message)
}