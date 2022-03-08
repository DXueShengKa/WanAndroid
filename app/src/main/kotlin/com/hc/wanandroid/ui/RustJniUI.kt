package com.hc.wanandroid.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hc.wanandroid.MJni
import com.hc.wanandroid.RustJni
import com.hc.wanandroid.db.AppDatabase
import com.hc.wanandroid.db.entity.City
import com.hc.wanandroid.db.entity.County
import com.hc.wanandroid.db.entity.Province
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@Composable
fun RustJniUI() {
    val vm: RustJniVm = hiltViewModel()
    Column(Modifier.fillMaxSize()) {
        Row {

            TextButton({
                vm.ref()
            }) {
                Text("刷新")
            }

            Text(vm.ab)

        }

        LazyColumn{
            items(vm.province){
                Text(it.toString())
            }
        }
    }
}

@HiltViewModel
class RustJniVm @Inject constructor(
    appDatabase: AppDatabase
): ViewModel() {
    private val addressDao = appDatabase.addressDao()

    var ab by mutableStateOf(RustJni.addJNI(RustJni.randomInt(),RustJni.randomInt()))

    fun ref() {

        viewModelScope.launch {
            province = addressDao.getAllProvince()

        }
    }

    var province by mutableStateOf<Array<Province>>(emptyArray())

    var city by mutableStateOf<Array<City>>(emptyArray())

    var county by mutableStateOf<Array<County>>(emptyArray())

//    var county by mutableStateOf<Array<County>>(emptyArray())






}