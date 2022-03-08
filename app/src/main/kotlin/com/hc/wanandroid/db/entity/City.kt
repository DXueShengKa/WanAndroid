package com.hc.wanandroid.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    val name: String,
    @PrimaryKey
    @ColumnInfo(name = "city_id")
    val cityId: String,
    @ColumnInfo(name = "province_id")
    val provinceId: String
)
