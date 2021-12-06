package com.hc.wanandroid.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode


fun networkCallback(context: Context, lifecycleOwner: LifecycleOwner, onNetworkMetered: (Boolean) -> Unit) {

    val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            onNetworkMetered(true)
        }

        override fun onLost(network: Network) {
            onNetworkMetered(false)
            Toast.makeText(context, "网络连接中断", Toast.LENGTH_SHORT).show()
        }

       /* override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val isActiveNetworkMetered = connectivityManager.isActiveNetworkMetered

            if (!isActiveNetworkMetered) {
                ToastUtils.showShort("网络连接中断")
            }

            onNetworkMetered(isActiveNetworkMetered)
        }*/
    }

    val lifecycle = lifecycleOwner.lifecycle

    lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            lifecycle.removeObserver(this)
        }
    })

}

enum class NetworkState {
    Available,
    Unavailable,
    Lost,
    Losing
}

@OptIn(ExperimentalCoroutinesApi::class)
fun networkCallback(context: Context): Flow<NetworkState> {

    return callbackFlow<NetworkState> {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(NetworkState.Available)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(NetworkState.Unavailable)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(NetworkState.Lost)
                Toast.makeText(context, "网络连接中断", Toast.LENGTH_SHORT).show()
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                trySend(NetworkState.Losing)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

}


fun htmlAnnotatedString(html:String): AnnotatedString {
    val body = Jsoup.parse(html).body()
    return buildAnnotatedString {
        body.childNodes().forEach {
            when(it){
                is Element ->{
                    htmlElement(it)
                }
                is TextNode ->{
                    append(it.text())
                }
            }
        }
    }
}

private fun AnnotatedString.Builder.htmlElement(e: Element){
    when(e.tagName()){
        "em"->{
            withStyle(SpanStyle(Color.Red,fontWeight = FontWeight.Bold)){
                append(e.text())
            }
        }
    }
}
