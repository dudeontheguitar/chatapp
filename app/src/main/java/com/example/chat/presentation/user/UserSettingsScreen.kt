package com.example.chat.presentation.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

@Composable
fun UserSettingsScreen(
    navController: NavController,
    viewModel: UserSettingsViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    var username by remember(user) { mutableStateOf(user?.username ?: "") }
    var avatarUrl by remember(user) { mutableStateOf(user?.avatarUrl ?: "") }
    val isUploading by viewModel.isUploading.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Выбор изображения
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                val croppedUri = cropImageToSquareAndCache(context, it)
                croppedUri?.let { finalUri ->
                    viewModel.uploadAvatar(finalUri) { uploadUrl ->
                        avatarUrl = uploadUrl
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", fontSize = 24.sp, modifier = Modifier.padding(bottom = 24.dp))

        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(100.dp)
                .clickable { launcher.launch("image/*") }
        )

        Text(
            text = "Tap avatar to change photo",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(top = 4.dp, bottom = 8.dp)
                .clickable { launcher.launch("image/*") }
        )

        if (isUploading) {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
        }

        // Сбросить аватарку
        Button(
            onClick = {
                val defaultUrl = "https://api.dicebear.com/6.x/identicon/svg?seed=${user?.uid}"
                avatarUrl = defaultUrl
                viewModel.updateUser(username, defaultUrl)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("Remove Photo", color = Color.Black)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = user?.email ?: "",
            onValueChange = {},
            label = { Text("Email") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.updateUser(username, avatarUrl)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }

            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Logout")
        }
    }

}
