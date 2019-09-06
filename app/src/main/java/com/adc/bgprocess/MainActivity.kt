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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {


    private val NOTIFICATION_POST_INTERVAL_MILLIS = 5*1000 // 5 seconds

    private val NOTIFICATION_ID = 12345

    private val CHANNEL_ID_TEST_NOTIFICATION_BG = "testNotificationBgChannelId"

    private var lastNotificationPostTimestamp = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.log("========== MainActivity::onCreate() ==========")

        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.startUiLoopBtn).setOnClickListener {

            BgApplication.instance.startUiWorker {

                checkForPostNotification()

            }

        }

        findViewById<View>(R.id.startBgLoopBtn).setOnClickListener {

            BgApplication.instance.startBgWorker {

                checkForPostNotification()

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

            // TODO: Implement periodic alarm forever to see restrictions
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

    private fun checkForPostNotification() {

        val now = SystemClock.uptimeMillis()

        val elapsedTimeMillis = now - lastNotificationPostTimestamp

        if (elapsedTimeMillis >= NOTIFICATION_POST_INTERVAL_MILLIS) {

            lastNotificationPostTimestamp = now

            BgApplication.instance.postNotification(NOTIFICATION_ID, newNotification())

        }

    }

    fun newNotification(): Notification {

        // Version 26 and Up requires a channel to post a notification
        if (Build.VERSION.SDK_INT >= 26) {

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            var fgServiceNotificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID_TEST_NOTIFICATION_BG)

            if (fgServiceNotificationChannel == null) {

                fgServiceNotificationChannel = NotificationChannel(
                        CHANNEL_ID_TEST_NOTIFICATION_BG,
                        "Channel to post regular notifications to test the BG mode",
                        NotificationManager.IMPORTANCE_DEFAULT
                )

                notificationManager.createNotificationChannel(fgServiceNotificationChannel)

            }

        }

        val pattern = "yyyy-MM-dd HH:mm:ss.SSS"

        val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)

        val date = simpleDateFormat.format(Date())

        val notificationText = """Notification post at:$date  """

        return NotificationCompat.Builder(this, CHANNEL_ID_TEST_NOTIFICATION_BG)
                .setSmallIcon(R.drawable.icon_ship_white)
                .setStyle(NotificationCompat.BigTextStyle()
                        .setBigContentTitle("ADC BG Test Notification")
                        .bigText(notificationText))
                .build()

    }

}
