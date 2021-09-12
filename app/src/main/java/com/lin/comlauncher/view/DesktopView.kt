package com.lin.comlauncher.view

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lin.comlauncher.entity.AppInfoBaseBean
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.ui.theme.MyBasicColumn
import com.lin.comlauncher.ui.theme.pagerFlingBehavior
import com.lin.comlauncher.util.LauncherUtils
import com.lin.comlauncher.util.LogUtils
import com.lin.comlauncher.util.SortUtils

@Composable
fun DesktopView(applist: State<AppInfoBaseBean?>){
    var width = LocalConfiguration.current.screenWidthDp
    var height = LocalConfiguration.current.screenHeightDp
    LogUtils.e("load")
    val state = rememberScrollState()
    var context = LocalContext.current
    Row(modifier = Modifier
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
            applist.addAll(list)

            Column(modifier = Modifier
                .width(width = width.dp)
                .height(height = height.dp)) {
                MyBasicColumn(){
                    applist.forEach {
                        var offsetX by remember { mutableStateOf(it.posX.dp) }
                        var offsetY by remember { mutableStateOf(it.posY.dp) }
                        var offy = offsetX
                        if(it.isDrag){
                            LogUtils.e("offsetX= ${offsetX} offsetY=${offsetY}")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .size(it.width.dp, it.height.dp)
                                .offset(if(it.isDrag)offsetX else it.posX.dp, if(it.isDrag)offsetY else it.posY.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart={off->
                                            it.isDrag = true
                                        },
                                        onDragEnd = {
                                            it.isDrag = false
                                            it.posX = offsetX.value.toInt()
//                                            it.posY = offsetY.value.toInt()
                                            LogUtils.e("dragex=${it.posX} dragY=${it.posY}")
                                            SortUtils.resetPos(applist)
                                            offsetX = it.posX.dp
                                            offsetY = it.posY.dp
                                            LogUtils.e("dragex=${it.posX} dragY=${it.posY}")

                                        }
                                    ) { change, dragAmount ->
                                        change.consumeAllChanges()
                                        offsetX += dragAmount.x.toDp()
                                        offsetY += dragAmount.y.toDp()
                                    }
                                }
                                .clickable {
                                    LauncherUtils.startApp(context, it)
                                }) {
                            it.icon?.let { icon->
                                Image(icon.asImageBitmap(), contentDescription = "",
                                    modifier = Modifier.size(56.dp,56.dp))

                            }
                            Text(text = it.name?:"",overflow = TextOverflow.Ellipsis,maxLines = 1,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(4.dp,10.dp,4.dp,0.dp))
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun dragEnd(){

}