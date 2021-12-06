package com.hc.wanandroid.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberImagePainter
import com.hc.wanandroid.entity.CoinInfo
import com.hc.wanandroid.entity.UserInfo
import com.hc.wanandroid.net.UserApi
import com.hc.wanandroid.net.throwData
import com.hc.wanandroid.utils.ToastUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun MyInfoUI() {
    val vm: MyInfoViewModel = hiltViewModel()

    Column {
        val imgModifier = Modifier.size(100.dp)
        if (vm.user.icon.isEmpty())
            Image(Icons.Default.Person, "头像", imgModifier)
        else
            Image(rememberImagePainter(vm.user.icon), "头像", imgModifier)

        Text(
            remember {
                buildAnnotatedString {
                    append("用户名：")
                    append(vm.user.username)
                    append('\n')

                    append("积分：")
                    append(vm.user.coinCount.toString())
                    append('\n')

                    append("收藏：")
                    append(vm.user.collectIds.size.toString())
                    append('\n')

                    append("积分排名：")
                    append(vm.coin.coinCount.toString())
                    withStyle(SpanStyle(Color.Red)) {
                        append(" lv")
                        append(vm.coin.level.toString())
                    }
                    append('\n')
                }
            }

        )

        Button(onClick = { vm.getUserInfo() }) {
            Text("更新")
        }

    }

}

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val api: UserApi,
    private val application: Application
) : ViewModel() {
    var user by mutableStateOf(UserInfo())

    var coin = CoinInfo()

    private val spc = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, s ->
        Log.w("MyInfoUI", s)
    }

    private val infoSp = application.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        .apply {
            registerOnSharedPreferenceChangeListener(spc)
        }

    init {
        coin = CoinInfo.load(application)
        user = UserInfo.load(application)
    }

    override fun onCleared() {
        super.onCleared()
        infoSp.unregisterOnSharedPreferenceChangeListener(spc)
    }

    fun getUserInfo() {
        viewModelScope.launch(ToastUtils.coroutineExceptionHandler()) {

            api.lgUserinfo().throwData().apply {
                UserInfo.save(application, userInfo)
                CoinInfo.save(application, coinInfo)
            }

        }
    }

}
