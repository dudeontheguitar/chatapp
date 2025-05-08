package com.example.chat.presentation.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun ChatScreen(
    chatId: String,
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Chat", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true // новые сообщения внизу
        ) {
            items(messages.reversed()) { msg ->
                MessageItem(
                    text = msg.text,
                    isMine = viewModel.isMyMessage(msg.senderId),
                    timestamp = msg.timestamp
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Write a message...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (textState.text.isNotBlank()) {
                        viewModel.sendMessage(textState.text)
                        textState = TextFieldValue("")
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}
