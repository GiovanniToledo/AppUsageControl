package com.example.controlappsusage

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log

class MyJobService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        // This method is called when the job starts
        Log.d("MyJobService", "Job started!")

        // Simulate some background work
        Thread {
            try {
                Handler(Looper.getMainLooper()).post {
                    val intent = Intent(this, StartsUsageServiceActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            // Notify that the job is finished
            jobFinished(params, false)
        }.start()

        // Return true if the job is still running
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // This method is called if the job is cancelled before it finishes
        Log.d("MyJobService", "Job cancelled!")
        return false
    }

    // Function to start an activity from the service
    private fun startMyActivity(context: Context) {
        val activityIntent = Intent(context, FullscreenLockActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(activityIntent)
    }
}
