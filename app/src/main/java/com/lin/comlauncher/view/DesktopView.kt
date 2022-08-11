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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
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
import com.lin.comlauncher.util.DoTranslateAnim
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
                    if ((currentSelect.value == index && dragInfoState.value != null)
                        || (dragInfoState.value == null&&Math.abs(currentSelect.value-index)<=1))
                        MyBasicColumn() {
                            applist.forEach {
                                IconView(
                                    it = it,
                                    applist = applist,
                                    toolBarList!!,
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
    lists.toobarList?.let { applist->
        MyBasicColumn(modifier = Modifier.zIndex(zIndex = 0f))
        {
            var homelist = homeList?.getOrNull(currentSelect.value)?: ArrayList()
            applist?.forEachIndexed { index, it ->
                    IconView(
                        it = it,
                        applist = homelist,
                        toolList = applist,
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

    var pos = offsetY.value
    if(dragUpState.value){
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
    Text(text = "fps:${1000 / (System.currentTimeMillis() - time1)}",
    Modifier.offset (20.dp,30.dp))

}

