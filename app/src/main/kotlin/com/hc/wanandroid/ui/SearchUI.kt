package com.hc.wanandroid.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.paging.*
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.imePadding
import com.hc.wanandroid.db.AppDatabase
import com.hc.wanandroid.db.entity.Article
import com.hc.wanandroid.db.entity.SmallDataDispose
import com.hc.wanandroid.db.entity.dataDispose
import com.hc.wanandroid.entity.Hotkey
import com.hc.wanandroid.navigation.LocalNavController
import com.hc.wanandroid.navigation.appNavigation
import com.hc.wanandroid.net.HomeApi
import com.hc.wanandroid.net.throwData
import com.hc.wanandroid.utils.ToastUtils
import com.hc.wanandroid.utils.htmlAnnotatedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Composable
fun SearchUI() {
    Column(Modifier.fillMaxSize()) {
        val vm: SearchViewModel = hiltViewModel()

        OutlinedTextField(
            value = vm.searchText,
            onValueChange = {
                vm.searchText = it
            },
            leadingIcon = {
                Icon(Icons.Default.Search, null)
            },
            placeholder = {
                Text("搜索")
            },
            trailingIcon = {
                Icon(Icons.Default.Clear, null, Modifier.clickable {
                    vm.searchText = ""
                })
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp),
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            keyboardActions = KeyboardActions(
                onSearch = {
                    vm.onSearch()
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )

        val pagingItems = vm.searchResult.collectAsLazyPagingItems()

        if (pagingItems.itemCount == 0) {
            val texts by vm.hotkeyFlow.collectAsState(emptyList())
            Hotkey(texts){
                vm.searchText = it
            }
        } else {
            ResultColumn(pagingItems, LocalNavController.current)
        }

    }
}

@Composable
private fun Hotkey(texts: List<Hotkey>,onClick:(String)->Unit) {
    FlowRow(
        Modifier
            .padding(10.dp)
            .imePadding(),
        mainAxisSpacing = 10.dp,
        crossAxisSpacing = 5.dp
    ) {
        texts.forEach {
            Text(
                it.name,
                Modifier
                    .clickable {
                        onClick(it.name)
                    }
                    .border(
                        1.dp,
                        MaterialTheme.colors.secondary,
                        MaterialTheme.shapes.small
                    )
                    .padding(10.dp),
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
private fun ResultColumn(pagingItems: LazyPagingItems<Article>, navController: NavController) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(pagingItems, key = { it.id }) { article ->
            article ?: return@items

            Text(
                remember {
                    htmlAnnotatedString(article.title)
                },
                Modifier.clickable {
                    navController.appNavigation { web(article.link) }
                }
            )
        }
    }
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val homeApi: HomeApi,
    appDatabase: AppDatabase
) : ViewModel() {

    private val hotkeyDispose: SmallDataDispose<Hotkey> = dataDispose("搜索热词", appDatabase) {
        try {
            homeApi.hotkey().throwData()
        } catch (e: Exception) {
            ToastUtils.showShort(e.message)
            emptyList()
        }
    }.also {
        it.refresh(viewModelScope)
    }

    val hotkeyFlow = hotkeyDispose.flow.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    var searchText by mutableStateOf("")

    private val searchTextFlow = MutableStateFlow("")

    val searchResult = searchTextFlow.flatMapLatest { text ->
        if (text.isEmpty()) return@flatMapLatest emptyFlow()

        Pager(
            config = PagingConfig(20, initialLoadSize = 20)
        ) {
            object : PagingSource<Int, Article>() {
                override fun getRefreshKey(state: PagingState<Int, Article>) = state.anchorPosition

                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
                    return try {
                        val loadKey = params.key ?: 0
                        val netPager = homeApi.query(loadKey, text).throwData()
                        LoadResult.Page(
                            data = netPager.datas,
                            nextKey = if (netPager.datas.isEmpty()) null else loadKey + 1,
                            prevKey = null
                        )
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                }

            }
        }.flow
    }.cachedIn(viewModelScope)


    fun onSearch() {
        searchTextFlow.value = searchText
    }


}