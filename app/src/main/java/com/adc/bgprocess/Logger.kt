package com.adc.bgprocess

import android.util.Log
import java.util.ArrayList

object Logger {

    private const val TAG = "ADC_BG_Test"
    private val HOOKS = ArrayList<Hook>()

    fun log(fmt: String, vararg args: Any) {

        if (BuildConfig.DEBUG) {

            Log.d(TAG, String.format(fmt, *args))

            dispatchToHooks(TAG, fmt)
        }

    }

    fun log(msg: String? = null, e: Throwable) {

        if (BuildConfig.DEBUG) {

            Log.d(TAG, msg.orEmpty(), e)

            dispatchToHooksWithException(TAG, msg.orEmpty(), e)

        }

    }

    fun addHook(loggerHook: Hook) {
        if (!HOOKS.contains(loggerHook)) {
            HOOKS.add(loggerHook)
        }
    }

    fun removeHook(loggerHook: Hook) {
        HOOKS.remove(loggerHook)
    }

    private fun dispatchToHooks(tag: String, msg: String) {
        for (hook in HOOKS) {
            hook.log(tag, msg)
        }
    }

    private fun dispatchToHooksWithException(tag: String, msg: String, e: Throwable) {
        for (hook in HOOKS) {
            hook.log(tag, msg, e)
        }
    }

    interface Hook {
        fun log(tag: String, msg: String)
        fun log(tag: String, msg: String, e: Throwable)
    }

}
