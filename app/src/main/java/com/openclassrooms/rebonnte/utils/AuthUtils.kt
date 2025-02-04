package com.openclassrooms.rebonnte.utils


import android.app.Activity
import android.content.Intent
import com.firebase.ui.auth.AuthUI

object AuthUtils {
    private const val SIGN_IN_REQUEST_CODE = 1234

    fun startFirebaseUIAuth(activity: Activity) {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()

        activity.startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE)
    }
}