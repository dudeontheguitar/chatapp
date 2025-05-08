package com.example.chat.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.domain.model.Message
import com.example.chat.domain.repository.AuthRepository
import com.example.chat.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages // ✅ исправлено

    private var currentChatId: String? = null

    fun loadMessages(chatId: String) {
        currentChatId = chatId
        chatRepository.getMessage(chatId).onEach {
            _messages.value = it
        }.launchIn(viewModelScope)
    }

    fun sendMessage(text: String) {
        val chatId = currentChatId ?: return
        val senderId = authRepository.currentUserId() ?: return

        val message = Message(
            senderId = senderId,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            try {
                chatRepository.sendMessage(chatId, message)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to send message", e)
            }
        }

    }

    fun isMyMessage(senderId: String): Boolean {
        return authRepository.currentUserId() == senderId
    }

}
