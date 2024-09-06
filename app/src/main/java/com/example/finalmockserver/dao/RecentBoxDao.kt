package com.example.finalmockserver.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalmockserver.model.RecentBox

@Dao
interface RecentBoxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentBox(recentBox: RecentBox)

    @Query("SELECT * FROM recentbox")
     fun getAllRecentBox(): List<RecentBox>

    @Query("SELECT * FROM recentbox WHERE user1Id = :userId OR user2Id = :userId")
     fun getRecentBoxesForUser(userId: Int): List<RecentBox>

    @Query("UPDATE recentbox SET lastMessageId = :lastMessageId WHERE recentBoxId = :recentBoxId")
    suspend fun updateLastMessageId(recentBoxId: Int, lastMessageId: Int)
}