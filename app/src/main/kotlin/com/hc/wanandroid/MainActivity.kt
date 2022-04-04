package com.hc.wanandroid

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hc.wanandroid.navigation.LocalNavController
import com.hc.wanandroid.navigation.Routes
import com.hc.wanandroid.navigation.navGraphBuilder
import com.hc.wanandroid.utils.networkCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
1
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = android.graphics.Color.TRANSPARENT


        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            val colors = if (isDarkTheme) darkColorScheme() else lightColorScheme()
            val barColor = colors.primary.run {
                android.graphics.Color.argb(0.4f, red, green, blue)
            }
            window.statusBarColor = barColor

            MaterialTheme(
                colorScheme = colors,
//                typography = ThemeShapes
            ) {
                MainProvider()
            }

        }

    }

    @Composable
    @OptIn(InternalComposeApi::class)
    private fun MainProvider() {

        val navController: NavHostController = rememberNavController()

        currentComposer.startProviders(
            arrayOf(
                LocalNavController provides navController,
                LocalActivity provides this
            )
        )

        MainUI(navController)

        currentComposer.endProviders()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainUI(navController: NavHostController) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerContent = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Routes.values().forEach { r ->
                        Text(
                            r.name,
                            Modifier
                                .padding(vertical = 10.dp)
                                .clickable {
                                    navController.navigate(r.name)
                                    scope.launch {
                                        drawerState.close()
                                    }
                                },
                            MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.navigationBarsPadding())
                }
            },
            drawerState = drawerState
        ){
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        modifier = Modifier.statusBarsPadding(),
                        navigationIcon = {
                            Icon(Icons.Default.Menu, null, Modifier.clickable {
                                scope.launch {
                                    drawerState.open()
                                }
                            })
                        },
                        title = {
                            val bse by navController.currentBackStackEntryAsState()
                            Text(bse?.destination?.route ?: "空", Modifier.clickable {
                                navController.navigateUp()
                            })
                        }
                    )
                }
            ){
                NavHost(
                    navController = navController,
                    startDestination = Routes.StaggeredGrid.name,
                    modifier = Modifier.padding(it),
                    builder = navGraphBuilder(navController)
                )
            }
        }

    }

    private fun NavController.navigateSaveState(route: String) {
        navigate(route) {
            popUpTo(graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

}

private const val TAG = "MainActivity"

val LocalActivity = staticCompositionLocalOf<Activity> { error("未设置Activity") }

