package com.lin.comlauncher.util

import android.graphics.drawable.BitmapDrawable

import android.graphics.Bitmap

import android.app.WallpaperManager
import android.content.Context
import com.lin.comlauncher.entity.ApplicationInfo
import androidx.core.content.ContextCompat.startActivity

import android.content.ComponentName

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.lang.Exception


object LauncherUtils {
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
}