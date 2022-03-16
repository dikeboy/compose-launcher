package com.lin.comlauncher.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.entity.CellBean

object SortUtils {
    fun calculPos(
        list: ArrayList<ApplicationInfo>, app: ApplicationInfo
    ) {
        var currentPos = findCurrentCell(app.posX, app.posY)
        if(app.position==LauncherConfig.POSITION_HOME) {

            if (currentPos < 0)
                currentPos = 0
            else if (currentPos >= list.size)
                currentPos = list.size-1
            var isEmpty = true
            list.forEach {
                if (it.cellPos == currentPos) {
                    isEmpty = false;
                    return@forEach
                }
            }
            if (isEmpty) {
                if (currentPos < 0) {
                    app.orignX = 0
                    app.orignY = 0
                } else if (currentPos >= 20) {
                    app.orignX = 3;
                    app.orignY = 4
                } else {
                    app.orignX = currentPos % 4
                    app.orignY = currentPos / 4
                }
            }
        }
    }

    fun resetChoosePos(
        list: ArrayList<ApplicationInfo>, app: ApplicationInfo
    ) {
        list.forEach {
            if (app == it)
                return@forEach
            it.orignX = it.posX
            it.orignY = it.posY
        }

        var currenPos = findCurrentCell(app.posX, app.posY)
        var prePos = findCurrentCell(app.orignX,app.orignY)
        if(app.position==LauncherConfig.POSITION_HOME){
            if(currenPos==prePos||currenPos<0||prePos<0)
                return
            LogUtils.e("currentPos=$currenPos")
            if (currenPos < 0)
                currenPos = 0
            else if (currenPos >= list.size)
                currenPos = list.size-1

            app.cellPos = currenPos
            var mIndex = 0
            list.sortedBy { it.cellPos }.forEachIndexed { pos, ai ->
                var index =if(ai==app)
                    currenPos
                else if(currenPos<prePos){
                    if(mIndex<currenPos) mIndex else mIndex+1
                }else{
                    if(mIndex>=currenPos) mIndex+1 else mIndex
                }
                LogUtils.e("name=${ai.name} pos=${index} curr=${ai.cellPos}")

                ai.orignX = (index % 4) * ai.width
                ai.orignY =
                    index / 4 * LauncherConfig.HOME_CELL_HEIGHT + LauncherConfig.DEFAULT_TOP_PADDING
                ai.needMoveX = ai.posX - ai.orignX
                ai.needMoveY = ai.posY - ai.orignY
                ai.cellPos = index
                if(ai!=app)
                    mIndex++;
            }
        }else{
            if(currenPos==prePos||currenPos>=0||prePos>=0)
                return
            currenPos = -currenPos-100;
            app.cellPos = currenPos
            var mIndex = 0
            list.sortedBy { it.cellPos }.forEachIndexed { pos, ai ->
                var index =if(ai==app)
                    currenPos
                else if(currenPos<prePos){
                    if(mIndex<currenPos) mIndex else mIndex+1
                }else{
                    if(mIndex>=currenPos) mIndex+1 else mIndex
                }
                LogUtils.e("name=${ai.name} pos=${index} curr=${ai.cellPos}")

                ai.orignX = (index) * ai.width
                ai.orignY = LauncherConfig.HOME_HEIGHT-LauncherConfig.HOME_CELL_WIDTH;
                ai.needMoveX = ai.posX - ai.orignX
                ai.needMoveY = ai.posY - ai.orignY
                ai.cellPos = index
                if(ai!=app)
                    mIndex++;
            }
        }

    }

    fun findCurrentCell(posX: Int, posY: Int): Int {
        var padding = 10
        if (posY < LauncherConfig.DEFAULT_TOP_PADDING-padding) {
            return -1
        }
        if(posY>=LauncherConfig.HOME_TOOLBAR_START-40){
           var pos =  (posX+padding) / LauncherConfig.HOME_CELL_WIDTH
            return -pos - 100;
        }
        if(posY>LauncherConfig.HOME_HEIGHT-LauncherConfig.HOME_WIDTH/4){
            var cellX = (posX+padding) / LauncherConfig.HOME_CELL_WIDTH
            return cellX+100;
        }
        var cellX = (posX+padding) / LauncherConfig.HOME_CELL_WIDTH
        var cellY = (posY - LauncherConfig.DEFAULT_TOP_PADDING+padding) / LauncherConfig.HOME_CELL_HEIGHT
        return cellX + cellY * 4
    }



}

