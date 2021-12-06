package com.hc.wanandroid.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hc.wanandroid.db.dao.*
import com.hc.wanandroid.db.entity.*

@Database(
    entities = [
        Article::class, RemoteKey::class, SmallData::class,
        HomeItem::class, Chapter::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    abstract fun remoteKeyDao(): RemoteKeyDao

    abstract fun smallDataDao(): SmallDataDao

    abstract fun homeItemDao():HomeItemDao

    abstract fun chapterDao():ChapterDao

}