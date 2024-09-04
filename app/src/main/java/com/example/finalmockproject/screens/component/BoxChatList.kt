package com.example.finalmockproject.screens.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
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
    LazyColumn {
        items(boxChats) { boxChat ->
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
                }
            )
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
        }
    }
}
