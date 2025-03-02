package com.openclassrooms.rebonnte.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.AisleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing aisles used by aisles and medicines screens.
 * Handles loading, adding, updating, and filtering medicines with StateFlow and Firebase.
 */

@HiltViewModel
class AisleViewModel @Inject constructor(private val repository: AisleRepository) : ViewModel() {
    var _aisles = MutableStateFlow<List<Aisle>>(emptyList())
    val aisles: StateFlow<List<Aisle>> get() = _aisles

    private val _aisleAddedStatus = MutableStateFlow<Result<String>?>(null)
    val aisleAddedStatus: StateFlow<Result<String>?> get() = _aisleAddedStatus

    init {
        loadAisles()
    }

    private fun loadAisles() {
        viewModelScope.launch {
            try {
                val loadedAisles = repository.getAllAisles()
                _aisles.value = loadedAisles
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun filterByName(name: String) {

        _aisles.value = repository.getAislesFilteredByName(name)
    }

    fun addAisle(aisleName: String) {
        viewModelScope.launch {
            try {
                if (repository.aisleExists(aisleName)) {
                    _aisleAddedStatus.value = Result.failure(Exception("Aisle already exists"))
                } else {
                    repository.addAisle(Aisle(name = aisleName))
                    _aisleAddedStatus.value = Result.success("Aisle added successfully")
                    loadAisles()
                }
            } catch (e: Exception) {
                _aisleAddedStatus.value = Result.failure(e)
            }
        }
    }


    fun uploadImage(imageUri: Uri, aisle: Aisle, onUploadSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val imageUrl = repository.uploadImageToFirestore(imageUri, aisle.id)

                if (imageUrl.isNotEmpty()) {
                    val updatedAisle = aisle.copy(mapUrl = imageUrl)
                    repository.updateAisle(updatedAisle)

                    onUploadSuccess(imageUrl)
                }
            } catch (e: Exception) {
                Log.e("Upload", "Failed to upload image: ${e.message}")
            }
            loadAisles()
        }
    }
}

