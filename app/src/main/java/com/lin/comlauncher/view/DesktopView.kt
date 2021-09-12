package com.lin.comlauncher.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lin.comlauncher.entity.AppInfoBaseBean
import com.lin.comlauncher.ui.theme.MyBasicColumn
import com.lin.comlauncher.ui.theme.pagerFlingBehavior
import com.lin.comlauncher.util.LauncherUtils

@Composable
fun DesktopView(applist: State<AppInfoBaseBean?>){
    var width = LocalConfiguration.current.screenWidthDp
    var height = LocalConfiguration.current.screenHeightDp

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
            Column(modifier = Modifier
                .width(width = width.dp)
                .height(height = height.dp)) {
                MyBasicColumn(){
                    list.forEach {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .size(it.width.dp, it.height.dp)
                                .offset(it.posX.dp, it.posY.dp)
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