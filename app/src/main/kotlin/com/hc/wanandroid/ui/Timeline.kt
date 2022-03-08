package com.hc.wanandroid.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


//@Preview(showBackground = true)
@Composable
fun TimelineUI() {

    val density = LocalDensity.current

    val stete = remember { TimelineState(density) }

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(stete.nestedScrollConnection)
    ) {

        val lazy = rememberLazyListState()

        stete.firstIndex = lazy.firstVisibleItemIndex

        LazyColumn(state = lazy, contentPadding = PaddingValues(top = stete.topBarHeightDp)) {
            items(100) { index ->
                Text(
                    "I'm item $index", modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        Box(
            Modifier
                .height(stete.topBarHeightDp)
                .fillMaxWidth()
                .background(Color.DarkGray)
        ) {
            Text("当前高度 ${stete.topBarHeightDp}")
        }
    }
}

private class TimelineState(
    private val density: Density
) {
    private val minHeight = 56.dp
    private val maxHeight = 200.dp

    private val minHeightPx: Float
    private val maxHeightPx: Float

    var firstIndex = 0

    init {
        with(density) {
            minHeightPx = minHeight.toPx()
            maxHeightPx = maxHeight.toPx()
        }
    }

    private var topBarHeight by mutableStateOf(maxHeightPx)

    val topBarHeightDp: Dp get() = with(density) { topBarHeight.toDp() }

    //嵌套滑动
    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            //只有在列表滑倒首位时才处理滑动
            if (firstIndex != 0) return Offset.Zero
            //y轴滑动距离
            val delta = available.y
            //计算滑动后的高度变化
            val newHeight = topBarHeight + delta
            //高度小于最低值时不处理
            if (newHeight < minHeightPx) {
                topBarHeight = minHeightPx
                return Offset.Zero
            }
            //高度大于最高值时不处理
            if (newHeight > maxHeightPx) {
                topBarHeight = maxHeightPx
                return Offset.Zero
            }

            topBarHeight = newHeight
            //消耗y轴的滑动
            return Offset(x = 0f,y = delta)
        }
    }

}