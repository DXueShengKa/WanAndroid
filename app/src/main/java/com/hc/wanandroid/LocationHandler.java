package com.hc.wanandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.core.location.LocationManagerCompat;
import androidx.core.util.Consumer;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LocationHandler {
    private final LocationManager locationManager;
    private Executor executor;
    private final Context context;

    private Lifecycle lifecycle;
    private final LifecycleObserver observer = new DefaultLifecycleObserver() {
        ExecutorService executorService;
        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {
            executorService = Executors.newSingleThreadExecutor();
            executor = executorService;
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            executorService.shutdown();
            lifecycle.removeObserver(observer);
        }
    };

    public LocationHandler(Context context, LocationManager locationManager, Executor executor) {
        this.context = context;
        this.locationManager = locationManager;
        this.executor = executor;
    }

    public LocationHandler(Context context, Executor executor) {
        this.context = context;
        this.locationManager = context.getSystemService(LocationManager.class);
        this.executor = executor;
    }

    public LocationHandler(Context context, Lifecycle lifecycle) {
        this.context = context;
        this.locationManager = context.getSystemService(LocationManager.class);
        this.lifecycle = lifecycle;
        this.lifecycle.addObserver(observer);
    }

    /**
     * 获取当前位置
     * @param provider {@link LocationManager#GPS_PROVIDER}
     * {@link LocationManager#NETWORK_PROVIDER} {@link LocationManager#PASSIVE_PROVIDER}
     */
    @SuppressLint("MissingPermission")
    public void getCurrentLocation(String provider,Consumer<Location> consumer) {
        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            throw new RuntimeException("未开启定位功能");
        }
        LocationManagerCompat.getCurrentLocation(
                locationManager,
                provider,
                null,
                executor,
                consumer
        );
    }

    /**
     * 获取当前位置，优先进行gps定位
     */
    public void getCurrentLocation(Consumer<Location> consumer) {

        var provider = LocationManagerCompat.hasProvider(
                locationManager,
                LocationManager.GPS_PROVIDER
        ) ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;

        getCurrentLocation(provider,consumer);
    }

    /**
     * 获取当前位置，如果第一次无法获取位置信息，则第二次进行网络定位
     */
    public void getCurrentLocationDouble(Consumer<Location> consumer) {
        var provider = LocationManagerCompat.hasProvider(
                locationManager,
                LocationManager.GPS_PROVIDER
        ) ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;

        getCurrentLocation(provider,location -> {
            if (location == null){
                getCurrentLocation(LocationManager.NETWORK_PROVIDER,consumer);
            }else {
                consumer.accept(location);
            }
        });
    }


    /**
     * 根据名称获取详细地址
     */
    public List<Address> getAddressByName(String locationName,int maxResults) throws IOException {
        if (Geocoder.isPresent()) throw new IOException("不支持地理编码（Geocoder）查询");
        var geocoder = new Geocoder(context, Locale.CHINA);
        return geocoder.getFromLocationName(locationName, maxResults,
                //中国经纬度矩形的的大致范围
                3.86, 73.66, 53.55, 135.05
        );
    }


    /**
     * 根据经纬度获取详细地址
     */
    public List<Address> getAddressByLocation(double latitude, double longitude, int maxResults) throws IOException {
        if (Geocoder.isPresent()) throw new IOException("不支持地理编码（Geocoder）查询");
        var geocoder = new Geocoder(context);
        return geocoder.getFromLocation(latitude, longitude, maxResults);
    }


}
