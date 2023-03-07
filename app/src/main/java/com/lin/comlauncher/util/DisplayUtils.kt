package com.lin.comlauncher.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import com.gyf.immersionbar.ImmersionBar

object DisplayUtils {
    fun dpToPx(dp:Int):Int{
        return (dp * Resources.getSystem().displayMetrics.density).toInt();

    }
    fun pxToDp(px:Int):Int{
        return  (px / Resources.getSystem().displayMetrics.density).toInt();

    }

    fun getRealHeight(context:Context): Int {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            val dm = DisplayMetrics()
            context.display?.getRealMetrics(dm)
            return dm.heightPixels
        }
        return context.resources.displayMetrics.heightPixels;
    }

    fun getScreenHeightCanUse(context: Context): Int {
        return getRealHeight(context) - getStatusHeight(context) - getNavigationBarHeightIfRoom(context)
    }

    fun getStatusHeight(context: Context): Int {
        var height = 0
        val resourceId = context.applicationContext.resources.getIdentifier(
            "status_bar_height",
            "dimen",
            "android"
        )
        if (resourceId > 0) {
            height = context.applicationContext.resources.getDimensionPixelSize(resourceId)
        }
        return height
    }

    fun getNavigationBarHeightIfRoom(context: Context): Int {
        return if (checkDeviceHasNavigationBar(context)) {
            getNavigationBarHeight(context)
        } else 0
    }

    fun getDisplayHeight(context:Context):Int{
        var totalHeight = getRealHeight(context =context)
        if(checkDeviceHasNavigationBar(context)){
            return totalHeight - getNavigationBarHeight(context)
        }
        return totalHeight
    }

    @SuppressLint("NewApi")
    fun checkDeviceHasNavigationBar(activity: Context?): Boolean {
        val hasMenuKey = ViewConfiguration.get(activity!!)
            .hasPermanentMenuKey()
        val hasBackKey = KeyCharacterMap
            .deviceHasKey(KeyEvent.KEYCODE_BACK)
        return !hasMenuKey && !hasBackKey
    }

    fun getNavigationBarHeight(activity: Context): Int {
        val resources: Resources = activity.resources
        val resourceId: Int = resources.getIdentifier(
            "navigation_bar_height",
            "dimen", "android"
        )
        return resources.getDimensionPixelSize(resourceId)
    }
}
