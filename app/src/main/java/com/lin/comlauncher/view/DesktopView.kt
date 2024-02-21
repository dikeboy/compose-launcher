package com.lin.comlauncher.view

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.lin.comlauncher.entity.AppInfoBaseBean
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.ui.theme.MyBasicColumn
import com.lin.comlauncher.ui.theme.pagerFlingBehavior
import com.lin.comlauncher.ui.theme.pagerLazyFlingBehavior
import com.lin.comlauncher.util.DisplayUtils
import com.lin.comlauncher.util.LauncherConfig
import com.lin.comlauncher.util.LogUtils
import com.lin.comlauncher.viewmodel.HomeViewModel

var lastTime = System.currentTimeMillis()

@Composable
fun DesktopView(lists: AppInfoBaseBean, viewModel: HomeViewModel, version:MutableState<Int>) {
    var time1 = System.currentTimeMillis()
    var width = LocalConfiguration.current.screenWidthDp
    var height = LocalConfiguration.current.screenHeightDp
    var widthPx = DisplayUtils.dpToPx(width);
    val state = rememberLazyListState()
    var foldOpenState = remember{ mutableStateOf<MutableList<ApplicationInfo>>(mutableListOf()) }
//    var scrollWidth = remember { mutableStateOf(0) }
    var context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val coroutineAnimScope = rememberCoroutineScope()

    var dragInfoState = remember { mutableStateOf<ApplicationInfo?>(null) }
    var dragUpState = remember {
        mutableStateOf(false)
    }

    var offsetX = remember { mutableStateOf(0.dp) }
    var offsetY = remember { mutableStateOf(0.dp) }
    var currentSelect = remember { mutableStateOf(0) }
    var animFinish = remember { mutableStateOf(false) }

    var homeList = lists.homeList
    var toolBarList = lists.toobarList


    //draw dot
    var dotWidth = 8;
    var indicationDot = homeList.size * dotWidth + (homeList.size - 1) * 6
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .width(width = indicationDot.dp)
            .height(height = height.dp)
            .offset(
                (width.dp - indicationDot.dp) / 2, (height - 150).dp
            )
    ) {
        homeList.forEachIndexed { index, arrayList ->
            Box(
                modifier = Modifier
                    .size(dotWidth.dp)
                    .clip(CircleShape)
                    .background(Color(if (currentSelect.value == index) 0xccffffff else 0x66ffffff))
            )
        }
    }


    // draw toolbar
    lists.toobarList?.let { applist ->
        var homelist = homeList?.getOrNull(currentSelect.value) ?: ArrayList()
        MyBasicColumn(modifier = Modifier
            .zIndex(zIndex = 0f)
        )
        {
            applist?.forEachIndexed { index, it ->
                IconView(
                    it = it,
                    dragUpState = dragUpState,
                    foldOpen = foldOpenState
                )
            }
        }
//
    }

    var pos = offsetX.value

    LazyRow(

        modifier = Modifier
            .offset(0.dp, 0.dp)
            .width(width = width.dp)
            .height(height = height.dp)
            .pointerInput(0) {
                detectLongPress(
                    context = context,
                    toolList = toolBarList!!,
                    homeList = homeList,
                    currentSel = currentSelect,
                    coroutineScope = coroutineScope,
                    coroutineAnimScope = coroutineAnimScope,
                    dragInfoState = dragInfoState,
                    animFinish = animFinish,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    dragUpState = dragUpState,
                    state = state,
                    foldOpenState
                )
            },
        state = state,
        flingBehavior = pagerLazyFlingBehavior(
            state,
            (lists.homeList?.size ?: 0)
        )
    ) {
        currentSelect.value = state.firstVisibleItemIndex
        lists.homeList?.let { homeList ->
            if (homeList.size == 0)
                return@let

            lists.homeList?.forEachIndexed { index, applist ->
                item {
                    Column(
                        modifier = Modifier
                            .width(width = width.dp)
                            .height(height = height.dp)
                            .offset(0.dp, 0.dp)

                    ) {
                        MyBasicColumn() {
                            applist.forEach {
                                IconView(
                                    it = it,
                                    dragUpState = dragUpState,
                                    foldOpen = foldOpenState
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    //draw fold
    if(foldOpenState.value.size>0){
        Box(modifier = Modifier
            .size(width.dp, height.dp)
            .clickable {
                foldOpenState.value = mutableListOf()
            })
        {
            Box(
                modifier = Modifier
                    .size(width.dp - 20.dp, 320.dp)
                    .offset(10.dp, (height.dp - 320.dp) / 2)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0.3f, 0.3f, 0.3f, 0.8f))
            ){
                foldOpenState.value.forEach {
                    LogUtils.e("foldSize=${it.posX}  ${it.posY}")

                    IconView(
                        it = it,
                        dragUpState = dragUpState,
                        foldOpen = foldOpenState
                    )
                }
            }
        }
    }


    if (dragUpState.value) {
        dragInfoState?.value?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(it.width.dp, it.height.dp)
                    .offset(it.posX.dp, it.posY.dp)
            ) {
//                LogUtils.e("dragUp = ${dragUpState.value}")
                IconViewDetail(it = it)
            }
        }
    }
//    LogUtils.e("usetime=${System.currentTimeMillis()-time1}")
//    var time = System.currentTimeMillis() - lastTime
//    if (time > 0) {
//        if (time > 30) {
//            Text(
//                text = "fps:30",
//                Modifier.offset(20.dp, 30.dp)
//            )
//        } else {
//            Text(
//                text = "fps:${1000 / time}",
//                Modifier.offset(20.dp, 30.dp)
//            )
//        }
//        lastTime = System.currentTimeMillis();
//    }
}


