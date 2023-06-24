package com.lin.comlauncher.view

import android.content.Context
import android.opengl.GLUtils
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.LocalImageLoader
import coil.compose.rememberAsyncImagePainter
import com.lin.comlauncher.entity.AppPos
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.util.DisplayUtils
import com.lin.comlauncher.util.DoTranslateAnim
import com.lin.comlauncher.util.LauncherConfig
import com.lin.comlauncher.util.LauncherUtils
import com.lin.comlauncher.util.LogUtils
import com.lin.comlauncher.util.SortUtils
import com.lin.comlauncher.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DragManager {
}

suspend fun PointerInputScope.detectLongPress(
    context: Context,
    toolList: ArrayList<ApplicationInfo>,
    homeList: ArrayList<ArrayList<ApplicationInfo>>,
    currentSel: MutableState<Int>,
    coroutineScope: CoroutineScope, coroutineAnimScope: CoroutineScope,
    dragInfoState: MutableState<ApplicationInfo?>, animFinish: MutableState<Boolean>,
    offsetX: MutableState<Dp>, offsetY: MutableState<Dp>,
    dragUpState: MutableState<Boolean>,
    state: LazyListState,
    version:MutableState<Int>,
    homeViewModel: HomeViewModel
) {
    detectDragGesturesAfterLongPress(
        onDragStart = { off ->
            var applist = homeList.get(currentSel.value)
            var it = if (off.y.toDp().value >= LauncherConfig.HOME_TOOLBAR_START) {
                SortUtils.findCurrentActorPix(toolList, off.x.toInt(), off.y.toInt())
            } else
                SortUtils.findCurrentActorPix(applist, off.x.toInt(), off.y.toInt())
            if (it == null)
                return@detectDragGesturesAfterLongPress;

            it.isDrag = true
            it.orignX = it.posX
            it.orignY = it.posY
            it.posFx = it.posX.dp.toPx()
            it.posFy = it.posY.dp.toPx()
            coroutineScope.launch {
                LauncherUtils.vibrator(context = context)
            }
            LogUtils.e("drag app ${it.name}")
            dragInfoState.value = it;
            dragUpState.value = true
            coroutineAnimScope.launch {
                var preCell =
                    SortUtils.findCurrentCellByPos(
                        DisplayUtils.pxToDp(off.x.toInt()),
                        DisplayUtils.pxToDp(off.y.toInt())
                    )
                var disPlayTime = 0
                var dragStop = false
                while (it.isDrag) {
                    var preX = it.posX
                    var preY = it.posY

                    delay(150)
                    if (!it.isDrag)
                        break
                    var curX = it.posX
                    var curY = it.posY
                    var movePage = false
                    if (Math.abs(preX - curX) < 10 && Math.abs(
                                preY - curY
                            ) < 10 && !animFinish.value
                    ) {
                        var cellIndex =
                            SortUtils.findCurrentCell(
                                curX,
                                curY
                            )
                        var appInfo = SortUtils.findCurrentActorDp(list =applist,curX,curY)
                        if(appInfo?.appType==LauncherConfig.CELL_TYPE_FOLD){
                            continue;
                        }
                        LogUtils.e("cellIndex=${curX} preCell=${curY} name=${appInfo?.name} height=${appInfo?.height}")
                        if (preCell == cellIndex && !dragStop) {
                            dragStop = true
                            disPlayTime = 0
                            continue;
                        } else if (preCell != cellIndex) {
                            dragStop = false;
                            disPlayTime = 0
                        } else if (dragStop) {
                            disPlayTime++
                        }
                        preCell = cellIndex

                        if (disPlayTime >= 1) {
                            if (preCell == cellIndex && cellIndex == LauncherConfig.CELL_POS_HOME_LEFT) {
                                if (state.firstVisibleItemIndex - 1 >= 0) {
                                    state.animateScrollToItem(state.firstVisibleItemIndex - 1)
                                    movePage = true;
                                }

                            } else if (preCell == cellIndex && cellIndex == LauncherConfig.CELL_POS_HOME_RIGHT) {
                                if (state.firstVisibleItemIndex + 1 < state.layoutInfo.totalItemsCount) {
                                    state.animateScrollToItem(state.firstVisibleItemIndex + 1)
                                    movePage = true;
                                }
                            }

                            if (movePage) {
//                                LogUtils.e("movePage")
                                delay(800)
                                continue;
                            }
                            if (disPlayTime == 1) {
                                run {
                                    SortUtils.resetChoosePos(
                                        applist,
                                        it, toolList
                                    )
                                    var xscale = 100
                                    var yscale = 100
                                    animFinish.value = true
                                    DoTranslateAnim(
                                        AppPos(0, 0),
                                        AppPos(100, 100),
                                        300
                                    ) { appPos, velocity ->
                                        applist.forEach continuing@{ appInfo ->
                                            if (appInfo == it || (appInfo.orignX == appInfo.posX && appInfo.orignY == appInfo.posY))
                                                return@continuing
                                            if (xscale > 0)
                                                appInfo.posX =
                                                    appInfo.orignX + (xscale - appPos.x) * appInfo.needMoveX / xscale;
                                            if (yscale > 0)
                                                appInfo.posY =
                                                    appInfo.orignY + (yscale - appPos.y) * appInfo.needMoveY / yscale;
                                        }
                                        toolList.forEach continuing@{ appInfo ->
                                            if (appInfo == it || (appInfo.orignX == appInfo.posX && appInfo.orignY == appInfo.posY))
                                                return@continuing
                                            if (xscale > 0)
                                                appInfo.posX =
                                                    appInfo.orignX + (xscale - appPos.x) * appInfo.needMoveX / xscale;

                                            if (yscale > 0)
                                                appInfo.posY =
                                                    appInfo.orignY + (yscale - appPos.y) * appInfo.needMoveY / yscale;
                                        }
                                        offsetX.value = appPos.x.dp
                                        offsetY.value = appPos.y.dp
                                    }
                                    applist.forEach { appInfo ->
                                        if (appInfo == it)
                                            return@forEach
                                        appInfo.orignY = appInfo.posY
                                        appInfo.orignX = appInfo.posX
                                    }
                                    toolList.forEach { appInfo ->
                                        if (appInfo == it)
                                            return@forEach
//                                        LogUtils.e("name=${appInfo.name}  orix = ${appInfo.orignX} pox=${  appInfo.posX}")
                                        appInfo.orignY = appInfo.posY
                                        appInfo.orignX = appInfo.posX
                                    }
                                    animFinish.value = false
                                }

                            }


                        }
                    }

                }
            }
        },
        onDragEnd = {
            dragInfoState.value?.let {
                var applist = homeList.get(currentSel.value)
                it.isDrag = false
                LogUtils.e("current=${currentSel.value} pagePos =${it.pagePos}")

                if (it.pagePos != currentSel.value.toInt()) {
                    var toList = homeList.get(currentSel.value)
                    if (toList.size >= LauncherConfig.HOME_PAGE_CELL_MAX_NUM) {
                        it.posX = it.orignX
                        it.posY = it.orignY
                        return@let;
                    }

                    it.orignX = toList.size % 4* LauncherConfig.HOME_CELL_WIDTH+LauncherConfig.HOME_DEFAULT_PADDING_LEFT
                    it.orignY = toList.size / 4* LauncherConfig.HOME_CELL_HEIGHT+LauncherConfig.DEFAULT_TOP_PADDING
                    it.cellPos  = toList.size;

                    offsetX.value = it.posX.dp
                    offsetY.value = it.posY.dp
                    dragUpState.value = false
                    coroutineScope.launch {
                        if (animFinish.value)
                            delay(200)
                        homeList.get(it.pagePos).remove(it)
                        toList.add(it)
                        it.pagePos = currentSel.value
                        DoTranslateAnim(
                            AppPos(it.posX, it.posY),
                            AppPos(it.orignX, it.orignY),
                            200
                        )
                        { appPos, velocity ->
                            it.posX = appPos.x
                            it.posY = appPos.y
                            offsetX.value = appPos.x.dp
                            offsetY.value = appPos.y.dp
                        }

                        offsetX.value = 200.dp
                    }

                } else {
                    var appInfo = SortUtils.findCurrentActorDp(list =applist,it.posX,it.posY)
                    LogUtils.e("appInfo=${appInfo?.appType} name=${appInfo?.name}")
                    SortUtils.calculPos(applist, it)
                    if(appInfo?.appType==LauncherConfig.CELL_TYPE_FOLD&&it.appType==LauncherConfig.CELL_TYPE_APP&&appInfo.childs.size<9){
                        appInfo.childs.add(it)
                        LauncherUtils.createFoldIcon(appInfo)
                        LauncherUtils.changeFoldPosition(appInfo.childs)
                        applist.remove(it)
                        dragInfoState.value = null;
                        offsetX.value = it.posX.dp
                        offsetY.value = it.posY.dp
                    }else{
                        offsetX.value = it.posX.dp
                        offsetY.value = it.posY.dp
                        LogUtils.e("dragEnd ")
                        dragUpState.value = false
                        coroutineScope.launch {
                            if (animFinish.value)
                                delay(200)
                            DoTranslateAnim(
                                AppPos(it.posX, it.posY),
                                AppPos(it.orignX, it.orignY),
                                200
                            )
                            { appPos, velocity ->
                                it.posX = appPos.x
                                it.posY = appPos.y
                                offsetX.value = appPos.x.dp
                                offsetY.value = appPos.y.dp
                            }
                            dragInfoState.value = null;
                            SortUtils.swapChange(applist = applist, toolList = toolList, app = it)
                            offsetX.value = 200.dp
                        }
                    }

                }


            }
        },
        onDragCancel = {
            dragInfoState.value?.let {
                it.isDrag = false
                dragUpState.value = false
                LogUtils.e("drag cancle")
                dragInfoState.value = null
            }
        }
    ) { change, dragAmount ->
        change.consumeAllChanges()
        dragInfoState.value?.let {
            it.posFx += dragAmount.x
            it.posFy += dragAmount.y
//                                        LogUtils.e("offx=${ it.posFx.toDp()} offy=${it.posFy.toDp()}")
            it.posX = it.posFx.toDp().value.toInt()
            it.posY = it.posFy.toDp().value.toInt()
//            LogUtils.e("drag cellX = ${it.posX}  cellY=${it.posY}")
            offsetX.value = dragAmount.x.toDp() + offsetX.value
            offsetY.value = dragAmount.y.toDp() + offsetY.value
        }

    }
}
