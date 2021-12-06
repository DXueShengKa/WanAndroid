package com.hc.wanandroid.ui

import android.content.Context
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import okhttp3.*
import okio.ByteString
import org.webrtc.*


@Composable
fun WebRtcView() {


    val context = LocalContext.current
    val videoView = remember {
        VideoView(context).apply {
            //......
        }
    }
    AndroidView(
        factory = { videoView }
    ){

    }
    DisposableEffect(videoView){
        onDispose {
            //销毁
        }
    }
}


class WebRtc () {

    lateinit var videoCapturer:VideoCapturer

    fun ok() {
        val r : RtcCertificatePem
        val httpClient = OkHttpClient.Builder()
            .build()

        val webSocket =
            httpClient.newWebSocket(Request.Builder().get().build(), object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {

                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {

                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {

                }
            })

        webSocket.request()

    }

    fun peerConnectionFactory(context: Context): PeerConnectionFactory {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .createInitializationOptions()
        )

        return PeerConnectionFactory.builder()
//        .setVideoDecoderFactory()
            .createPeerConnectionFactory()
    }

    fun videoSource(peerConnectionFactory: PeerConnectionFactory) {
        val videoSource = peerConnectionFactory.createVideoSource(false)
        val videoTrack = peerConnectionFactory.createVideoTrack("VIDEO_TRACK_ID", videoSource)

        val audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        val audioTrack = peerConnectionFactory.createAudioTrack("AUDIO_TRACK_ID", audioSource)

    }

    private fun videoCapturer(cameraEnumerator: CameraEnumerator) {
        val deviceNames = cameraEnumerator.deviceNames
        deviceNames.forEach {
            if (cameraEnumerator.isFrontFacing(it)) {
                cameraEnumerator.createCapturer(it, null)?.also { capturer ->
                    videoCapturer = capturer
                    return
                }
            }
        }

        deviceNames.forEach {
            if (cameraEnumerator.isBackFacing(it)) {
                cameraEnumerator.createCapturer(it, null)?.also { capturer ->
                    videoCapturer = capturer
                }
            }
        }
    }

    fun videoCapturer(context: Context) {
        if (Camera2Enumerator.isSupported(context)) {
            videoCapturer(Camera2Enumerator(context))
        } else {
            videoCapturer(Camera1Enumerator(false))
        }
    }

    fun surfaceTextureHelper(
        eglBaseContext: EglBase.Context,
        context: Context,
        videoSource: VideoSource,
        videoTrack: VideoTrack
    ) {

        val textureHelper = SurfaceTextureHelper.create("SurfaceTextureHelper", eglBaseContext)

        videoCapturer.initialize(textureHelper, context, videoSource.capturerObserver)

        videoTrack.setEnabled(true)

    }


}