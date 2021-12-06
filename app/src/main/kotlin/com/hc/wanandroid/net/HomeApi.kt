package com.hc.wanandroid.net

import com.hc.wanandroid.db.entity.Article
import com.hc.wanandroid.entity.BannerJson
import com.hc.wanandroid.entity.FriendWeb
import com.hc.wanandroid.entity.Hotkey
import retrofit2.http.*

interface HomeApi {

    /**
     * 首页banner
     */
    @GET("banner/json")
    suspend fun bannerJson(): NetResult<List<BannerJson>>?

    /**
     * 常用网站
     */
    @GET("friend/json")
    suspend fun friendJson(): NetResult<List<FriendWeb>>?

    /**
     * 首页文章列表
     */
    @GET("article/list/{page}/json")
    suspend fun articleList(@Path("page") page: Int): NetPageResult<Article>?

    /**
     * 置顶文章
     */
    @GET("article/top/json")
    suspend fun articleTop(): NetResult<List<Article>>?


    @GET("hotkey/json")
    suspend fun hotkey(): NetResult<List<Hotkey>>?


    /**
     * 搜索
     */
    @FormUrlEncoded
    @POST("/article/query/{page}/json")
    suspend fun query(@Path("page") page: Int, @Field("k") key: String): NetPageResult<Article>?


}