package com.example.finalmockproject.items

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ImageUrlDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    navController: NavController
) {
    var imageUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Create New Account") },
        text = {
            Column {
                Text(
                    text = "Username does not exist." +
                            " If you want to create new account, please enter your avatar url:"
                )
                TextField(
                    value = imageUrl,
                    onValueChange = { newValue -> imageUrl = newValue },
                    label = { Text("URL") }
                )
                if (!errorMessage.isNullOrEmpty()) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        style = TextStyle(fontSize = 12.sp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (imageUrl.isBlank()) {
                        errorMessage = "Image URL cannot be empty or contain only spaces."
                    } else {
                        onConfirm(imageUrl)
                        errorMessage = null
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
