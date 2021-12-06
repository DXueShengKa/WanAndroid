package com.hc.wanandroid.utils.video

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaPeriod
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    uri: String
) {
    val context = LocalContext.current
    val player = remember(uri) {
        ExoPlayer.Builder(context)
            .build()
    }
    DisposableEffect(uri){
        player.addMediaItem(MediaItem.fromUri(uri))
        onDispose {
            player.release()
        }
    }
    AndroidView(
        {
            StyledPlayerView(it)
        },
        modifier
    ) {
        it.player = player
        player.prepare()
        Log.d("VideoPlayer","AndroidView update")
    }
}