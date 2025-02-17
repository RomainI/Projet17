package com.openclassrooms.rebonnte.utils


import android.app.Activity
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.openclassrooms.rebonnte.R

object AuthUtils {
    private const val SIGN_IN_REQUEST_CODE = 1234

    fun startFirebaseUIAuth(activity: Activity) {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false, true)
            .setTheme(R.style.FirebaseLoginTheme)
            .setLogo(R.drawable.logomed)
            .build()

        activity.startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE)
    }
}