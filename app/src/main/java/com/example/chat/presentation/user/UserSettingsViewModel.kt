package com.example.chat.presentation.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat.domain.repository.AuthRepository
import com.example.chat.domain.repository.StorageRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val db: DatabaseReference,
    private val storageRepo: StorageRepository,


): ViewModel() {
    val isUploading = MutableStateFlow(false)

    private val _user = MutableStateFlow<UserProfile?>(null)
    val user: StateFlow<UserProfile?> = _user

    init{
        loadUser()
    }

    private fun loadUser(){
        val uid = authRepository.currentUserId() ?: return
        db.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(UserProfile::class.java)
                _user.value = profile
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun updateUser(username: String?, avatarUrl: String?) {
        val uid = authRepository.currentUserId() ?: return
        val updates = mutableMapOf<String, Any>()

        if (!username.isNullOrBlank()) updates["username"] = username
        if (!avatarUrl.isNullOrBlank()) updates["avatarUrl"] = avatarUrl

        if (updates.isNotEmpty()) {
            db.child("users").child(uid).updateChildren(updates)
        }
    }


    fun logout() = authRepository.logout()

    fun uploadAvatar(imageUri: Uri, onComplete: (String) -> Unit) {
        val uid = authRepository.currentUserId() ?: return
        viewModelScope.launch {
            isUploading.value = true
            val url = storageRepo.uploadAvatarImage(uid, imageUri)
            db.child("users").child(uid).child("avatarUrl").setValue(url)
            isUploading.value = false

            loadUser()

            onComplete(url)
        }
    }

}