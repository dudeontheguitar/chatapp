package com.example.chat.domain.repository

import android.net.Uri

interface StorageRepository {
    suspend fun uploadAvatarImage(uid: String, imageUri: Uri): String
}