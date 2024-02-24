package com.lin.comlauncher.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lin.comlauncher.entity.AppManagerBean
import com.lin.comlauncher.entity.AppPos
import com.lin.comlauncher.entity.ApplicationInfo
import com.lin.comlauncher.util.DoTranslateAnim
import com.lin.comlauncher.util.LauncherConfig
import com.lin.comlauncher.util.LauncherUtils
import com.lin.comlauncher.util.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MoreInfoView(
    context: Context,
    homeList: ArrayList<ArrayList<ApplicationInfo>>,
    currentSel: MutableState<Int>,
    appManagerState: MutableState<AppManagerBean?>,
    coroutineScope: CoroutineScope, coroutineAnimScope: CoroutineScope,
    offsetX: MutableState<Dp>, offsetY: MutableState<Dp>,
) {
    var it = appManagerState.value!!
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
                .offset(Math.min(it.startX, LauncherConfig.HOME_WIDTH - 150).dp, it.startY.dp - 50.dp)
                .size(150.dp, 50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0.3f, 0.3f, 0.3f, 0.8f))
    ) {
        Text("Fold", color = Color.White,
            modifier = Modifier.clickable {
                LauncherUtils.addFoldToCurrentPage(homeList.get(currentSel.value), currentSel.value)?.let { addBean ->
                    addBean.posX = it.startX
                    addBean.posY = it.startY
                    LogUtils.e("posy= ${it.startY}  des=${addBean.orignY}")
                    coroutineScope.launch {
                        DoTranslateAnim(
                            AppPos(it.startX, it.startY),
                            AppPos(addBean.orignX, addBean.orignY),
                            200
                        )
                        { appPos, velocity ->
                            addBean.posX = appPos.x
                            addBean.posY = appPos.y
                            offsetX.value = (appPos.x * appPos.y).dp
                            offsetY.value = (appPos.x * appPos.y).dp

                            LogUtils.e("posy= ${offsetX.value}  des=${offsetY.value}")
                        }
                        offsetY.value = addBean.orignY.dp
                        appManagerState.value = null
                    }
                }

            })
        Text("Info", color = Color.White,
            modifier = Modifier.clickable {
                LauncherUtils.goAppDetail(context = context, it.applicationInfo)
                appManagerState.value = null
            })
        Text("Delete", color = Color.White,
            modifier = Modifier.clickable {
                if (it.applicationInfo.appType == LauncherConfig.CELL_TYPE_FOLD) {
                    if (it.applicationInfo.childs.size == 0) {
                        homeList.get(currentSel.value).remove(it.applicationInfo);
                    }
                    appManagerState.value = null
                } else {
                    LauncherUtils.goAppDelete(context = context, it.applicationInfo)
                    appManagerState.value = null
                }

            })
    }
}