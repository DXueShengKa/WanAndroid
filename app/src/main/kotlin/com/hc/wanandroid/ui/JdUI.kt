package com.hc.wanandroid.ui

import android.icu.text.CaseMap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.LocalContentColor
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import com.google.accompanist.common.HorizonEquidistant
import com.google.accompanist.common.fixed2
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun JdUI() {
    val px = with(LocalDensity.current) { 50.dp.toPx() }
    Column {
        var progress by remember { mutableStateOf(0f) }
        val state = rememberLazyListState()

        progress =
            if (state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset < px) {
                state.firstVisibleItemScrollOffset / px
            } else {
                1f
            }

        Title(progress, Modifier.fillMaxWidth())
        Content(state)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
private fun Content(state: LazyListState) {
    val myItems = remember {
        generateSequence { Random.nextInt().toString() }.take(40).toList()
    }

    LazyColumn(
        state = state
    ) {
        item {
            Classify(Modifier.height(50.dp))
        }
        item {
            MyPage()
        }
        item {
            OptionBox()
        }

        fixed2(4) {
            if (it == 0)
                Seckill("秒杀", "快抢购") {
                    var time by remember(true) { mutableStateOf(20) }
                    LaunchedEffect(true) {
                        while (isActive) {
                            time -= 1
                            if (time < 1) time = 20
                            delay(1000)
                        }
                    }
                    Text("倒计时${time}秒", Modifier.background(Color.Green, CircleShape), Color.White)
                }
            else
                Seckill("大$it", "小$it") {
                    Text("-->$it", color = Color.Red)
                }
        }

        stickyHeader {
            Row(Modifier.fillParentMaxWidth().height(50.dp).border(1.dp, Color.Black)) {
                Text("111", Modifier.weight(1f))
                Text("2222", Modifier.weight(1f))
                Text("4444", Modifier.weight(1f))
            }

        }

        fixed2(myItems.size,10.dp){
            Card(Modifier.fillMaxWidth().height(100.dp)) {
                Text(myItems[it])
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun MyPage() {
    Box {

        val startIndex = Int.MAX_VALUE / 2
        val pageContent = 8
        val state = rememberPagerState(startIndex)

        HorizontalPager(
            Int.MAX_VALUE,
            Modifier.fillMaxWidth().height(150.dp),
            state,
            itemSpacing = 10.dp
        ) {
            PagerItem((it - startIndex).floorMod(pageContent))
        }

        PageIndex(
            Modifier.padding(bottom = 16.dp).align(Alignment.BottomCenter),
            pageContent,
            (state.currentPage - startIndex).floorMod(pageContent)
        )
    }

}


@ExperimentalPagerApi
@Composable
private fun OptionBox() {
    Column {
        val state = rememberPagerState(0)
        HorizontalPager(2, state = state) {
            HorizonEquidistant(5, Modifier.fillMaxWidth()) {
                repeat(10) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Star, null)
                        Text(it.toString())
                    }
                }

            }
        }

        PageIndex(
            Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
            state.pageCount,
            state.currentPage
        )
    }
}

@Composable
private fun PageIndex(modifier: Modifier, pageContent: Int, selectIndex: Int) {
    Row(modifier) {
        for (i in 0 until pageContent) {
            val w = remember(i) { mutableStateOf(4.dp) }
            w.value = if (i == selectIndex) 10.dp else 4.dp
            Box(
                Modifier
                    .padding(horizontal = 2.dp)
                    .height(4.dp)
                    .width(w.value)
                    .background(Color.Blue, CircleShape)
            )
        }
    }
}

private fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}

@Composable
private fun PagerItem(index: Int) {
    Card {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(index.toString())
        }
    }
}

@Composable
private fun Classify(modifier: Modifier = Modifier) {
    val items = remember {
        generateSequence { Random.nextInt(20).toString() }.take(10).toList()
    }
    var selectIndex by remember { mutableStateOf(0) }
    ScrollableTabRow(selectIndex, modifier, edgePadding = 0.dp) {
        items.forEachIndexed { i, s ->
            Tab(
                selectIndex == i,
                { selectIndex = i }
            ) {
                Text(s, Modifier.fillMaxHeight())
            }
        }
    }
}

@Composable
private fun Title(progress: Float, modifier: Modifier = Modifier) {
    MotionLayout(
        start = startConstraintSet,
        end = ConstraintSet {
            val image = createRefFor("image")
            val msg = createRefFor("msg")
            val search = createRefFor("search")
//            val classify = createRefFor("classify")

            constrain(image) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }

            if (progress < 1) {
                val imageText = createRefFor("imageText")
                constrain(imageText) {
                    start.linkTo(image.end)
                    top.linkTo(image.top)
                }
            }

            constrain(msg) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            }

            constrain(search) {
                start.linkTo(image.end)
                end.linkTo(msg.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
            }

        },
        progress = progress,
        modifier = modifier.height((100 - progress * 50).dp)
    ) {

        Icon(Icons.Default.AccountBox, null, Modifier.layoutId("image").size(50.dp))

        if (progress < 1)
            Text(
                "全厂5折",
                Modifier.layoutId("imageText"),
                color = LocalContentColor.current.copy(alpha = 1 - progress)
            )

        Icon(Icons.Default.MailOutline, null, Modifier.layoutId("msg").size(50.dp))

        var searchText by remember { mutableStateOf("") }

        OutlinedTextField(
            searchText, { searchText = it },
            Modifier.layoutId("search").height(50.dp),
            leadingIcon = {
                Icon(Icons.Default.Search, null)
            },
            trailingIcon = {
                Icon(Icons.Default.Create, null)
            },
            shape = CircleShape
        )
    }

}

private val startConstraintSet = ConstraintSet {
    val image = createRefFor("image")
    val imageText = createRefFor("imageText")
    val msg = createRefFor("msg")
    val search = createRefFor("search")

    constrain(image) {
        start.linkTo(parent.start)
        top.linkTo(parent.top)
    }
    constrain(imageText) {
        start.linkTo(image.end)
        top.linkTo(image.top)
    }
    constrain(msg) {
        end.linkTo(parent.end)
        top.linkTo(parent.top)
    }

    constrain(search) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(image.bottom)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
    }

}

@Composable
private fun Seckill(title: String, subTitle: String, tip: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth()) {
            Text(title)
            tip()
        }
        Text(subTitle, fontSize = 12.sp)
        Image(Icons.Default.DateRange, null)
        Text("商品", fontSize = 14.sp, color = Color.Blue)
    }
}