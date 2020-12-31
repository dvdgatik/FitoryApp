package com.dimakers.fitoryapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.dimakers.fitoryapp.activities.BeaconEncontrado;
import com.dimakers.fitoryapp.activities.ClubMain;
import com.dimakers.fitoryapp.activities.MainActivity;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Fecha;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.Visita;
import com.dimakers.fitoryapp.services.BuscarBeacon;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Share;
import com.onesignal.OneSignal;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.io.Serializable;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class FitoryApp extends Application implements BootstrapNotifier, RangeNotifier {

    private static final String TAG = "BeaconReferenceApp";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private ClubMain monitoringActivity = null;
    private String cumulativeLog = "";
    private BeaconManager beaconManager;
    private FitoryService service = API.getApi().create(FitoryService.class);
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public void onCreate() {
        super.onCreate();
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
        // layout expression for other beacon types, do a web search for "setBeaconLayout"
        // including the quotes.
        //
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
//
//        beaconManager.setDebug(true);


        // Uncomment the code below to use a foreground service to scan for beacons. This unlocks
        // the ability to continually scan for long periods of time in the background on Andorid 8+
        // in exchange for showing an icon at the top of the screen and a always-on notification to
        // communicate to users that your app is using resources in the background.
        //


        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_bluetooth);
        builder.setContentTitle("Tienes una meta ¡Cúmplela!");
        Intent intent = new Intent(this, ClubMain.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
                    "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Notification Channel Description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
            beaconManager.enableForegroundServiceScanning(builder.build(), 456);
        }
        // For the above foreground scanning service to be useful, you need to disable
        // JobScheduler-based scans (used on Android 8+) and set a fast background scan
        // cycle that would otherwise be disallowed by the operating system.
        //
        beaconManager.setEnableScheduledScanJobs(false);
//        beaconManager.setBackgroundBetweenScanPeriod(60000l);
//        beaconManager.setBackgroundScanPeriod(11000);


        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        // wake up the app when a beacon is seen
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);

        // If you wish to test beacon detection in the Android Emulator, you can use code like this:
        // BeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
        // ((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();
    }

    public void disableMonitoring() {
        if (regionBootstrap != null) {
            regionBootstrap.disable();
            regionBootstrap = null;
        }
    }
    public void enableMonitoring() {
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }


    @Override
    public void didEnterRegion(Region arg0) {
//        // In this example, this class sends a notification to the user whenever a Beacon
//        // matching a Region (defined above) are first seen.
//        Log.d(TAG, "did enter region.");
//        if (!haveDetectedBeaconsSinceBoot) {
//            Log.d(TAG, "auto launching MainActivity");
//
//            // The very first time since boot that we detect an beacon, we launch the
//            // MainActivity
//            Intent intent = new Intent(this, ClubMain.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            // Important:  make sure to add android:launchMode="singleInstance" in the manifest
//            // to keep multiple copies of this activity from getting created if the user has
//            // already manually launched the app.
//            this.startActivity(intent);
//            haveDetectedBeaconsSinceBoot = true;
//        } else {
//            if (monitoringActivity != null) {
//                // If the Monitoring Activity is visible, we log info about the beacons we have
//                // seen on its display
//                Log.d(TAG, "I see a beacon again");
//                sendNotification();
//            } else {
//                // If we have already seen beacons before, but the monitoring activity is not in
//                // the foreground, we send a notification to the user on subsequent detections.
//                Log.d(TAG, "Sending notification.");
//                sendNotification();
//            }
//        }


    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "I no longer see a beacon");
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
//        Log.d(TAG, "Current region state is: " + (state == 1 ? "INSIDE" : "OUTSIDE ("+state+")"));
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.addRangeNotifier(this);
    }

    private void sendNotification() {
//        Toast.makeText(monitoringActivity, "LMAO", Toast.LENGTH_SHORT).show();
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(this)
//                        .setContentTitle("Beacon Reference Application")
//                        .setContentText("An beacon is nearby.")
//                        .setSmallIcon(R.drawable.ic_bluetooth);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addNextIntent(new Intent(this, ClubMain.class));
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        builder.setContentIntent(resultPendingIntent);
//        NotificationManager notificationManager =
//                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, builder.build());

    }

    public void setMonitoringActivity(ClubMain activity) {
        this.monitoringActivity = activity;
    }


    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
//        Log.d(TAG, "Ranging");
        for(Beacon beacon : collection) {
            Log.d(TAG, beacon.getId1()+"");
            if (beacon.getId1().toString().equals("74278bda-b644-4520-8f0c-720eaf059935")) {
                if (beacon.getDistance()<4) {
                    Log.d(TAG, "Beacon found");
                    Call<Sucursal> sucursalCall = service.obtenerSucursalMayorMenor(collection.iterator().next().getId2().toString(),collection.iterator().next().getId3().toString(),true);
                    sucursalCall.enqueue(new Callback<Sucursal>() {
                        @Override
                        public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                            if (response.isSuccessful()) {
                                if (!response.body().getResults().isEmpty()) {
                                    Sucursal sucursal = response.body().getResults().get(0);
                                    int clienteID = sharedPreferences.getInt(Variables.CLIENTEID,0);
                                    if (clienteID != 0) {
                                        confirmarAsistencia(sucursal,beacon, clienteID);
                                    }
                                } else {
                                }
                            } else {
                            }
                        }

                        @Override
                        public void onFailure(Call<Sucursal> call, Throwable t) {
                        }
                    });
//                Intent intent = new Intent(this, BeaconEncontrado.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                this.startActivity(intent);
                    return;
                }
            }
        }
    }

    private void confirmarAsistencia(Sucursal sucursal, Beacon beacon, int clienteID) {
        Call<Fecha> fechaCall = service.consultarFecha();
        fechaCall.enqueue(new Callback<Fecha>() {
            @Override
            public void onResponse(Call<Fecha> call, Response<Fecha> response) {
                if (response.isSuccessful()) {
                    String fecha = response.body().getFecha();

                    Call<Visita> visitaCall = service.comprobarVisita(clienteID,sucursal.getId(),fecha);
                    visitaCall.enqueue(new Callback<Visita>() {
                        @Override
                        public void onResponse(Call<Visita> call, Response<Visita> response) {
                            if (response.isSuccessful()) {
                                //Ya tiene asistencia en la sucursal por tanto no mostraremos de nuevo la pantalla de confirmar asistencia
                                if (!response.body().getResults().isEmpty()) {
//                                    Toast.makeText(BuscarBeacon.this, "Ya tiene asistencia", Toast.LENGTH_SHORT).show();//
                                } else {
                                    //No tiene asistencia a la sucursal, comprobaremos sesiones y subscripciones vigentes
//                                    Toast.makeText(BuscarBeacon.this, "No tiene asistencia", Toast.LENGTH_SHORT).show();
                                    editor.putInt(Variables.SUCURSAL_ID, sucursal.getId());
                                    editor.commit();
                                    /**
                                     *	Iniciar activity al detectar beacon
                                     */
                                    Intent intent = new Intent(FitoryApp.this, BeaconEncontrado.class);
                                    intent.putExtra(Variables.BEACON, (Serializable) beacon);
                                    intent.putExtra("SUCURSAL", sucursal);
                                    intent.putExtra("FECHA", fecha);
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                            } else {
                            }
                        }

                        @Override
                        public void onFailure(Call<Visita> call, Throwable t) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Fecha> call, Throwable t) {
                Toast.makeText(FitoryApp.this, "No se pudo comprobar la fecha del dispositivo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
