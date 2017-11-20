package com.example.root.restaurant;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GPS extends AppCompatActivity {

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private final String TAG = "jennysMsg";
    private Location lastLoc = null;
    TextView t3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        Log.i(TAG, "what the hell??????????????????????????????????");

        t3 = (TextView) findViewById(R.id.textView3);

        Log.i(TAG, "After text");

        locationMangaer = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        Log.i(TAG, "After get system service");

        locationListener = new MyLocationListener();
        Log.i(TAG, "After listener");
        locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        Log.i(TAG, "After request");
        lastLoc = locationMangaer.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(lastLoc != null) {
            //locationMangaer.removeUpdates(locationListener);
            t3.setText("Location : " + String.valueOf(lastLoc.getLatitude()));
        }

    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            lastLoc = loc;
            Log.i(TAG, "onLocationChanged");

            /*Toast.makeText(getBaseContext(),"Location changed : Lat: " +
                            loc.getLatitude()+ " Lng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " +loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " +loc.getLatitude();
            Log.v(TAG, latitude);

            t3.setText("Location : " + longitude + latitude);*/

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}

