package com.hc.wanandroid.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hc.wanandroid.db.AppDatabase
import com.hc.wanandroid.db.RemoteServer
import com.hc.wanandroid.db.entity.Article
import com.hc.wanandroid.db.entity.Chapter
import com.hc.wanandroid.db.pagerDB
import com.hc.wanandroid.navigation.LocalNavController
import com.hc.wanandroid.navigation.appNavigation
import com.hc.wanandroid.net.ChapterNavigationApi
import com.hc.wanandroid.net.throwData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.stream.Stream
import javax.inject.Inject
import kotlin.streams.toList


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChapterUI() {

    val vm: ChapterViewModel = hiltViewModel()
    val pagingItems = vm.articleItemFlow.collectAsLazyPagingItems()
    val navController = LocalNavController.current

    SwipeRefresh(
        state = rememberSwipeRefreshState(pagingItems.loadState.refresh == LoadState.Loading),
        onRefresh = {
            vm.getData()
        }
    ) {
        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            stickyHeader {
                val modifier = Modifier.fillParentMaxWidth()

                ChapterTabRow(
                    modifier,
                    produceState(emptyList<Chapter>()) {
                        vm.oneFlow.collect {
                            if (it.isNotEmpty())
                                vm.oneSelectId.value = it[0].id
                            value = it
                        }
                    }.value
                ) {
                    vm.oneSelectId.value = it.id
                }

                ChapterTabRow(modifier,
                    produceState(emptyList<Chapter>(),vm.twoFlow) {
                        vm.twoFlow.collect {
                            if (it.isNotEmpty())
                                vm.twoSelectId.value = it[0].id
                            value = it
                        }
                    }.value
                ) {
                    vm.twoSelectId.value = it.id
                }

            }

            items(pagingItems){
                if (it != null)
                    ArticleItem(article = it){
                        navController.appNavigation {
                            web(it.link)
                        }
                    }
            }

        }
    }
}

@Composable
fun ChapterTabRow(modifier: Modifier, list: List<Chapter>, onClick: (Chapter) -> Unit) {

    var selectIndex by remember(list) {
        mutableStateOf(0)
    }

    ScrollableTabRow(
        selectIndex,
        modifier
    ) {
        val tabModifier = Modifier.height(40.dp)

        list.forEachIndexed { i, c ->
            Tab(
                selected = i == selectIndex,
                onClick = {
                    selectIndex = i
                    onClick(c)
                },
                tabModifier
            ) {
                Text(c.name, Modifier.padding(horizontal = 10.dp))
            }
        }
    }

}

@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val api: ChapterNavigationApi,
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val chapterDao = appDatabase.chapterDao()

    val oneFlow = chapterDao.queryByParentChapterId(0)
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val oneSelectId = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val twoFlow = oneSelectId.flatMapLatest {
        chapterDao.queryByParentChapterId(it)
    }

    val twoSelectId = MutableStateFlow(0)

    fun getData() {

        viewModelScope.launch {

            val data: List<Chapter> = api.treeJson().throwData()

            val chapterList = data.stream().flatMap {
                val builder = Stream.builder<Chapter>()
                builder.add(it)
                it.children?.forEach(builder::add)
                builder.build()
            }.toList()

            chapterDao.insertAll(chapterList)
        }
    }

    //----------------------------------------------------------

    private val articleDao = appDatabase.articleDao()

    private fun getArticleFlow(chapterId: Int): Flow<PagingData<Article>> {
        return pagerDB(appDatabase, "体系$chapterId", object : RemoteServer<Article> {
            private var datas: List<Article>? = null

            override suspend fun loadData(
                loadType: LoadType,
                state: PagingState<Int, Article>,
                loadKey: Int
            ) {
                datas = api.article(loadKey, chapterId).throwData().datas
            }

            override fun isEnd(): Boolean {
                return datas.isNullOrEmpty()
            }

            override suspend fun clearDB() {

            }

            override suspend fun saveToDB() {
                articleDao.insertAll(datas ?: return)
            }

        }) {
            articleDao.pagingSource(chapterId)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val articleItemFlow = twoSelectId.flatMapLatest {
        if (it == 0) emptyFlow() else getArticleFlow(it)
    }.cachedIn(viewModelScope)

}