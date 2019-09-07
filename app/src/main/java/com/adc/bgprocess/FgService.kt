package com.adc.bgprocess

import android.app.*
import android.content.Intent
import android.widget.Toast
import android.content.Context
import android.os.*
import androidx.core.app.NotificationCompat


class FgService : Service() {

    private val CHANNEL_ID_FG_SERVICE = "fgServiceChannelId"

    private val NOTIFICATION_ID = 1

    private val localBinder = LocalBinder()

    private var startCmdCount = 0


    override fun onCreate() {
        super.onCreate()

        Logger.log("========== FgService::onCreate() ==========")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Logger.log("========== FgService::onStartCommand() ==========")

        startCmdCount ++

        postForegroundServiceNotification()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {

        Logger.log("========== FgService::onBind() ==========")

        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {

        Logger.log("========== FgService::onUnbind() ==========")

        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {

        Logger.log("========== FgService::onRebind() ==========")

    }

    override fun onDestroy() {
        super.onDestroy()

        val now = SystemClock.uptimeMillis()

        val milliSecDiff = now - BgApplication.instance.mainActivityLastStopTimestamp

        val secDiff = milliSecDiff / 60*60*1000

        Logger.log("========== BgService::onDestroy() [BG_UPTIME=$secDiff hours; START_CMD_COUNT=$startCmdCount] ==========")

    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        Logger.log("========== FgService::onTaskRemoved() ==========")

        Toast.makeText(
                applicationContext,
                "FgService.onTaskRemoved()",
                Toast.LENGTH_SHORT
        ).show()

    }

    override fun onLowMemory() {

        Logger.log("========== FgService::onLowMemory() ==========")

    }

    inner class LocalBinder : Binder() {
        internal val service: FgService
            get() = this@FgService
    }

    private fun postForegroundServiceNotification() {

        // Version 26 and Up requires a channel to post a notification
        if (Build.VERSION.SDK_INT >= 26) {

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            var fgServiceNotificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID_FG_SERVICE)

            if (fgServiceNotificationChannel == null) {

                fgServiceNotificationChannel = NotificationChannel(
                        CHANNEL_ID_FG_SERVICE,
                        "Foreground Sticky notifications",
                        NotificationManager.IMPORTANCE_LOW
                )

                notificationManager.createNotificationChannel(fgServiceNotificationChannel)

            }

        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_FG_SERVICE)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setStyle(NotificationCompat.BigTextStyle()
                        .setBigContentTitle("Foreground Service")
                        .bigText("Service running non interruptable in the background"))
                .setOngoing(true)
                .build()

        startForeground(NOTIFICATION_ID, notification)

    }

}