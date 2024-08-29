package com.example.finalmockserver.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalmockserver.model.RecentBox

@Dao
interface RecentBoxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentBox(recentBox: RecentBox)

    @Query("SELECT * FROM recentbox WHERE recentBoxId = :recentBoxId")
    suspend fun getRecentBoxById(recentBoxId: Int): RecentBox?

    @Delete
    suspend fun deleteRecentBox(recentBox: RecentBox)
}