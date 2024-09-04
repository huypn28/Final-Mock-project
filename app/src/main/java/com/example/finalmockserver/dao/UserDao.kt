package com.example.finalmockserver.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalmockserver.model.User
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Query("SELECT * FROM user WHERE userId = :userId")
    fun getUserById(userId: Int): User

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String?): User?

    @Delete
    suspend fun deleteUser(user: User)


    @Query("SELECT * FROM user WHERE userId != :userId")
    suspend fun getAllUsersExcept(userId: Int): List<User>

    @Query("UPDATE User SET status = :status WHERE userId = :userId")
    suspend fun updateUserStatus(userId: Int, status: String)
}