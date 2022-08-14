package com.lin.comlauncher.entity

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImagePainter

class ApplicationInfo(
    var name:String?=null,
    var pageName:String?=null,
    var activityName:String?=null,
    var icon: Bitmap?=null,
    var posX:Int = 0,
    var posY:Int = 0,
    var width:Int = 0,
    var height:Int = 0,
    var isDrag:Boolean = false,
    var orignX:Int = 0,
    var orignY:Int = 0,
    var needMoveX:Int = 0,
    var needMoveY:Int = 0,
    var posFx:Float = 0f,
    var posFy:Float = 0f,
    var cellPos:Int = 0,
    var isAnimi:Boolean =false,
    var position:Int = 0,
    var iconWidth:Int = 0,
    var iconHeight:Int = 0,
    var dragInfo:ApplicationInfo? =null,
    var showText:Boolean = true,
    var imageBitmap: AsyncImagePainter? =null
)

class AppInfoBaseBean(
    var homeList:ArrayList<ArrayList<ApplicationInfo>> = ArrayList(),
    var toobarList:ArrayList<ApplicationInfo> = ArrayList()
)

data class AppPos(
    var x:Int = 0,
    var y:Int = 0,
    var appName:String?=null
)

data class CellBean(
    var x:Int = 0,
    var y:Int = 0,
    var page:Int = 0
)
