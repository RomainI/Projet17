package com.openclassrooms.rebonnte.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.MedicineRepository
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.History
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import java.util.Random
import javax.inject.Inject

/**
 * ViewModel for managing medicines used by medecine and aisles screens.
 * Handles loading, adding, updating, and filtering medicines with StateFlow and Firebase.
 */


@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val repository: MedicineRepository
) : ViewModel() {


    var _medicines = MutableStateFlow<List<Medicine>>(mutableListOf())
    val medicines: StateFlow<List<Medicine>> get() = _medicines

    init {
        loadMedicines()
    }


    fun loadMedicines() {
        viewModelScope.launch {
            try {
                val loadedMedicines = repository.getAllMedicines()
                _medicines.value = loadedMedicines
                println("Loaded medicines: $loadedMedicines")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshMedicines() {
        viewModelScope.launch {
            try {
                val updatedMedicines = repository.getAllMedicines()
                _medicines.value = updatedMedicines.toMutableList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            try {
                repository.addMedicine(medicine)
                _medicines.update { currentList -> currentList + medicine }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteMedicine(id: String) {
        repository.deleteMedicine(id)
        loadMedicines()
    }



    suspend fun filterByName(name: String) {
        _medicines.value = repository.getMedicinesFilteredByName(name)
    }


    suspend fun sortByName() {
        _medicines.value = repository.getMedicinesSortedByName()
    }

    fun sortByStock() {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.sortWith(Comparator.comparingInt(Medicine::stock))
        _medicines.value = currentMedicines
    }

    fun incrementStock(medicine: Medicine, userEmail: String?) {
        viewModelScope.launch {
            val updatedMedicine = medicine.copy(
                stock = medicine.stock + 1,
                histories = medicine.histories + History(
                    medicineName = medicine.name,
                    userEmail = userEmail ?: "Unknown",
                    date = Date(),
                    details = "Adding 1 to ${medicine.name}"
                )
            )
            updateMedicineInDatabase(updatedMedicine)
            _medicines.update { currentList ->
                currentList.map { if (it.id == updatedMedicine.id) updatedMedicine else it }
            }
        }
    }

    fun decrementStock(medicine: Medicine, userEmail: String?) {
        viewModelScope.launch {
            if (medicine.stock > 0) {
                val updatedMedicine = medicine.copy(
                    stock = medicine.stock - 1,
                    histories = medicine.histories + History(
                        medicineName = medicine.name,
                        userEmail = userEmail ?: "Unknown",
                        date = Date(),
                        details = "Minus 1 from ${medicine.name}"
                    )
                )
                updateMedicineInDatabase(updatedMedicine)
                _medicines.update { currentList ->
                    currentList.map { if (it.id == updatedMedicine.id) updatedMedicine else it }
                }
            }
        }
    }


    private suspend fun updateMedicineInDatabase(updatedMedicine: Medicine) {
        try {
            repository.updateMedicine(updatedMedicine)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun uploadImage(imageUri: Uri, onUploadSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val imageUrl = repository.uploadImageToFirestore(imageUri = imageUri)
                onUploadSuccess(imageUrl)
            } catch (e: Exception) {
                Log.e("Upload", "Failed to upload image: ${e.message}")
            }
        }
    }


    fun updateMedicineImage(
        imageUri: Uri,
        medicine: Medicine,
        onUploadSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val imageUrl = repository.uploadImageToFirestore(imageUri, medicine.id)

                if (imageUrl.isNotEmpty()) {
                    val updatedMedicine = medicine.copy(photoUrl = imageUrl)

                    updateMedicineInDatabase(updatedMedicine)

                    onUploadSuccess(imageUrl)
                }
            } catch (e: Exception) {
            }
        }
    }



}

