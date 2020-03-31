package com.example.myapplicationmaps;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    final static int PERMISSION_ALL=1;
    final static String[] PERMISSIONS={Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

    private GoogleMap mMap;
    LocationManager locationManager;

MarkerOptions mo;
    private Marker marker;
    Button btnButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
mo=new MarkerOptions().position(new LatLng(0,0)).title("My Current Location");
if(Build.VERSION.SDK_INT>=23 && !isPermissionGranted()){
requestPermissions(PERMISSIONS,PERMISSION_ALL);
}
else requestLocation();
if(!isLocationEnabled())
{
    showAlert(1);
}
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker=mMap.addMarker(mo);


    }

    @Override
    public void onLocationChanged(Location location) {
LatLng myCoordinatnates=new LatLng(location.getLatitude(),location.getLongitude());
        String cityName = getCityName(myCoordinatnates);
        Toast.makeText(MapsActivity.this,
                cityName, Toast.LENGTH_SHORT).show();
marker.setPosition(myCoordinatnates);
mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinatnates));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("MissingPermission")
    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 10000, 10, this);
    }
    private boolean isLocationEnabled()
    {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
@SuppressLint("NewApi")
public boolean isPermissionGranted(){
if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
    Log.v("mylog", "Permission is granted");
    return true;
}
else {
    Log.v("mylog","Permission is notgranted");
    return false;
}

}
public  void showAlert(final int status)
{
    String message,title,btntext;
    if(status==1)
    {
        message="yours location";
        title="enable Location";
        btntext="Location Settings";
    }
    else {
        message="Please allow";
        title="permission acess";
        btntext="Grant";
    }
    final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
    dialog.setCancelable(false);
    dialog.setTitle(title).setMessage(message).setPositiveButton(btntext, new DialogInterface.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            if (status == 1) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            } else
               requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }
    }) .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            finish();
        }
    });
    dialog.show();

    }
    private String getCityName(LatLng myCoordinates) {
        String myCity = "";
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getLocality();
            Log.d("mylog", "Complete Address: " + addresses.toString());
            Log.d("mylog", "Address: " + address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCity;
    }


}


