package com.example.chat.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
    val isSuccess by remember { derivedStateOf { viewModel.isSuccess } }
    val errorMessage by remember { derivedStateOf { viewModel.errorMessage } }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Переход на экран чата после успешного входа
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.navigate("chatlist") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", fontSize = 24.sp, modifier = Modifier.padding(bottom = 32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
        ) {
            Text("Login", color = Color.White)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Don't have an account? Register",
            color = Color(0xFF6C63FF),
            modifier = Modifier.clickable {
                navController.navigate("register") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
        Spacer(Modifier.height(24.dp))
        if (isLoading) CircularProgressIndicator()
        errorMessage?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
