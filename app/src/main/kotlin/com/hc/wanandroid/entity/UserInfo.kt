package com.hc.wanandroid.entity

import android.content.Context
import androidx.core.content.edit
import com.hc.wanandroid.db.entity.Chapter
import com.hc.wanandroid.di.kotlinJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

@Serializable
data class UserInfo(
    val admin: Boolean = false,
    val chapterTops: List<Chapter> = emptyList(),
    val coinCount: Int = 0,
    val collectIds: List<Int> = emptyList(),
    val email: String = "",
    val icon: String = "",
    val id: Int = 0,
    val nickname: String = "",
    val password: String = "",
    val publicName: String = "",
    val token: String = "",
    val type: Int = 0,
    val username: String = ""
){
    companion object{
        @JvmStatic
        fun load(context:Context):UserInfo{
            val sp = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE)
            return sp.run {
                UserInfo(
                    admin = sp.getBoolean("admin",false),
                    coinCount = sp.getInt("coinCount",0),
                    email = sp.getString("email","")!!,
                    icon = sp.getString("icon","")!!,
                    id = sp.getInt("id",0),
                    nickname = sp.getString("nickname","")!!,
                    password = sp.getString("password","")!!,
                    publicName = sp.getString("publicName","")!!,
                    token = sp.getString("token","")!!,
                    type = sp.getInt("type",0),
                    username = sp.getString("username","")!!,

                    collectIds = sp.getString("collectIds",null)?.let { kotlinJson.decodeFromString(ListSerializer(Int.serializer()),it) }?: emptyList(),
                    chapterTops = sp.getString("chapterTops",null)?.let{ kotlinJson.decodeFromString(ListSerializer(Chapter.serializer()),"")}?: emptyList()
                )
            }
        }

        @JvmStatic
        fun save(context:Context,userInfo: UserInfo){
            val sp = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE)

            sp.edit {
                putBoolean("admin",userInfo.admin)
                putInt("coinCount",userInfo.coinCount)
                putString("email",userInfo.email)
                putString("icon",userInfo.icon)
                putInt("id",userInfo.id)
                putString("nickname",userInfo.nickname)
                putString("password",userInfo.password)
                putString("publicName",userInfo.publicName)
                putString("token",userInfo.token)
                putInt("type",userInfo.type)
                putString("username",userInfo.username)

                if (userInfo.collectIds.isNotEmpty())
                putString("collectIds",kotlinJson.encodeToString(ListSerializer(Int.serializer()),userInfo.collectIds))

                if (userInfo.chapterTops.isNotEmpty())
                putString("chapterTops",kotlinJson.encodeToString(ListSerializer(Chapter.serializer()),userInfo.chapterTops))

            }
        }

    }
}