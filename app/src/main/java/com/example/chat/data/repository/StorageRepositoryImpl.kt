package com.example.chat.data.repository

import android.net.Uri
import com.example.chat.domain.repository.StorageRepository
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
): StorageRepository {
    override suspend fun uploadAvatarImage(uid: String, imageUri: Uri): String {
        val ref = storage.reference.child("avatars/$uid.jpg")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }
}