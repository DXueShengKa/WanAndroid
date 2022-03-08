package com.hc.wanandroid

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hc.wanandroid.db.AppDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.stream.Stream

class DbWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DbWorkerEntryPoint {
        fun appDatabase(): AppDatabase
    }

    override suspend fun doWork(): Result {
        val appDatabase = EntryPointAccessors.fromApplication(
            applicationContext,
            DbWorkerEntryPoint::class.java
        ).appDatabase()

        withContext(Dispatchers.IO){
            val citySql = applicationContext.assets.open("address/city.sql").bufferedReader().lines()
            val countySql = applicationContext.assets.open("address/county.sql").bufferedReader().lines()
            val provinceSql = applicationContext.assets.open("address/province.sql").bufferedReader().lines()

            appDatabase.openHelper.readableDatabase.apply {
                beginTransaction()

                Stream.concat(
                    Stream.concat(citySql,countySql),
                    provinceSql
                ).forEach(::execSQL)

                setTransactionSuccessful()
                endTransaction()
            }

            Log.d("DbWorker", "数据库初始化完毕")
        }

        return Result.success()
    }
}