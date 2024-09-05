package com.example.finalmockproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finalmockproject.items.ConfirmDeleteDialog
import com.example.finalmockproject.screens.component.ChatBubble
import com.example.finalmockproject.screens.component.ChatHeader
import com.example.finalmockproject.screens.component.MessageInput
import com.example.finalmockproject.viewmodel.ChatAppViewModel
import com.example.finalmockserver.model.User

@Composable
fun ChatScreen(
    navController: NavController,
    receiverId: Int,
    receiverName: String,
    receiverUrl: String,
    currentUserId: Int,
    viewModel: ChatAppViewModel = viewModel()
) {
    val messages by viewModel.messages.observeAsState(emptyList())

    val receiverUser = remember(receiverId) { User(userId = receiverId) }
    val currentUser = remember(currentUserId) { User(userId = currentUserId) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser.userId, receiverUser.userId) {
        viewModel.getMessagesForUser(currentUser.userId, receiverUser.userId)
    }

    Scaffold(modifier = Modifier.fillMaxSize().imePadding()) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                ChatHeader(
                    profileImageRes = receiverUrl,
                    contactName = receiverName,
                    onBackClick = { navController.popBackStack() },
                    onDeleteClick = { showDeleteDialog = true }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { message ->
                            if ((message.senderId == currentUser.userId && message.receiverId == receiverUser.userId) ||
                                (message.senderId == receiverUser.userId && message.receiverId == currentUser.userId)
                            ) {
                                if (message.deletedByUserId?.contains(currentUser.userId.toString()) == false) {
                                    ChatBubble(message = message, user = currentUser, onDelete = { msg ->
                                        viewModel.deleteMessage(msg, currentUser.userId)
                                        viewModel.getMessagesForUser(currentUser.userId, receiverUser.userId)
                                    })
                                }
                            }
                        }
                    }

                    MessageInput(onMessageSent = { messageText ->
                        viewModel.sendMessage(
                            senderId = currentUser.userId,
                            receiverId = receiverUser.userId,
                            messageText = messageText
                        )
                    })
                }
            }
        }
    }

    ConfirmDeleteDialog(
        showDialog = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onConfirm = {
            viewModel.deleteChat(currentUser.userId, receiverUser.userId)
            viewModel.getMessagesForUser(currentUser.userId, receiverUser.userId)
            navController.popBackStack()        }
    )
}



