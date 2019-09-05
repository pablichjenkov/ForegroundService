package com.adc.bgprocess

import android.app.*
import android.content.Intent
import android.util.Log
import android.widget.Toast
import android.support.v4.app.NotificationCompat
import android.content.Context
import android.os.*


class FgServiceTask : Service() {

    private val CHANNEL_ID = "my_channel_01"

    private val NOTIFICATION_ID = 1

    private val localBinder = LocalBinder()


    override fun onCreate() {
        super.onCreate()

        Log.d(BgApplication.TAG, "========== FgService::onCreate() ==========")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(BgApplication.TAG, "========== FgService::onStartCommand() ==========")

        postForegroundServiceNotification()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {

        Log.d(BgApplication.TAG, "========== FgService::onBind() ==========")

        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {

        Log.d(BgApplication.TAG, "========== FgService::onUnbind() ==========")

        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {

        Log.d(BgApplication.TAG, "========== FgService::onRebind() ==========")

    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(BgApplication.TAG, "========== FgService::onDestroy() ==========")

    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        Log.d(BgApplication.TAG, "========== FgService::onTaskRemoved() ==========")

        Toast.makeText(
                applicationContext,
                "FgService.onTaskRemoved()",
                Toast.LENGTH_SHORT
        ).show()

    }

    override fun onLowMemory() {

        Log.d(BgApplication.TAG, "========== FgService::onLowMemory() ==========")

    }

    inner class LocalBinder : Binder() {
        internal val serviceTask: FgServiceTask
            get() = this@FgServiceTask
    }

    private fun postForegroundServiceNotification() {
        if (Build.VERSION.SDK_INT >= 26) {

            val channel = NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_LOW)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon_ship_white)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .setBigContentTitle("Foreground Service")
                            .bigText("Service running non interruptable in the background"))
                    .build()

            startForeground(NOTIFICATION_ID, notification)

        }
    }

}