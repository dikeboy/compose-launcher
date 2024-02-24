package com.lin.comlauncher.util

import com.lin.comlauncher.entity.ApplicationInfo

object SortUtils {
    fun calculLeftPos(
        toList: ArrayList<ApplicationInfo>, pagePos: Int, app: ApplicationInfo
    ) {
        if (toList.size >= LauncherConfig.HOME_PAGE_CELL_NUM) {
            return;
        }
        app.orignX = toList.size % 4
        app.orignY = toList.size / 4
    }

    fun calculPos(
        list: ArrayList<ApplicationInfo>, app: ApplicationInfo
    ) {
        var currentPos = findCurrentCell(app.posX, app.posY)
        if (app.position == LauncherConfig.POSITION_HOME) {

            if (currentPos < 0)
                currentPos = 0
            else if (currentPos >= list.size)
                currentPos = list.size - 1
            var isEmpty = true
            list.forEach {
                if (it.cellPos == currentPos) {
                    isEmpty = false;
                    return@forEach
                }
            }
            if (isEmpty) {
                findPosByCell(currentPos)?.let {
                    app.orignX = it[0]
                    app.orignY = it[1]
                    app.cellPos = currentPos
                }
            }
        }
    }

    fun resetChoosePos(
        list: ArrayList<ApplicationInfo>, app: ApplicationInfo,
        toolList: ArrayList<ApplicationInfo>
    ) {
        list.forEach continuing@{
            if (app == it)
                return@continuing
            it.orignX = it.posX
            it.orignY = it.posY
        }
        toolList.forEach continuing@{
            if (app == it)
                return@continuing
            it.orignX = it.posX
            it.orignY = it.posY
        }
        var currenPos = findCurrentCell(app.posX, app.posY)
        var prePos = findCurrentCell(app.orignX, app.orignY)
        LogUtils.e("cellIndex=${currenPos} preCell=${prePos}")
//        LogUtils.e("currentPos=${-currenPos-100} prePos=$prePos pos=${app.position} pos=${app.position}")

        if (app.position == LauncherConfig.POSITION_HOME) {
            if (currenPos == prePos)
                return
            if (currenPos <= -100) {
                currenPos = -currenPos - 100;

                toolList.firstOrNull { it.cellPos == currenPos }?.let { destApp ->
                    LogUtils.e("1  ${app.dragInfo == destApp} dragInfo=${app.dragInfo}")
                    var appCell = destApp.cellPos;
                    if (destApp == app.dragInfo) {
                        return;
                    } else {
                        var dragInfo = app.dragInfo
                        if (dragInfo != null) {
                            var cacheOrignX = destApp.orignX;
                            var cacheOrignY = destApp.orignY;
                            destApp.needMoveX = -dragInfo.orignX + destApp.posX;
                            destApp.needMoveY = -dragInfo.orignY + destApp.posY;
                            destApp.orignX = dragInfo.orignX
                            destApp.orignY = dragInfo.orignY
                            destApp.cellPos = dragInfo.cellPos
                            destApp.showText = true

                            dragInfo.orignX = app.orignX
                            dragInfo.orignY = app.orignY
                            dragInfo.needMoveX = dragInfo.posX - app.orignX
                            dragInfo.needMoveY = dragInfo.posY - app.orignY
                            dragInfo.cellPos = app.cellPos
                            dragInfo.showText = false

                            app.orignX = destApp.posX
                            app.orignY = destApp.posY
                            app.needMoveX = app.orignX - app.posX
                            app.needMoveY = app.orignY - app.posY
                            app.dragInfo = destApp
                            app.cellPos = appCell
                            app.showText = false
                        } else {
                            destApp.needMoveX = -app.orignX + destApp.posX;
                            destApp.needMoveY = -app.orignY + destApp.posY;
                            destApp.orignX = app.orignX
                            destApp.orignY = app.orignY
                            destApp.cellPos = app.cellPos
                            destApp.showText = true

                            app.orignX = destApp.posX
                            app.orignY = destApp.posY
                            app.needMoveX = app.orignX - app.posX
                            app.needMoveY = app.orignY - app.posY
                            app.dragInfo = destApp
                            app.cellPos = appCell
                            app.showText = false

                        }
                    }
                }
                return
            }
            app.dragInfo?.let { dragInfo ->
                var cOrignX = dragInfo.orignX
                var cOrignY = dragInfo.orignY
                var appCell = dragInfo.cellPos;

                dragInfo.orignX = app.orignX
                dragInfo.orignY = app.orignY
                dragInfo.needMoveX = dragInfo.posX - app.orignX
                dragInfo.needMoveY = dragInfo.posY - app.orignY
                dragInfo.cellPos = app.cellPos
                dragInfo.showText = false

                app.orignX = cOrignX
                app.orignY = cOrignY
                app.needMoveX = app.orignX - app.posX
                app.needMoveY = app.orignY - app.posY
                app.dragInfo = null
                app.cellPos = appCell
                app.showText = true
            }
            if (currenPos < 0)
                currenPos = 0
            else if (currenPos >= list.size)
                currenPos = list.size - 1

            app.cellPos = currenPos
            var mIndex = 0
            list.sortedBy { it.cellPos }.forEachIndexed { pos, ai ->
                var index = if (ai == app)
                    currenPos
                else if (currenPos < prePos) {
                    if (mIndex < currenPos) mIndex else mIndex + 1
                } else {
                    if (mIndex >= currenPos) mIndex + 1 else mIndex
                }

                ai.orignX = LauncherConfig.HOME_DEFAULT_PADDING_LEFT + (index % 4) * ai.width
                ai.orignY =
                    index / 4 * LauncherConfig.HOME_CELL_HEIGHT + LauncherConfig.DEFAULT_TOP_PADDING
                ai.needMoveX = ai.posX - ai.orignX
                ai.needMoveY = ai.posY - ai.orignY
                ai.cellPos = index
                if (ai != app)
                    mIndex++;
            }
        } else {
            if (currenPos == prePos || currenPos >= 0 || prePos >= 0)
                return
            currenPos = -currenPos - 100;
            app.cellPos = currenPos
            var mIndex = 0
            toolList.sortedBy { it.cellPos }.forEachIndexed { pos, ai ->
                var index = if (ai == app)
                    currenPos
                else if (currenPos < prePos) {
                    if (mIndex < currenPos) mIndex else mIndex + 1
                } else {
                    if (mIndex >= currenPos) mIndex + 1 else mIndex
                }

                ai.orignX = LauncherConfig.HOME_DEFAULT_PADDING_LEFT + (index) * ai.width
                ai.orignY = LauncherConfig.HOME_HEIGHT - LauncherConfig.HOME_CELL_WIDTH;
                ai.needMoveX = ai.posX - ai.orignX
                ai.needMoveY = ai.posY - ai.orignY
                ai.cellPos = index
                if (ai != app)
                    mIndex++;
            }
        }

    }

    fun findCurrentCellByPos(posX: Int, posY: Int): Int {
        var padding = 10
        if (posY < LauncherConfig.DEFAULT_TOP_PADDING) {
            return -1
        }
        if (posX <= LauncherConfig.HOME_DEFAULT_PADDING_LEFT)
            return LauncherConfig.CELL_POS_HOME_LEFT;
        if (posX >= LauncherConfig.HOME_WIDTH - LauncherConfig.HOME_DEFAULT_PADDING_LEFT)
            return LauncherConfig.CELL_POS_HOME_RIGHT;

        if (posY >= LauncherConfig.HOME_TOOLBAR_START - 40) {
            var pos = (posX + LauncherConfig.HOME_CELL_WIDTH / 2) / LauncherConfig.HOME_CELL_WIDTH;
            return -pos - 100;
        }

        var cellX = (posX - LauncherConfig.HOME_DEFAULT_PADDING_LEFT) / (LauncherConfig.HOME_CELL_WIDTH);


        var cellY = (posY - LauncherConfig.DEFAULT_TOP_PADDING) / LauncherConfig.HOME_CELL_HEIGHT

        LogUtils.e("cell=$cellX  cellY=$cellY de=${posX / (LauncherConfig.HOME_WIDTH / 8)}")

        return cellX + cellY * 4
    }

    fun findCurrentActorPix(list: List<ApplicationInfo>, pixX: Int, pixY: Int): ApplicationInfo? {
        var posX = DisplayUtils.pxToDp(pixX);
        var posY = DisplayUtils.pxToDp(pixY)
        list.forEach {
            if (posX >= it.posX && posX < it.posX + it.width && posY >= it.posY && posY < it.posY + it.height) {
                return it;
            }
        }
        return null;
    }

    fun findCurrentActorDp(list: List<ApplicationInfo>, dpX: Int, dpY: Int): ApplicationInfo? {
        var posX = dpX;
        var posY = dpY;
        list.forEach {
            if (posX >= it.posX && posX < it.posX + it.width && posY >= it.posY && posY < it.posY + it.height) {
                return it;
            }
        }
        return null;
    }

    fun findCurrentActorFolder(list: List<ApplicationInfo>, pixX: Int, pixY: Int): ApplicationInfo? {
        var posX = DisplayUtils.pxToDp(pixX);
        var posY = DisplayUtils.pxToDp(pixY)
        list.forEach {
            if (posX >= it.posX && posX < it.posX + it.width && posY >= it.posY && posY < it.posY + it.height) {
                return it;
            }
        }
        return null;
    }

    fun findCurrentActorCell(list: List<ApplicationInfo>, cellX: Int, cellY: Int): ApplicationInfo? {
        list.forEach {
            if (cellX + cellY * 4 == it.cellPos) {
                return it;
            }
        }
        return null;
    }

    fun findCurrentCell(posX: Int, posY: Int): Int {
        if (posY < LauncherConfig.DEFAULT_TOP_PADDING - LauncherConfig.CELL_ICON_WIDTH / 2) {
            return -1
        }
        var centerX = posX + LauncherConfig.HOME_CELL_WIDTH
//        LogUtils.e("posX = $posX width=${LauncherConfig.HOME_WIDTH}")

        if (posX <= -LauncherConfig.HOME_CELL_WIDTH / 3) {
            return LauncherConfig.CELL_POS_HOME_LEFT;
        } else if (posX >= LauncherConfig.HOME_WIDTH - LauncherConfig.HOME_CELL_WIDTH * 2 / 3) {
            return LauncherConfig.CELL_POS_HOME_RIGHT;
        }
        if (posY >= LauncherConfig.HOME_TOOLBAR_START - 40) {
            var pos = (posX + LauncherConfig.HOME_CELL_WIDTH / 2) / LauncherConfig.HOME_CELL_WIDTH;
            return -pos - 100;
        }

        var cellX = (posX + LauncherConfig.HOME_CELL_WIDTH / 2) / LauncherConfig.HOME_CELL_WIDTH;


        var cellY = (posY - LauncherConfig.DEFAULT_TOP_PADDING
                + LauncherConfig.HOME_CELL_HEIGHT / 2) / LauncherConfig.HOME_CELL_HEIGHT
//        LogUtils.e("cell=$cellX  cellY=$cellY de=${posX / (LauncherConfig.HOME_WIDTH/8)}")

        return cellX + cellY * 4
    }

    fun findPosByCell(currentCell: Int): Array<Int>? {
        if (currentCell > 100 && currentCell < 0)
            return null;
        var cellX = currentCell % 4;
        var cellY = currentCell / 4;
        var posX = cellX * LauncherConfig.HOME_CELL_WIDTH + LauncherConfig.HOME_DEFAULT_PADDING_LEFT;
        var posY = cellY * LauncherConfig.HOME_CELL_HEIGHT + LauncherConfig.DEFAULT_TOP_PADDING;
        return arrayOf(posX, posY)
    }

    fun swapChange(applist: ArrayList<ApplicationInfo>, toolList: ArrayList<ApplicationInfo>, app: ApplicationInfo) {
        var app1 = app
        var app2 = app.dragInfo
        if (app1 != null && app2 != null) {
            var index = toolList.indexOf(app2);
            toolList.remove(app2)
            toolList.add(index, app1);
            applist.remove(app1);
            applist.add(app2);

            app.position = LauncherConfig.POSITION_TOOLBAR
            app2.position = LauncherConfig.POSITION_HOME
            LogUtils.e("swap")
        }

        app.dragInfo = null
    }

}

