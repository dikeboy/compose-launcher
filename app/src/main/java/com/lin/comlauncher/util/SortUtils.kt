package com.lin.comlauncher.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.dp
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.entity.CellBean

object SortUtils {
    fun calculPos(
        list: SnapshotStateList<ApplicationInfo>, app: ApplicationInfo
    ) {
        var currentPos = findCurrentCell(app.posX, app.posY)
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

    fun resetChoosePos(
        list: SnapshotStateList<ApplicationInfo>, app: ApplicationInfo
    ) {
        list.forEach {
            if (app == it)
                return@forEach
            it.orignX = it.posX
            it.orignY = it.posY
        }

        var currenPos = findCurrentCell(app.posX, app.posY)
        LogUtils.e("currentPos=$currenPos")
        if (currenPos < 0)
            currenPos = 0
        else if (currenPos >= 20)
            currenPos = 19
        list.remove(app)
        list.add(currenPos, app)

        list.forEachIndexed { index, ai ->
            if (index == currenPos)
                return@forEachIndexed
            ai.orignX = (index % 4) * ai.width
            ai.orignY = index / 4 * LauncherConfig.HOME_CELL_HEIGHT + LauncherConfig.DEFAULT_TOP_PADDING
            ai.needMoveX = ai.posX - ai.orignX
            ai.needMoveY = ai.posY - ai.orignY
            ai.cellPos = index
//            LogUtils.e("pos=${ai.posX} posY=${ai.posY} desx=${ai.orignX} desy=${ai.orignY} name=${ai.name}")
        }
    }

    fun findCurrentCell(posX: Int, posY: Int): Int {
        if (posY < LauncherConfig.DEFAULT_TOP_PADDING) {
            return -1
        }
        var cellX = posX / LauncherConfig.HOME_CELL_WIDTH
        var cellY = (posY - LauncherConfig.DEFAULT_TOP_PADDING) / LauncherConfig.HOME_CELL_HEIGHT
        return cellX + cellY * 4
    }


}

