package com.lin.comlauncher.util

import android.graphics.drawable.BitmapDrawable

import android.graphics.Bitmap

import android.app.WallpaperManager
import android.content.Context
import com.lin.comlauncher.entity.ApplicationInfo
import androidx.core.content.ContextCompat.startActivity

import android.content.ComponentName

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.lin.comlauncher.entity.CellBean
import java.lang.Exception
import android.view.Display

import android.view.WindowManager
import com.lin.comlauncher.entity.AppOrignBean


object LauncherUtils {
    var TOOL_BAR_NAME =
        arrayListOf<String>("com.android.contacts", "com.android.camera", "com.android.mms", "com.android.browser")

    fun getCurrentWallPaper(mContext: Context): Bitmap {
        val wallpaperManager = WallpaperManager
                .getInstance(mContext)
        val wallpaperDrawable = wallpaperManager.drawable
        val bm = (wallpaperDrawable as BitmapDrawable).bitmap
        return bm;
    }

    fun startApp(context: Context, app: ApplicationInfo) {
        try {
            if (app.pageName == LauncherConfig.APP_TYPE_FUNCTION) {
                return;
            }
            val intent = Intent()
            intent.component = ComponentName(app.pageName!!, app.activityName!!)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    fun goAppDetail(context: Context, app: ApplicationInfo) {
        try {
            val intent = Intent()
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", app.pageName, null))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun addFoldToCurrentPage(list: ArrayList<ApplicationInfo>, currentPage: Int) {
        if (list.size >= LauncherConfig.HOME_PAGE_CELL_MAX_NUM) {
            return;
        }
        var pos = list.size;
        var appInfo = ApplicationInfo()
        appInfo.name = "文件夹"
        appInfo.appType = LauncherConfig.CELL_TYPE_FOLD
        appInfo.position = LauncherConfig.POSITION_TOOLBAR
        appInfo.width = LauncherConfig.HOME_CELL_WIDTH
        appInfo.height = LauncherConfig.HOME_CELL_HEIGHT
        appInfo.iconWidth = LauncherConfig.CELL_ICON_WIDTH
        appInfo.iconHeight = LauncherConfig.CELL_ICON_WIDTH
        appInfo.posX = LauncherConfig.HOME_DEFAULT_PADDING_LEFT + (pos % 4) * LauncherConfig.HOME_CELL_WIDTH
        appInfo.posY =
            pos / 4 * LauncherConfig.HOME_CELL_HEIGHT + LauncherConfig.DEFAULT_TOP_PADDING
        appInfo.cellPos = pos
        appInfo.pagePos = currentPage
        list.add(appInfo);
    }

    fun goAppDelete(context: Context, app: ApplicationInfo) {
        try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.setData(Uri.fromParts("package", app.pageName, null))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    fun vibrator(context: Context) {
        (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)?.let {
            it.vibrate(70)
        }
    }

    fun isToolBarApplication(packageName: String?): Boolean {
        return TOOL_BAR_NAME.contains(packageName)
    }

    fun findCurrentCell(posX: Int, posY: Int): CellBean? {
        if (posY < LauncherConfig.DEFAULT_TOP_PADDING) {
            return null
        }
        var cellX = posX / LauncherConfig.HOME_CELL_WIDTH
        var cellY = (posY - LauncherConfig.DEFAULT_TOP_PADDING) / LauncherConfig.HOME_CELL_HEIGHT
        return CellBean(cellX, cellY)
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

    fun changeFoldPosition(list: ArrayList<ApplicationInfo>) {
        list.forEachIndexed { i, appInfo ->
            appInfo.posX = i % 4 * appInfo.width
            appInfo.posY = i / 4 * appInfo.height + 20
        }

    }

    fun createFoldIcon(ai: ApplicationInfo) {
        var imageWidth = DisplayUtils.dpToPx(LauncherConfig.CELL_ICON_WIDTH)
        var padding = imageWidth / 4 / 4
        var childWidth = imageWidth / 4
        if (ai.childs.isNotEmpty()) {
            var bmp = Bitmap.createBitmap(
                imageWidth,
                imageWidth, Bitmap.Config.ARGB_8888
            )
            var canvas = Canvas(bmp)
            var paint = Paint()
            paint.isAntiAlias = true
            ai.childs.forEachIndexed { index, achild ->
                if (index >= 9)
                    return@forEachIndexed
                achild.icon?.let { icon ->
                    var childIcon = getRounderBitmap(icon, DisplayUtils.dpToPx(8).toFloat());
                    var px = padding + (childWidth + padding) * (index % 3)
                    var py = padding + (childWidth + padding) * (index / 3)
                    canvas.drawBitmap(
                        childIcon, Rect(0, 0, icon.width, icon.height),
                        Rect(px, py, childWidth + px, childWidth + py), paint
                    )
                }

            }
            ai.icon = bmp
        }
    }

    fun getRounderBitmap(oldBmp: Bitmap, rounder: Float): Bitmap {
        var bmp = Bitmap.createBitmap(
            oldBmp.width,
            oldBmp.height, Bitmap.Config.ARGB_8888
        )
        var canvas = Canvas(bmp)
        var paint = Paint()
        var rect = Rect(0, 0, oldBmp.width, oldBmp.height)
        canvas.drawRoundRect(RectF(rect), rounder, rounder, paint)
        paint.isAntiAlias = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(
            oldBmp, Rect(0, 0, oldBmp.width, oldBmp.height), Rect(0, 0, oldBmp.width, oldBmp.height), paint
        )
        return bmp;
    }

}