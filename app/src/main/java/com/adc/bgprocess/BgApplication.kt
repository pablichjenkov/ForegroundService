package com.adc.bgprocess

import android.app.Application
import android.util.Log

class BgApplication : Application() {


    var mainActivityLastStopTimestamp: Long = 0

    override fun onCreate() {
        super.onCreate()

        instance = this

        Log.d(TAG, "BgApplication::onCreate()")

    }

    override fun onLowMemory() {
        super.onLowMemory()

        Log.d(TAG, "BgApplication::onLowMemory()")

    }

    override fun onTerminate() {
        super.onTerminate()

        Log.d(TAG, "BgApplication::onTerminate()")

    }

    companion object {

        lateinit var instance: BgApplication

        const val TAG = "ADC_BG_Test"

    }

}