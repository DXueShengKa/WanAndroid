package com.hc.wanandroid.navigation

import androidx.navigation.NavController
import java.lang.ref.WeakReference


object WanNavigation {

    private var navWeakReference: WeakReference<NavController>? = null


    @JvmStatic
    fun web(url: String) {
        navWeakReference?.get()?.navigate("${Routes.Web}?url=$url")
    }


    @JvmStatic
    fun setNav(navController: NavController) {
        if (navWeakReference?.get() == null) {
            navWeakReference = WeakReference(navController)
        }
    }
}

