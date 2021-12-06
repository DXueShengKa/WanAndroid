package com.hc.wanandroid.db

import androidx.paging.*
import androidx.room.withTransaction
import com.hc.wanandroid.db.entity.RemoteKey
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class RemoteMediatorDB<Value : Any>(
    private val appDatabase: AppDatabase,
    private val keyLabel: String,
    private val rs: RemoteServer<Value>
) : RemoteMediator<Int, Value>() {

    private val remoteKeyDao = appDatabase.remoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Value>
    ): MediatorResult {

//        Log.w("RemoteMediator", "loadType $loadType PagingState $state")

        try {

            val loadKey = when (loadType) {
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> {
                    remoteKeyDao.queryNextKey(keyLabel)
                }
                LoadType.REFRESH -> {
                    null
                }
            }

//            Log.w("RemoteMediator", "loadKey $loadKey")

            val key = if (loadKey == null) {
                remoteKeyDao.insertOne(RemoteKey(keyLabel, 0))
                0
            } else loadKey

            rs.loadData(loadType, state, key)

            if (rs.isEnd()) return MediatorResult.Success(true)

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    rs.clearDB()
                }
                remoteKeyDao.update(keyLabel, key + 1)
                rs.saveToDB()
            }

            return MediatorResult.Success(false)

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        val t = remoteKeyDao.queryUpdateTime(keyLabel) ?: 0
        //10秒内再次加宰不需要重新请求
        return if (System.currentTimeMillis() - t > 1000 * 10)
            InitializeAction.LAUNCH_INITIAL_REFRESH
        else
            InitializeAction.SKIP_INITIAL_REFRESH
    }
}

interface RemoteServer<Value : Any> {
    @Throws(Exception::class)
    suspend fun loadData(loadType: LoadType, state: PagingState<Int, Value>, loadKey: Int)

    fun isEnd(): Boolean
    suspend fun clearDB()
    suspend fun saveToDB()
}


class RemoteServerBuild<T : Any>{
    lateinit var loadData: (LoadType, PagingState<Int, T>, Int) -> List<T>?
    lateinit var isEnd: (List<T>?) -> Boolean
    lateinit var clearDB: suspend (List<T>?) -> Unit
    lateinit var saveToDB: suspend (List<T>?) -> Unit
}


@OptIn(ExperimentalPagingApi::class)
fun <T : Any> pagerDB(
    appDatabase: AppDatabase,
    keyLabel: String,
    remoteServer: RemoteServer<T>,
    initialKey: Int? = null,
    config:PagingConfig = PagingConfig(pageSize = 30, initialLoadSize = 30),
    pagingSourceFactory: () -> PagingSource<Int, T>
): Flow<PagingData<T>> {
    return Pager(
        config = config,
        initialKey = initialKey,
        remoteMediator = RemoteMediatorDB(appDatabase, keyLabel, remoteServer),
        pagingSourceFactory = pagingSourceFactory
    ).flow
}