package com.hc.wanandroid.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hc.wanandroid.db.entity.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<Article>)

    @Query("SELECT * FROM Article")
    fun pagingSource(): PagingSource<Int, Article>

    @Query("SELECT * FROM Article where chapterId = :chapterId")
    fun pagingSource(chapterId:Int): PagingSource<Int, Article>

    @Query("SELECT * FROM Article where chapterId = :chapterId")
    suspend fun queryByChapterId(chapterId:Int): List<Article>

    @Query("SELECT * FROM Article where id in (:ids)")
    suspend fun queryByIds(ids:Collection<Int>): List<Article>

    @Query("select * from Article where id = :id")
    suspend fun getById(id:Int): Article

    @Query("DELETE FROM Article")
    suspend fun clearAll()
}