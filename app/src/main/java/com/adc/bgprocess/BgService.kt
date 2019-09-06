package com.adc.bgprocess

import android.app.Service
import android.content.Intent
import android.os.*
import android.widget.Toast


class BgService : Service() {

    private val localBinder = LocalBinder()

    private var startCmdCount = 0


    override fun onCreate() {
        super.onCreate()

        Logger.log("========== BgService::onCreate() ==========")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Logger.log("========== BgService::onStartCommand() ==========")

        startCmdCount ++

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {

        Logger.log("========== BgService::onBind() ==========")

        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {

        Logger.log("========== BgService::onUnbind() ==========")

        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {

        Logger.log("========== BgService::onRebind() ==========")

    }

    override fun onDestroy() {
        super.onDestroy()

        val now = SystemClock.uptimeMillis()

        val milliSecDiff = now - BgApplication.instance.mainActivityLastStopTimestamp

        val secDiff = milliSecDiff / 1000

        Logger.log("""========== BgService::onDestroy() 
            [BG_UPTIME=$secDiff sec; START_CMD_COUNT=$startCmdCount] ==========""")

    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        Logger.log("========== BgService::onTaskRemoved() ==========")

        Toast.makeText(
                applicationContext,
                "BgService::onTaskRemoved() was called",
                Toast.LENGTH_SHORT
        ).show()

    }

    override fun onLowMemory() {

        Logger.log("========== BgService::onLowMemory() ==========")

    }


    inner class LocalBinder : Binder() {
        internal val service: BgService
            get() = this@BgService
    }

}