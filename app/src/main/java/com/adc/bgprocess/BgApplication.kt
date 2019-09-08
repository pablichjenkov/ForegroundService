package com.adc.bgprocess

import android.app.Application
import android.app.Notification
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.PowerManager
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
                TimeUnit.SECONDS.toMillis(30)
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
                TimeUnit.SECONDS.toMillis(30)
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

    fun getNotificationData(): String {

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

        /*
        val wl = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "com.adc.bgprocess:bgApplication")


        wl.acquire()
        ..screen will stay on during this section..
        wl.release()
        */

        val batterySaverInfo = if (Build.VERSION.SDK_INT >= 21) {

            "isPowerSaveMode(Battery Saver) = ${powerManager.isPowerSaveMode} \n"

        } else "isPowerSaveMode(Battery Saver) = N/A \n"

        val dozeInfo = if (Build.VERSION.SDK_INT >= 23) {

            "isDeviceIdleMode(Doze/StandBy) = ${powerManager.isDeviceIdleMode} \n"

        } else "isDeviceIdleMode(Doze/StandBy) = N/A \n"

        val batteryIgnoreOptInfo = if (Build.VERSION.SDK_INT >= 23) {

            "isIgnoringBatteryOptimizations = ${powerManager.isIgnoringBatteryOptimizations(packageName)} \n"

        } else "isIgnoringBatteryOptimizations = N/A \n"


        return dozeInfo + batterySaverInfo + batteryIgnoreOptInfo + getDataSaverInfo()

    }

    private fun getDataSaverInfo(): String {

        var result = "N/A"

        if (Build.VERSION.SDK_INT >= 24) {

            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

            connectivityManager?.apply {

                when (restrictBackgroundStatus) {
                    ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED -> {
                        // Background data usage is blocked for this app. Wherever possible,
                        // the app should also use less data in the foreground.
                        result = "RESTRICT_BACKGROUND_STATUS_ENABLED"
                    }
                    ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED -> {
                        // The app is whitelisted. Wherever possible,
                        // the app should use less data in the foreground and background.
                        result = "RESTRICT_BACKGROUND_STATUS_WHITELISTED"
                    }
                    ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED -> {
                        // Data Saver is disabled. Since the device is connected to a
                        // metered network, the app should use less data wherever possible.
                        result = "RESTRICT_BACKGROUND_STATUS_DISABLED"
                    }
                }

            }

        }

        return "Data Saver = $result \n"
    }


    companion object {

        lateinit var instance: BgApplication

    }

}
