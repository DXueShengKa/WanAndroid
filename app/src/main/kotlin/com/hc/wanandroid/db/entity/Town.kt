package com.hc.wanandroid.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "town")
data class Town(
    val name: String,
    @PrimaryKey
    @ColumnInfo(name = "town_id")
    val townId: String,
    @ColumnInfo(name = "county_id")
    val countyId: String
)
