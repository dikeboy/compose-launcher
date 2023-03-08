package com.lin.comlauncher.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.lin.comlauncher.entity.AppInfoBaseBean
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.ui.theme.MyBasicColumn
import com.lin.comlauncher.ui.theme.pagerFlingBehavior
import com.lin.comlauncher.util.LogUtils
import com.lin.comlauncher.viewmodel.HomeViewModel

var lastTime = System.currentTimeMillis()
@Composable
fun DesktopView(lists: AppInfoBaseBean, viewModel: HomeViewModel) {
    var time1=System.currentTimeMillis()
    var width = LocalConfiguration.current.screenWidthDp
    var height = LocalConfiguration.current.screenHeightDp
    val state = rememberScrollState()
    var scrollWidth = remember { mutableStateOf(0) }
    var context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val coroutineAnimScope = rememberCoroutineScope()

    var dragInfoState = remember { mutableStateOf<ApplicationInfo?>(null) }
    var dragUpState = remember {
        mutableStateOf(false)
    }

    var offsetX = remember { mutableStateOf(0.dp) }
    var offsetY = remember { mutableStateOf(0.dp) }
    var currentSelect = remember { mutableStateOf(0)}
    var animFinish = remember { mutableStateOf(false) }

    var homeList = lists.homeList
    var toolBarList =lists.toobarList
    Row(
        modifier = Modifier
                .width(width = width.dp)
                .height(height = height.dp)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(
                    state,
                    flingBehavior = pagerFlingBehavior(
                        state,
                        (lists.homeList?.size ?: 0)
                    )
                )
    ) {
        var pos = offsetX.value
        lists.homeList?.let {homeList->
            if(homeList.size==0)
                return@let
            if (state.maxValue > 0 && state.maxValue < 10000f) {
                scrollWidth.value = state.maxValue
            }
            var selIndex = if (scrollWidth.value == 0) 0
            else state.value / (scrollWidth.value / (homeList.size))
            currentSelect.value = if(selIndex>=homeList.size) homeList.size-1 else selIndex

            lists.homeList?.forEachIndexed { index, applist ->
                Column(
                    modifier = Modifier
                            .width(width = width.dp)
                            .height(height = height.dp)
                ) {
                    if (Math.abs(currentSelect.value-index)<=1)
                        MyBasicColumn() {
                            applist.forEach {
                                IconView(
                                    it = it,
                                    applist = applist,
                                    toolList = toolBarList!!,
                                    state = state,
                                    coroutineScope = coroutineScope,
                                    coroutineAnimScope = coroutineAnimScope,
                                    dragInfoState = dragInfoState,
                                    animFinish = animFinish,
                                    offsetX = offsetX,
                                    offsetY = offsetY,
                                    dragUpState = dragUpState
                                )
                            }
                        }
                }
            }
        }
    }

    //draw dot
    var dotWidth = 8;
    var indicationDot = homeList.size *dotWidth +(homeList.size-1)*6
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
                .width(width =indicationDot.dp)
                .height(height = height.dp).offset((width.dp - indicationDot.dp)/2,(height-150).dp
                )){
        homeList.forEachIndexed { index, arrayList ->
            Box(
                modifier = Modifier.size(dotWidth.dp).clip(CircleShape)
                        .background(Color( if(currentSelect.value ==index) 0xccffffff else 0x66ffffff))
            )
        }
    }


    // draw toolbar
    lists.toobarList?.let { applist->
        MyBasicColumn(modifier = Modifier.zIndex(zIndex = 0f))
        {
            var homelist = homeList?.getOrNull(currentSelect.value)?: ArrayList()
            applist?.forEachIndexed { index, it ->
                    IconView(
                        it = it,
                        applist = homelist,
                        toolList = applist,
                        state = state,
                        coroutineScope = coroutineScope,
                        coroutineAnimScope = coroutineAnimScope,
                        dragInfoState = dragInfoState,
                        animFinish = animFinish,
                        offsetX = offsetX,
                        offsetY = offsetY,
                        dragUpState = dragUpState
                    )
            }
        }
//
    }

    if(dragUpState.value){
        dragInfoState?.value?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .size(it.width.dp, it.height.dp)
                        .offset(it.posX.dp, it.posY.dp)
            ) {
//                LogUtils.e("dragUp = ${dragUpState.value}")
                IconViewDetail(it = it,dragUpState=dragUpState)
            }
        }
    }
    var time = System.currentTimeMillis() - lastTime
    if(time>0){
        if(time>30){
            Text(text = "fps:30",
                Modifier.offset (20.dp,30.dp))
        }else{
            Text(text = "fps:${1000 /time}",
                Modifier.offset (20.dp,30.dp))
        }
        lastTime = System.currentTimeMillis();

    }
//    LogUtils.e("load time ${System.currentTimeMillis()-time1}")

}

