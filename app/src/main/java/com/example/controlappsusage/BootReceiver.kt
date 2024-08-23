package com.example.controlappsusage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule the job after a reboot
            val mainActivity = MainActivity()
            mainActivity.scheduleJob()
        }
    }
}
