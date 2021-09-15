package com.lin.comlauncher.util

import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.lin.comlauncher.entity.AppPos

class AnimUtils {
}

suspend fun DoTranslateAnim(startPos:AppPos,endPos:AppPos,duration:Int, block: (value: AppPos, velocity: AppPos) -> Unit){
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
        initialValue = startPos,
        targetValue = endPos,
        initialVelocity = AppPos(0, 0),
        animationSpec = tween(duration),
        block = block
    )
}