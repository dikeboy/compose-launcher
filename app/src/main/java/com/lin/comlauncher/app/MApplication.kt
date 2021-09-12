package com.lin.comlauncher.app

import android.app.Application
import android.util.Log

class MApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Log.e("linlog","initTime = ${System.currentTimeMillis()%10000}")
    }
}