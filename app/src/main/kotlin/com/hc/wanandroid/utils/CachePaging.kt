package com.hc.wanandroid.utils

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import java.util.function.Predicate

class CachePaging<K:Any,V : Any> {

    private val list = mutableListOf<V>()
    private lateinit var pagingSource:MPagingSource

    private var isUpdate = false
    private var nextKey:K? = null

    private inner class MPagingSource(
        private val getResult: suspend (LoadParams<K>)-> LoadResult<K, V>
    ): PagingSource<K,V>() {
        override fun getRefreshKey(state: PagingState<K, V>): K? = null

        override suspend fun load(params: LoadParams<K>): LoadResult<K, V> {

            if(params is LoadParams.Refresh){
//                println("Refresh ${params.key}")
                if (list.size > 0 && ! isUpdate){
                    list.clear()
                }
            }else if (params is LoadParams.Append){
//                println("Append ${params.key}")
                isUpdate = false
            }

            if (isUpdate){
                isUpdate = false
                return LoadResult.Page(
                    prevKey = null,
                    nextKey = nextKey,
                    data = list
                )
            }

            val result = getResult(params)

            if (result is LoadResult.Page){
                list.addAll(result.data)
                nextKey = result.nextKey
            }

            return result
        }
    }

    fun flow(
        config: PagingConfig = PagingConfig(pageSize = 10,initialLoadSize = 10),
        initialKey: K? = null,
        getResult: suspend (PagingSource.LoadParams<K>)-> PagingSource.LoadResult<K, V>
    ) : Flow<PagingData<V>> = Pager(config, initialKey) {
        pagingSource = MPagingSource(getResult)
        pagingSource
    }.flow


    fun remove(v:V){
        list.remove(v)

        pagingSource.invalidate()
        isUpdate = true
    }

    fun removeAt(index: Int){
        list.removeAt(index)

        pagingSource.invalidate()
        isUpdate = true
    }

    fun removeIf(filter: Predicate<V>){
        list.removeIf(filter)
        pagingSource.invalidate()
        isUpdate = true
    }
}