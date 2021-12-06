package com.hc.wanandroid.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hc.wanandroid.db.entity.RemoteKey

@Dao
interface RemoteKeyDao {

    @Query("update RemoteKey set nextKey = :nextKey, updateTime = :updateTime where label = :label")
    suspend fun update(label:String,nextKey:Int?,updateTime:Long = System.currentTimeMillis())

    @Query("select nextKey from RemoteKey where label = :label")
    suspend fun queryNextKey(label: String): Int?

    @Query("select updateTime from RemoteKey where label = :label")
    suspend fun queryUpdateTime(label: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(key: RemoteKey)

    @Update
    suspend fun updateOne(key: RemoteKey)

    @Delete
    suspend fun deleteOne(key: RemoteKey)

    @Query("select * from RemoteKey where label = :label")
    suspend fun queryOne(label: String): RemoteKey?

    @Query("delete from RemoteKey")
    suspend fun clearAll()
}