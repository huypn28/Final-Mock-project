package com.example.finalmockproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalmockserver.IMyAidlInterface
import com.example.finalmockserver.model.Message
import com.example.finalmockserver.model.RecentBox
import com.example.finalmockserver.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatAppViewModel @Inject constructor() : ViewModel(){
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _chatBoxes = MutableLiveData<List<RecentBox>>()
    val chatBoxes: LiveData<List<RecentBox>> = _chatBoxes

    private val _userStatuses = MutableLiveData<Map<Int, String?>>()
    val userStatuses: LiveData<Map<Int, String?>> = _userStatuses

    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int> get() = _userId


    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    var currentUserId: Int = -1
        private set

    private var aidlService: IMyAidlInterface? = null

    fun setUserService(service: IMyAidlInterface) {
        aidlService = service
    }



    fun getUserIdByUsername(username: String, callback: (Int?) -> Unit) {
        viewModelScope.launch {
            try {
                aidlService?.let { service ->
                    val userList = service.getAllUsers()
                    val user = userList?.find { it.username == username }
                    callback(user?.userId)
                } ?: run {
                    callback(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }


    fun updateUserStatus(userId: Int, status: String) {
        viewModelScope.launch {
            try {
                aidlService?.updateUserStatus(userId, status)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addNewUser(username: String, imageUrl: String, callback: (Int?) -> Unit) {
        viewModelScope.launch {
            try {
                aidlService?.let { service ->
                    val newUserId = service.addUser(
                        User(
                            username = username,
                            status = "Online",
                            imageUrl = imageUrl
                        )
                    )
                    callback(newUserId)
                    loadUsers()
                } ?: run {
                    callback(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }

    fun pairWithExistingUsers(newUserId: Int) {
        viewModelScope.launch {
            try {
                aidlService?.let { service ->
                    val existingUsers = service.getAllUsers()
                        ?.filter { it.userId != newUserId } // Exclude the new user
                    existingUsers?.forEach { existingUser ->
                        service.addRecentBox(
                            RecentBox(
                                user1Id = newUserId,
                                user2Id = existingUser.userId,
                                lastMessageId = 0
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
