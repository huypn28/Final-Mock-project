package com.example.finalmockproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.finalmockproject.screens.component.BoxChatList
import com.example.finalmockproject.viewmodel.ChatAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxChatScreen(
    userId: Int,
    navController: NavController,
    viewModel: ChatAppViewModel
) {
    LaunchedEffect(userId) {
        viewModel.setCurrentUserId(userId)
    }

    val users by viewModel.users.observeAsState(emptyList())
    val boxChats by viewModel.chatBoxes.observeAsState(emptyList())
    val userStatuses by viewModel.userStatuses.observeAsState(emptyMap())

    fun onLogout() {
        viewModel.updateUserStatus(userId, "Offline")
        navController.navigate("login_screen") {
            popUpTo("login_screen") { inclusive = true }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Box Chats") },

                actions = {
                    IconButton(onClick = { onLogout() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(color = Color.White)

        ) {
            BoxChatList(
                boxChats = boxChats,
                currentUserId = userId,
                users = users,
                userStatuses = userStatuses,
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}