package com.hc.wanandroid.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hc.wanandroid.db.AppDatabase
import com.hc.wanandroid.db.entity.Article
import com.hc.wanandroid.db.entity.Chapter
import com.hc.wanandroid.db.entity.dataDispose
import com.hc.wanandroid.net.ChapterNavigationApi
import com.hc.wanandroid.net.throwData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.streams.toList


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WebNaviUI() {
    val vm:NaviViewModel = hiltViewModel()
    SwipeRefresh(rememberSwipeRefreshState(vm.isRefreshing), onRefresh = { vm.refresh() }) {

        LazyColumn(Modifier.fillMaxSize()){
            stickyHeader {
                ChapterTabRow(
                    Modifier.fillParentMaxWidth(),
                    vm.navFlow.collectAsState(emptyList()).value
                ){
                    vm.currNav(it.id)
                }
            }

            items(vm.navLinkList){
                Text(it.title)
                Text(it.link)
                Divider()
            }
        }
    }
}


@HiltViewModel
class NaviViewModel @Inject constructor(
    private val api: ChapterNavigationApi,
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val articleDao = appDatabase.articleDao()
    private val chapterDao = appDatabase.chapterDao()

    var isRefreshing by mutableStateOf(false)

    private val navIdDispose = dataDispose("常用网站导航",appDatabase){
        isRefreshing = true

        val throwData = api.naviJson().throwData()
        val navIds = mutableListOf<Int>()
        val chapters = mutableListOf<Chapter>()

        throwData.forEach {
            navIds.add(it.cid)
            chapters.add(
                Chapter(id = it.cid,name = it.name)
            )
        }

        val articles = throwData.stream().flatMap {
            it.articles.stream()
        }.toList()

        appDatabase.withTransaction {
            chapterDao.insertAll(chapters)
            articleDao.insertAll(articles)
        }

        isRefreshing = false
        throwData.map { it.cid }
    }

    val navFlow = navIdDispose.flow.map {
        val list = chapterDao.queryByIds(it)
        if (navLinkList.isEmpty()) currNav(list[0].id)
        list
    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    var navLinkList by mutableStateOf<List<Article>>(emptyList())

    fun currNav(cid:Int){
        viewModelScope.launch {
            navLinkList = articleDao.queryByChapterId(cid)
        }
    }

    fun refresh() {
        navIdDispose.refresh(viewModelScope)
    }
}