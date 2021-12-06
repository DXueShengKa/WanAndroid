package com.hc.wanandroid.utils

import android.widget.Toast
import com.hc.wanandroid.App
import kotlinx.coroutines.CoroutineExceptionHandler
import java.lang.ref.WeakReference

object ToastUtils {
    @JvmStatic
    private var toastReference: WeakReference<Toast>? = null

    @JvmStatic
    fun showShort(text: CharSequence?) {
        toastReference?.get()?.apply {
            duration = Toast.LENGTH_SHORT
            setText(text)
            show()
        } ?: App.app.run {
            Toast.makeText(this, text, Toast.LENGTH_SHORT)
        }.apply {
            toastReference = WeakReference(this)
            show()
        }
    }

    @JvmStatic
    fun showLong(text: CharSequence?) {

        toastReference?.get()?.apply {
            duration = Toast.LENGTH_LONG
            setText(text)
            show()
        } ?: App.app.run {
            Toast.makeText(this, text, Toast.LENGTH_LONG)
        }.apply {
            toastReference = WeakReference(this)
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
