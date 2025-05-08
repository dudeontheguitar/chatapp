package com.example.chat.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    var isLoading by mutableStateOf(false); private set
    var isSuccess by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set
    var currentUserId by mutableStateOf<String?>(null); private set

    init {
        currentUserId = repository.currentUserId()
    }

    fun login(email: String, password: String) {
        updateLoading(true)
        viewModelScope.launch {
            val result = repository.login(email, password)
            updateLoading(false)
            isSuccess = result
            if (result) {
                currentUserId = repository.currentUserId()
            } else {
                errorMessage = "Login failed"
            }
        }
    }

    fun register(email: String, password: String, username: String) {
        updateLoading(true)
        viewModelScope.launch {
            val result = repository.register(email, password, username)
            updateLoading(false)
            isSuccess = result
            if (result) {
                currentUserId = repository.currentUserId()
            } else {
                errorMessage = "Registration failed"
            }
        }
    }


    fun logout() {
        repository.logout()
        currentUserId = null
        isSuccess = false
    }

    fun currentUser() = currentUserId

    private fun updateLoading(value: Boolean) {
        isLoading = value
    }

    fun resetState() {
        isSuccess = false
        errorMessage = null
        isLoading = false
    }

}
