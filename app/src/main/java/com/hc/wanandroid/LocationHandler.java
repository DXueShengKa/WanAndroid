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
     * ??????????????????
     * @param provider {@link LocationManager#GPS_PROVIDER}
     * {@link LocationManager#NETWORK_PROVIDER} {@link LocationManager#PASSIVE_PROVIDER}
     */
    @SuppressLint("MissingPermission")
    public void getCurrentLocation(String provider,Consumer<Location> consumer) {
        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            throw new RuntimeException("?????????????????????");
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
     * ?????????????????????????????????gps??????
     */
    public void getCurrentLocation(Consumer<Location> consumer) {

        var provider = LocationManagerCompat.hasProvider(
                locationManager,
                LocationManager.GPS_PROVIDER
        ) ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;

        getCurrentLocation(provider,consumer);
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????
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
     * ??????????????????????????????
     */
    public List<Address> getAddressByName(String locationName,int maxResults) throws IOException {
        if (Geocoder.isPresent()) throw new IOException("????????????????????????Geocoder?????????");
        var geocoder = new Geocoder(context, Locale.CHINA);
        return geocoder.getFromLocationName(locationName, maxResults,
                //???????????????????????????????????????
                3.86, 73.66, 53.55, 135.05
        );
    }


    /**
     * ?????????????????????????????????
     */
    public List<Address> getAddressByLocation(double latitude, double longitude, int maxResults) throws IOException {
        if (Geocoder.isPresent()) throw new IOException("????????????????????????Geocoder?????????");
        var geocoder = new Geocoder(context);
        return geocoder.getFromLocation(latitude, longitude, maxResults);
    }


}
