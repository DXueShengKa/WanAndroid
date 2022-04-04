package com.hc.wanandroid.utils

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

class WeakReference<T>(private val create:()->T) {

    private var wr:WeakReference<T>? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val value = wr?.get()
        return if (value == null){
            val newValue = create()
            wr = WeakReference(newValue)
            newValue
        }else {
            value
        }
    }

}

fun <T> weakReference(create:()->T):com.hc.wanandroid.utils.WeakReference<T>{
    return com.hc.wanandroid.utils.WeakReference(create)
}



