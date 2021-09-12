package com.lin.comlauncher.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.dp
import com.lin.comlauncher.entity.ApplicationInfo

object SortUtils {
    fun resetPos(list:SnapshotStateList<ApplicationInfo>
    ){
        list.sortWith(comparator = { a1, a2->
            if(a1.posY==a2.posY){
              a1.posX-a2.posX
            }else{
                a1.posY-a2.posY
            }

        })

        list.forEachIndexed { index, ai ->
            ai.posX = (index%4)*ai.width
            ai.posY = index/4*100+80
            LogUtils.e("pos=${ai.posX} posY=${ai.posY} name=${ai.name}")
        }
    }
}

