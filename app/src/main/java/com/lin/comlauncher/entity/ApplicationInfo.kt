package com.lin.comlauncher.entity

import android.graphics.Bitmap
import androidx.compose.ui.unit.Dp

class ApplicationInfo(
    var name:String?=null,
    var pageName:String?=null,
    var activityName:String?=null,
    var icon: Bitmap?=null,
    var posX:Int = 0,
    var posY:Int = 0,
    var width:Int = 0,
    var height:Int = 0,
    var isDrag:Boolean = false
)

class AppInfoBaseBean(
    var homeList:ArrayList<ArrayList<ApplicationInfo>> = ArrayList(),
    var toobarList:ArrayList<ApplicationInfo> = ArrayList()
)