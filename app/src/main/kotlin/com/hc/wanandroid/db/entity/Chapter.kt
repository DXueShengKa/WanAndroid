package com.hc.wanandroid.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Chapter(
    @PrimaryKey
    val id: Int,
    val courseId: Int = 0,
    val name: String = "",
    val order: Int = 0,
    val parentChapterId: Int = 0,
    val userControlSetTop: Boolean = false,
    val visible: Int = 0
) {
    @Ignore
    var children: List<Chapter>? = null
}

