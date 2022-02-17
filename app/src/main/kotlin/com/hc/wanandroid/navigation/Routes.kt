package com.hc.wanandroid.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.activity
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hc.wanandroid.component.ComposableWeb
import com.hc.wanandroid.ui.*
import com.hc.wanandroid.utils.video.VideoPlayer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

enum class Routes {
    Home,
    Web,
    Search,
    Setting,
    LocalCache,
    Chapter,
    WebNavi,
    Login,
    MyInfo,
    QrUI,
    VideoPlayer,
    LocationUI,
    MyBrowsePicture,
    JdUI,
    LazyStaggeredUI;
}


fun navGraphBuilder(navController: NavController): NavGraphBuilder.() -> Unit = {

    composable(Routes.Home.name) {
        HomeUI(navController)
    }

    composable(Routes.Search.name) {
        SearchUI()
    }

    composable(Routes.LocalCache.name) {
        LocalCacheUI()
    }

    composable(Routes.Chapter.name) {
        ChapterUI()
    }

    composable(Routes.Setting.name) {
        SettingUI()
    }

    composable(Routes.WebNavi.name) {
        WebNaviUI()
    }

    composable(Routes.MyInfo.name){
        MyInfoUI()
    }

    composable(
        "${Routes.Web}?url={url}",
        listOf(navArgument("url") {
            nullable = true
            type = NavType.StringType
        })
    ) {
        val url = it.arguments?.getString("url")
        if (url != null)
            ComposableWeb(url, Modifier.fillMaxSize())
        else
            Text(
                "url为空",
                Modifier.fillMaxWidth().padding(top = 20.dp),
                textAlign = TextAlign.Center
            )
    }

    composable(Routes.Login.name){
        LoginUI()
    }

    composable(Routes.LocationUI.name){
        LocationUI()
    }

    composable(Routes.VideoPlayer.name){
        VideoPlayer(uri = "http://192.168.50.198:8081/file/player")
    }

    composable(Routes.QrUI.name){
        QrUI()
    }

    composable(Routes.MyBrowsePicture.name){
        MyBrowsePicture()
    }

    composable(Routes.JdUI.name){
        JdUI()
    }

    composable(Routes.LazyStaggeredUI.name){
        LazyStaggeredUI()
    }

}

@OptIn(ExperimentalContracts::class)
inline fun NavController.appNavigation(block: WanNavigation.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    WanNavigation.setNav(this)
    block(WanNavigation)
}

val LocalNavController = staticCompositionLocalOf<NavController> { error("未初始化NavController") }