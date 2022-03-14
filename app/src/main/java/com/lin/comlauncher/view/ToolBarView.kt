package com.lin.comlauncher.view

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.lin.comlauncher.entity.AppInfoBaseBean
import com.lin.comlauncher.entity.AppPos
import com.lin.comlauncher.ui.theme.MyBasicColumn
import com.lin.comlauncher.ui.theme.pagerFlingBehavior
import com.lin.comlauncher.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ToolBarView(applist: State<AppInfoBaseBean?>) {
    var width = LocalConfiguration.current.screenWidthDp
    var height = LocalConfiguration.current.screenHeightDp
    var appheight = applist.value?.toobarList?.firstOrNull()?.height?:0
    var context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val coroutineAnimScope = rememberCoroutineScope()

    MyBasicColumn()
    {
        var toolbarlist = applist.value?.toobarList
        toolbarlist?.forEachIndexed { index, it ->
            var offsetX by remember { mutableStateOf(it.posX.dp) }
            var offsetY by remember { mutableStateOf(it.posY.dp) }
            var animFinish by remember { mutableStateOf(false) }

            var off = offsetX
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement= Arrangement.Center,
                modifier = Modifier
                    .size(it.width.dp, it.height.dp)
                    .offset(it.posX.dp, it.posY.dp)
                    .zIndex(if (it.isDrag) 1f else 0f)
                    .pointerInput(it) {
                        detectDragGesturesAfterLongPress(
                                onDragStart = { off ->
                                    it.isDrag = true
                                    it.orignX = it.posX
                                    it.orignY = it.posY
                                    it.posFx = it.posX.dp.toPx()
                                    it.posFy = it.posY.dp.toPx()
                                    LauncherUtils.vibrator(context = context)
                                    LogUtils.e("drag tool app ${it.name} dragx=${it.posX}  dragY=${it.posY}")

                                    coroutineAnimScope.launch {

                                        var preCell = SortUtils.findCurrentCell(it.posX,it.posY)

                                        while (it.isDrag){
                                            var preX= it.posX
                                            var preY = it.posY
                                            delay(300)
                                            var curX = it.posX
                                            var curY = it.posY
                                            run {
                                                if (Math.abs(preX - curX) < 10 && Math.abs(preY - curY) < 10 && !animFinish) {
                                                    var cellIndex = SortUtils.findCurrentCell(curX,curY)
                                                    if (cellIndex==preCell)
                                                        return@run
                                                    preCell = cellIndex
                                                    LogUtils.e("disx =${preX - curX}  disY=${preY - curY} " +
                                                            "cellIndex=${cellIndex} posX=${it.posX} " +
                                                            "posY=${it.posY}")

                                                    SortUtils.resetToolbarPos(toolbarlist, it)
                                                    var xscale = 100
                                                    var yscale = 100
                                                    animFinish = true
                                                    DoTranslateAnim( AppPos(0,0),AppPos(100,100),300) {
                                                            appPos, velocity ->
                                                        toolbarlist.forEach { appInfo ->
                                                            if (appInfo == it)
                                                                return@forEach
                                                            appInfo.posX =
                                                                appInfo.orignX + (xscale - appPos.x) * appInfo.needMoveX / xscale;
                                                            appInfo.posY =
                                                                appInfo.orignY + (yscale - appPos.y) * appInfo.needMoveY / yscale;
                                                        }
                                                        offsetX = appPos.x.dp
                                                        offsetY = appPos.y.dp
                                                    }
                                                    LogUtils.e("disx2 =${preX - curX}  disY2=${preY - curY} " +
                                                            "cellIndex=${cellIndex} posX=${it.posX} " +
                                                            "posY=${it.posY}")
                                                    animFinish = false
                                                }
                                            }
                                        }
                                    }
                                },
                                onDragEnd = {
                                    it.isDrag = false
                                    offsetX = it.posX.dp
                                    offsetY = it.posY.dp
                                    LogUtils.e("dragEnd ")
                                    LogUtils.e("posY=${it.posY} orignY =${it.orignY} cellWidth=${LauncherConfig.HOME_CELL_WIDTH}");
                                    coroutineScope.launch {
                                        DoTranslateAnim( AppPos(it.posX,it.posY),
                                            AppPos(it.orignX,it.orignY),300)
                                        {appPos, velocity ->
                                            it.posX = appPos.x
                                            it.posY = appPos.y
                                            offsetX = appPos.x.dp
                                            offsetY = appPos.y.dp
                                        }
                                    }
                                },
                                onDragCancel = {
                                    it.isDrag = false
                                    LogUtils.e("drag cancle")

                                }
                                ) { change, dragAmount ->
                                change.consumeAllChanges()
                                it.posFx += dragAmount.x
                                it.posFy += dragAmount.y
//                                        LogUtils.e("offx=${ it.posFx.toDp()} offy=${it.posFy.toDp()}")
                                it.posX = it.posFx.toDp().value.toInt()
                                it.posY = it.posFy.toDp().value.toInt()
                                offsetX += dragAmount.x.toDp()
                                offsetY += dragAmount.y.toDp()
                            }
                    }
                    .clickable( interactionSource = MutableInteractionSource(),indication=null){
                        LauncherUtils.startApp(context, it)
                    }) {
                it.icon?.let { icon ->
                    Image(
                        icon.asImageBitmap(), contentDescription = "",
                        modifier = Modifier.size(56.dp, 56.dp)
                    )
                }
            }

        }
    }
}