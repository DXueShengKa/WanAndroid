package com.hc.wanandroid.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.hc.wanandroid.entity.Tag
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Article(
    @PrimaryKey
    var id: Int = 0,
    val audit: Int = 0,
    val author: String = "",
    val canEdit: Boolean = false,
    val chapterId: Int = 0,
    val chapterName: String = "",
    val collect: Boolean = false,
    val courseId: Int = 0,
    val desc: String = "",
    val descMd: String = "",
    val envelopePic: String = "",
    val fresh: Boolean = false,
    val host: String = "",
    val link: String = "",
    val niceDate: String? = null,
    val niceShareDate: String? = null,
    val origin: String = "",
    val prefix: String = "",
    val projectLink: String = "",
    val publishTime: Long = 0L,
    val realSuperChapterId: Int = 0,
    val selfVisible: Int = 0,
    val shareDate: Long? = null,
    val shareUser: String = "",
    val superChapterId: Int = 0,
    val superChapterName: String = "",
    val title: String = "",
    val type: Int = 0,
    val userId: Int = 0,
    val visible: Int = 0,
    val zan: Int = 0
){
    @Ignore
    var tags: List<Tag>? = null

    @Ignore
    var apkLink: String? = null
}

