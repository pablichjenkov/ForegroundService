package com.adc.bgprocess

import android.app.Application
import android.app.Notification
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


class BgApplication : Application() {

    var mainActivityLastStopTimestamp: Long = 0

    private val isUiPeriodicRunStarted = AtomicBoolean(false)

    private var uiPeriodicRunnable: PeriodicRunnable? = null

    private val isBgPeriodicRunStarted = AtomicBoolean(false)

    private var bgPeriodicRunnable: PeriodicRunnable? = null

    private val isPeriodicAlarmStarted = AtomicBoolean(false)


    override fun onCreate() {
        super.onCreate()

        instance = this

        Logger.log("BgApplication::onCreate()")

    }

    override fun onLowMemory() {
        super.onLowMemory()

        Logger.log("BgApplication::onLowMemory()")

    }

    override fun onTerminate() {
        super.onTerminate()

        Logger.log("BgApplication::onTerminate()")

    }

    /**
     * Starts a cycle of periodic block code execution in the main UI Thread.
     * It has the Application Scope so it will only be killed when the Application object gets
     * destroyed, unless we stop it explicitly calling {@link stopUiWorker()}
     */
    fun startUiWorker(block: () -> Unit) {

        if (isUiPeriodicRunStarted.getAndSet(true)) {
            return
        }

        uiPeriodicRunnable = PeriodicRunnable(
                MainScheduledExecutor.instance,
                block,
                TimeUnit.SECONDS.toMillis(2)
        ).also { it.start() }

    }

    /**
     * Stops the periodic execution cycle. The idea is not using it in the test so we can see how
     * far our periodic execution can get.
     */
    fun stopUiWorker() { uiPeriodicRunnable?.stop() }

    /**
     * Starts a cycle of periodic block code execution in a ThreadPool Executor.
     * It has the Application Scope so it will only be killed when the Application object gets
     * destroyed, unless we stop it explicitly calling {@link stopBgWorker()}
     */
    fun startBgWorker(block: () -> Unit) {

        if (isBgPeriodicRunStarted.getAndSet(true)) {
            return
        }

        bgPeriodicRunnable = PeriodicRunnable(
                BgScheduledExecutor.instance,
                block,
                TimeUnit.SECONDS.toMillis(2)
        ).also { it.start() }

    }

    /**
     * Stops the periodic execution cycle. The idea is not using it in the test so we can see how
     * far our periodic execution can get.
     */
    fun stopBgWorker() { bgPeriodicRunnable?.stop() }

    fun startAlarm() {

        if (isPeriodicAlarmStarted.getAndSet(true)) {
            return
        }

        PeriodicAlarm.instance().startAlarm(this, PeriodicAlarm.NOTIFICATION_POST_INTERVAL_MILLIS)

    }

    fun stopAlarm() {

        PeriodicAlarm.instance().stop(this)

    }

    fun postNotification(notificationId: Int, notification: Notification) {

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, notification)
        }

    }


    companion object {

        lateinit var instance: BgApplication

    }

}
