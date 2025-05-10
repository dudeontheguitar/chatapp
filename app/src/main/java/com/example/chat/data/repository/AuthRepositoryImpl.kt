package com.example.chat.data.repository

import com.example.chat.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl (
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
) : AuthRepository {

    override suspend fun register(email: String, password: String, username: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val uid = user.uid
                val userMap = mapOf(
                    "uid" to uid,
                    "email" to email,
                    "username" to username,
                    "avatarUrl" to "https://firebasestorage.googleapis.com/v0/b/chatapp-cdec1.firebasestorage.app/o/avatar.png?alt=media&token=b45338e4-c787-470d-839a-2ee05530bb17"
                )
                database.child("users").child(uid).setValue(userMap).await()

                createChatsWithAllUsers(uid)

                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun login(email: String, password: String): Boolean {
        return try{
            auth.signInWithEmailAndPassword(email, password).await()
            true
        }catch (e: Exception){
            false
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun currentUserId(): String? = auth.currentUser?.uid

    private suspend fun createChatsWithAllUsers(newUserId: String) {
        val usersSnapshot = database.child("users").get().await()
        for (userSnap in usersSnapshot.children) {
            val otherUserId = userSnap.key ?: continue
            if (otherUserId == newUserId) continue

            val chatSnapshot = database.child("chats").get().await()
            var chatExists = false

            for (chat in chatSnapshot.children) {
                val users = chat.child("users").children.mapNotNull { it.key }
                if (newUserId in users && otherUserId in users) {
                    chatExists = true
                    break
                }
            }

            if (!chatExists) {
                val newChatRef = database.child("chats").push()
                val chatUsers = mapOf(
                    newUserId to true,
                    otherUserId to true
                )
                newChatRef.child("users").setValue(chatUsers).await()
            }
        }
    }

}