package com.hc.wanandroid.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "county")
data class County(
    val name: String,
    @PrimaryKey
    @ColumnInfo(name = "county_id")
    val countyId: String,
    @ColumnInfo(name = "city_id")
    val cityId: String,
)
