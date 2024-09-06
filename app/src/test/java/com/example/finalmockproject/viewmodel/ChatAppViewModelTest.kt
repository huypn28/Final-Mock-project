
package com.example.finalmockproject.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.finalmockserver.IMyAidlInterface
import com.example.finalmockserver.model.Message
import com.example.finalmockserver.model.RecentBox
import com.example.finalmockserver.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class ChatAppViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val aidlService: IMyAidlInterface = mock()
    private lateinit var viewModel: ChatAppViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatAppViewModel()
        viewModel.setUserService(aidlService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setUserId should update userId LiveData`() {
        val observer = mock<Observer<Int>>()
        viewModel.userId.observeForever(observer)

        viewModel.setUserId(123)

        verify(observer).onChanged(123)
    }

    @Test
    fun `setUserService should initialize and register callbacks`() {
        val userStatusCallback = viewModel.userStatusCallback
        val recentBoxCallback = viewModel.recentBoxCallback
        val messageReceivedCallback = viewModel.messageReceivedCallback

        viewModel.setUserService(aidlService)

        verify(aidlService).registerUserStatusCallback(userStatusCallback)
        verify(aidlService).registerRecentBoxUpdateCallbacks(recentBoxCallback)
        verify(aidlService).registerMessageReceivedCallback(messageReceivedCallback)
    }

    @Test
    fun `updateUserStatus should call aidlService updateUserStatus`() = runTest {
        viewModel.setUserService(aidlService)

        viewModel.updateUserStatus(1, "Online")

        verify(aidlService).updateUserStatus(1, "Online")
    }

    @Test
    fun `setCurrentUserId should update userId and load data`() = runTest {
        val chatBoxes = listOf(RecentBox(1,1, 2, 1))
        val messages = listOf(Message(1, 1,2 ,"Hello", System.currentTimeMillis().toString()))

        whenever(aidlService.getRecentBoxesForUser(1)).thenReturn(chatBoxes)
        whenever(aidlService.getMessagesForUser(1)).thenReturn(messages)
        viewModel.setUserService(aidlService)

        viewModel.setCurrentUserId(1)

        verify(aidlService).getRecentBoxesForUser(1)
        verify(aidlService).getMessagesForUser(1)
        Assert.assertEquals(chatBoxes, viewModel.chatBoxes.value)
        Assert.assertEquals(messages, viewModel.messages.value)
    }

    @Test
    fun `loadUsers should update users LiveData`() = runTest {
        val users = listOf(User(1, "user", "Online", "url"))
        whenever(aidlService.getAllUsers()).thenReturn(users)

        viewModel.loadUsers()

        verify(aidlService).getAllUsers()
        Assert.assertEquals(users, viewModel.users.value)
    }

    @Test
    fun `getUserIdByUsername should call callback with correct userId`() = runTest {
        val username = "user"
        val user = User(1, username, "Online", "url")
        val users = listOf(user)
        whenever(aidlService.getAllUsers()).thenReturn(users)

        var resultUserId: Int? = null
        viewModel.getUserIdByUsername(username) { userId -> resultUserId = userId }

        Assert.assertEquals(1, resultUserId)
    }

    @Test
    fun `sendMessage should call aidlService sendMessage`() {
        val message = Message(1, 1, 2,"Hello", System.currentTimeMillis().toString())
        viewModel.setUserService(aidlService)

        viewModel.sendMessage(1, 2, "Hello")

        verify(aidlService).sendMessage(message)
    }

    @Test
    fun `deleteMessage should update message and call aidlService sendMessage`() = runTest {
        val message = Message(1, 1,2, "Hello", System.currentTimeMillis().toString())
        val updatedMessage = message.copy(deletedByUserId = listOf("1"))

        viewModel.setUserService(aidlService)

        viewModel.deleteMessage(message, 1)

        verify(aidlService).sendMessage(updatedMessage)
    }

    @Test
    fun `deleteChat should update messages and call aidlService updateMessage`() = runTest {
        val messages = listOf(Message(1,1, 2, "Hello", System.currentTimeMillis().toString()))
        whenever(aidlService.getMessagesBetweenUsers(1, 2)).thenReturn(messages)

        viewModel.setUserService(aidlService)
        viewModel.deleteChat(1, 2)

        verify(aidlService).updateMessage(any())
    }

    @Test
    fun `onCleared should unregister callbacks`() {
        viewModel.setUserService(aidlService)
        viewModel.setCurrentUserId(1)

        viewModel.onCleared()

        verify(aidlService).unregisterUserStatusCallback(viewModel.userStatusCallback)
        verify(aidlService).unregisterRecentBoxUpdateCallbacks(viewModel.recentBoxCallback)
        verify(aidlService).unregisterMessageReceivedCallback(viewModel.messageReceivedCallback)
    }
}
