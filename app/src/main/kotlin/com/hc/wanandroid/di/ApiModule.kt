package com.hc.wanandroid.di

import com.hc.wanandroid.net.ChapterNavigationApi
import com.hc.wanandroid.net.HomeApi
import com.hc.wanandroid.net.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.create


@Module
@InstallIn(ViewModelComponent::class)
object ApiModule {

    @Provides
    fun providesHomeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create()
    }


    @Provides
    fun providesChapterNavigationApi(retrofit: Retrofit): ChapterNavigationApi {
        return retrofit.create()
    }

    @Provides
    fun providesUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create()
    }
}