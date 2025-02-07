package com.openclassrooms.rebonnte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.repository.AisleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AisleViewModel @Inject constructor(private val repository: AisleRepository) : ViewModel() {
    var _aisles = MutableStateFlow<List<Aisle>>(emptyList())
    val aisles: StateFlow<List<Aisle>> get() = _aisles

    private val _aisleAddedStatus = MutableStateFlow<Result<String>?>(null)
    val aisleAddedStatus: StateFlow<Result<String>?> get() = _aisleAddedStatus

    init {
        loadAisles()
    }

//    fun addRandomAisle() {
//        val currentAisles: MutableList<Aisle> = ArrayList(aisles.value)
//        currentAisles.add(Aisle("Aisle " + (currentAisles.size + 1)))
//        _aisles.value = currentAisles
//    }


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
}

