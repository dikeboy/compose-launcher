package com.lin.comlauncher.util

import android.content.res.Resources

object DisplayUtils {
    fun dpToPx(dp:Int):Int{
        return (dp * Resources.getSystem().displayMetrics.density).toInt();

    }
    fun pxToDp(px:Int):Int{
        return  (px / Resources.getSystem().displayMetrics.density).toInt();

    }

}