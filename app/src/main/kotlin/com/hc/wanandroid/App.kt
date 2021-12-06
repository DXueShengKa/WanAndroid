package com.hc.wanandroid

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess


@HiltAndroidApp
class App : Application() {

    companion object {
        private var _app: App? = null

        @JvmStatic
        val app: App
            get() = _app!!
    }

    override fun onCreate() {
        super.onCreate()
        _app = this


//        com.hc.wanandroid.navigation.s4()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e("Application", t.toString())
            Log.e("Application", e.stackTraceToString())
            exitProcess(-1)
        }
    }

}
