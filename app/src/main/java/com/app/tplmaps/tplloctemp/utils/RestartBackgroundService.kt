package com.app.tplmaps.tplloctemp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast

/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 04/10/2023
 */
class RestartBackgroundService: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val intentService = Intent(context, LocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(intentService)
                Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()

            } else {
                context?.startService(intentService)
                Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}