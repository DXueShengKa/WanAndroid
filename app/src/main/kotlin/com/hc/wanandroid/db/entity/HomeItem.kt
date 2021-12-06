package com.hc.wanandroid.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class HomeItem(
    @PrimaryKey
    val articleId:Int
)

data class HomeArticle(
    @Embedded
    val homeItem: HomeItem,

    @Relation(
        parentColumn = "articleId",
        entityColumn = "id"
    )
    val article: Article
)