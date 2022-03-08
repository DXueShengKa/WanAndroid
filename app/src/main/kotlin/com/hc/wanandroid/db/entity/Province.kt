package com.hc.wanandroid.db.entity

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.TEXT
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "province")
data class Province(
    val name:String,
    @PrimaryKey
    @ColumnInfo(name = "province_id",)
    val provinceId:String
)
