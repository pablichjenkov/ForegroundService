package com.ncl.bgservice

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var startBgServiceBtn : Button
    private lateinit var startFgServiceBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBgServiceBtn = findViewById(R.id.startBgServiceBtn)
        startBgServiceBtn.setOnClickListener{ view: View ->

            val intent = Intent(this@MainActivity, BgService::class.java)
            startService(intent)
        }

        startFgServiceBtn = findViewById(R.id.startFgServiceBtn)
        startFgServiceBtn.setOnClickListener{ view: View ->

            val intent = Intent(this@MainActivity, FgService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
            else {
                Toast.makeText(this@MainActivity
                        , "Context.startForegroundService() not exist"
                        ,Toast.LENGTH_SHORT)
                        .show()
            }

        }

    }

}
