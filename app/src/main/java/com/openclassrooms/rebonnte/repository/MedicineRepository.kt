package com.openclassrooms.rebonnte.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MedicineRepository @Inject constructor(
    private val firestore: FirebaseFirestore
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
            .whereEqualTo("name", name)
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
}