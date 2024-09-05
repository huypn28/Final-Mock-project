package com.example.finalmockproject.screens.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.finalmockserver.model.Message
import com.example.finalmockserver.model.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(message: Message, user: User, onDelete: (Message) -> Unit) {
    val alignment = if (message.senderId == user.userId) Alignment.End else Alignment.Start
    val bubbleColor = if (message.senderId == user.userId) Color(0xFF4FC3F7) else Color(0xFFF0F0F0)
    val textColor = if (message.senderId == user.userId) Color.White else Color.Black
    var showDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = alignment,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { showDialog = true }
            )
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .widthIn(max = 250.dp)
                .background(bubbleColor, shape = RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            message.message?.let {
                Text(
                    text = it,
                    color = textColor
                )
            }
        }
        message.time?.let {
            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.toLong()), ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
            val formattedTime = dateTime.format(formatter)
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Message") },
            text = { Text("Are you sure you want to delete this message?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(message)
                        showDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
