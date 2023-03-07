package com.lin.comlauncher.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.lin.comlauncher.entity.AppInfoBaseBean
import com.lin.comlauncher.entity.AppPos
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.util.*
import com.lin.comlauncher.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun IconView(it: ApplicationInfo,applist:ArrayList<ApplicationInfo>,
             toolList:ArrayList<ApplicationInfo>,
             state: ScrollState,
             coroutineScope: CoroutineScope,coroutineAnimScope:CoroutineScope,
             dragInfoState:MutableState<ApplicationInfo?>,animFinish:MutableState<Boolean>,
             offsetX:MutableState<Dp>,offsetY:MutableState<Dp>,
             dragUpState:MutableState<Boolean>
) {
    var posX = it.posX
    var posY = it.posY
    var context = LocalContext.current;
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(it.width.dp, it.height.dp)
            .offset(posX.dp, posY.dp)
            .alpha(if (it.isDrag) 0f else 1f)
            .pointerInput(it) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { off ->
                        it.isDrag = true
                        it.orignX = it.posX
                        it.orignY = it.posY
                        it.posFx = it.posX.dp.toPx()
                        it.posFy = it.posY.dp.toPx()
                        LauncherUtils.vibrator(context = context)
                        LogUtils.e("drag app ${it.name}")
                        dragInfoState.value = it;
                        dragUpState.value =true
//
                        coroutineAnimScope.launch {
//                            state.scrollBy(width.dp.toPx())
//                            state.animateScrollBy(width.dp.toPx())
                            var preCell =
                                SortUtils.findCurrentCell(it.posX, it.posY)

                            while (it.isDrag) {
                                var preX = it.posX
                                var preY = it.posY

                                delay(200)
                                if(!it.isDrag)
                                    break
                                var curX = it.posX
                                var curY = it.posY
                                run {
                                    if (Math.abs(preX - curX) < 10 && Math.abs(
                                            preY - curY
                                        ) < 10 && !animFinish.value
                                    ) {
                                        var cellIndex =
                                            SortUtils.findCurrentCell(
                                                curX,
                                                curY
                                            )
                                        if (cellIndex == preCell)
                                            return@run
                                        preCell = cellIndex
                                        LogUtils.e(
                                            "disx =${preX - curX}  disY=${preY - curY} " +
                                                    "cellIndex=${cellIndex} posX=${it.posX} " +
                                                    "posY=${it.posY}"
                                        )
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
                                            200
                                        ) { appPos, velocity ->
                                            applist.forEach continuing@{ appInfo ->
                                                if (appInfo == it||(appInfo.orignX==appInfo.posX&&appInfo.orignY==appInfo.posY))
                                                    return@continuing
                                                if(xscale>0)
                                                appInfo.posX =
                                                    appInfo.orignX + (xscale - appPos.x) * appInfo.needMoveX / xscale;
                                                if(yscale>0)
                                                appInfo.posY =
                                                    appInfo.orignY + (yscale - appPos.y) * appInfo.needMoveY / yscale;
                                            }
                                            toolList.forEach continuing@{ appInfo->
                                                if (appInfo == it||(appInfo.orignX==appInfo.posX&&appInfo.orignY==appInfo.posY))
                                                    return@continuing
                                                if(xscale>0)
                                                appInfo.posX =
                                                    appInfo.orignX + (xscale - appPos.x) * appInfo.needMoveX / xscale;
                                                if(yscale>0)
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
                                            appInfo.orignY = appInfo.posY
                                            appInfo.orignX = appInfo.posX
                                        }
                                        animFinish.value = false
                                    }
                                }
                            }
                        }
                    },
                    onDragEnd = {
                        it.isDrag = false
                        SortUtils.calculPos(applist, it)
                        offsetX.value = it.posX.dp
                        offsetY.value = it.posY.dp
                        LogUtils.e("dragEnd ")
                        dragUpState.value = false
                        coroutineScope.launch {
                            if(animFinish.value)
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
                          SortUtils.swapChange(applist = applist,toolList = toolList,app=it)
                        }
                    },
                    onDragCancel = {
                        it.isDrag = false
                        dragUpState.value = false
                        LogUtils.e("drag cancle")
                        dragInfoState.value = null
                    }
                ) { change, dragAmount ->
                    change.consumeAllChanges()
                    it.posFx += dragAmount.x
                    it.posFy += dragAmount.y
//                                        LogUtils.e("offx=${ it.posFx.toDp()} offy=${it.posFy.toDp()}")
                    it.posX = it.posFx.toDp().value.toInt()
                    it.posY = it.posFy.toDp().value.toInt()
                    offsetX.value=dragAmount.x.toDp()+offsetX.value
                    offsetY.value= dragAmount.y.toDp()+offsetY.value
                }
            }
            .background(Color.Transparent)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                LauncherUtils.startApp(context, it)
            }) {
        IconViewDetail(it,it.showText,dragUpState)
    }
}
@Composable
fun IconViewDetail(it: ApplicationInfo,showText: Boolean=true, dragUpState:MutableState<Boolean>){
    it.imageBitmap?.let { icon ->
//        Image(
//            icon.asImageBitmap(), contentDescription = "",
//            modifier = Modifier.size(it.iconWidth.dp, it.iconHeight.dp)
//        )
        Image(
            painter = icon,
            contentDescription = it.pageName,
            modifier = Modifier.size(it.iconWidth.dp, it.iconHeight.dp)
        )

    }
    if(showText){
        Text(
            text = it.name ?: "",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontSize = 14.sp,
            modifier = Modifier.padding(4.dp, 10.dp, 4.dp, 0.dp)
        )
    }
}