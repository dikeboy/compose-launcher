package com.lin.comlauncher.ui.theme

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import com.lin.comlauncher.util.LogUtils
import kotlin.math.abs
import kotlin.reflect.KProperty

class ComPager() : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        return 0f;
    }
}

@Composable
fun pagerFlingBehavior(state: ScrollState,childNum: Int): FlingBehavior {
    val flingSpec = rememberSplineBasedDecay<Float>()
    return remember(flingSpec) {
        PagerFling(flingSpec, state,childNum = childNum)
    }
}

class PagerFling(private val flingDecay: DecayAnimationSpec<Float>,val state:ScrollState,
                 var childNum:Int) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        // come up with the better threshold, but we need it since spline curve gives us NaNs
//        Log.e("lin","velocity=$initialVelocity,value=${state.value},maxValue=${state.maxValue}")
        return if (abs(initialVelocity) >= 0f) {
            var velocityX = initialVelocity
            var childWidth = state.maxValue/(childNum-1)
            var destValue = 0f
            if(childWidth==0)
                return 0f
            var childLeft = state.value%childWidth;
            if(abs(velocityX)<500){
                if(childLeft<childWidth/2){
                    destValue = state.value.toFloat()-childLeft
                }else{
                    destValue = state.value.toFloat()-childLeft+childWidth;
                }
            }else{
                if(velocityX<0)
                    destValue = state.value.toFloat()-childLeft
                else
                    destValue = state.value.toFloat()-childLeft+childWidth;
            }

            var velocityLeft = initialVelocity
            var startPos = state.value
            animate(state.value.toFloat(),destValue,0f,tween(300)){value, velocity ->
                velocityLeft = value-startPos
                scrollBy(velocityLeft)
                startPos = value.toInt()
            }
            velocityLeft
        } else {
            initialVelocity
        }
    }
}

@Composable
fun MyBasicColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints)
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Track the y co-ord we have placed children up to
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(x = 0, y = 0)
//                Log.e("linlog","width=${placeable.width} height=${placeable.height} index=$index")
            }
        }
    }
}


