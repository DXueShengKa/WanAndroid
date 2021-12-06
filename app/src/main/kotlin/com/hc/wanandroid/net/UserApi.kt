package com.hc.wanandroid.net

import com.hc.wanandroid.entity.UserInfo
import com.hc.wanandroid.entity.UserLg
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface UserApi {

    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): NetResult<UserInfo?>?

    @GET("user/logout/json")
    suspend fun logout(): NetResult<String>?


    @GET("user/lg/userinfo/json")
    suspend fun lgUserinfo(): NetResult<UserLg>?


}