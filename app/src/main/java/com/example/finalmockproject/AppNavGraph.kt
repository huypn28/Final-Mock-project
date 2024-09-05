package com.example.finalmockproject

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.finalmockproject.screens.BoxChatScreen
import com.example.finalmockproject.screens.ChatScreen
import com.example.finalmockproject.screens.LoginScreen
import com.example.finalmockproject.viewmodel.ChatAppViewModel

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: ChatAppViewModel) {
    NavHost(
        navController = navController,
        startDestination = "login_screen"
    ) {
        composable("login_screen") {
            LoginScreen(navController = navController, viewModel = viewModel)
        }
        composable("box_chat_screen/{userId}") {
            val userId = it.arguments?.getString("userId")?.toIntOrNull() ?: 0
            BoxChatScreen(userId = userId, navController = navController, viewModel = viewModel)
        }
        composable(
            route = "chat_screen/{receiverId}/{receiverName}/{receiverUrl}/{currentUserId}",
            arguments = listOf(
                navArgument("receiverId") { type = NavType.IntType },
                navArgument("receiverName") { type = NavType.StringType },
                navArgument("receiverUrl") { type = NavType.StringType },
                navArgument("currentUserId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val receiverId = backStackEntry.arguments?.getInt("receiverId") ?: 0
            val receiverName = backStackEntry.arguments?.getString("receiverName") ?: ""
            val receiverUrl = backStackEntry.arguments?.getString("receiverUrl") ?: ""
            val currentUserId = backStackEntry.arguments?.getInt("currentUserId") ?: 0
            ChatScreen(
                navController = navController,
                receiverId = receiverId,
                receiverName = receiverName,
                receiverUrl = receiverUrl,
                currentUserId = currentUserId,
                viewModel = viewModel
            )
        }
    }
}

