package com.adc.bgprocess

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var startBgServiceBtn : Button
    private lateinit var startFgServiceBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(BgApplication.TAG, "========== MainActivity::onCreate() ==========")

        setContentView(R.layout.activity_main)

        startBgServiceBtn = findViewById(R.id.startBgServiceBtn)

        startBgServiceBtn.setOnClickListener{ view: View ->

            val bgServiceIntent = Intent(this@MainActivity, BgServiceTask::class.java)

            startService(bgServiceIntent)
        }

        startFgServiceBtn = findViewById(R.id.startFgServiceBtn)

        startFgServiceBtn.setOnClickListener{ view: View ->

            val fgServiceIntent = Intent(this@MainActivity, FgServiceTask::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(fgServiceIntent)
            }
            else {

                Log.d(
                        BgApplication.TAG,
                        "========== SDK < 26, starting FgService as normal service ==========")

                startService(fgServiceIntent)

                Toast.makeText(
                        this@MainActivity,
                        "Context::startForegroundService() doesn't exist, starting regular Service",
                        Toast.LENGTH_SHORT
                ).show()

            }

        }

    }

    override fun onStart() {
        super.onStart()

        Log.d(BgApplication.TAG, "========== MainActivity::onStart() ==========")

    }

    override fun onStop() {
        super.onStop()

        Log.d(BgApplication.TAG, "========== MainActivity::onStop() ==========")

        BgApplication.instance.mainActivityLastStopTimestamp = SystemClock.elapsedRealtime()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(BgApplication.TAG, "========== MainActivity::onDestroy() ==========")

    }

}
