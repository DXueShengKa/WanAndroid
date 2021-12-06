package com.hc.wanandroid.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.LocalImageLoader
import coil.request.ImageRequest


@OptIn(ExperimentalCoilApi::class)
@Composable
fun rememberImagePainter2(
    data: Any,
    builder: ImageRequest.Builder.() -> Unit = {},
): ImagePainter {

    val context = LocalContext.current

    val request = remember(data) {
        ImageRequest.Builder(context)
            .data(data)
            .apply(builder)
            .build()
    }

    return coil.compose.rememberImagePainter(
        request,
        LocalImageLoader.current,
        ImagePainter.ExecuteCallback.Default
    )
}


val LocalIsActiveNetworkMetered = compositionLocalOf { false }