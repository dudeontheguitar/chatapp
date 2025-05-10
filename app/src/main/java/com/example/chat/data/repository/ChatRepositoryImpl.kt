package com.example.chat.data.repository

import com.example.chat.domain.model.ChatPreview
import com.example.chat.domain.model.Message
import com.example.chat.domain.repository.ChatRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val db: DatabaseReference
): ChatRepository{
    override suspend fun getUserChats(currentUserId: String): List<ChatPreview> {
        val snapshot = db.child("chats").get().await()
        val chats = mutableListOf<ChatPreview>()

        for (chatSnap in snapshot.children) {
            val chatId = chatSnap.key ?: continue
            val users = chatSnap.child("users").children.mapNotNull { it.key }

            if (currentUserId in users) {
                val otherId = users.firstOrNull { it != currentUserId } ?: continue

                val userSnap = db.child("users").child(otherId).get().await()
                val username = userSnap.child("username").getValue(String::class.java) ?: "Unknown"
                val avatarUrl = userSnap.child("avatarUrl").getValue(String::class.java)
                    ?: "https://firebasestorage.googleapis.com/v0/b/chatapp-cdec1.firebasestorage.app/o/avatar.png?alt=media&token=b45338e4-c787-470d-839a-2ee05530bb17"



                val messagesSnap = chatSnap.child("messages")
                val lastMsg = messagesSnap.children.maxByOrNull {
                    it.child("timestamp").getValue(Long::class.java) ?: 0L
                }?.child("text")?.getValue(String::class.java)

                chats.add(
                    ChatPreview(
                        chatId = chatId,
                        otherUserId = otherId,
                        otherUsername = username,
                        avatarUrl = avatarUrl,
                        lastMessage = lastMsg
                    )
                )
            }
        }

        return chats
    }


    override fun getMessage(chatId: String): Flow<List<Message>> = callbackFlow{
        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(Message::class.java)?.copy(id = it.key ?: "")
                }
                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        db.child("chats").child(chatId).child("messages").addValueEventListener(listener)
        awaitClose{
            db.child("chats").child(chatId).child("messages").removeEventListener(listener)
        }
    }

    override suspend fun sendMessage(chatId: String, message: Message) {
        val ref = db.child("chats").child(chatId).child("messages").push()
        ref.setValue(message.copy(id = ref.key ?: "")).await()
    }

    override suspend fun createChatIfNotExists(currentUserId: String, otherUserId: String): String {
        val snapshot = db.child("chats").get().await()
        for(chat in snapshot.children){
            val users = chat.child("users").children.mapNotNull { it.key }
            if(users.contains(currentUserId) && users.contains(otherUserId)){
                return chat.key ?: ""
            }
        }

        val newChatRef = db.child("chats").push()
        val chatId = newChatRef.key ?: return ""
        val usersMap = mapOf(currentUserId to true, otherUserId to true)
        newChatRef.child("users").setValue(usersMap).await()
        return chatId
    }

}