package com.example.controlappsusage

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

class AppUsageService : Service() {

    companion object {
        const val CHANNEL_ID = "AppUsageServiceChannel"

    }

    var count = 0

    private var prefs: SharedPreferences? = null
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): AppUsageService = this@AppUsageService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())
        prefs = this.getSharedPreferences(Constants.MY_PREFS_FILE, MODE_PRIVATE)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start your background task (e.g., detect app usage)
        Thread {
            while (count < 14) { // runs for 7 minutes
                if(Constants.detectForegroundApp(context = this)) {
                    Handler(Looper.getMainLooper()).post {
                        val myIntent = Intent(this, FullscreenLockActivity::class.java)
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(myIntent)
                    }
                }
                Thread.sleep(1 * 30 * 1000)  // Check every 1 minute
            }
        }.start()

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "App Usage Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("App Usage Service")
            .setContentText("Monitoring app usage...")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
    }
}
