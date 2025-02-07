package com.openclassrooms.rebonnte.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val auth: FirebaseAuth) {

    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun deleteUser(): Boolean {
        val user = auth.currentUser
        return try {
            user?.delete()?.await()
            true
        } catch (e: Exception) {
            false
        }
    }
}