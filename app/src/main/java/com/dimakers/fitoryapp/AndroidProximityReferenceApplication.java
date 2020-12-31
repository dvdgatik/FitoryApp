package com.dimakers.fitoryapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.dimakers.fitoryapp.activities.MainActivity;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

public class AndroidProximityReferenceApplication extends Application implements BootstrapNotifier {
    private static final String TAG = "AndroidProximityReferenceApplication";
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();
        Region region = new Region("com.example.backgroundRegion",
                Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6"), null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void didEnterRegion(Region region) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.
        this.startActivity(intent);
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
