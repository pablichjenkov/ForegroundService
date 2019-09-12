package com.adc.bgprocess

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class FgReceiver : BroadcastReceiver() {

    override fun onReceive(broadcasterContext: Context?, intent: Intent?) {

        Logger.log("========== FgReceiver::onReceive() ==========")

        with(BgApplication.instance) {

            // If our app is not in the foreground launch the mainActivity
            if (! appLifecycleMonitor.isForeground) {

                val newActivityIntent = Intent(this, MainActivity::class.java)

                newActivityIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(newActivityIntent)

            } else {

                // TODO: Dispatch this event to any Activity subscribed. So the Activity will
                // propagate to persenters and they will decide what to do with the event.

            }

        }

    }

}
