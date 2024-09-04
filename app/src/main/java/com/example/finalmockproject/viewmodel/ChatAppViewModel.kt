package com.example.finalmockproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalmockserver.IMyAidlInterface
import com.example.finalmockserver.IUserStatusCallback
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
        loadUsers()
        service.registerUserStatusCallback(userStatusCallback)
    }

    private val userStatusCallback = object : IUserStatusCallback.Stub() {
        override fun onUserStatusChanged(userId: Int, status: String) {
            updateUserStatusLocally(userId, status)
        }
    }

    private fun updateUserStatusLocally(userId: Int, status: String) {
        viewModelScope.launch {
            val currentStatuses = _userStatuses.value?.toMutableMap() ?: mutableMapOf()
            currentStatuses[userId] = status
            _userStatuses.postValue(currentStatuses)
        }
    }


    fun loadUsers() {
        viewModelScope.launch {
            try {
                aidlService?.let { service ->
                    val result = service.getAllUsers()
                    _users.value = result ?: emptyList()

                    val userStatusMap = result?.associate { it.userId to it.status } ?: emptyMap()
                    _userStatuses.value = userStatusMap
                } ?: run {
                    _users.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _users.value = emptyList()
            }
        }
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

    fun getMessagesForUser(senderId: Int, receiverId: Int) {
        aidlService?.let {
            val messages = it.getMessagesBetweenUsers(senderId, receiverId)
            _messages.postValue(messages)
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

    private fun loadLastMessagesForChatBoxes() {
        viewModelScope.launch {
            aidlService?.let { service ->
                val chatBoxesForUser = service.getRecentBoxesForUser(currentUserId)
                val updatedChatBoxes = chatBoxesForUser?.map { chatBox ->
                    val user1Id = chatBox.user1Id
                    val user2Id = chatBox.user2Id

                    val messages = service.getMessagesBetweenUsers(user1Id, user2Id)

                    val filteredMessages = messages?.filterNot {
                        it.deletedByUserId?.contains(currentUserId.toString()) ?: false
                    }

                    val lastMessage =
                        filteredMessages?.maxByOrNull { it.time?.toLongOrNull() ?: 0L }

                    chatBox.copy(
                        lastMessageId = lastMessage?.messageId ?: chatBox.lastMessageId
                    )
                } ?: emptyList()

                _chatBoxes.postValue(updatedChatBoxes)
            } ?: run {
                _chatBoxes.postValue(emptyList())
            }
        }
    }

    fun getLastMessageById(messageId: Int): Message? {
        return aidlService?.getMessageById(messageId)
    }

    fun deleteChat(currentUserId: Int, receiverUserId: Int) {
        viewModelScope.launch {
            val messages =
                aidlService?.getMessagesBetweenUsers(currentUserId, receiverUserId) ?: emptyList()
            messages.forEach { message ->
                val updatedDeletedByUserId =
                    message.deletedByUserId?.toMutableList() ?: mutableListOf()
                if (!updatedDeletedByUserId.contains(currentUserId.toString())) {
                    updatedDeletedByUserId.add(currentUserId.toString())
                }
                val updatedMessage = message.copy(deletedByUserId = updatedDeletedByUserId)
                aidlService?.updateMessage(updatedMessage)
            }
            getMessagesForUser(currentUserId, receiverUserId)
            loadLastMessagesForChatBoxes()
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (currentUserId != -1) {
            updateUserStatus(currentUserId, "Offline")
        }
        aidlService?.unregisterUserStatusCallback(userStatusCallback)
    }

}
