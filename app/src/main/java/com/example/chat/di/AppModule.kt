package com.example.chat.di

import com.example.chat.data.repository.AuthRepositoryImpl
import com.example.chat.data.repository.ChatRepositoryImpl
import com.example.chat.data.repository.StorageRepositoryImpl
import com.example.chat.domain.repository.AuthRepository
import com.example.chat.domain.repository.ChatRepository
import com.example.chat.domain.repository.StorageRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Provides
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule{
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideDatabaseReference(): DatabaseReference = Firebase.database.reference

    @Provides
    @Singleton
    fun providesAuthRepository(
        auth: FirebaseAuth,
        database: DatabaseReference
    ): AuthRepository = AuthRepositoryImpl(auth, database)

    @Provides
    @Singleton
    fun provideChatRepository(
        database: DatabaseReference
    ): ChatRepository = ChatRepositoryImpl(database)

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Provides
    @Singleton
    fun provideStorageRepository(
        storage: FirebaseStorage
    ): StorageRepository = StorageRepositoryImpl(storage)

}