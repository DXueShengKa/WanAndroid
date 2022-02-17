package com.hc.wanandroid

import android.content.Context
import androidx.startup.Initializer
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import coil.util.DebugLogger
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.File

class AppInitializer : Initializer<Unit> {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppInitializerEntryPoint {
        fun getOkHttpClient(): OkHttpClient
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun create(context: Context) {

        val entryPoint =
            EntryPointAccessors.fromApplication(context, AppInitializerEntryPoint::class.java)

        Coil.setImageLoader(
            ImageLoader.Builder(context)
                .networkObserverEnabled(true)
                .logger(DebugLogger())
                .diskCache(
                    DiskCache.Builder()
                        .directory(File(context.externalCacheDir,"coilCache"))
                        .build()
                )
                .okHttpClient(entryPoint.getOkHttpClient())
                .build()
        )

    }


    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}