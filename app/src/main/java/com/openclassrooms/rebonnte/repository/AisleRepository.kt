package com.openclassrooms.rebonnte.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.model.Aisle
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AisleRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getAllAisles(): List<Aisle> {
        val snapshot = firestore.collection("aisles").get().await()
        val aisles =snapshot.toObjects(Aisle::class.java)
        println("Retrieved Aisles: $aisles")
        return aisles
    }

    suspend fun addAisle(aisle: Aisle) {
        firestore.collection("aisles")
            .document(aisle.name)
            .set(aisle)
            .await()
    }

    suspend fun aisleExists(aisleName: String): Boolean {
        val snapshot = firestore.collection("aisles")
            .whereEqualTo("name", aisleName)
            .get()
            .await()
        return !snapshot.isEmpty
    }
}