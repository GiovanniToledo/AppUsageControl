package com.example.controlappsusage

import android.app.AppOpsManager
import android.app.Service.MODE_PRIVATE
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log

class Constants {
    companion object {
        const val MY_PREFS_FILE = "MY_PREFS_FILE"
        const val FULLSCREEN_SHOWN = "FULLSCREEN_SHOWN"

        fun detectForegroundApp(context: Context): Boolean {

            val prefs = context.getSharedPreferences(MY_PREFS_FILE, MODE_PRIVATE)

            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            val startTime = endTime - 1000 * 10 * 6 // 10 seconds earlier

            val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
            val event = UsageEvents.Event()
            var lastApp: String? = null

            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    lastApp = event.packageName
                }
            }

            if (lastApp != null) {
                Log.d("AppUsageService", "App opened: $lastApp")
                if (lastApp == "com.google.android.youtube" || lastApp == "com.android.chrome") {
                    Log.d("AppUsageService", "Launch Fullscreen: $lastApp")
                    return true
                }
            }
            return false
        }

        // Check if Usage Access permission is granted
        fun isUsageAccessGrantedVer2(context: Context): Boolean {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
            } else {
                appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
            }
            return mode == AppOpsManager.MODE_ALLOWED
        }

        fun isUsageAccessGranted(context: Context): Boolean {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, System.currentTimeMillis())
            return appList.isNotEmpty()
        }

        // Redirect user to the Usage Access settings page
        fun requestUsageAccessPermission(context: Context) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
