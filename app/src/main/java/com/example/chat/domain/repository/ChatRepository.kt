package com.example.chat.domain.repository

import com.example.chat.domain.model.ChatPreview
import com.example.chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getUserChats(currentUserId: String): List<ChatPreview>

    fun getMessage(chatId: String): Flow<List<Message>>

    suspend fun sendMessage(chatId: String, message: Message)

    suspend fun createChatIfNotExists(currentUserId: String, otherUserId: String): String
}