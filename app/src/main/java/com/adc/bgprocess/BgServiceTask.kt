package com.adc.bgprocess

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import java.util.concurrent.TimeUnit


class BgServiceTask : Service() {

    private val localBinder = LocalBinder()

    private var startCmdCount = 0

    private lateinit var periodicWorker: PeriodicWorker

    override fun onCreate() {
        super.onCreate()

        Log.d(BgApplication.TAG, "========== BgService::onCreate() ==========")

        periodicWorker = PeriodicWorker(
                Handler(Looper.getMainLooper()),
                TimeUnit.SECONDS.toMillis(2)
        )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(BgApplication.TAG, "========== BgService::onStartCommand() ==========")

        if (startCmdCount == 0) {
            periodicWorker.start()
        }

        startCmdCount ++

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {

        Log.d(BgApplication.TAG, "========== BgService::onBind() ==========")

        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {

        Log.d(BgApplication.TAG, "========== BgService::onUnbind() ==========")

        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {

        Log.d(BgApplication.TAG, "========== BgService::onRebind() ==========")

    }

    override fun onDestroy() {
        super.onDestroy()

        periodicWorker.stop()

        val now = SystemClock.uptimeMillis()

        val milliSecDiff = now - BgApplication.instance.mainActivityLastStopTimestamp

        val secDiff = milliSecDiff / 1000

        Log.d(
                BgApplication.TAG,
                "========== BgService::onDestroy() [BG_UPTIME=$secDiff sec; START_CMD_COUNT=$startCmdCount] ==========")

    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        Log.d(BgApplication.TAG, "========== BgService::onTaskRemoved() ==========")

        Toast.makeText(
                applicationContext,
                "BgService::onTaskRemoved() was called",
                Toast.LENGTH_SHORT
        ).show()

    }

    override fun onLowMemory() {

        Log.d(BgApplication.TAG, "========== BgService::onLowMemory() ==========")

    }


    inner class LocalBinder : Binder() {
        internal val serviceTask: BgServiceTask
            get() = this@BgServiceTask
    }

}