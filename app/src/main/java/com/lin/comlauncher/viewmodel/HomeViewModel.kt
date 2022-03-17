package com.lin.comlauncher.viewmodel

import android.content.pm.PackageManager
import com.lin.comlauncher.entity.ApplicationInfo
import android.content.pm.ResolveInfo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import com.lin.comlauncher.entity.AppInfoBaseBean
import com.lin.comlauncher.util.DisplayUtils
import com.lin.comlauncher.util.LauncherConfig
import com.lin.comlauncher.util.LauncherUtils
import com.lin.comlauncher.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class HomeViewModel:ViewModel() {
    var infoBaseBean = AppInfoBaseBean();

    val channel = Channel<Int>(UNLIMITED)

    var currentVersion = 0;

    var loadInfoLiveData  = MutableLiveData<Int>(currentVersion)

    var appVersionLiveData:LiveData<Int> = loadInfoLiveData


    suspend fun sendData(value:Int){
        channel.send(value)
    }
    var uiState by mutableStateOf<String>("111")
    private set

    fun loadApp(pm:PackageManager,width:Int,height:Int){

        viewModelScope.launch(Dispatchers.IO) {
            var startTime = System.currentTimeMillis();
            var dpWidth = DisplayUtils.pxToDp(width)
            var dpHeight = DisplayUtils.pxToDp(height)
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            var appInfoBaseBean = AppInfoBaseBean()

            var mlist = ArrayList<ArrayList<ApplicationInfo>>();
            var cacheList = ArrayList<ApplicationInfo>()
            var mToolBarList = ArrayList<ApplicationInfo>()

            var findSet = HashSet<String>()
            var index = 0;
            pm.queryIntentActivities(intent, 0)?.forEach continuing@{ resolveInfo ->
                if (findSet.contains(resolveInfo.activityInfo.packageName))
                    return@continuing
                if (index == 10)
                    findSet.add(resolveInfo.activityInfo.packageName)
                index %= 20;
                var ai = ApplicationInfo(
                    name = resolveInfo.loadLabel(pm).toString(),
                    resolveInfo.resolvePackageName
                )
                var image = resolveInfo.activityInfo.loadIcon(pm)
//                Log.e("linlog","package==${resolveInfo.activityInfo.packageName} ${resolveInfo.loadLabel(pm).toString()}")
                if (image is BitmapDrawable)
                    ai.icon = image?.bitmap
                else {
                    var iWidth = image.intrinsicWidth
                    var iHeight = image.intrinsicHeight
                    if (iWidth < 0)
                        iWidth = 1
                    if (iHeight < 0)
                        iHeight < 1
                    var bmp = Bitmap.createBitmap(iWidth, iHeight, Bitmap.Config.ARGB_8888)
                    var canvas = Canvas(bmp)
                    image.setBounds(0, 0, canvas.width, canvas.height)
                    image.draw(canvas)
                    ai.icon = bmp
                }
                ai.activityName = resolveInfo.activityInfo.name
                ai.pageName = resolveInfo.activityInfo.packageName
                LauncherConfig.HOME_TOOLBAR_START = dpHeight - dpWidth / 4;
                ai.iconWidth = LauncherConfig.CELL_ICON_WIDTH;
                ai.iconHeight = LauncherConfig.CELL_ICON_WIDTH;
                if (LauncherUtils.isToolBarApplication(ai.pageName) && mToolBarList.size < 4) {
                    ai.width = dpWidth / 4;
                    ai.height = dpWidth / 4;
                    ai.posY = dpHeight - dpWidth / 4
                    ai.posX = mToolBarList.size % 4 * dpWidth / 4
                    ai.position = LauncherConfig.POSITION_TOOLBAR
                    mToolBarList.add(ai)
                } else {
                    ai.width = dpWidth / 4;
                    ai.height = LauncherConfig.HOME_CELL_HEIGHT
                    ai.posX = (index % 4) * dpWidth / 4
                    ai.posY =
                        index / 4 * LauncherConfig.HOME_CELL_HEIGHT + LauncherConfig.DEFAULT_TOP_PADDING
                    ai.position = LauncherConfig.POSITION_HOME
                    cacheList.add(ai)
                    if (index == 19) {
                        cacheList = ArrayList()
                    }
                    if (index == 0) {
                        mlist.add(cacheList)
                    }
                    ai.cellPos = index
                    index++;
                }
                ai.orignX = ai.posX
                ai.orignY = ai.posY
                LauncherConfig.HOME_CELL_WIDTH = ai.width
            }

            appInfoBaseBean.homeList.clear()
            appInfoBaseBean.homeList.addAll(mlist)
            appInfoBaseBean.toobarList = mToolBarList
            var userTime = System.currentTimeMillis() - startTime;
            Log.e("linlog", "loadA==${mlist.size} toolbar=${mToolBarList.size} time=$userTime")
            infoBaseBean = appInfoBaseBean;
            loadInfoLiveData.postValue(++currentVersion)
        }
    }
}
