package com.dimakers.fitoryapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.dimakers.fitoryapp.activities.Login;
import com.dimakers.fitoryapp.services.BuscarBeacon;

public class BroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent serviceIntent = new Intent(context, BuscarBeacon.class);
//        serviceIntent.setAction("com.dimakers.fitoryapp.services.BuscarBeacon");
//        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
