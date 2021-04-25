package com.example.smartnote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlertReceiver: BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    val serviceIntent = Intent(context, UploadService::class.java)
    context.let { it1 ->
      if (it1 != null) {
        ContextCompat.startForegroundService(it1, serviceIntent)
      }
    }
    context?.startService(serviceIntent)
  }
}
