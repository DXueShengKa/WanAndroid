package com.google.accompanist.common

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlin.math.abs


@SuppressLint("RememberReturnType")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun <T> BrowsePicture(
    modifier: Modifier = Modifier,
    state: BrowsePictureState<T>,
    contentModifier: Modifier = Modifier,
    content: @Composable (contentModifier: Modifier, data: T) -> Unit,
) {

    remember(state.pagerState.currentPage) {
        state.flushed()
    }

    HorizontalPager(
        state.pageCount,
        state = state.pagerState,
        modifier = modifier
            .onSizeChanged {
                state.imageWidth = it.width.toFloat()
            }
    ) { page ->
        content(
            contentModifier.then(
                if (state.pagerState.currentPage == page) Modifier
                    .graphicsLayer(
                        scaleY = state.scale,
                        scaleX = state.scale,
                        translationX = state.offset.x,
                        translationY = state.offset.y
                    )
                    .pointerInput(page, state.detectZoom())
                else Modifier
            ),
            state[page]
        )
    }

}

@Stable
class BrowsePictureState<T>() {
    private val datas = mutableListOf<T>()

    @ExperimentalPagerApi
    val pagerState = PagerState()

    var pageCount by mutableStateOf(0)

    private val scaleState = mutableStateOf(1f)
    val scale by scaleState
    var offset by mutableStateOf(Offset.Zero)

    var imageWidth = 0f

    /**
     * 是否消耗触摸事件
     */
    private var isConsume = false

    operator fun get(index: Int): T {
        return datas[index]
    }

    fun flushed() {
        scaleState.value = 1f
        offset = Offset.Zero
    }

    @OptIn(ExperimentalPagerApi::class)
    fun addAll(elements: Collection<T>) {
        datas.addAll(elements)
        pageCount = datas.size
    }

    fun detectZoom(): suspend PointerInputScope.() -> Unit = {
        forEachGesture {

            var zoom = 1f
            var pastTouchSlop = false
            val touchSlop = viewConfiguration.touchSlop
            var pan = Offset.Zero

            awaitPointerEventScope {
                do {
                    val event = awaitPointerEvent()
                    val canceled = event.changes.fastAny { it.positionChangeConsumed() }
                    if (!canceled) {
                        val zoomChange = event.calculateZoom()
                        val panChange = event.calculatePan()

                        if (!pastTouchSlop) {
                            zoom *= zoomChange
                            pan += panChange

                            val centroidSize = event.calculateCentroidSize(useCurrent = false)
                            val zoomMotion = abs(1 - zoom) * centroidSize
                            val panMotion = pan.getDistance()

                            if (zoomMotion > touchSlop || panMotion > touchSlop) {
                                pastTouchSlop = true
                            }
                        }

                        if (pastTouchSlop) {
                            if (zoomChange != 1f || panChange != Offset.Zero) {
                                onZoomChange(zoomChange, panChange)
                            }
                        }

                        if (isConsume) {
                            event.changes.fastForEach {
                                if (it.positionChanged()) {
                                    it.consumeAllChanges()
                                }
                            }
                        }

                    }
                } while (!canceled && event.changes.fastAny { it.pressed })
            }
        }
    }

    private fun onZoomChange(zoomChange: Float, offsetChange: Offset) {
        if (scale < 1.1f) {
            offset = Offset.Zero
        } else {
            offset += offsetChange
        }

        (scale * zoomChange)
            .takeIf { it in 1f..3f }
            ?.also {
                scaleState.value = it

                val currWidth = imageWidth * (it / 3f)
                isConsume = it > 1.1f && abs(offset.x) < currWidth

//                Log.w("detectZoom", "$offset $currWidth $imageWidth")
            }
    }
}