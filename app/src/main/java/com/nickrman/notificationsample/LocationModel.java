package com.nickrman.notificationsample;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import org.reactivestreams.Subscriber;

import io.reactivex.Observable;

public class LocationModel {
    private static final String TAG = LocationModel.class.getSimpleName();

    private LocationManager locationManager;

    public LocationModel(Context context) {
        locationManager = (LocationManager)context
                .getSystemService(Context.LOCATION_SERVICE);
    }

    public Observable<Location> getLocation(){
        return Observable.create(emitter -> {
            final LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(final Location location) {
                    emitter.onNext(location);

                    if (Looper.myLooper()!=null) {
                        Looper.myLooper().quit();
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkStatus = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            String locationProvider ="";
            if(gpsStatus){
                locationProvider = LocationManager.GPS_PROVIDER;
            }
            else if(networkStatus){
                locationProvider = LocationManager.NETWORK_PROVIDER;
            }
            else {
                emitter.onError(new Throwable("Location unavailable."));
                return;
            }
            Looper.prepare();

            try {
                locationManager.requestSingleUpdate(locationProvider,
                        locationListener, Looper.myLooper());
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            Looper.loop();
        });
    }

}
