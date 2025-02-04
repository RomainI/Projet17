package com.openclassrooms.rebonnte.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.openclassrooms.rebonnte.viewmodel.MainViewModel
import com.openclassrooms.rebonnte.viewmodel.MedicineViewModel
import javax.inject.Inject

class BroadcastReceiverManager @Inject constructor(
    private val context: Context,
) {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("BroadcastManager", "Broadcast received: ${intent?.action}")
            onBroadcastReceived?.invoke()
        }
    }

    private var onBroadcastReceived: (() -> Unit)? = null


    fun setOnBroadcastReceivedListener(listener: () -> Unit) {
        onBroadcastReceived = listener
    }

    fun startReceiver() {
        val filter = IntentFilter().apply {
            addAction("com.rebonnte.ACTION_UPDATE")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(broadcastReceiver, filter)
        }
    }

    fun stopReceiver() {
        context.unregisterReceiver(broadcastReceiver)
    }

    fun sendBroadcast() {
        val intent = Intent("com.rebonnte.ACTION_UPDATE")
        context.sendBroadcast(intent)
    }
}