package com.hc.wanandroid.ui

import android.app.Application
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.common.BrowsePicture
import com.hc.wanandroid.entity.CoinInfo
import com.hc.wanandroid.entity.UserInfo
import com.hc.wanandroid.net.UserApi
import com.hc.wanandroid.net.throwData
import com.hc.wanandroid.utils.ToastUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/*
@SuppressLint("RememberReturnType")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun LoginUI3() {
    val c = remember { BrowsePictureState<Uri>() }

    val l = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        c.addAll(it)
    }

    Column {
        remember(c.pagerState.currentPage) {
            c.flushed()
        }

        HorizontalPager(
            c.pagerState,
            Modifier
                .fillMaxSize()
                .weight(1f)
                .onSizeChanged {
                    c.imageWidth = it.width.toFloat()
                },
            dragEnabled = true
        ) { page ->

            Image(
                rememberAsyncImagePainter(data = c[page]), null,
                Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.Green)
                    .then(
                        if (c.pagerState.currentPage == page) Modifier
                            .graphicsLayer(
                                scaleY = c.scale,
                                scaleX = c.scale,
                                translationX = c.offset.x,
                                translationY = c.offset.y
                            )
                            .pointerInput(page, c.detectZoom())
                        else Modifier
                    )
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .height(70.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("添加", Modifier.clickable {
                l.launch("image/*")
            })

        }
        Spacer(Modifier.navigationBarsHeight())
    }
}
*/
 */

@Composable
fun LoginUI() {

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val vm: LoginViewModel = hiltViewModel()

        val paddingValues = PaddingValues(top = 30.dp)

        TextField(
            vm.username,
            { vm.username = it },
            Modifier.padding(paddingValues),
            label = {
                Text("用户名")
            },
            leadingIcon = {
                Icon(Icons.Default.Person, null)
            },
            singleLine = true
        )

        var showPass by remember { mutableStateOf(false) }

        TextField(
            vm.password,
            { vm.password = it },
            Modifier.padding(paddingValues),
            label = {
                Text("密码")
            },
            leadingIcon = {
                Icon(Icons.Default.Lock, null)
            },
            trailingIcon = {
                Icon(Icons.Default.Info, null, Modifier.clickable { showPass = !showPass })
            },
            singleLine = true,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(
                '*'
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(
            { vm.login() },
            Modifier
                .width(200.dp)
                .padding(paddingValues)
        ) {
            Text("登陆")
        }

    }
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: UserApi,
    private val application: Application
) : ViewModel() {
    var username by mutableStateOf("风平浪静的明天")
    var password by mutableStateOf("")


    fun getUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            api.lgUserinfo().throwData()?.apply {
                UserInfo.save(application, userInfo)
                CoinInfo.save(application, coinInfo)
            }
        }
    }

    fun login() {

        viewModelScope.launch(Dispatchers.IO) {

            val s = api.login(username, password).throwData()

            UserInfo.save(application, s!!)

            ToastUtils.showSnackbar("登陆成功")

        }
    }
}
