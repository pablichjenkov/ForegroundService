package com.ncl.bgservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast


class BgService : Service() {

    private val TAG = "BgService"
    private val localBinder = LocalBinder()


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BgService.onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "BgService.onStartCommand()")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "BgService.onBind()")
        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "BgService.onUnbind()")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "BgService.onRebind()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "BgService.onDestroy()")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "BgService.onTaskRemoved()")
        Toast.makeText(applicationContext, "BgService.onTaskRemoved()", Toast.LENGTH_SHORT).show()
    }


    inner class LocalBinder : Binder() {
        internal val service: BgService
            get() = this@BgService
    }

}