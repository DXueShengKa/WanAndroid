package com.hc.wanandroid

import android.content.Context
import androidx.startup.Initializer
import coil.Coil
import coil.ImageLoader
import coil.util.DebugLogger
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

class AppInitializer : Initializer<Unit> {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppInitializerEntryPoint {
        fun getOkHttpClient(): OkHttpClient
    }

    override fun create(context: Context) {

        val entryPoint = EntryPointAccessors.fromApplication(context,AppInitializerEntryPoint::class.java)

        Coil.setImageLoader(
            ImageLoader.Builder(context)
                .networkObserverEnabled(true)
                .logger(DebugLogger())
                .okHttpClient(entryPoint.getOkHttpClient())
                .build()
        )

    }


    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}