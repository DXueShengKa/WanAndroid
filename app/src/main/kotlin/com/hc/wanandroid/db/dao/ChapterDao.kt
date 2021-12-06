package com.hc.wanandroid.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hc.wanandroid.db.entity.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<Chapter>)

    @Query("select * from Chapter where parentChapterId = :parentChapterId")
    fun queryByParentChapterId(parentChapterId:Int):Flow<List<Chapter>>

    @Query("select * from Chapter where id in (:ids)")
    suspend fun queryByIds(ids:Collection<Int>):List<Chapter>

}