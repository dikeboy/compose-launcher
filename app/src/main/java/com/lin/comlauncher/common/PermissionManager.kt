package com.lin.comlauncher.common

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


/**
 *
 * @des:     类
 * @auth:         ldh
 * @date:     2020/10/28 3:55 PM
 */
class PermissionManager(var reqCode: Int = 101) {
    var successFunc: (() -> Unit)? = null
    var failFunc: (() -> Unit)? = null
    var reqTime = 0L
    fun checkPermission(activity: Activity, array: Array<String>, reqFunc: (() -> Unit)? = null) {
        var hasPermission = true
        reqTime = System.currentTimeMillis()
        array.forEach {
            val permission = ContextCompat.checkSelfPermission(
                activity, it
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false
                return@forEach
            }
        }
        if (!hasPermission) {
            successFunc = reqFunc
            activity.requestPermissions(array, reqCode)
        } else {
            reqFunc?.invoke()
        }
    }

    fun checkPermission(mContext: Context, activity: Fragment, array: Array<String>, reqFunc: (() -> Unit)? = null) {
        var hasPermission = true
        reqTime = System.currentTimeMillis()
        array.forEach {
            val permission = ContextCompat.checkSelfPermission(
                mContext, it
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false
                return@forEach
            }
        }
        if (!hasPermission) {
            successFunc = reqFunc
            activity.requestPermissions(array, reqCode)
        } else {
            reqFunc?.invoke()
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>?,
        grantResults: IntArray
    ): Boolean {
        if (reqCode == requestCode) {
            var hasPermission = true
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false
                    return@forEach
                }
            }
            if (hasPermission)
                successFunc?.invoke()
            else
                failFunc?.invoke()
            if (System.currentTimeMillis() - reqTime <= 300) {
                Log.e("lin", "权限选择不在提醒,请到手机设置里面主动开启权限")
            }
            return true
        }
        return false
    }
}