package com.adc.bgprocess

import android.app.AlarmManager
import android.os.Build
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock


class PeriodicAlarm {

    private var pendingIntent: PendingIntent? = null

    fun startAlarm(context: Context, millis: Long) {

        Logger.log("========== PeriodicAlarm::startAlarm() ==========")

        val alarmIntent
                = Intent(context, AlarmReceiver::class.java)
                .apply {
                    action = PERIODIC_ALARM_INTENT_ACTION
                }

        val pendingIntent
                = PendingIntent
                .getBroadcast(
                        context,
                        REQUEST_CODE,
                        alarmIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                )

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        manager.cancel(pendingIntent)

        val alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP// AlarmManager.ELAPSED_REALTIME

        val alarmPeriodicTimeMillis = SystemClock.uptimeMillis() + millis

        if (Build.VERSION.SDK_INT >= 23) {

            manager.setExactAndAllowWhileIdle(alarmType, alarmPeriodicTimeMillis, pendingIntent)

        } else if (Build.VERSION.SDK_INT >= 19) {

            manager.setExact(alarmType, alarmPeriodicTimeMillis, pendingIntent)

        } else {

            manager.set(alarmType, alarmPeriodicTimeMillis, pendingIntent)

        }

    }

    fun stop(context: Context) {

        pendingIntent?.let {

            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            manager.cancel(it)

        }

    }


    companion object {

        val PERIODIC_ALARM_INTENT_ACTION = "com.adc.bgprocess.periodicAlarmIntentAction"

        val NOTIFICATION_ID_ALARM_BROADCASTER = 12347

        val NOTIFICATION_POST_INTERVAL_MILLIS = 60*1000L // 1 minute

        private val REQUEST_CODE = 12348

        private val periodicAlarm: PeriodicAlarm? = null

        fun instance(): PeriodicAlarm {

            return when(periodicAlarm) {

                null -> PeriodicAlarm()

                else -> periodicAlarm

            }

        }

    }

}
