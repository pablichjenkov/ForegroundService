package com.adc.bgprocess

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val NOTIFICATION_POST_INTERVAL_MILLIS = 60*1000 // 1 minute

    private val NOTIFICATION_ID_UI_EXECUTOR = 12345

    private val NOTIFICATION_ID_BG_EXECUTOR = 12346

    private var lastUINotificationPostTimestamp = 0L

    private var lastBGNotificationPostTimestamp = 0L

    companion object {

        const val CHANNEL_ID_TEST_NOTIFICATION = "testNotificationChannelId"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.log("========== MainActivity::onCreate() ==========")

        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.startUiLoopBtn).setOnClickListener {

            BgApplication.instance.startUiWorker {

                checkForPostUINotification()

            }

        }

        findViewById<View>(R.id.startBgLoopBtn).setOnClickListener {

            BgApplication.instance.startBgWorker {

                checkForPostBGNotification()

            }

        }

        findViewById<View>(R.id.startBgServiceBtn).setOnClickListener {

            val bgServiceIntent = Intent(this@MainActivity, BgService::class.java)

            startService(bgServiceIntent)
        }


        findViewById<View>(R.id.startFgServiceBtn).setOnClickListener {

            val fgServiceIntent = Intent(this@MainActivity, FgService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                startForegroundService(fgServiceIntent)

            } else {

                Logger.log("========== SDK < 26, starting FgService as normal service ==========")

                startService(fgServiceIntent)

                Toast.makeText(
                        this@MainActivity,
                        "Context::startForegroundService() doesn't exist, starting regular Service",
                        Toast.LENGTH_SHORT
                ).show()

            }

        }

        findViewById<View>(R.id.startAlarmTaskBtn).setOnClickListener {

            BgApplication.instance.startAlarm()

        }

    }

    override fun onStart() {
        super.onStart()

        Logger.log("========== MainActivity::onStart() ==========")

    }

    override fun onStop() {
        super.onStop()

        Logger.log("========== MainActivity::onStop() ==========")

        BgApplication.instance.mainActivityLastStopTimestamp = SystemClock.elapsedRealtime()
    }

    override fun onDestroy() {
        super.onDestroy()

        Logger.log("========== MainActivity::onDestroy() ==========")

    }

    private fun checkForPostUINotification() {

        val now = SystemClock.uptimeMillis()

        val elapsedTimeMillis = now - lastUINotificationPostTimestamp

        if (elapsedTimeMillis >= NOTIFICATION_POST_INTERVAL_MILLIS) {

            lastUINotificationPostTimestamp = now

            BgApplication.instance.postNotification(
                    NOTIFICATION_ID_UI_EXECUTOR,
                    newNotification(android.R.drawable.ic_dialog_info, "UI Executor")
            )

        }

    }

    private fun checkForPostBGNotification() {

        val now = SystemClock.uptimeMillis()

        val elapsedTimeMillis = now - lastBGNotificationPostTimestamp

        if (elapsedTimeMillis >= NOTIFICATION_POST_INTERVAL_MILLIS) {

            lastBGNotificationPostTimestamp = now

            BgApplication.instance.postNotification(
                    NOTIFICATION_ID_BG_EXECUTOR,
                    newNotification(android.R.drawable.star_on, "ThreadPool Executor")
            )

        }

    }

    fun newNotification(@DrawableRes iconResId: Int, title: String): Notification {

        // Version 26 and Up requires a channel to post a notification
        if (Build.VERSION.SDK_INT >= 26) {

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            var notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID_TEST_NOTIFICATION)

            if (notificationChannel == null) {

                notificationChannel = NotificationChannel(
                        CHANNEL_ID_TEST_NOTIFICATION,
                        "Channel to post regular notifications to test the BG mode",
                        NotificationManager.IMPORTANCE_DEFAULT
                )

                notificationChannel.setSound(null, null)

                notificationManager.createNotificationChannel(notificationChannel)

            }

        }

        val pattern = "yyyy-MM-dd HH:mm:ss.SSS"

        val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)

        val date = simpleDateFormat.format(Date())

        val notificationText = """Notification post at:$date  """

        val deviceData = BgApplication.instance.getNotificationData()

        return NotificationCompat.Builder(this, CHANNEL_ID_TEST_NOTIFICATION)
                .setSmallIcon(iconResId)
                .setStyle(NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(notificationText + "\n" + deviceData))
                .build()

    }

}
