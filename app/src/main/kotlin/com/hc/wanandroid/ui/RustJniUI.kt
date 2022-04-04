package com.hc.wanandroid.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hc.wanandroid.RustJni
import com.hc.wanandroid.db.AppDatabase
import com.hc.wanandroid.db.dao.AddressDao
import com.hc.wanandroid.db.entity.City
import com.hc.wanandroid.db.entity.County
import com.hc.wanandroid.db.entity.Province
import com.hc.wanandroid.utils.ToastUtils
import com.hc.wanandroid.utils.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


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

        AddressUI(uiState = vm.uiState)

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddressUI(uiState: AddressState) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(10.dp, 10.dp)
    ) {
        stickyHeader {
            Column(Modifier.background(MaterialTheme.colorScheme.background)) {
                Text("请选择地址", Modifier.padding(vertical = 10.dp))
                Row(Modifier.clickable(onClick = uiState.onPrev)) {
                    Text(uiState.provinceName, Modifier.padding(4.dp))
                    Text(uiState.cityName, Modifier.padding(4.dp))
                    Text(uiState.countyName, Modifier.padding(4.dp))
                }
            }

        }

        items(uiState.addressItems.size) { index ->
            val item = uiState.addressItems[index]

            Row(Modifier.clickable {
                uiState.select = index
                uiState.clicks(index)

                if (uiState.clickIndex < 2)
                    uiState.clickIndex += 1
            }
            ) {
                Text(item)

                if (uiState.select == index)
                    Icon(Icons.Default.DateRange, null)
            }
        }
    }
}

@Stable
class AddressState(private val coroutineScope: CoroutineScope, private val addressDao: AddressDao) {

    init {
        coroutineScope.launch {
            provinces = addressDao.getAllProvince()
            addressItems = provinces.map { it.name }
        }
    }

    private var provinceIndex = 0
    private var cityIndex = 0
    private var countyIndex = 0

    private fun onProvince() {
        coroutineScope.launch {
            provinceName = provinces[provinceIndex].name
            citys = addressDao.getCityByProvinceId(provinces[provinceIndex].provinceId)
            addressItems = citys.map { it.name }
        }
    }

    private fun onCity() {
        coroutineScope.launch {
            cityName = citys[cityIndex].name
            countys = addressDao.getCountyByCityId(citys[cityIndex].cityId)
            addressItems = countys.map { it.name }
        }
    }

    private fun onCounty() {
        coroutineScope.launch {
            countyName = countys[countyIndex].name
            ToastUtils.showShort("点击了${countys[countyIndex]}")
        }
    }

    private fun onPrevCity() {
        countyName = ""
        addressItems = countys.map { it.name }
    }

    private fun onPrevProvince() {
        cityName = ""
        addressItems = provinces.map { it.name }
    }

    var addressItems by mutableStateOf(emptyArray<String>())

    private var provinces: Array<Province> = emptyArray()
    private var citys: Array<City> = emptyArray()
    private var countys: Array<County> = emptyArray()

    var provinceName by mutableStateOf("")
    var cityName by mutableStateOf("")
    var countyName by mutableStateOf("")


    var select by mutableStateOf(-1)

    fun clicks(it: Int) {
        when (clickIndex) {
            0 -> {
                provinceIndex = it
                onProvince()
                select = -1
            }
            1 -> {
                cityIndex = it
                onCity()
                select = -1
            }
            else -> {
                countyIndex = it
                onCounty()
            }
        }
    }

    var clickIndex = 0

    val onPrev = {
        select = -1
        if (clickIndex > 0) {
            clickIndex -= 1

            when (clickIndex) {
                0 -> {
                    onPrevProvince()
                }
                1 -> {
                    onPrevCity()
                }
            }
        }
    }


}

@HiltViewModel
class RustJniVm @Inject constructor(
    appDatabase: AppDatabase
) : ViewModel() {
    private val addressDao = appDatabase.addressDao()

    var ab by mutableStateOf(RustJni.addJNI(RustJni.randomInt(), RustJni.randomInt()))

    fun ref() {

    }

    val uiState = AddressState(viewModelScope, addressDao)


}
