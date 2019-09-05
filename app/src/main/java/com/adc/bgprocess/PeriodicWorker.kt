package com.adc.bgprocess

import android.os.Handler
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean


class PeriodicWorker(private val handler: Handler, private val delayInMillis: Long) {

    // private var runnable: Runnable? = null

    // To test issue with interface <-> lambda SAM conversion
    // private var runnable: (() -> Unit)? = null

    private var isStarted: AtomicBoolean = AtomicBoolean(false)

    fun start() {

        if (isStarted.getAndSet(true)) {
            return
        }

        val runnable = object: Runnable {

            override fun run() {

                Log.d(BgApplication.TAG, "PeriodicWorker::run() executing")

                handler.postDelayed(this, delayInMillis)

            }

        }

        /*runnable = {

            Log.d(BgApplication.TAG, "PeriodicWorker::run() executing")

            runnable?.let {
                handler.postDelayed(it, delayInMillis)
            }


        }*/

        // Start the initial runnable task by posting through the handler
        handler.post(runnable)

    }

    fun stop() {

        //handler.removeCallbacks(runnable)

        //runnable = null

    }

}