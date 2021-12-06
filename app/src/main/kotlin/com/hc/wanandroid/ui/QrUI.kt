package com.hc.wanandroid.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.zxing.NotFoundException
import com.hc.wanandroid.QrCodeUtils
import java.io.File
import java.util.concurrent.Executors

@Composable
fun QrUI() {
    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current
    val resultText = remember { mutableStateOf("") }
    val qrState = remember {
        QrState(lifecycle, context) {
            resultText.value = it
        }
    }
    val imageResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent(), qrState::photoQr)
    Column(Modifier.fillMaxSize()) {
        AndroidView(
            {
                val previewView = PreviewView(it)
                previewView.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                qrState.initCamera(previewView)
                previewView
            },
            Modifier.weight(1f)
        )
        BottomNavigation {
            Text(resultText.value)

            val show = remember {
                mutableStateOf(false)
            }

            Icon(Icons.Default.MoreVert, null, Modifier.clickable {
                show.value = true
            })

            DropdownMenu(
                show.value,
                onDismissRequest = {
                    show.value = false
                }
            ) {
                DropdownMenuItem(onClick = {
                    imageResultLauncher.launch("image/*")
                }) {
                    Text("从文件选择")
                }
            }
        }
    }
}

private const val TAG = "QrUI"

private class QrState(
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val onResult: (String) -> Unit
) {

    private val qrCodeUtils = QrCodeUtils()
    private val executor = Executors.newFixedThreadPool(2)
    private lateinit var imageCapture: ImageCapture
    var onClose: (() -> Unit)? = null

    var isCaptureQr = false

    private val qrAnalyzer = object : ImageAnalysis.Analyzer {

        private var notRun = true

        @Synchronized
        override fun analyze(imageProxy: ImageProxy) {

            if (notRun) {
                Log.d(TAG, "二维码：analyze")
                try {
                    notRun = false

                    val buffer = imageProxy.planes[0].buffer
                    val yuvData = ByteArray(buffer.remaining())
                    buffer.get(yuvData)
                    imageProxy.close()

                    val readYuvCode =
                        qrCodeUtils.readYuvCode(yuvData, imageProxy.width, imageProxy.height)

                    Log.d(TAG, "二维码：$readYuvCode")

                    onResult(readYuvCode)

                    if (isCaptureQr)
                        captureQr()

                } catch (e: NotFoundException) {
                    Log.e(TAG, "二维码", e)
                    notRun = true
                }
            }
        }
    }

    init {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                executor.shutdown()
                Log.d(TAG, "onDestroy")
            }
        })
    }

    fun initCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val preview: Preview = Preview.Builder()
                .build()

            preview.setSurfaceProvider(previewView.surfaceProvider)

            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK) //设置选择摄像头
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                .build()

            imageAnalysis.setAnalyzer(executor, qrAnalyzer)

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraProvider = cameraProviderFuture.get()


            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalysis)
                .addUseCase(imageCapture)
//                .setViewPort( ViewPort.Builder(Rational(800, 800), window.decorView.display.rotation).build())
                .build()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                useCaseGroup
            )

        }, ContextCompat.getMainExecutor(context))

    }

    /**
     * 捕获二维码图片
     */
    private fun captureQr() {
        val photoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(ImageCapture.Metadata())
            .build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                    val savedUri = results.savedUri
                    Log.e(TAG, "拍照成功 $savedUri ")
                    onClose?.invoke()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "拍照失败", exception)
                }

            })
    }

    fun photoQr(photoUri: Uri?) {
        photoUri ?: return
        executor.execute {
            try {
                val openInputStream = context.contentResolver.openInputStream(photoUri)!!
                val qrText = qrCodeUtils.readQrCodeRGB(openInputStream)
                Log.d(TAG, "文件qr ： $qrText")
                onResult(qrText)
            } catch (e: Exception) {
                Log.d(TAG, "文件qr ： ", e)
            }
        }
    }
}