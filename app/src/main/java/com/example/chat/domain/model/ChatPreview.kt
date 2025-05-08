package com.example.chat.domain.model

data class ChatPreview (
    val chatId: String,
    val otherUserId: String,
    val otherUsername: String,
    val avatarUrl: String = "https://firebasestorage.googleapis.com/v0/b/chatapp-cdec1.firebasestorage.app/o/avatar.png?alt=media&token=b45338e4-c787-470d-839a-2ee05530bb17",
    val lastMessage: String? = null
)