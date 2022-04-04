package com.hc.wanandroid.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hc.wanandroid.BuildConfig

private class ComposeItemAdapter(
    private val itemContent: @Composable (Int) -> Unit
) : RecyclerView.Adapter<ComposeItemAdapter.HV>() {

    var count = 0

    class HV(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

    override fun onViewRecycled(holder: HV) {
        holder.composeView.disposeComposition()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HV {
        return HV(ComposeView(parent.context))
    }

    override fun onBindViewHolder(holder: HV, position: Int) {
        holder.composeView.setContent {
            itemContent(position)
        }
    }

    override fun getItemCount() = count
}

/**
 * 嵌套RecyclerView实现瀑布流（垂直方向）
 */
@Composable
fun VerticalStaggeredGrid(
    count: Int,
    modifier: Modifier = Modifier,
    spanCount: Int = 2,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    itemContent: @Composable (Int) -> Unit
) {
    val state = remember {
        if (BuildConfig.DEBUG && spanCount < 2) error("spanCount<2的话不需要StaggeredGridCompose")
        RecyclerComposeState(spanCount, StaggeredGridLayoutManager.VERTICAL, onRefresh, itemContent)
    }
    val backgroundColor: Color = MaterialTheme.colors.surface
    state.backgroundColorScheme = backgroundColor.toArgb()
    state.schemeColors = contentColorFor(backgroundColor).toArgb()

    AndroidView(
        state::initView,
        modifier
    ) {
        state.update(count)
        state.isRefreshing(isRefreshing)
    }
}

/**
 * 嵌套RecyclerView实现瀑布流（水平方向）
 */
@Composable
fun HorizontalStaggeredGrid(
    count: Int,
    modifier: Modifier = Modifier,
    spanCount: Int = 2,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    itemContent: @Composable (Int) -> Unit
) {

    val state = remember {
        if (BuildConfig.DEBUG && spanCount < 2) error("spanCount<2的话不需要StaggeredGridCompose")
        RecyclerComposeState(spanCount, StaggeredGridLayoutManager.HORIZONTAL, onRefresh, itemContent)
    }

    val backgroundColor: Color = MaterialTheme.colors.surface
    state.backgroundColorScheme = backgroundColor.toArgb()
    state.schemeColors = contentColorFor(backgroundColor).toArgb()

    AndroidView(
        state::initView,
        modifier
    ) {
        state.update(count)
        state.isRefreshing(isRefreshing)
    }
}


private class RecyclerComposeState(
    private val spanCount: Int,
    private val orientation: Int,
    private val onRefresh: (() -> Unit)?,
    private val itemContent: @Composable (Int) -> Unit
) {

    //compose与view的滑动交互也没实现，别问，问就是在做了，都用view了多一个无妨
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var composeItemAdapter: ComposeItemAdapter

    var schemeColors = 0
    var backgroundColorScheme = 0

    fun initView(context: Context): View {

        recyclerView = RecyclerView(context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            composeItemAdapter = ComposeItemAdapter(itemContent)
            adapter = composeItemAdapter
            layoutManager = StaggeredGridLayoutManager(spanCount, orientation)
        }

        if (onRefresh == null) return recyclerView

        return SwipeRefreshLayout(context).apply {
            setColorSchemeColors(schemeColors)
            setProgressBackgroundColorSchemeColor(backgroundColorScheme)
            addView(recyclerView)
            setOnRefreshListener(onRefresh)
            swipeRefreshLayout = this
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(count: Int) {
        if (composeItemAdapter.count == count) return

        composeItemAdapter.count = count
        composeItemAdapter.notifyDataSetChanged()
    }

    fun isRefreshing(isRefreshing: Boolean) {
        swipeRefreshLayout?.isRefreshing = isRefreshing
    }
}