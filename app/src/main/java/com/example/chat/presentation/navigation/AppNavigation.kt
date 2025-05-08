package com.example.chat.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chat.presentation.auth.AuthViewModel
import com.example.chat.presentation.auth.LoginScreen
import com.example.chat.presentation.auth.RegisterScreen
import com.example.chat.presentation.chat.ChatListScreen
import com.example.chat.presentation.chat.ChatScreen
import com.example.chat.presentation.user.UserSettingsScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(viewModel, navController)
        }

        composable("register") {
            RegisterScreen(viewModel, navController)
        }

        composable("chatlist") {
            ChatListScreen(navController = navController)
        }

        composable("chat/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId")!!
            ChatScreen(chatId = chatId, navController = navController)
        }
        composable("settings") {
            UserSettingsScreen(navController = navController)
        }

    }
}
