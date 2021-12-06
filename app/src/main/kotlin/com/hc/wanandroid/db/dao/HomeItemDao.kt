package com.hc.wanandroid.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.hc.wanandroid.db.entity.HomeArticle
import com.hc.wanandroid.db.entity.HomeItem

@Dao
interface HomeItemDao {

    @Transaction
    @Query("select * from HomeItem")
    fun pagingSource(): PagingSource<Int, HomeArticle>

    @Insert
    suspend fun insert(list: List<HomeItem>)

    @Query("delete from HomeItem")
    suspend fun deleteAll()
}