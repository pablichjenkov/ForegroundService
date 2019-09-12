package com.adc.bgprocess

interface AppLifecycleListener {
    fun onAppOpened()
    fun onAppGotoForeground()
    fun onAppGotoBackground()
    fun onAppClosed()
}