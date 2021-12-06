package com.hc.wanandroid.net

import kotlinx.serialization.Serializable

@Serializable
data class NetResult<T>(
    val data: T? = null,
    val errorCode: Int = 0,
    val errorMsg: String = ""
)

@Serializable
data class NetPageResult<T>(
    val data: NetPager<T>,
    val errorCode: Int = 0,
    val errorMsg: String = ""
)

@Serializable
data class NetPager<T>(
    val datas: List<T> = emptyList(),
    val curPage: Int = 0,
    val offset: Int = 0,
    val over: Boolean = false,
    val pageCount: Int = 0,
    val size: Int = 0,
    val total: Int = 0
)

fun <T> NetResult<T>?.throwData():T{
    if (this == null) throw NetException("未获取服务器数据",404)
    if (errorMsg.isNotEmpty()) throw NetException(errorMsg,errorCode)
    if (data == null) throw NetException(errorMsg,errorCode)
    return data
}

fun <T> NetPageResult<T>?.throwData() : NetPager<T> {
    if (this == null) throw NetException("未获取服务器数据",404)
    if (errorMsg.isNotEmpty()) throw NetException(errorMsg,errorCode)
    return data
}

class NetException(override val message:String,val errorCode:Int):Exception(message)