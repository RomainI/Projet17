package com.openclassrooms.rebonnte.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository for managing Authentication
 * Used to  sign out and deletion of account and having user data from Firebase Auth
 */

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

    fun getUserName(): String? {
        val user = auth.currentUser
        return try {
            user?.displayName
        } catch (e: Exception) {
            null
        }
    }
}
