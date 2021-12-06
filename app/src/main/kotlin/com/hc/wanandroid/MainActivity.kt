package com.hc.wanandroid

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.hc.wanandroid.navigation.LocalNavController
import com.hc.wanandroid.navigation.Routes
import com.hc.wanandroid.navigation.navGraphBuilder
import com.hc.wanandroid.utils.LocalIsActiveNetworkMetered
import com.hc.wanandroid.utils.networkCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

//        WindowInsetsControllerCompat(window,window.decorView).apply {
//            isAppearanceLightStatusBars = true
//            isAppearanceLightNavigationBars = true
//        }

        networkCallback(this, this, LocalIsActiveNetworkMetered::provides)

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

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            Log.e(TAG, it.toString())
        }.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
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

        ProvideWindowInsets {
            MainUI(navController)
        }

        currentComposer.endProviders()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainUI(navController: NavHostController) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        NavigationDrawer(
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
                    startDestination = Routes.LocationUI.name,
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

