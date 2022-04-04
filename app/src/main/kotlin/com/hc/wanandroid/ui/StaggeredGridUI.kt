package com.hc.wanandroid.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hc.wanandroid.component.VerticalStaggeredGrid
import com.hc.wanandroid.utils.ToastUtils
import kotlinx.coroutines.delay
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.streams.toList


@Composable
fun StaggeredGridUI() {
    var refres by remember { mutableStateOf(1) }
    var isRefreshing by remember { mutableStateOf(false) }

    val list = remember {
        mutableStateListOf<Dp>()
    }

    LaunchedEffect(refres) {
        isRefreshing = true
        delay(1500)
        list.clear()
        Stream.generate {
            Random.nextInt(100, 200).dp
        }.limit(40)
            .toList()
            .also(list::addAll)
        isRefreshing = false
        ToastUtils.showShort("刷新完毕")
    }

    VerticalStaggeredGrid(
        list.size,
        spanCount = 3,
        isRefreshing = isRefreshing,
        onRefresh = {
            refres++
        }
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, Color.Green)
                .height(list[it])
        )
    }
}