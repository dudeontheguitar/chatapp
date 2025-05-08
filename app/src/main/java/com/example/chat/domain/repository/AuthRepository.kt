package com.example.chat.domain.repository

interface AuthRepository {
    suspend fun register(email: String, password: String, username: String): Boolean
    suspend fun login(email: String, password: String): Boolean
    fun logout()
    fun currentUserId(): String?
}