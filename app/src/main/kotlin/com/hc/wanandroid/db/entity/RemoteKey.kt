package com.hc.wanandroid.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKey(
    @PrimaryKey
    val label: String,
    var nextKey: Int? = null,
//    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    var updateTime: Long = System.currentTimeMillis()
)
