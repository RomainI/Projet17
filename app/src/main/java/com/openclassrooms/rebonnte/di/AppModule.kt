package com.openclassrooms.rebonnte.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.rebonnte.repository.AisleRepository
import com.openclassrooms.rebonnte.repository.AuthRepository
import com.openclassrooms.rebonnte.repository.MedicineRepository
import com.openclassrooms.rebonnte.utils.BroadcastReceiverManager
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMedicineRepository(firestore: FirebaseFirestore, storage :FirebaseStorage): MedicineRepository {
        return MedicineRepository(firestore, storage)
    }

    @Provides
    @Singleton
    fun provideAisleRepository(firestore: FirebaseFirestore, storage :FirebaseStorage): AisleRepository {
        return AisleRepository(firestore, storage)
    }

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun provideBroadcastReceiverManager(@ApplicationContext context: Context): BroadcastReceiverManager {
        return BroadcastReceiverManager(context)
    }


    @Provides
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository(FirebaseAuth.getInstance())
    }
}