package com.example.chat.presentation.chat

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.domain.model.ChatPreview
import com.example.chat.domain.model.Message
import com.example.chat.domain.repository.AuthRepository
import com.example.chat.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,

) :ViewModel(){

    private val _chatList = MutableStateFlow<List<ChatPreview>>(emptyList())

    val chatList: StateFlow<List<ChatPreview>> = _chatList

    fun loadChats(){
        val currentUserId = authRepository.currentUserId() ?: return
        viewModelScope.launch {
            val chats = chatRepository.getUserChats(currentUserId)
            _chatList.value = chats
        }
    }
}