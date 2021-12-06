package com.hc.wanandroid.db

import androidx.room.TypeConverter
import com.hc.wanandroid.entity.Tag
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @ExperimentalSerializationApi
    @TypeConverter
    fun listToString(list: List<Tag>?): ByteArray? {

        return ProtoBuf.encodeToByteArray(list?:return null)
    }

    @ExperimentalSerializationApi
    @TypeConverter
    fun stringToList(bytes: ByteArray?): List<Tag>? {

        return ProtoBuf.decodeFromByteArray(bytes ?: return null)
    }
}