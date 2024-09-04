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
}