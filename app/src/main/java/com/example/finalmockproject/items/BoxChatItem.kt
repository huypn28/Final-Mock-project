package com.example.finalmockproject.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.finalmockproject.R
import com.example.finalmockproject.viewmodel.ChatAppViewModel
import com.example.finalmockserver.model.RecentBox
import com.example.finalmockserver.model.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxChatItem(
    boxChat: RecentBox,
    currentUserId: Int,
    users: List<User>,
    userStatuses: Map<Int, String?>,
    viewModel: ChatAppViewModel,
    onItemClick: (Int, String, String, Int) -> Unit
) {
    val receiverId = if (boxChat.user1Id == currentUserId) boxChat.user2Id else boxChat.user1Id
    val receiver = users.find { it.userId == receiverId }

    val lastMessageId = boxChat.lastMessageId
    var lastMessage = remember(lastMessageId) {
        viewModel.getLastMessageById(lastMessageId)
    }

    val showDeleteDialog = remember { mutableStateOf(false) }
    val receiverStatus = userStatuses[receiverId] ?: "Offline"

    LaunchedEffect(lastMessageId) {
        viewModel.getLastMessageById(lastMessageId)?.let {
            lastMessage = it
        }
    }

    receiver?.let { user ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .combinedClickable(
                    onClick = {
                        onItemClick(
                            receiverId,
                            user.username ?: "Unknown",
                            user.imageUrl ?: "",
                            currentUserId
                        )
                    },
                    onLongClick = {
                        showDeleteDialog.value = true
                    }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = user.imageUrl,
                    error = painterResource(id = R.drawable.img_default_avatar),
                    placeholder = painterResource(id = R.drawable.img_loading_avatar)
                ),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                user.username?.let {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (lastMessage?.deletedByUserId?.contains(currentUserId.toString()) == true) {
                    Text(text = "", color = Color.Gray, fontSize = 14.sp)
                } else {
                    Text(
                        text = (if (lastMessage?.senderId == currentUserId) {
                            "You: ${lastMessage!!.message}"
                        } else lastMessage?.message)
                            ?: "",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                val dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(lastMessage?.time?.toLong() ?: 0L),
                    ZoneId.systemDefault()
                )
                val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
                val formattedTime = dateTime.format(formatter)
                if (lastMessage?.deletedByUserId?.contains(currentUserId.toString()) == true) {
                    Text(text = "", color = Color.Gray, fontSize = 14.sp)
                } else {
                    Text(
                        text = if (formattedTime.contains("1970")) "" else formattedTime,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = receiverStatus,
                    color = if (receiverStatus == "Online") Color.Green else Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        ConfirmDeleteDialog(
            showDialog = showDeleteDialog.value,
            onDismiss = { showDeleteDialog.value = false },
            onConfirm = {
                viewModel.deleteChat(currentUserId, receiverId)
                showDeleteDialog.value = false
            }
        )

    }
}
