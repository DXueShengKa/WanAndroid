package com.hc.wanandroid.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hc.wanandroid.db.entity.SmallData
import kotlinx.coroutines.flow.Flow

@Dao
interface SmallDataDao {

    @Query("select * from SmallData where label = :label")
    fun queryByLabel(label:String):Flow<List<SmallData>?>

    @Query("delete from SmallData where label = :label")
    suspend fun deleteByLabel(label:String)

    @Query("select count(1) from SmallData where label = :label")
    suspend fun queryCount(label:String):Int

    @Insert
    suspend fun insertAll(datas:Collection<SmallData>)
}