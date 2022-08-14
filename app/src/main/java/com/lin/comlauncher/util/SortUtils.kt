package com.lin.comlauncher.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.entity.CellBean
import com.lin.comlauncher.view.isStop
import java.util.Collections

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
        list.forEach continuing@{
            if (app == it)
                return@continuing
            it.orignX = it.posX
            it.orignY = it.posY
        }

        var currenPos = findCurrentCell(app.posX, app.posY)
        var prePos = findCurrentCell(app.orignX,app.orignY)
        LogUtils.e("currentPos=$currenPos prePos=$prePos pos=${app.position} pos=${app.position}")
        var totalList = list.filter { it.position ==LauncherConfig.POSITION_TOOLBAR }

        if(app.position==LauncherConfig.POSITION_HOME){
            if(currenPos==prePos)
                return
            if(currenPos<=-100){
                currenPos = -currenPos-100;

                totalList.firstOrNull { it.cellPos ==currenPos &&it.position==LauncherConfig.POSITION_TOOLBAR }?.let { destApp->
                    LogUtils.e("findapp ${destApp.name}")
                    var appCell = destApp.cellPos;
                    if(destApp==app.dragInfo){

                        return;
                    }else{
                        var dragInfo = app.dragInfo
                        if(dragInfo!=null){
                            destApp.needMoveX = -dragInfo.orignX+destApp.posX;
                            destApp.needMoveY = -dragInfo.orignY+destApp.posY;
                            destApp.orignX = dragInfo.orignX
                            destApp.orignY = dragInfo.orignY
                            destApp.cellPos = dragInfo.cellPos
                            destApp.showText = true
                            destApp.position = LauncherConfig.POSITION_HOME

                            dragInfo.orignX = app.orignX
                            dragInfo.orignY = app.orignY
                            dragInfo.needMoveX = dragInfo.posX-app.posX
                            dragInfo.needMoveY = dragInfo.posY-app.posY
                            dragInfo.cellPos = app.cellPos
                            dragInfo.showText = false
                            dragInfo.position = LauncherConfig.POSITION_TOOLBAR

                            app.orignX = destApp.posX
                            app.orignY = destApp.posY
                            app.needMoveX = app.orignX-app.posX
                            app.needMoveY = app.orignY-app.posY
                            app.dragInfo = destApp
                            app.cellPos = appCell
                            app.showText = false
                            app.position = LauncherConfig.POSITION_TOOLBAR
                        }else{
                            destApp.needMoveX = -app.orignX+destApp.posX;
                            destApp.needMoveY = -app.orignY+destApp.posY;
                            destApp.orignX = app.orignX
                            destApp.orignY = app.orignY
                            destApp.cellPos = app.cellPos
                            destApp.showText= true

                            app.orignX = destApp.posX
                            app.orignY = destApp.posY
                            app.needMoveX = app.orignX-app.posX
                            app.needMoveY = app.orignY-app.posY
                            app.dragInfo = destApp
                            app.cellPos = appCell
                            app.showText = false

                        }

                    }


                }
                return
            }
            app.dragInfo?.let { dragInfo->
                var cOrignX = dragInfo.orignX
                var cOrignY  = dragInfo.orignY
                var appCell = dragInfo.cellPos;

                dragInfo.orignX = app.orignX
                dragInfo.orignY = app.orignY
                dragInfo.needMoveX = dragInfo.posX-app.orignX
                dragInfo.needMoveY = dragInfo.posY-app.orignY
                dragInfo.cellPos =   app.cellPos
                dragInfo.showText = false

                app.orignX = cOrignX
                app.orignY = cOrignY
                app.needMoveX = app.orignX-app.posX
                app.needMoveY = app.orignY-app.posY
                app.dragInfo = null
                app.cellPos =appCell
                app.showText = true
            }
            if (currenPos < 0)
                currenPos = 0
            else if (currenPos >= list.size)
                currenPos = list.size-1

            app.cellPos = currenPos
            var mIndex = 0
            list.sortedBy { it.cellPos }.forEachIndexed { pos, ai ->
                if(ai.position==LauncherConfig.POSITION_TOOLBAR)
                    return@forEachIndexed;
                var index =if(ai==app)
                    currenPos
                else if(currenPos<prePos){
                    if(mIndex<currenPos) mIndex else mIndex+1
                }else{
                    if(mIndex>=currenPos) mIndex+1 else mIndex
                }

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
            totalList.sortedBy { it.cellPos }.forEachIndexed { pos, ai ->
                var index =if(ai==app)
                    currenPos
                else if(currenPos<prePos){
                    if(mIndex<currenPos) mIndex else mIndex+1
                }else{
                    if(mIndex>=currenPos) mIndex+1 else mIndex
                }

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
        if (posY < LauncherConfig.DEFAULT_TOP_PADDING-LauncherConfig.CELL_ICON_WIDTH/2) {
            return -1
        }
        if(posY>=LauncherConfig.HOME_TOOLBAR_START-40){
            var pos = (posX +LauncherConfig.HOME_CELL_WIDTH/2)/LauncherConfig.HOME_CELL_WIDTH;
            return -pos - 100;
        }

        var cellX = (posX +LauncherConfig.HOME_CELL_WIDTH/2)/LauncherConfig.HOME_CELL_WIDTH;


        var cellY = (posY - LauncherConfig.DEFAULT_TOP_PADDING
                +LauncherConfig.HOME_CELL_HEIGHT/2)/ LauncherConfig.HOME_CELL_HEIGHT

//        LogUtils.e("cell=$cellX  cellY=$cellY de=${posX / (LauncherConfig.HOME_WIDTH/8)}")

        return cellX + cellY * 4
    }

    fun swapChange(app: ApplicationInfo) {
        var app1 = app
        var app2 = app.dragInfo
        if(app1!=null&&app2!=null){
            var cellIndex = app.cellIndex
            var cellPos = app.cellPos
            var position = app.position

            app.cellPos = app2.cellPos
            app.cellIndex = app2.cellIndex
            app.position = app2.position
//
            app2.cellPos = cellPos
            app2.cellIndex = cellIndex
            app2.position = position
            LogUtils.e("pos=${app1.name} ${app1.position}  pos2=${app2.name} ${app2.position}")

        }
        isStop = true;
        app.dragInfo=null
    }

}

