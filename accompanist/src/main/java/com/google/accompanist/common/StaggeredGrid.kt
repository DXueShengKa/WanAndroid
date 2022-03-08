package com.google.accompanist.common

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.abs


@Composable
fun LazyStaggeredGrid(
    modifier: Modifier = Modifier,
    cells: StaggeredCells,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    content: StaggeredGridScope.() -> Unit,
) {
    val scope = StaggeredGridScopeImpl().apply(content)

    BoxWithConstraints(
        modifier = modifier
    ) {
        StaggeredGrid(
            scope = scope,
            padding = contentPadding,
            columnsNumber = if (cells is StaggeredCells.Fixed) cells.count else maxOf(
                (maxWidth / (cells as StaggeredCells.Adaptive).minSize).toInt(),
                1
            ),
            state = state,
        )
    }
}



@Composable
private fun StaggeredGrid(
    scope: StaggeredGridScopeImpl,
    padding: PaddingValues,
    columnsNumber: Int,
    state: LazyListState,
) {

    val states = Array(columnsNumber) { state }
    for (i in 1 until columnsNumber)
        states[i] = rememberLazyListState()

    val layoutDirection = LocalLayoutDirection.current
    val coroutineScope = rememberCoroutineScope()
    val flingDecay = rememberSplineBasedDecay<Float>()

    val nestedScroll = object : NestedScrollConnection {

        override suspend fun onPreFling(available: Velocity): Velocity {
            coroutineScope.launch {
                joinAll(*Array(states.size) { launch { flingDecay.flingBehavior(-available.y, states[it]) } })
            }

            return super.onPreFling(available)
        }

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            states.forEach {
                it.dispatchRawDelta(-available.y)
            }
            return super.onPreScroll(available, source)
        }

        suspend fun DecayAnimationSpec<Float>.flingBehavior(
            i: Float,
            state: ScrollableState,
        ) {
            if (abs(i) > 1f) {
                var velocityLeft = i
                var lastValue = 0f
                AnimationState(
                    initialValue = 0f,
                    initialVelocity = i,
                ).animateDecay(this) {
                    val delta = value - lastValue
                    lastValue = value
                    velocityLeft = this.velocity
                    val c = state.dispatchRawDelta(delta)
                    if (abs(delta - c) > 0.5f) this.cancelAnimation()
                }
                state.scrollBy(velocityLeft)
            } else {
                state.scrollBy(i)
            }
        }
    }

    Row(Modifier.nestedScroll(nestedScroll)) {
        repeat(columnsNumber) {
            LazyColumn(
                modifier = Modifier
                    .weight(1F)
                    .nestedScroll(VerticalScrollConsumer),
                state = states[it],
                contentPadding = PaddingValues(
                    start = if (it == 0) padding.calculateLeftPadding(layoutDirection) else 0.dp,
                    end = if (it == columnsNumber - 1) padding.calculateRightPadding(layoutDirection) else 0.dp,
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
            ) {
                for (i in scope.content.indices step columnsNumber) {
                    if (scope.content.size > i + it) {
                        item(content = scope.content[i + it])
                    }
                }
            }
        }
    }

}


sealed class StaggeredCells {
    /**
     * Combines cells with a fixed number of columns.
     */
    class Fixed(val count: Int) : StaggeredCells()

    /**
     * Combine cells with an adaptive number of columns depends on each screen with the given [minSize]
     */
    class Adaptive(val minSize: Dp) : StaggeredCells()
}


private class StaggeredGridScopeImpl : StaggeredGridScope {
    private val _data = mutableListOf<@Composable LazyItemScope.() -> Unit>()

    val content get() = _data.toList()

    override fun item(content: @Composable LazyItemScope.() -> Unit) {
        _data.add(content)
    }

    override fun items(count: Int, itemContent: @Composable LazyItemScope.(index: Int) -> Unit) {
        repeat(count) {
            _data.add {
                itemContent(it)
            }
        }
    }

    override fun <T> items(
        items: Array<T>,
        itemContent: @Composable LazyItemScope.(item: T) -> Unit
    ) {
        items.forEach {
            _data.add {
                itemContent(it)
            }
        }
    }


    override fun <T> items(
        items: List<T>,
        itemContent: @Composable LazyItemScope.(item: T) -> Unit
    ) {
        items.forEach {
            _data.add {
                itemContent(it)
            }
        }
    }
}

interface StaggeredGridScope {
    /**
     * Add a single items
     *
     * @param content the content
     */
    fun item(content: @Composable LazyItemScope.() -> Unit)


    /**
     * Add an item that will be repeated [count] times
     *
     * @param count count of times
     * @param itemContent items content
     */
    fun items(count: Int, itemContent: @Composable LazyItemScope.(index: Int) -> Unit)


    /**
     * Add an array of items
     *
     * @param items items array
     * @param itemContent items content
     */
    fun <T> items(items: Array<T>, itemContent: @Composable LazyItemScope.(item: T) -> Unit)


    /**
     * Add an list of items
     *
     * @param items items list
     * @param itemContent items content
     */
    fun <T> items(items: List<T>, itemContent: @Composable LazyItemScope.(item: T) -> Unit)
}


private val VerticalScrollConsumer = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource) =
        available.copy(x = 0f)

    override suspend fun onPreFling(available: Velocity) = available.copy(x = 0f)
}