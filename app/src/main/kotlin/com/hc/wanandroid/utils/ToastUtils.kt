package com.hc.wanandroid.utils

import android.widget.Toast
import com.hc.wanandroid.App
import kotlinx.coroutines.CoroutineExceptionHandler
import java.lang.ref.WeakReference

object ToastUtils {
    @JvmStatic
    private val toast by weakReference { Toast(App.app) }

    @JvmStatic
    fun showShort(text: CharSequence?) {
        toast.apply {
            duration = Toast.LENGTH_SHORT
            setText(text)
            show()
        }
    }

    @JvmStatic
    fun showLong(text: CharSequence?) {
        toast.apply {
            duration = Toast.LENGTH_LONG
            setText(text)
            show()
        }
    }

    @JvmStatic
    var showSnackbar: ((String) -> Unit)? = null

    @JvmStatic
    fun showSnackbar(text: String) {
        showSnackbar?.invoke(text)
    }

    @JvmStatic
    fun coroutineExceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        showShort(throwable.message)
    }

}
