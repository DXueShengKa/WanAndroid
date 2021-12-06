package com.hc.wanandroid.entity

import com.hc.wanandroid.db.entity.Article
import kotlinx.serialization.Serializable

@Serializable
data class Navi(
    val cid:Int,
    val name:String,
    val articles:List<Article>
)