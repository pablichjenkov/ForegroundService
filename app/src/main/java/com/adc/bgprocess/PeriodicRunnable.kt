package com.adc.bgprocess

import java.util.concurrent.atomic.AtomicBoolean


class PeriodicRunnable(
        private val delayExecutor: DelayExecutor,
        private val block: () -> Unit,
        private val delayInMillis: Long
) {

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

                Logger.log("PeriodicRunnable::run() on ${Thread.currentThread().name}")

                block.invoke()

                delayExecutor.postDelayed(this, delayInMillis)

            }

        }

        /*runnable = {

            Log.d(BgApplication.TAG, "PeriodicRunnable::run() executing")

            runnable?.let {
                delayExecutor.postDelayed(it, delayInMillis)
            }


        }*/

        delayExecutor.postNow(runnable)

    }

    fun stop() {

        delayExecutor.shutdown()

        // runnable = null

    }

}