package com.example.finalmockproject.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalmockproject.R
import com.example.finalmockproject.items.ImageUrlDialog
import com.example.finalmockproject.viewmodel.ChatAppViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ChatAppViewModel
) {
    var username by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf("") }


    fun resetLoginState() {
        username = ""
        isLoading = false
        errorMessage = null
        showDialog = false
    }

    fun onLogin() {
        if(username.isBlank()) {
            errorMessage = "Username cannot be empty or contain only spaces"
            return
        }

        isLoading = true
        viewModel.getUserIdByUsername(username) { userId ->
            isLoading = false
            if (userId != null) {
                viewModel.setUserId(userId)
                viewModel.updateUserStatus(userId, "Online")
                isLoading = false
                navController.navigate("box_chat_screen/${userId}"){
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            } else {
                showDialog = true
            }
        }
    }
    fun onConfirmImageUrl(imageUrl: String) {
        selectedImageUrl = imageUrl
        viewModel.addNewUser(username, selectedImageUrl) { newUserId ->
            isLoading = false
            if (newUserId != null) {
                viewModel.pairWithExistingUsers(newUserId)
                viewModel.updateUserStatus(newUserId, "Online")
                navController.navigate("box_chat_screen/$newUserId"){
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            } else {
                errorMessage = "Failed to create new user"
            }
        }
        showDialog = false
    }
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logos),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(180.dp)
                )

                Spacer(modifier = Modifier.height(80.dp))

                Text(
                    text = "Hello Welcome Back",
                    fontSize = 24.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome back please sign in again",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("UserName") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(26.dp))

                Button(
                    onClick = { onLogin() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01776C)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Login", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!errorMessage.isNullOrEmpty()) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator()
                }

                Spacer(modifier = Modifier.height(23.dp))

                Text(text = "Or Sign In With", color = Color.Gray, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6E4E2)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_facebook),
                            contentDescription = "Facebook Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Facebook", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6E4E2)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_google),
                            contentDescription = "Facebook Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Gmail", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(58.dp))

                TextButton(onClick = { }) {
                    Text(text = "Can't log in? Find out the error", color = Color.Black)
                }
            }
        }
    }

    if (showDialog) {
        ImageUrlDialog(
            onDismiss = {
                resetLoginState()
            },
            onConfirm = { imageUrl ->
                onConfirmImageUrl(imageUrl)
            },
            navController = navController
        )
    }
}
