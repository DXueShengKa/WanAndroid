package com.hc.wanandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.paging.map
import com.hc.wanandroid.db.AppDatabase
import com.hc.wanandroid.db.RemoteServer
import com.hc.wanandroid.db.entity.Article
import com.hc.wanandroid.db.entity.HomeArticle
import com.hc.wanandroid.db.entity.HomeItem
import com.hc.wanandroid.db.entity.dataDispose
import com.hc.wanandroid.db.pagerDB
import com.hc.wanandroid.entity.BannerJson
import com.hc.wanandroid.net.HomeApi
import com.hc.wanandroid.net.throwData
import com.hc.wanandroid.utils.ToastUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.plus
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeApi: HomeApi,
    appDatabase: AppDatabase
) : ViewModel() {


    private val coroutineScope = viewModelScope + ToastUtils.coroutineExceptionHandler()

    private val articleDao = appDatabase.articleDao()

    //--------------------------------------------------------

    private val bannerDispose = dataDispose<BannerJson>("轮播图", appDatabase) {
        homeApi.bannerJson().throwData()
    }.also {
        it.refresh(coroutineScope)
    }

    val bannerFlow = bannerDispose.flow.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    //--------------------------------------------------------

    private val topDispose = dataDispose<Int>("置顶文章", appDatabase) {
            val tops = homeApi.articleTop().throwData()

            articleDao.insertAll(tops)

            tops.map { it.id }
    }.also {
        it.refresh(coroutineScope)
    }

    val topFlow = topDispose.flow
        .map {
            articleDao.queryByIds(it)
        }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    //------------------------------------------------------------

    fun refreshData() {

        bannerDispose.refresh(coroutineScope)
        topDispose.refresh(coroutineScope)
    }

    private val homeItemDao = appDatabase.homeItemDao()

    val homeFlow = pagerDB(
        appDatabase, "首页",
        object : RemoteServer<HomeArticle> {

            private var datas: List<Article>? = null
            private var items: List<HomeItem>? = null

            @Throws(Exception::class)
            override suspend fun loadData(
                loadType: LoadType,
                state: PagingState<Int, HomeArticle>,
                loadKey: Int
            ) {

                datas = homeApi.articleList(loadKey).throwData().datas
                    .also { d ->
                        items = d.map { HomeItem(it.id) }
                    }
            }

            override fun isEnd() = datas.isNullOrEmpty()

            override suspend fun clearDB() {
                homeItemDao.deleteAll()
            }

            override suspend fun saveToDB() {
                articleDao.insertAll(datas ?: return)
                homeItemDao.insert(items ?: return)
            }
        }
    ) {
        homeItemDao.pagingSource()
    }.map { pagingData ->
        pagingData.map { it.article }
    }.cachedIn(coroutineScope)


}