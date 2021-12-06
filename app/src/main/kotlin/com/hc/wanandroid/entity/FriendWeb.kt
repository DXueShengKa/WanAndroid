package com.hc.wanandroid.entity

import kotlinx.serialization.Serializable

@Serializable
data class FriendWeb(
    val category: String,
    val icon: String,
    val id: Int,
    val link: String,
    val name: String,
    val order: Int,
    val visible: Int
)