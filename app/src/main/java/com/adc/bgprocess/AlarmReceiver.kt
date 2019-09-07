package com.adc.bgprocess

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(broadcasterContext: Context?, intent: Intent?) {

        Logger.log("========== AlarmReceiver::onReceive() ==========")

        val context = broadcasterContext ?: BgApplication.instance.applicationContext

        with(BgApplication.instance) {

            postNotification(
                    PeriodicAlarm.NOTIFICATION_ID_ALARM_BROADCASTER,
                    newNotification(
                            context,
                            android.R.drawable.ic_btn_speak_now,
                            "Alarm Broadcaster"
                    )
            )

        }

        PeriodicAlarm.instance().startAlarm(context, PeriodicAlarm.NOTIFICATION_POST_INTERVAL_MILLIS)

    }

    fun newNotification(
            broadcasterContext: Context,
            @DrawableRes iconResId: Int,
            title: String): Notification {

        // Version 26 and Up requires a channel to post a notification
        if (Build.VERSION.SDK_INT >= 26) {

            val notificationManager
                    = broadcasterContext
                    .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            var notificationChannel
                    = notificationManager
                    .getNotificationChannel(MainActivity.CHANNEL_ID_TEST_NOTIFICATION)

            if (notificationChannel == null) {

                notificationChannel = NotificationChannel(
                        MainActivity.CHANNEL_ID_TEST_NOTIFICATION,
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

        return NotificationCompat.Builder(broadcasterContext, MainActivity.CHANNEL_ID_TEST_NOTIFICATION)
                .setSmallIcon(iconResId)
                .setStyle(NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(notificationText))
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .build()

    }

}