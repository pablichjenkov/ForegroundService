package com.ncl.bgservice

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import android.support.v4.app.NotificationCompat
import android.content.Context
import android.os.Build


class FgService : Service() {

    private val CHANNEL_ID = "my_channel_01"
    private val NOTIFICATION_ID = 1
    private val TAG = "FgService"
    private val localBinder = LocalBinder()


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "FgService.onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "FgService.onStartCommand()")
        postForegroundServiceNotification()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "FgService.onBind()")
        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "FgService.onUnbind()")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "FgService.onRebind()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "FgService.onDestroy()")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "FgService.onTaskRemoved()")
        Toast.makeText(applicationContext, "FgService.onTaskRemoved()", Toast.LENGTH_SHORT).show()
    }


    inner class LocalBinder : Binder() {
        internal val service: FgService
            get() = this@FgService
    }

    private fun postForegroundServiceNotification() {
        if (Build.VERSION.SDK_INT >= 26) {

            val channel = NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon_ship_white)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .setBigContentTitle("Norwegian Service")
                            .bigText("This service handles the calls and messages while on " +
                                " Norwegian Cruise Ship. If turned off the Communication Package on" +
                                "the App won't work properly"))
                    .build()

            startForeground(NOTIFICATION_ID, notification)

        }
    }

}