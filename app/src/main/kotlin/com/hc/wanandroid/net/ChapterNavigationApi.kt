package com.hc.wanandroid.net

import com.hc.wanandroid.db.entity.Article
import com.hc.wanandroid.db.entity.Chapter
import com.hc.wanandroid.entity.Navi
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChapterNavigationApi {

    /**
     * 体系数据
     */
    @GET("tree/json")
    suspend fun treeJson(): NetResult<List<Chapter>>?

    /**
     *  知识体系下的文章
     *  @param cid 分类的id，上述二级目录的id
     */
    @GET("article/list/{page}/json")
    suspend fun article(@Path("page") page: Int,@Query("cid") cid:Int): NetPageResult<Article>?

    /**
     * @param author 作者昵称，不支持模糊匹配。
     */
    @GET("article/list/{page}/json")
    suspend fun article(@Path("page") page: Int,@Query("author") author:String): NetPageResult<Article>?

    /**
     * 导航数据
     */
    @GET("navi/json")
    suspend fun naviJson(): NetResult<List<Navi>>?

    /**
     * 公众号
     */
    @GET("wxarticle/chapters/json")
    suspend fun wxArticle(): NetResult<List<Chapter>>?

}