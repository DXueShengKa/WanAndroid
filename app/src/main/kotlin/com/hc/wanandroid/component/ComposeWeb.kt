package com.hc.wanandroid.component

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ComposableWeb(url: String, modifier: Modifier) {
//    val vm:WebViewModel = hiltViewModel()
//    LaunchedEffect(vm){
//        vm.getData(url)
//    }

    val progressState = remember { mutableStateOf(0) }
    if (progressState.value < 100)
        LinearProgressIndicator(
            progressState.value / 100f,
            Modifier.fillMaxWidth()
        )

    AndroidView(
        {
            val webView = WebView(it)
            webView.settings.apply {
                val useCache = false
                cacheMode =
                    if (useCache) WebSettings.LOAD_CACHE_ELSE_NETWORK else WebSettings.LOAD_NO_CACHE
                databaseEnabled = useCache
                javaScriptEnabled = useCache
                domStorageEnabled = useCache
                loadWithOverviewMode = true
            }
            webView.webChromeClient = ComposableWebChromeClient(
                progressState
            )
            webView.loadUrl(url)
//            webView.loadData("","text/html",Charsets.UTF_8.name())
            webView
        },
        modifier
    )
}

private class ComposableWebChromeClient(
    private val progressState: MutableState<Int>
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        progressState.value = newProgress
    }

}

@HiltViewModel
class WebViewModel @Inject constructor(
    private val okHttpClient: OkHttpClient
) : ViewModel() {
    val string = mutableStateOf("")

    fun getData(url: String) {
        viewModelScope.launch() {
            string.value = withContext(Dispatchers.IO) {
                okHttpClient.newCall(
                    Request.Builder()
                        .url(url)
                        .build()
                ).execute()
                    .body?.string() ?: "1"
            }
        }
    }
}