package com.lin.comlauncher.util

import android.util.Log

object LogUtils {
    private val TAG = "linlog"
    fun e(value: String) {
        Log.e(TAG, value)
    }
}