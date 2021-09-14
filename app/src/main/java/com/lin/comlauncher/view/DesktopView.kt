package com.lin.comlauncher.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.lifecycle.ViewModel
import com.lin.comlauncher.entity.AppInfoBaseBean
import com.lin.comlauncher.entity.AppPos
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.ui.theme.MyBasicColumn
import com.lin.comlauncher.ui.theme.pagerFlingBehavior
import com.lin.comlauncher.util.LauncherUtils
import com.lin.comlauncher.util.LogUtils
import com.lin.comlauncher.util.SortUtils
import com.lin.comlauncher.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@Composable
fun DesktopView(applist: State<AppInfoBaseBean?>, viewModel: HomeViewModel) {
    var width = LocalConfiguration.current.screenWidthDp
    var height = LocalConfiguration.current.screenHeightDp
    LogUtils.e("load")
    val state = rememberScrollState()
    var context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .width(width = width.dp)
            .height(height = height.dp)
            .verticalScroll(rememberScrollState())
            .horizontalScroll(
                state,
                flingBehavior = pagerFlingBehavior(
                    state,
                    (applist.value?.homeList?.size ?: 0)
                )
            )
    ) {

        applist.value?.homeList?.forEachIndexed { index, list ->
            val applist = remember { mutableStateListOf<ApplicationInfo>() }
            var lastSelPos = remember {
                mutableStateOf(0)
            }
            applist.addAll(list)
            Column(
                modifier = Modifier
                    .width(width = width.dp)
                    .height(height = height.dp)
            ) {
                MyBasicColumn() {
                    var animFinish by remember { mutableStateOf(1f) }
                    applist.forEach {
                        var offsetX by remember { mutableStateOf(it.posX.dp) }
                        var offsetY by remember { mutableStateOf(it.posY.dp) }
                        var posX = it.posX
                        var posY = it.posY
                        var off = offsetX

                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .size(it.width.dp, it.height.dp)
                                .offset(posX.dp, posY.dp)
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
                                            LogUtils.e("drag app ${it.name}")
                                        },
                                        onDragEnd = {
                                            it.isDrag = false
                                            SortUtils.resetPos(applist, it)
                                            offsetX = it.posX.dp
                                            offsetY = it.posY.dp
                                            var xscale = 100
                                            var yscale = 100
                                            coroutineScope.launch {
                                                animate(
                                                    typeConverter = TwoWayConverter(
                                                        convertToVector = { size: AppPos ->
                                                            AnimationVector2D(
                                                                size.x.toFloat(),
                                                                size.y.toFloat()
                                                            )
                                                        },
                                                        convertFromVector = { vector: AnimationVector2D ->
                                                            AppPos(
                                                                vector.v1.toInt(),
                                                                vector.v2.toInt()
                                                            )
                                                        }
                                                    ),
                                                    initialValue = AppPos(0, 0),
                                                    targetValue = AppPos(100, 100),
                                                    initialVelocity = AppPos(0, 0),
                                                    animationSpec = tween(300),
                                                ) { appPos, velocity ->
                                                    applist.forEach { appInfo ->
                                                        appInfo.posX =
                                                            appInfo.orignX + (xscale - appPos.x) * appInfo.needMoveX / xscale;
                                                        appInfo.posY =
                                                            appInfo.orignY + (yscale - appPos.y) * appInfo.needMoveY / yscale;
                                                    }
                                                    offsetX = appPos.x.dp
                                                    offsetY = appPos.y.dp
                                                    LogUtils.e("apppos x=${appPos}")
                                                }
                                            }
                                        }
                                    ) { change, dragAmount ->
                                        change.consumeAllChanges()
                                        it.posFx += dragAmount.x
                                        it.posFy += dragAmount.y
                                        LogUtils.e("offx=${change.position.x} offy=${change.position}")
                                        it.posX = it.posFx.toDp().value.toInt()
                                        it.posY = it.posFy.toDp().value.toInt()
                                        offsetX += dragAmount.x.toDp()
                                        offsetY += dragAmount.y.toDp()
                                    }
                                }
                                .background(Color.Transparent)
                                .clickable( interactionSource = MutableInteractionSource(),indication=null){
                                    LauncherUtils.startApp(context, it)
                                }) {
                            it.icon?.let { icon ->
                                Image(
                                    icon.asImageBitmap(), contentDescription = "",
                                    modifier = Modifier.size(56.dp, 56.dp)
                                )

                            }
                            Text(
                                text = it.name ?: "",
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(4.dp, 10.dp, 4.dp, 0.dp)
                            )
                        }

                    }
                }
            }
        }
    }
}
