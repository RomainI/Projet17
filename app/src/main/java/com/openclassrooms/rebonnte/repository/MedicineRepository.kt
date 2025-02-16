package com.openclassrooms.rebonnte.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.rebonnte.model.Medicine
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class MedicineRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage

) {

    suspend fun getAllMedicines(): List<Medicine> {
        val snapshot = firestore.collection("medicines").get().await()
        return snapshot.documents.mapNotNull { document ->
            val medicine = document.toObject(Medicine::class.java)
            medicine?.copy(id = document.id) // Associer l'ID Firebase au modèle
        }
    }

    suspend fun addMedicine(medicine: Medicine) {
        firestore.collection("medicines")
            .document(medicine.id)
            .set(medicine)
            .await()
    }

    suspend fun deleteMedicine(id: String) {
        firestore.collection("medicines").document(id).delete().await()
    }

    suspend fun getMedicinesFilteredByName(name: String): List<Medicine> {
        val snapshot = firestore.collection("medicines")
            .whereGreaterThanOrEqualTo("name", name)
            .whereLessThanOrEqualTo("name", name + '\uf8ff')
            .get()
            .await()
        return snapshot.toObjects(Medicine::class.java)
    }

    suspend fun getMedicinesSortedByName(): List<Medicine> {
        val snapshot = firestore.collection("medicines")
            .orderBy("name")
            .get()
            .await()
        return snapshot.toObjects(Medicine::class.java)
    }

    suspend fun updateMedicine(medicine: Medicine) {
        val medicineMap = mapOf(
            "name" to medicine.name,
            "stock" to medicine.stock,
            "nameAisle" to medicine.nameAisle,
            "histories" to medicine.histories.map {
                mapOf(
                    "medicineName" to it.medicineName,
                    "userEmail" to it.userEmail,
                    "date" to it.date,
                    "details" to it.details
                )
            }
        )

        FirebaseFirestore.getInstance()
            .collection("medicines")
            .document(medicine.id)
            .update(medicineMap)
            .await()
    }

    suspend fun uploadImageToFirestore(imageUri: Uri, medicineId: String): String {
        return try {
            val storageRef = storage.reference.child("medicine_images/${UUID.randomUUID()}.jpg")

            // Upload du fichier
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            // Mise à jour du champ photoUrl dans Firestore
            firestore.collection("medicines")
                .document(medicineId)
                .update("photoUrl", downloadUrl)
                .await()

            Log.d("Firebase", "Image uploaded successfully and URL updated in Firestore: $downloadUrl")

            return downloadUrl
        } catch (e: Exception) {
            Log.e("Firebase", "Image upload failed: ${e.message}")
            ""
        }
    }
}