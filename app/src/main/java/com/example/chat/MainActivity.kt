package com.example.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.chat.presentation.auth.AuthViewModel
import com.example.chat.presentation.auth.LoginScreen
import com.example.chat.presentation.navigation.AppNavigation
import com.example.chat.ui.theme.ChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

                val navController = rememberNavController()
                val viewModel: AuthViewModel = hiltViewModel()
                val currentUserId = viewModel.currentUser()

                LaunchedEffect(currentUserId) {
                    if (currentUserId != null) {
                        navController.navigate("chatlist") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }

                AppNavigation(navController = navController, viewModel = viewModel)

        }

    }
}
