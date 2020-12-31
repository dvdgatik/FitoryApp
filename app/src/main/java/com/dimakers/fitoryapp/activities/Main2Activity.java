package com.dimakers.fitoryapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;

import static android.location.LocationManager.NETWORK_PROVIDER;

public class Main2Activity extends AppCompatActivity implements LocationListener {
    LocationManager mlocManager;
    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 1000 * 10 * 1; // 1 minutol
    //Minima distancia para updates en metros.
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = 1; // 1.5 metros

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mlocManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(NETWORK_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location changed", Toast.LENGTH_SHORT).show();
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
}
