package com.lin.comlauncher.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.dp
import com.lin.comlauncher.entity.ApplicationInfo

object SortUtils {
    fun resetPos(list:SnapshotStateList<ApplicationInfo>,app:ApplicationInfo
    ){
        list.forEach {
            if (app == it)
                return@forEach
            it.orignX = it.posX
            it.orignY = it.posY
        }
        run outside@{
            var preY = LauncherConfig.DEFAULT_TOP_PADDING
            list.forEach {
                if(app==it)
                    return@forEach
                if(app.posY<it.posY){
                    app.posY = preY
                    return@outside
                }
                preY = it.posY
            }
            app.posY = preY
        }

        list.sortWith(comparator = { a1, a2->
            if(a1.posY==a2.posY){
              a1.posX-a2.posX
            }else{
                a1.posY-a2.posY
            }
        })

        list.forEachIndexed { index, ai ->
            ai.orignX = (index%4)*ai.width
            ai.orignY = index/4*LauncherConfig.HOME_CELL_HEIGHT+LauncherConfig.DEFAULT_TOP_PADDING
            ai.needMoveX = ai.posX-ai.orignX
            ai.needMoveY = ai.posY-ai.orignY
//            LogUtils.e("pos=${ai.posX} posY=${ai.posY} desx=${ai.orignX} desy=${ai.orignY} name=${ai.name}")
        }
    }
}

