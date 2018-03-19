package com.ahmedabdelmeged.storage

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 *
 * Base class for Services that keep track of the number of active jobs and self-stop when the
 * count is zero.
 */
abstract class MyBaseTaskService : Service() {

    val PROGRESS_NOTIFICATION_ID = 0
    val FINISHED_NOTIFICATION_ID = 1
    private val TAG = "MyBaseTaskService"

    private var mNumTasks = 0

    fun taskStarted() {
        changeNumberOfTasks(1)
    }

    fun taskCompleted() {
        changeNumberOfTasks(-1)
    }

    @Synchronized
    private fun changeNumberOfTasks(delta: Int) {
        Log.d(TAG, "changeNumberOfTasks:$mNumTasks:$delta")
        mNumTasks += delta

        // If there are no tasks left, stop the service
        if (mNumTasks <= 0) {
            Log.d(TAG, "stopping")
            stopSelf()
        }
    }

    /**
     * Show notification with a progress bar.
     */
    protected fun showProgressNotification(caption: String, completedUnits: Long, totalUnits: Long) {
        var percentComplete = 0
        if (totalUnits > 0) {
            percentComplete = (100 * completedUnits / totalUnits).toInt()
        }

        val builder = NotificationCompat.Builder(this, "Firebase")
                .setSmallIcon(R.drawable.ic_file_upload_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(caption)
                .setProgress(100, percentComplete, false)
                .setOngoing(true)
                .setAutoCancel(false)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(PROGRESS_NOTIFICATION_ID, builder.build())
    }

    /**
     * Show notification that the activity finished.
     */
    protected fun showFinishedNotification(caption: String, intent: Intent, success: Boolean) {
        // Make PendingIntent for notification
        val pendingIntent = PendingIntent.getActivity(this, 0 /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val icon = if (success) R.drawable.ic_check_white_24 else R.drawable.ic_error_white_24dp

        val builder = NotificationCompat.Builder(this, "Firebase")
                .setSmallIcon(icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(caption)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(FINISHED_NOTIFICATION_ID, builder.build())
    }

    /**
     * Dismiss the progress notification.
     */
    protected fun dismissProgressNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.cancel(PROGRESS_NOTIFICATION_ID)
    }

}
