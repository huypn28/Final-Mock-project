@file:Suppress("DEPRECATION")

package com.example.finalmockproject

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.finalmockproject.viewmodel.ChatAppViewModel
import com.example.finalmockserver.IMyAidlInterface
import com.example.finalmockserver.model.Message
import com.example.finalmockserver.model.RecentBox
import com.example.finalmockserver.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class ChatAppViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var aidlService: IMyAidlInterface

    private lateinit var viewModel: ChatAppViewModel

    private var closeable: AutoCloseable? = null

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        closeable = MockitoAnnotations.openMocks(this) // Initialize mocks
        viewModel = ChatAppViewModel()
        viewModel.setUserService(aidlService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher
        testDispatcher.cancelChildren()
        closeable?.close() // Close mocks
    }

    @Test
    fun `setUserId updates LiveData`() = runTest {
        val expectedUserId = 123
        viewModel.setUserId(expectedUserId)
        assertEquals(expectedUserId, viewModel.userId.value)
    }







    @Test
    fun `getUserIdByUsername returns correct userId`() = runTest {
        val username = "User1"
        val userId = 1
        `when`(aidlService.getAllUsers()).thenReturn(listOf(User(userId = userId, username = username)))
        var resultUserId: Int? = null
        viewModel.getUserIdByUsername(username) { resultUserId = it }
        advanceUntilIdle()
        assertEquals(userId, resultUserId)
    }





    @Test
    fun `pairWithExistingUsers creates recent boxes for all existing users`() = runTest {
        val newUserId = 1
        val existingUsers = listOf(User(userId = 2), User(userId = 3))
        `when`(aidlService.getAllUsers()).thenReturn(existingUsers)
        viewModel.pairWithExistingUsers(newUserId)
        advanceUntilIdle()
        existingUsers.forEach { user ->
            verify(aidlService).addRecentBox(RecentBox(user1Id = newUserId, user2Id = user.userId, lastMessageId = 0))
        }
    }

    @Test
    fun `getLastMessageById returns correct message`() = runTest {
        val messageId = 1
        val message = Message(messageId = messageId, senderId = 1, receiverId = 2, message = "Hello")
        `when`(aidlService.getMessageById(messageId)).thenReturn(message)
        val resultMessage = viewModel.getLastMessageById(messageId)
        assertEquals(message, resultMessage)
    }






    @Test
    fun `getUserIdByUsername handles error correctly`() = runTest {
        val username = "NonExistentUser"
        `when`(aidlService.getAllUsers()).thenThrow(RuntimeException("Error fetching users"))
        viewModel.getUserIdByUsername(username) { resultUserId ->
            assertNull(resultUserId)
        }
        advanceUntilIdle()
    }

}
