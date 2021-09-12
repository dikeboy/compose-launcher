package com.lin.comlauncher.view

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.lin.comlauncher.util.LogUtils

@Composable
fun ToolBarView(applist: State<AppInfoBaseBean?>) {
    var width = LocalConfiguration.current.screenWidthDp
    var height = LocalConfiguration.current.screenHeightDp
    var appheight = applist.value?.toobarList?.firstOrNull()?.height?:0
    var context = LocalContext.current
    Row(
        modifier = Modifier
            .width(width = width.dp)
            .height(height = appheight.dp)
            .offset(0.dp,(height-appheight).dp)
    ) {
        applist.value?.toobarList?.forEachIndexed { index, it ->
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement= Arrangement.Center,
                modifier = Modifier
                    .size(it.width.dp, it.height.dp)
                    .clickable {
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