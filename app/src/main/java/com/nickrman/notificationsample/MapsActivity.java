package com.nickrman.notificationsample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    final RxPermissions rxPermissions = new RxPermissions(this);
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationModel locationModel;

    float lat;
    float longitute;
    int radius;

    Marker myPosition;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();

        if (args!=null) {
            if (args.containsKey(FCMService.LAT)) {
                lat = Float.valueOf(args.getString(FCMService.LAT));
            }
            if (args.containsKey(FCMService.LONG)) {
                longitute = Float.valueOf(args.getString(FCMService.LONG));
            }
            if (args.containsKey(FCMService.RADIUS)) {
                radius = Integer.valueOf(args.getString(FCMService.RADIUS));
            }
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            locationModel = new LocationModel(this);
            rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION )
                    .subscribe(grant->{
                        if (grant){
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            mMap.getUiSettings().setCompassEnabled(true);
                            mMap.getUiSettings().setZoomControlsEnabled(true);
                            mMap.setMyLocationEnabled(true);
                            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                                    result ->{
                                        if (result != null) {
                                            setMyPositionMarker(result);
                                            setMarkerFromPushIfExist();
                                        }else {
                                            locationModel.getLocation()
                                                    .subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(
                                                    location -> {
                                                        setMyPositionMarker(location);
                                                        setMarkerFromPushIfExist();
                                                    },
                                                    e ->{
                                                        Toast.makeText(MapsActivity.this, e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                                    }
                                            );
                                        }
                                    }
                            ).addOnFailureListener(
                                    e -> {
                                        Toast.makeText(MapsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    });
        });
    }

    private void setMarkerFromPushIfExist() {
        if (lat>0 && longitute>0){
            LatLng myLocation = new LatLng(lat,longitute);
            MarkerOptions marker = new MarkerOptions().position(myLocation).title("Marker in Sydney");
            marker.icon(bitmapDescriptorFromVector(this,R.drawable.ic_pin));
            Marker m = mMap.addMarker(marker);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            if (myPosition!=null){
                builder.include(myPosition.getPosition());
                builder.include(myLocation);
            }
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
            mMap.animateCamera(cu);
        }
    }

    private void setMyPositionMarker(Location result) {
        LatLng myLocation = new LatLng(result.getLatitude(),result.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(myLocation).title("Marker in Sydney");
        marker.icon(bitmapDescriptorFromVector(this,R.drawable.ic_pin));
        myPosition = mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,16.0f));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
