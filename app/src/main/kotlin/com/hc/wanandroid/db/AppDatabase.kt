package com.hc.wanandroid.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hc.wanandroid.db.dao.*
import com.hc.wanandroid.db.entity.*

@Database(
    entities = [
        Article::class, RemoteKey::class, SmallData::class,
        HomeItem::class, Chapter::class,
        Province::class, City::class, County::class, Town::class
    ],
    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object{
        const val DB_NAME = "wan.db"
    }

    abstract fun articleDao(): ArticleDao

    abstract fun remoteKeyDao(): RemoteKeyDao

    abstract fun smallDataDao(): SmallDataDao

    abstract fun homeItemDao(): HomeItemDao

    abstract fun chapterDao(): ChapterDao

    abstract fun addressDao(): AddressDao

}
