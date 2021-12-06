package com.hc.wanandroid.entity

import android.content.Context
import androidx.core.content.edit
import kotlinx.serialization.Serializable

@Serializable
data class UserLg(
    val coinInfo: CoinInfo,
    val userInfo: UserInfo
)

/**
 * 积分
 */
@Serializable
data class CoinInfo(
    /** 积分排名 */
    val coinCount: Int = 0,
    val level: Int = 0,
    val nickname: String = "",
    val rank: String = "",
    val userId: Int = 0,
    val username: String = ""
){
    companion object{

        @JvmStatic
        fun load(context: Context):CoinInfo{
            val sp = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE)
            return sp.run {
                CoinInfo(
                    coinCount = sp.getInt("coinCount",0),
                    level = sp.getInt("level",0),
                    nickname = sp.getString("nickname","")!!,
                    rank = sp.getString("rank","")!!,
                    userId = sp.getInt("userId",0),
                    username = sp.getString("usernameCoin","")!!,
                )
            }
        }

        @JvmStatic
        fun save(context:Context,coinInfo: CoinInfo){
            val sp = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE)
            sp.edit {
                putInt("coinCount",coinInfo.coinCount)
                putInt("level",coinInfo.level)
                putString("nickname",coinInfo.nickname)
                putString("rank",coinInfo.rank)
                putInt("userId",coinInfo.userId)
                putString("usernameCoin",coinInfo.username)
            }
        }
    }
}