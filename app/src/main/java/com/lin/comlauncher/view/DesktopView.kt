package com.lin.comlauncher.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.lin.comlauncher.entity.AppInfoBaseBean
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.ui.theme.MyBasicColumn
import com.lin.comlauncher.ui.theme.pagerFlingBehavior
import com.lin.comlauncher.util.LauncherConfig
import com.lin.comlauncher.util.LogUtils
import com.lin.comlauncher.viewmodel.HomeViewModel

var lastTime = System.currentTimeMillis()
var isStop = false

@Composable
fun DesktopView(lists: AppInfoBaseBean, viewModel: HomeViewModel) {
    var time1 = System.currentTimeMillis()
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
    var currentSelect = remember { mutableStateOf(0) }
    var animFinish = remember { mutableStateOf(false) }


    var homeList = HashMap<Int, ArrayList<ApplicationInfo>>()
    var toolBarList = lists.homeList.filter { it.position==LauncherConfig.POSITION_TOOLBAR }
    for (i in 0 until lists.cellNum) {
        var list = ArrayList<ApplicationInfo>()
        list.addAll(toolBarList);
        homeList.put(i, list);
    }

    lists.homeList.forEach {
        when (it.position) {
            LauncherConfig.POSITION_HOME -> {
                homeList.get(it.cellIndex)?.add(it)
            }
        }
    }
    Row(
        modifier = Modifier
                .width(width = width.dp)
                .height(height = height.dp)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(
                    state,
                    flingBehavior = pagerFlingBehavior(
                        state,
                        (homeList?.size ?: 0)
                    )
                )
    ) {
        var pos = offsetX.value
        homeList?.let { homeList ->
            if (homeList.size == 0)
                return@let
            if (state.maxValue > 0 && state.maxValue < 10000f) {
                scrollWidth.value = state.maxValue
            }
            var selIndex = if (scrollWidth.value == 0) 0
            else state.value / (scrollWidth.value / (homeList.size))
            currentSelect.value = if (selIndex >= homeList.size) homeList.size - 1 else selIndex

            for (index in 0 until lists.cellNum) {
                homeList.get(index)?.let { applist ->
                    Column(
                        modifier = Modifier
                                .width(width = width.dp)
                                .height(height = height.dp)
                    ) {
                        if ((currentSelect.value == index && dragInfoState.value != null)
                                || (dragInfoState.value == null && Math.abs(currentSelect.value - index) <= 1)
                        )
                            MyBasicColumn() {
                                applist.forEach {
                                    if(it.position==LauncherConfig.POSITION_HOME&&it.visible){
                                        IconView(
                                            it = it,
                                            applist = applist,
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
        }
    }
    var applist = homeList?.get(currentSelect.value) ?: ArrayList()
    applist.forEach {
        if(it.position==LauncherConfig.POSITION_TOOLBAR){
            IconView(
                it = it,
                applist = applist,
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

    var pos = offsetY.value
    if (dragUpState.value) {
        dragInfoState?.value?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .size(it.width.dp, it.height.dp)
                        .offset(it.posX.dp, it.posY.dp)
            ) {
                IconViewDetail(it = it)
            }
        }
    }
    var time = System.currentTimeMillis() - lastTime
    if (time > 0) {
        if (time > 30) {
            Text(
                text = "",
                Modifier.offset(20.dp, 30.dp)
            )
        } else {
            Text(
                text = "fps:${1000 / time}",
                Modifier.offset(20.dp, 30.dp)
            )
        }
        lastTime = System.currentTimeMillis();
        LogUtils.e("use time = ${System.currentTimeMillis()-time1}")
    }


}

