package com.openclassrooms.rebonnte.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideMedicineRepository(firestore: FirebaseFirestore): MedicineRepository {
        return MedicineRepository(firestore)
    }

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideBroadcastReceiverManager(@ApplicationContext context: Context): BroadcastReceiverManager {
        return BroadcastReceiverManager(context)
    }
}