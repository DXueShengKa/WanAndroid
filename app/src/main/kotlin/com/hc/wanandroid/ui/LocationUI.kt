package com.hc.wanandroid.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.location.LocationManagerCompat
import com.hc.wanandroid.App
import com.hc.wanandroid.utils.ToastUtils
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Composable
fun LocationUI() {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        val l = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ map ->
            if (map.entries.any { !it.value })
                ToastUtils.showShort("权限被拒绝")
        }
        val locationState = remember {
            l.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            LocationState()
        }
        TextButton({
            locationState.getCurrentLocation(true)
        }, enabled = locationState.isNotLocation) {
            Text("获取定位信息")
        }
        var city by remember { mutableStateOf("中国广东省深圳市南山区国人通信大厦") }
        TextField(city,{city = it},
                trailingIcon = {
                    Icon(Icons.Default.LocationOn,null,Modifier.clickable {
                        locationState.getLocationByName(city)
                    })
                }
            )
        if (locationState.currentLocation == null) {
            Icon(Icons.Default.LocationOn, null)
        } else {
            Text(locationState.locationString)
            Divider()
            Text(locationState.addressState)
            Divider()
        }
        Text(locationState.locationState)
    }
}


class LocationState(
    private val context: Context = App.app,
    private val locationManager: LocationManager = context.getSystemService(LocationManager::class.java)
) : RememberObserver {
    private var executor: ExecutorService? = null

    var currentLocation by mutableStateOf<Location?>(null)
    var locationString = ""

    var addressState by mutableStateOf("")
    var locationState by mutableStateOf("")

    var isNotLocation by mutableStateOf(true)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(isNetwork: Boolean = false) {
        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            throw RuntimeException("未开启定位功能")
        }
        isNotLocation = false
        val provider = when {
            isNetwork -> LocationManager.NETWORK_PROVIDER
            LocationManagerCompat.hasProvider(
                locationManager,
                LocationManager.GPS_PROVIDER
            ) -> LocationManager.GPS_PROVIDER
            else -> LocationManager.NETWORK_PROVIDER
        }

        LocationManagerCompat.getCurrentLocation(
            locationManager,
            provider,
            null,
            executor!!
        ) {
            if (it != null) {
                currentLocation = it
                locationString = """
                    当前位置信息 $provider
                    纬度：${it.latitude}
                    经度：${it.longitude}
                    海拔：${it.altitude}
                    时间戳：${it.time}
                """.trimIndent()
                getAddress(context,it)
            } else {
                getCurrentLocation(true)
            }

            isNotLocation = true
        }

    }

    private fun getAddress(context: Context,location: Location){
        val geocoder = Geocoder(context)
        val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 4)
        addressState = addressList.joinToString(prefix = "\n") { it.toString() }
    }

    fun getLocationByName(locationName:String){
        val geocoder = Geocoder(context, Locale.CHINA)
        val addressList = geocoder.getFromLocationName(locationName, 3,
            //中国经纬度矩形的的大致范围
            3.86,73.66,53.55,135.05
        )
        locationState = addressList.joinToString("---------------------------------------") {
            """
                
                countryName ${it.countryName}
                countryCode ${it.countryCode}
                locality ${it.locality}
                subLocality ${it.subLocality}
                featureName ${it.featureName}
                longitude ${it.longitude}
                latitude ${it.latitude}
                addressLine ${buildString { 
                    for (i in 0..it.maxAddressLineIndex) append(it.getAddressLine(i))    
                }}
                adminArea ${it.adminArea}
                subLocality ${it.subLocality}
                thoroughfare ${it.thoroughfare}
                subThoroughfare ${it.subThoroughfare}
                
                ${it.toString()}
            """.trimIndent()

        }
    }


    override fun onAbandoned() {

    }

    override fun onRemembered() {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor()
        }
    }

    override fun onForgotten() {
        executor?.shutdown()
        executor = null
        Log.d(TAG, "onForgotten")
    }

}


private const val TAG = "LocationState"