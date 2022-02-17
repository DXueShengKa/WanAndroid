package com.hc.wanandroid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hc.wanandroid.db.entity.Article
import com.hc.wanandroid.entity.BannerJson
import com.hc.wanandroid.navigation.appNavigation


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeUI(navController: NavController) {

    val vm: HomeViewModel = hiltViewModel()
    val pagingItems = vm.homeFlow.collectAsLazyPagingItems()
    val banners by vm.bannerFlow.collectAsState(emptyList())
    val tops by vm.topFlow.collectAsState(emptyList())

    SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = false), onRefresh = {
        vm.refreshData()
        pagingItems.refresh()
    }) {

        LazyColumn(
            // verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            item ("轮播图",content = banner(banners) { url ->
                navController.appNavigation { web(url) }
            })

            val onClick: (Article) -> Unit = { article ->
                navController.appNavigation { web(article.link) }
            }

            items(tops) {
                ArticleItem(it, true, onClick)
            }

            items(pagingItems) {
                it ?: return@items
                ArticleItem(it, onClick = onClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ArticleItem(article: Article, isTop: Boolean = false, onClick: (Article) -> Unit) {
    ListItem(
        Modifier.clickable {
            onClick(article)
        },
        icon = {
            Column() {
                Icon(Icons.Default.Star, null)
                if (isTop)
                    Text("置顶", color = Color.Red)
                if (article.fresh)
                    Text("新", color = Color.Red)
            }
        },
        secondaryText = {
            Text(article.niceShareDate ?: "null")
        },
        overlineText = {
            Text(buildAnnotatedString {
                append(article.shareUser)
                append("  ")
                append(article.superChapterName)
                append('/')
                append(article.chapterName)
            })
        },
        text = {
            Text(article.title)
        }
    )
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
private fun banner(
    banners: List<BannerJson>,
    onClick: (String) -> Unit
): @Composable LazyItemScope.() -> Unit = {
    if (banners.isEmpty()) {
        CircularProgressIndicator()
    } else {
        val state = rememberPagerState()

        /*    LaunchedEffect(state) {
                while (isActive) {
                    delay(3000)
                    if (! state.isScrollInProgress) {
                        val page = state.currentPage + 1
                        state.animateScrollToPage(
                            if (page > state.pageCount) 0 else page
                        )
                    }
                }
            }*/

        HorizontalPager(
            banners.size,
            Modifier
                .fillParentMaxWidth()
                .height(180.dp),
            state,
            itemSpacing = 10.dp
        ) { page ->

            Card(
                onClick = {
                    onClick(banners[page].url)
                },
            ) {
                Image(rememberAsyncImagePainter(banners[page].imagePath), null)
            }
        }
    }


}