package com.lin.comlauncher.util

import android.graphics.drawable.BitmapDrawable

import android.graphics.Bitmap

import android.app.WallpaperManager
import android.content.Context
import com.lin.comlauncher.entity.ApplicationInfo
import androidx.core.content.ContextCompat.startActivity

import android.content.ComponentName

import android.content.Intent
import android.graphics.Point
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.lin.comlauncher.entity.CellBean
import java.lang.Exception
import android.view.Display

import android.view.WindowManager





object LauncherUtils {
    var TOOL_BAR_NAME = arrayListOf<String>("com.android.contacts","com.android.camera","com.android.mms","com.android.browser")
    fun getCurrentWallPaper(mContext: Context):Bitmap{
        val wallpaperManager = WallpaperManager
            .getInstance(mContext)
        val wallpaperDrawable = wallpaperManager.drawable
        val bm = (wallpaperDrawable as BitmapDrawable).bitmap
        return bm;
    }

    fun startApp(context:Context,app:ApplicationInfo){
        try{
            val intent = Intent()
            intent.component =  ComponentName(app.pageName!!, app.activityName!!)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }catch (ex:Exception){
            ex.printStackTrace()
        }

    }
    fun vibrator(context:Context){
        (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)?.let {
            it.vibrate(70)
        }
    }

    fun isToolBarApplication(packageName:String?):Boolean{
       return  TOOL_BAR_NAME.contains(packageName)
    }

    fun findCurrentCell(posX:Int,posY:Int): CellBean?{
        if(posY<LauncherConfig.DEFAULT_TOP_PADDING){
            return null
        }
        var cellX = posX/LauncherConfig.HOME_CELL_WIDTH
        var cellY = (posY-LauncherConfig.DEFAULT_TOP_PADDING)/LauncherConfig.HOME_CELL_HEIGHT
        return CellBean(cellX,cellY)
    }

    fun getScreenWidth3(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = windowManager.defaultDisplay
        val outPoint = Point()
        defaultDisplay.getRealSize(outPoint)
        return outPoint.x
    }

    fun getScreenHeight3(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = windowManager.defaultDisplay
        val outPoint = Point()
        defaultDisplay.getRealSize(outPoint)
        return outPoint.y
    }

}