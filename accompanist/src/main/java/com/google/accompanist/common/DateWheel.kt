package com.google.accompanist.common

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.chrono.IsoChronology


/**
 * 日期选择器底部弹出布局
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DateWheelLayout(
    layoutState: DateWheelLayoutState,
    itemHeight: Dp = 34.dp,
    sheetHeight: Dp = 254.dp,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = ContentAlpha.medium),
    content: @Composable () -> Unit
) {
    layoutState.coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .background(MaterialTheme.colors.primary)
            ) {
                Text(
                    "取消",
                    Modifier
                        .clickable { layoutState.hide(false) }
                        .heightCenterVertically(itemHeight)
                        .padding(start = 20.dp),
                    MaterialTheme.colors.onPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    "确定",
                    Modifier
                        .align(Alignment.TopEnd)
                        .clickable { layoutState.hide(true) }
                        .heightCenterVertically(itemHeight)
                        .padding(end = 20.dp),
                    MaterialTheme.colors.onPrimary,
                    textAlign = TextAlign.Center
                )
            }

            DateWheel(
                layoutState.dateWheel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sheetHeight),
                itemHeight = itemHeight,
                selectedContentColor = selectedContentColor,
                unselectedContentColor = unselectedContentColor,
                coroutineScope = layoutState.coroutineScope
            )
        },
        sheetState = layoutState.sheetState,
        content = content
    )
}

@Stable
@OptIn(ExperimentalMaterialApi::class)
class DateWheelLayoutState(
    startDate: LocalDate,
    endDate: LocalDate,
    showDay: Boolean = true,
    private val onDate: ((LocalDate) -> Unit)? = null
) {
    internal lateinit var coroutineScope: CoroutineScope

    val dateWheel = DateWheelState(startDate, endDate, showDay)

    val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)

    fun show() {
        coroutineScope.launch {
            sheetState.animateTo(ModalBottomSheetValue.Expanded)
        }
    }

    fun hide(isOk: Boolean) {
        coroutineScope.launch {
            sheetState.hide()
            if (isOk) onDate?.invoke(dateWheel.currentDate)
        }
    }
}

/**
 * 日期选择器
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DateWheel(
    dateWheel: DateWheelState,
    modifier: Modifier,
    itemHeight: Dp = 34.dp,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = 0.4f),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    dateWheel.coroutineScope = coroutineScope
    dateWheel.dy()

    Row(modifier) {

        val wheelModifier = Modifier
            .fillMaxHeight()
            .weight(1f)

        Wheel(
            items = dateWheel.yearArrayUI,
            modifier = wheelModifier,
            itemHeight = itemHeight,
            swipeableState = dateWheel.yearSwipeable,
            selectedContentColor = selectedContentColor,
            unselectedContentColor = unselectedContentColor,
            coroutineScope = coroutineScope
        )

        Wheel(
            items = dateWheel.monthArrayUI,
            modifier = wheelModifier,
            itemHeight = itemHeight,
            swipeableState = dateWheel.monthSwipeable,
            selectedContentColor = selectedContentColor,
            unselectedContentColor = unselectedContentColor,
            coroutineScope = coroutineScope
        )

        if (dateWheel.showDay)
            Wheel(
                items = dateWheel.dayArrayUI,
                modifier = wheelModifier,
                itemHeight = itemHeight,
                swipeableState = dateWheel.daySwipeable,
                selectedContentColor = selectedContentColor,
                unselectedContentColor = unselectedContentColor,
                coroutineScope = coroutineScope
            )
    }

}

@RequiresApi(26)
@OptIn(ExperimentalMaterialApi::class)
@Stable
class DateWheelState(
    private val start: LocalDate,
    private val end: LocalDate,
    val showDay: Boolean = true
) {
    companion object {
        private const val YEAR = 0
        private const val MONTH = 1
        private const val DAY = 2
    }

    internal var coroutineScope: CoroutineScope? = null

    private val currentDateArray =
        arrayOf(start.year, start.monthValue, start.dayOfMonth)

    var currentDate: LocalDate
        get() = LocalDate.of(currentYear, currentMonth, currentDay)
        set(value) {
            currentDateArray[YEAR] = value.year
            currentDateArray[MONTH] = value.monthValue
            currentDateArray[DAY] = value.dayOfMonth

            coroutineScope.let {
                it ?: throw NullPointerException("请在ui渲染后再设置当前选择的时间")
            }.launch {
                yearSwipeable.animateTo(yearArray.indexOf(currentDateArray[YEAR]))

                delay(200)
                monthSwipeable.animateTo(monthArray.indexOf(value.monthValue))

                delay(200)
                daySwipeable.animateTo(dayArray.indexOf(value.dayOfMonth))
            }
        }

    val currentYear: Int get() = currentDateArray[YEAR]
    val currentMonth: Int get() = currentDateArray[MONTH]
    val currentDay: Int get() = currentDateArray[DAY]


    internal val yearSwipeable = SwipeableState(0)
    private val yearArray = start.run {
        var y = year
        IntArray(end.year - year + 1) { y++ }
    }
    internal val yearArrayUI = Array(yearArray.size) { "${yearArray[it]}年" }


    internal val monthSwipeable = SwipeableState(0)
    private var monthArray = IntArray(0)
        set(value) {
            monthArrayUI = Array(value.size) { "${value[it]}月" }
            field = value
        }
    internal var monthArrayUI by mutableStateOf(Array(0) { "" })

    private fun createMonthArray(year: Int): IntArray {
        if (start.year == end.year) {
            var m = start.monthValue
            return IntArray(end.monthValue - start.monthValue + 1) { m++ }
        }
        var m = if (year == start.year) start.monthValue else 1
        val size = if (year == end.year) end.monthValue else 12

        return IntArray(size - m + 1) { m++ }
    }


    internal val daySwipeable = SwipeableState(0)
    private var dayArray = IntArray(0)
        set(value) {
            dayArrayUI = Array(value.size) { "${value[it]}日" }
            field = value
        }
    internal var dayArrayUI by mutableStateOf(Array(0) { "" })

    private fun createDayArray(year: Int, month: Month): IntArray {
        val size = if (end.year == year && end.month == month) end.dayOfMonth else month.length(
            IsoChronology.INSTANCE.isLeapYear(year.toLong())
        )
        var day = if (start.year == year && start.month == month) start.dayOfMonth else 1
        return IntArray(size - if (day == 1) 0 else day - 1) { day++ }
    }

    init {
        monthArray = createMonthArray(start.year)
        dayArray = createDayArray(start.year, start.month)
    }

    //订阅
    @Composable
    internal fun dy() {

        val y = yearSwipeable.currentValue
        val m = monthSwipeable.currentValue
        val d = daySwipeable.currentValue

        LaunchedEffect(y) {
            monthArray = createMonthArray(yearArray[y])
            dayArray = createDayArray(yearArray[y], Month.of(monthArray[0]))
            monthSwipeable.snapTo(0)

            currentDateArray[YEAR] = yearArray[y]
            currentDateArray[MONTH] = monthArray[0]
            currentDateArray[DAY] = dayArray[0]
        }


        LaunchedEffect(m) {
            if (showDay) {
                dayArray = createDayArray(yearArray[y], Month.of(monthArray[m]))
                daySwipeable.snapTo(0)
                currentDateArray[DAY] = dayArray[0]
            }
            currentDateArray[MONTH] = monthArray[m]

        }

        if (showDay) {
            LaunchedEffect(d) {
                currentDateArray[DAY] = dayArray[d]
            }
        }
    }


    /*
     fun years(range: ClosedRange<LocalDate>):Array<Year>{
        val yearStart = Year.from(range.start)
        val yearEnd = Year.from(range.endInclusive)
        return Array(yearEnd.value - yearStart.value + 1){ Year.of(range.start.year + it) }
    }

    fun months(year: Year,range:ClosedRange<LocalDate>):Array<YearMonth>{
        val yearMonthRange = YearMonth.from(range.start)..YearMonth.from(range.endInclusive)
        return Month.values()
            .map { year.atMonth(it) }
            .filter { it in yearMonthRange }
            .toTypedArray()
    }

    fun days(month: YearMonth,range: ClosedRange<LocalDate>):List<LocalDate>{
        return Array(month.lengthOfMonth()){
            month.atDay(it+1)
        }.filter {
            it in range
        }
    }
     */
}

/**
 * 滚轮选择器
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Wheel(
    items: Array<String>,
    modifier: Modifier,
    itemHeight: Dp = 40.dp,
    swipeableState: SwipeableState<Int> = rememberSwipeableState(0),
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = ContentAlpha.medium),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {

    val itemHeightPx = with(LocalDensity.current) { itemHeight.roundToPx() }
    var spacerHeight by remember { mutableStateOf(Dp.Hairline) }
    val anchorsMap = remember(items) {
        mapOf(*Array(items.size) { itemHeightPx * it.toFloat() to it })
    }

    Box(
        modifier
            .swipeable(
                state = swipeableState,
                anchors = anchorsMap,
                orientation = Orientation.Vertical,
                reverseDirection = true
            )
            .drawWithCache {
                val start = (size.height - itemHeightPx) / 2
                spacerHeight = start.toDp()
                val end = start + itemHeightPx

                onDrawBehind {
                    drawLine(selectedContentColor, Offset(0f, start), Offset(size.width, start))
                    drawLine(selectedContentColor, Offset(0f, end), Offset(size.width, end))
                }
            },
        contentAlignment = Alignment.TopCenter
    ) {
        val scrollState = rememberScrollState()

        swipeableState.offset.value.also { offset ->
            coroutineScope.launch {
                scrollState.scrollTo(offset.toInt())
            }
        }

        Column(
            Modifier
                .verticalScroll(scrollState, false)
                .padding(vertical = spacerHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items.forEachIndexed { i, string ->
                Text(
                    string,
                    Modifier.heightCenterVertically(itemHeightPx),
                    color = if (i == swipeableState.targetValue) selectedContentColor else unselectedContentColor,
                )
            }
        }
    }
}


/**
 * 内容高度居中
 */
fun Modifier.heightCenterVertically(height: Dp) = layout { measurable, constraints ->
    val heightPx = height.roundToPx()
    val placeable = measurable.measure(constraints)
    layout(placeable.width, heightPx) {
        placeable.placeRelative(0, (heightPx - placeable.height) / 2)
    }
}

/**
 * 内容高度居中
 */
fun Modifier.heightCenterVertically(heightPx: Int) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, heightPx) {
        placeable.placeRelative(0, (heightPx - placeable.height) / 2)
    }
}