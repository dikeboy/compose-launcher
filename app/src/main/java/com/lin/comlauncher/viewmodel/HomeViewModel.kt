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
import androidx.lifecycle.*
import com.lin.comlauncher.util.DisplayUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeViewModel:ViewModel() {
    public var loadInfoLiveData:MutableLiveData<List<List<ApplicationInfo>>> = MutableLiveData()
    val appLiveData: LiveData<List<List<ApplicationInfo>>> = loadInfoLiveData

    fun loadApp(pm:PackageManager,width:Int,height:Int){
        viewModelScope.launch(Dispatchers.IO){
            var dpWidth = DisplayUtils.pxToDp(width)
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            var mlist = ArrayList<ArrayList<ApplicationInfo>>();
            var cacheList = ArrayList<ApplicationInfo>()

            var findSet = HashSet<String>()
            var index = 0;
            pm.queryIntentActivities(intent, 0)?.forEach continuing@{resolveInfo->
                if(findSet.contains(resolveInfo.activityInfo.packageName))
                    return@continuing
                findSet.add(resolveInfo.activityInfo.packageName)
                index %= 20;
                var ai = ApplicationInfo(name=resolveInfo.loadLabel(pm).toString(),resolveInfo.resolvePackageName)
                var image = resolveInfo.activityInfo.loadIcon(pm)
                Log.e("linlog","package==${resolveInfo.activityInfo.packageName} $image")
                if(image is BitmapDrawable)
                    ai.icon = image?.bitmap
                else {
                    var iWidth = image.intrinsicWidth
                    var iHeight =image.intrinsicHeight
                    if(iWidth<0)
                        iWidth = 1
                    if(iHeight<0)
                        iHeight<1
                    var bmp = Bitmap.createBitmap(iWidth,iHeight,Bitmap.Config.ARGB_8888)
                    var canvas = Canvas(bmp)
                    image.setBounds(0,0,canvas.width,canvas.height)
                    image.draw(canvas)
                    ai.icon = bmp
                }
                ai.width = dpWidth/4;
                ai.height = 100
                ai.posX = (index%4)*dpWidth/4
                ai.posY = index/4*100+100
                ai.activityName = resolveInfo.activityInfo.name
                ai.pageName = resolveInfo.activityInfo.packageName
                cacheList.add(ai)
                if(index==19){
                    cacheList = ArrayList()
                }
                if(index==0){
                    mlist.add(cacheList)
                }
                index++;
            }
            Log.e("linlog","loadA==${mlist.size}")
            loadInfoLiveData.postValue(mlist)
        }
    }
}