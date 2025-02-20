package com.openclassrooms.rebonnte.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject


class AisleRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage

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

    suspend fun getAislesFilteredByName(name: String): List<Aisle> {
        val snapshot = firestore.collection("aisles")
            .whereGreaterThanOrEqualTo("name", name)
            .whereLessThanOrEqualTo("name", name + '\uf8ff')
            .get()
            .await()
        return snapshot.toObjects(Aisle::class.java)
    }
    suspend fun updateAisle(aisle: Aisle) {
        try {
            firestore.collection("aisles")
                .document(aisle.id)
                .set(aisle, com.google.firebase.firestore.SetOptions.merge())
                .await()
        } catch (e: Exception) {
        }
    }

    suspend fun uploadImageToFirestore(imageUri: Uri, aisleId: String): String {
        return try {
            val storageRef = storage.reference.child("aisle_maps/${UUID.randomUUID()}.jpg")

            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            firestore.collection("aisles")
                .document(aisleId)
                .set(mapOf("mapUrl" to downloadUrl), com.google.firebase.firestore.SetOptions.merge())
                .await()


            return downloadUrl
        } catch (e: Exception) {
            ""
        }
    }


}