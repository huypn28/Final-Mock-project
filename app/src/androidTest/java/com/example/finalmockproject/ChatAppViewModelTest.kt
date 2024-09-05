@file:Suppress("DEPRECATION")

package com.example.finalmockproject

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.finalmockproject.viewmodel.ChatAppViewModel
import com.example.finalmockserver.IMyAidlInterface
import com.example.finalmockserver.model.Message
import com.example.finalmockserver.model.RecentBox
import com.example.finalmockserver.model.User
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import javax.inject.Inject


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChatAppViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Mock
    lateinit var aidlService: IMyAidlInterface

    lateinit var dispatcher: CoroutineDispatcher

    private lateinit var viewModel: ChatAppViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        hiltRule.inject()

        val testDispatcher = TestCoroutineDispatcher()
        viewModel = ChatAppViewModel(testDispatcher)
    }

    @After
    fun tearDown() {
        (dispatcher as? TestCoroutineDispatcher)?.cleanupTestCoroutines()
    }



    @Test
    fun testSetUserId() {
        val testUserId = 123

        viewModel.setUserId(testUserId)

        assertEquals(testUserId, viewModel.userId.value)
    }

    @Test
    fun testSetUserService() {
        Mockito.doNothing().`when`(aidlService).registerUserStatusCallback(any())
        Mockito.doNothing().`when`(aidlService).registerRecentBoxUpdateCallbacks(any())
        Mockito.doNothing().`when`(aidlService).registerMessageReceivedCallback(any())

        viewModel.setUserService(aidlService)

        verify(aidlService).registerUserStatusCallback(any())
        verify(aidlService).registerRecentBoxUpdateCallbacks(any())
        verify(aidlService).registerMessageReceivedCallback(any())
    }


    @Test
    fun testUpdateRecentBoxLocally_WithValidData() {
        val recentBoxId = 1
        val lastMessageId = 101
        val recentBox = RecentBox(
            recentBoxId = 1,
            user1Id = 1,
            user2Id = 2,
            lastMessageId = 100
        )
        viewModel._chatBoxes.value = listOf(recentBox)

        viewModel.updateRecentBoxLocally(recentBoxId, lastMessageId)

        assertEquals(lastMessageId, viewModel.chatBoxes.value?.first()?.lastMessageId)
    }


    @Test
    fun testUpdateRecentBoxLocally_WithEmptyData() {
        val recentBoxId = 1
        val lastMessageId = 101
        viewModel._chatBoxes.value = emptyList()

        viewModel.updateRecentBoxLocally(recentBoxId, lastMessageId)

        assertTrue(viewModel.chatBoxes.value?.isEmpty() == true)
    }


    @Test
    fun testUpdateUserStatusLocally_WithValidData() {
        val userId = 1
        val status = "Online"

        viewModel.updateUserStatusLocally(userId, status)

        assertEquals(status, viewModel.userStatuses.value?.get(userId))
    }

    @Test
    fun testUpdateUserStatusLocally_WithEmptyStatuses() {
        val userId = 1
        val status = "Online"
        viewModel._userStatuses.value = emptyMap()

        viewModel.updateUserStatusLocally(userId, status)

        assertEquals(status, viewModel.userStatuses.value?.get(userId))
    }


    @Test
    fun testHandleReceivedMessage_WithValidMessage() {
        val message = Message(
            messageId = 1, senderId = 1, receiverId = 2,
            message = "Hello", time = System.currentTimeMillis().toString(), deletedByUserId = null
        )
        viewModel._messages.value = emptyList()

        viewModel.handleReceivedMessage(message)

        assertTrue(viewModel.messages.value?.contains(message) == true)
    }

    @Test
    fun testHandleReceivedMessage_WithEmptyMessages() {
        val message = Message(
            messageId = 1, senderId = 1, receiverId = 2,
            message = "Hello", time = System.currentTimeMillis().toString(), deletedByUserId = null
        )
        viewModel._messages.value = emptyList()

        viewModel.handleReceivedMessage(message)

        assertTrue(viewModel.messages.value?.isNotEmpty() == true)
    }


    @Test
    fun testUpdateUserStatus_Success() = runTest {
        val userId = 1
        val status = "Online"
        Mockito.doNothing().`when`(aidlService)?.updateUserStatus(userId, status)

        viewModel.updateUserStatus(userId, status)

        verify(aidlService)?.updateUserStatus(userId, status)
    }

    @Test
    fun testUpdateUserStatus_ServiceThrowsException() = runTest {
        val userId = 1
        val status = "Online"
        Mockito.doThrow(RuntimeException("Test Exception")).`when`(aidlService)?.updateUserStatus(userId, status)

        try {
            viewModel.updateUserStatus(userId, status)
        } catch (e: Exception) {
            fail("Exception was thrown: ${e.message}")
        }

        verify(aidlService)?.updateUserStatus(userId, status)
    }

    @Test
    fun testUpdateUserStatus_ServiceNotInitialized() = runTest {
        val userId = 1
        val status = "Online"

        viewModel.updateUserStatus(userId, status)

        verify(aidlService, Mockito.never())?.updateUserStatus(userId, status)
    }



    @Test
    fun testSetCurrentUserId_Success() = runTest {
        val userId = 123
        val status = "Online"
        Mockito.doNothing().`when`(aidlService).updateUserStatus(userId, status)
        Mockito.`when`(aidlService.getRecentBoxesForUser(userId)).thenReturn(emptyList())

        viewModel.setCurrentUserId(userId)

        assertEquals(userId, viewModel.currentUserId)
        verify(aidlService).updateUserStatus(userId, status)
        verify(aidlService).getRecentBoxesForUser(userId)
    }

    @Test
    fun testSetCurrentUserId_EmptyChatBoxes() = runTest {
        val userId = 123
        val status = "Online"
        Mockito.doNothing().`when`(aidlService).updateUserStatus(userId, status)
        Mockito.`when`(aidlService.getRecentBoxesForUser(userId)).thenReturn(emptyList())

        viewModel.setCurrentUserId(userId)

        assertEquals(userId, viewModel.currentUserId)
        assertTrue(viewModel.chatBoxes.value?.isEmpty() == true)
        verify(aidlService).updateUserStatus(userId, status)
        verify(aidlService).getRecentBoxesForUser(userId)
    }

    @Test
    fun testSetCurrentUserId_ExceptionHandling() = runTest {
        val userId = 123
        val status = "Online"
        Mockito.doThrow(RuntimeException::class.java).`when`(aidlService).updateUserStatus(userId, status)
        Mockito.`when`(aidlService.getRecentBoxesForUser(userId)).thenReturn(emptyList())

        viewModel.setCurrentUserId(userId)

        assertEquals(userId, viewModel.currentUserId)
        assertTrue(viewModel.chatBoxes.value?.isEmpty() == true)
        verify(aidlService).updateUserStatus(userId, status)
        verify(aidlService).getRecentBoxesForUser(userId)
    }


    @Test
    fun testLoadUsers_WithValidData() = runTest {
        val userList = listOf(
            User(userId = 1, status = "Online"),
            User(userId = 2, status = "Offline")
        )
        Mockito.`when`(aidlService.getAllUsers()).thenReturn(userList)

        viewModel.loadUsers()

        assertEquals(userList, viewModel.users.value)
        assertEquals(mapOf(1 to "Online", 2 to "Offline"), viewModel.userStatuses.value)
    }

    @Test
    fun testLoadUsers_WithEmptyData() = runTest {
        Mockito.`when`(aidlService.getAllUsers()).thenReturn(emptyList())

        viewModel.loadUsers()

        assertTrue(viewModel.users.value.isNullOrEmpty())
        assertTrue(viewModel.userStatuses.value.isNullOrEmpty())
    }

    @Test
    fun testLoadUsers_WithError() = runTest {
        Mockito.`when`(aidlService.getAllUsers()).thenThrow(RuntimeException("Service error"))

        viewModel.loadUsers()

        assertTrue(viewModel.users.value.isNullOrEmpty())
        assertTrue(viewModel.userStatuses.value.isNullOrEmpty())
    }


    @Test
    fun testLoadChatBoxes_WithValidData() = runTest {
        val chatBoxes = listOf(
            RecentBox(recentBoxId = 1, user1Id = 1, user2Id = 2, lastMessageId = 100),
            RecentBox(recentBoxId = 2, user1Id = 2, user2Id = 3, lastMessageId = 200)
        )
        Mockito.`when`(aidlService.getRecentBoxesForUser(viewModel.currentUserId))
            .thenReturn(chatBoxes)

        viewModel.loadChatBoxes()

        assertEquals(chatBoxes, viewModel.chatBoxes.value)
    }

    @Test
    fun testLoadChatBoxes_WithEmptyData() = runTest {
        val emptyList = emptyList<RecentBox>()
        Mockito.`when`(aidlService.getRecentBoxesForUser(viewModel.currentUserId))
            .thenReturn(emptyList)

        viewModel.loadChatBoxes()

        assertEquals(emptyList, viewModel.chatBoxes.value)
    }

    @Test
    fun testLoadChatBoxes_WithException() = runTest {
        Mockito.`when`(aidlService.getRecentBoxesForUser(viewModel.currentUserId))
            .thenThrow(RuntimeException("Service error"))

        viewModel.loadChatBoxes()

        assertTrue(viewModel.chatBoxes.value.isNullOrEmpty())
    }


}
