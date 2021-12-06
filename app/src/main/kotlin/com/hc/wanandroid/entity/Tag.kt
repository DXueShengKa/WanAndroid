package com.hc.wanandroid.entity

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val name: String,
    val url: String
)
