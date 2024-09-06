package com.example.finalmockproject.screens.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalmockproject.items.BoxChatItem
import com.example.finalmockproject.viewmodel.ChatAppViewModel
import com.example.finalmockserver.model.RecentBox
import com.example.finalmockserver.model.User
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun BoxChatList(
    boxChats: List<RecentBox>,
    currentUserId: Int,
    users: List<User>,
    userStatuses: Map<Int, String?>,
    viewModel: ChatAppViewModel,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        items(boxChats) { boxChat ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                BoxChatItem(
                    boxChat = boxChat,
                    currentUserId = currentUserId,
                    users = users,
                    userStatuses = userStatuses,
                    viewModel = viewModel,
                    onItemClick = { receiverId, receiverName, receiverUrl, currentUserId ->
                        val encodedReceiverUrl =
                            URLEncoder.encode(receiverUrl, StandardCharsets.UTF_8.toString())
                        navController.navigate("chat_screen/$receiverId/$receiverName/$encodedReceiverUrl/$currentUserId")
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
