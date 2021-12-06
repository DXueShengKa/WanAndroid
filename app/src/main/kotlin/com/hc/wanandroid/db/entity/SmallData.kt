package com.hc.wanandroid.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.withTransaction
import com.hc.wanandroid.db.AppDatabase
import com.hc.wanandroid.db.dao.SmallDataDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer

@Entity
data class SmallData(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(index = true)
    val label: String,
    var data: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SmallData

        if (id != other.id) return false
        if (label != other.label) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + label.hashCode()
        return result
    }


}


@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> dataDispose(
    label: String,
    appDatabase: AppDatabase,
    noinline dataFactory: suspend () -> List<T>?
): SmallDataDispose<T> {
    val serializer = ProtoBuf.serializersModule.serializer<T>()
    return SmallDataDispose(label, appDatabase, serializer, serializer, dataFactory)
}

@OptIn(ExperimentalSerializationApi::class)
class SmallDataDispose<T>(
    private val label: String,
    private val appDatabase: AppDatabase,
    deserializer: DeserializationStrategy<T>,
    private val serializer: SerializationStrategy<T>,
    private val dataFactory: suspend () -> List<T>?
) {
    private val smallDataDao: SmallDataDao = appDatabase.smallDataDao()

    val flow: Flow<List<T>> = smallDataDao.queryByLabel(label)
        .map { list ->
            if (list.isNullOrEmpty()) return@map emptyList()

            list.map { ProtoBuf.decodeFromByteArray(deserializer, it.data) }
        }

    suspend fun count(): Int {
        return smallDataDao.queryCount(label)
    }

    suspend fun deleteData() {
        smallDataDao.deleteByLabel(label)
    }

    suspend fun addData(collection: Collection<T>) {
        smallDataDao.insertAll(
            collection.map {
                SmallData(label = label, data = ProtoBuf.encodeToByteArray(serializer, it))
            }
        )
    }

    suspend fun refresh() {
        val dataList = dataFactory()
        if (!dataList.isNullOrEmpty())
            appDatabase.withTransaction {
                smallDataDao.deleteByLabel(label)
                addData(dataList)
            }
    }

    fun refresh(scope: CoroutineScope) {
        scope.launch {
            val dataList = dataFactory()

            if (!dataList.isNullOrEmpty())
                appDatabase.withTransaction {
                    smallDataDao.deleteByLabel(label)
                    addData(dataList)
                }
        }
    }

}