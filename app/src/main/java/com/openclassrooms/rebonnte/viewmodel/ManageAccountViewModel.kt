package com.openclassrooms.rebonnte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openclassrooms.rebonnte.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing account used by manage account screen.
 * Handles sign-in status, account deletion, and authentication status.
 */



@HiltViewModel
class ManageAccountViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    private val _isAuthenticated = MutableStateFlow(authRepository.isUserAuthenticated())
    val isAuthenticated: StateFlow<Boolean> get() = _isAuthenticated

    private val _deleteAccountState = MutableStateFlow<Boolean?>(null)
    val deleteAccountState: StateFlow<Boolean?> get() = _deleteAccountState

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _isAuthenticated.value = false
        }
    }

    fun getAccountName() : String?{
            return authRepository.getUserName()

    }

    fun deleteUserAccount() {
        viewModelScope.launch {
            val result = authRepository.deleteUser()
            _deleteAccountState.value = result
            if (result) {
                _isAuthenticated.value = false
            }
        }
    }
}