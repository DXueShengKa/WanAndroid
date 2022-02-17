package com.hc.wanandroid.di

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import coil.ImageLoader
import coil.util.CoilUtils
import com.hc.wanandroid.db.AppDatabase
import com.hc.wanandroid.utils.converter_factory.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.time.Duration
import java.util.stream.Collectors
import javax.inject.Singleton

val kotlinJson = Json {
    ignoreUnknownKeys = true
}

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    private const val NetLog = "NetLog"

    @Provides
    @Singleton
    fun providesOkHttpClient(application: Application): OkHttpClient {

        Log.w(NetLog, "providesOkHttpClient")

        val sp = application.getSharedPreferences("net", Context.MODE_PRIVATE)

        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor {
                    Log.d(NetLog, it)
                }.setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .connectTimeout(Duration.ofSeconds(10))
            .cookieJar(object : CookieJar {
                private val map = mutableMapOf<String, List<Cookie>>()

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    val host = url.host

                    if (! map.containsKey(host) && sp.contains(host)) {
                        sp.getStringSet(host, emptySet())?.also {
                            map[host] = it.map { s ->
                                val c = s.split(",")
                                Cookie.Builder()
                                    .domain(c[0])
                                    .name(c[1])
                                    .value(c[2])
                                    .build()
                            }
                        }
                    }

                    return map[host] ?: emptyList()
                }

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    if (cookies.isEmpty()) return
                    map[url.host] = cookies

                    sp.edit {
                        val set = cookies.stream().map { "${it.domain},${it.name},${it.value}" }
                            .collect(Collectors.toSet())
                        putStringSet(url.host, set)
                    }

                    map.forEach { s, list ->
                        Log.d(NetLog, "CookieJar -> $s \n $list")
                    }
                }
            })
            .build()

    }


    @Provides
    @Singleton
    @ExperimentalSerializationApi
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .client(okHttpClient)
            .addConverterFactory(
                kotlinJson.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

//    private fun getConverterFactory() = GsonConverterFactory.create(
//        GsonBuilder()
//            .setDateFormat("yyyy-MM-dd HH:mm")
//            .registerTypeAdapter(
//                LocalDateTime::class.java,
//                JsonDeserializer { json, typeOfT, context ->
//                    LocalDateTime.parse(
//                        json.asString,
//                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//                    )
//                })
//            .create()
//    )

    @Provides
    @Singleton
    fun providesRoom(application: Application): AppDatabase {

        return Room.databaseBuilder(application, AppDatabase::class.java, "wa.db")
            .addCallback(object : RoomDatabase.Callback() {
                private val CALLBACK_TAG = "RoomDatabase.Callback"

                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d(CALLBACK_TAG, "onCreate")
                }

                override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                    super.onDestructiveMigration(db)
                    Log.d(CALLBACK_TAG, "onDestructiveMigration")
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Log.d(CALLBACK_TAG, "onOpen")
                }
            })
            .build()
    }

}