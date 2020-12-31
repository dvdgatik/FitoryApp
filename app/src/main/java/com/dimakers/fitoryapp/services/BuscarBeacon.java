package com.dimakers.fitoryapp.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.activities.BeaconEncontrado;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.activities.MainActivity;
import com.dimakers.fitoryapp.adapters.SuscripcionesMes;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Cliente;
import com.dimakers.fitoryapp.api.models.Fecha;
import com.dimakers.fitoryapp.api.models.Sesion;
import com.dimakers.fitoryapp.api.models.SubscripcionMes;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.Visita;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscarBeacon extends Service implements BeaconConsumer{
    private static final String TAG = "FITORYRANGING";
    public static boolean MUST_BE_RUNNING = true;
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ArrayList<String> beaconList;
    private BeaconManager beaconManager;
    private BackgroundPowerSaver backgroundPowerSaver;

    
    @Override
    public void onBeaconServiceConnect() {

        this.beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                Log.d(TAG, "didRangeBeaconsInRegion: ");
                if (collection.size() > 0) {
                    Beacon beacon = collection.iterator().next();

//                        if (beacon.getDistance()<4) {
//                            MUST_BE_RUNNING = true;
//                            if (MUST_BE_RUNNING) {
//                                MUST_BE_RUNNING = false;
//                                int clienteID = sharedPreferences.getInt(Variables.CLIENTEID,0);
//                                if (clienteID != 0) {
//
//                                    Call<Sucursal> sucursalCall = service.obtenerSucursalMayorMenor(collection.iterator().next().getId2().toString(),collection.iterator().next().getId3().toString(),true);
//                                    sucursalCall.enqueue(new Callback<Sucursal>() {
//                                        @Override
//                                        public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
//                                            if (response.isSuccessful()) {
//                                                if (!response.body().getResults().isEmpty()) {
//                                                    Sucursal sucursal = response.body().getResults().get(0);
//
//                                                    confirmarAsistencia(sucursal,beacon, clienteID);
//
//    //                                                consultarFecha(clienteID, sucursal.getId());
//
//                                                } else {
//                                                    MUST_BE_RUNNING = true;
//                                                }
//                                            } else {
//                                                MUST_BE_RUNNING = true;
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<Sucursal> call, Throwable t) {
//                                            MUST_BE_RUNNING = true;
//                                        }
//                                    });
//
//                                }
//
//                        }
//                    } else {
//                            MUST_BE_RUNNING = false;
//                        }
                }
            }
        });
        try {
            Region region = new Region("FitoryApp", Identifier.parse("74278bda-b644-4520-8f0c-720eaf059935"), null, null);
            this.beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        this.beaconList = new ArrayList<String>();
        this.beaconManager = BeaconManager.getInstanceForApplication(this);
        this.beaconManager.getBeaconParsers().add(new BeaconParser(). setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        //Notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setContentTitle("Tienes una meta ¡Cúmplela!")
//                    .setSmallIcon(R.drawable.ic_bluetooth)
//                    .setContentIntent(pendingIntent)
//                    .build();

//            startForeground(1, notification);
        }

//        beaconManager.setEnableScheduledScanJobs(false);
//        beaconManager.setBackgroundBetweenScanPeriod(1L);
//        beaconManager.setBackgroundScanPeriod(1100L);
        beaconManager.setForegroundBetweenScanPeriod(0);
        beaconManager.setForegroundScanPeriod(8000);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(8000);
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        this.beaconManager.bind(this);
//        Variables.is_running = true;
        MUST_BE_RUNNING = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Variables.is_running = false;
    }

//        public void consultarFecha(int clienteID, int sucursalID){
//        Call<Fecha> fechaCall = service.consultarFecha();
//        fechaCall.enqueue(new Callback<Fecha>() {
//            @Override
//            public void onResponse(Call<Fecha> call, Response<Fecha> response) {
//                if (response.isSuccessful()) {
//                    Fecha fecha = response.body();
//                    Toast.makeText(getApplicationContext(), ""+fecha.getFecha(), Toast.LENGTH_SHORT).show();
//                    comprobarAsistencia(clienteID, sucursalID, fecha);
//                } else {
//                    Toast.makeText(getApplicationContext(), "No se pudo obtener fecha del servidor", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Fecha> call, Throwable t) {
//
//            }
//        });
//
//    }

//    private void comprobarAsistencia(int clienteID, int sucursalID, Fecha fecha) {
//        Call<Visita> visitaCall = service.comprobarVisita(clienteID,sucursalID,fecha.getFecha());
//        visitaCall.enqueue(new Callback<Visita>() {
//            @Override
//            public void onResponse(Call<Visita> call, Response<Visita> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getResults().isEmpty()) {
//                        consultarSesiones(sucursalID, clienteID, fecha);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Ya tiene la asistencia del día", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Visita> call, Throwable t) {
//
//            }
//        });
//    }

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
//                                    Toast.makeText(BuscarBeacon.this, "Ya tiene asistencia", Toast.LENGTH_SHORT).show();
                                    MUST_BE_RUNNING = true;
//
                                } else {
                                    //No tiene asistencia a la sucursal, comprobaremos sesiones y subscripciones vigentes
//                                    Toast.makeText(BuscarBeacon.this, "No tiene asistencia", Toast.LENGTH_SHORT).show();
                                    editor.putInt(Variables.SUCURSAL_ID, sucursal.getId());
                                    editor.commit();
                                    /**
                                     *	Iniciar activity al detectar beacon
                                     */
//                                    Intent intent = new Intent(BuscarBeacon.this, BeaconEncontrado.class);
//                                    intent.putExtra(Variables.BEACON, (Serializable) beacon);
//                                    intent.putExtra("SUCURSAL", sucursal);
//                                    intent.putExtra("FECHA", fecha);
//                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                            } else {
                                MUST_BE_RUNNING = true;
                            }
                        }

                        @Override
                        public void onFailure(Call<Visita> call, Throwable t) {
                            MUST_BE_RUNNING = true;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Fecha> call, Throwable t) {
                Toast.makeText(BuscarBeacon.this, "No se pudo comprobar la fecha del dispositivo", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void borrarSuscripcion(int subscripcionID) {
        Call<ResponseBody> sesionCall = service.removerSubscripcion(subscripcionID);
        sesionCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                MUST_BE_RUNNING = true;
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Subscripción vigente fue borrada con éxito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                MUST_BE_RUNNING = true;
            }
        });
    }


    private void borrarSesion(int sesionID) {
        Call<ResponseBody> sesionCall = service.removerSesion(sesionID);
        sesionCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Sesión vigente fue borrada con éxito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consultarSesiones(int sucursalID, int clienteID , String fecha, Beacon beacon) {
        Call<Sesion> sesionCall = service.comprobarSesionesSucursal(String.valueOf(sucursalID),String.valueOf(clienteID),true);
        sesionCall.enqueue(new Callback<Sesion>() {
            @Override
            public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getResults().isEmpty()) {
                        Sesion sesion = response.body().getResults().get(0);
                        Toast.makeText(getApplicationContext(), "Sesiones: "+sesion.getSesionesRestantes(), Toast.LENGTH_SHORT).show();
                        //Tiene sesiones pero debemos evaluar cuantas tiene restantes
                        if (sesion.getSesionesRestantes()==0) {
                            // El número de sesiones restantes se termino por la tanto borraremos dicha paquete de sesiones y ahora verificaremos si tiene alguna suscripción vigente
                            borrarSesion(sesion.getId());
                            consultarSuscripciones(sucursalID, clienteID, fecha, beacon);
                        } else {
                            //Checar que la sesion este vigente
//                        descontarSession(sucursalID,clienteID,sesion);
                            //Caducidad de la sesión
                            Calendar cal = Calendar.getInstance();
                            cal.setTime( stringToDate(sesion.getCaducidad()));

                            //Fecha del día de hoy
                            Calendar cal2 = Calendar.getInstance();
                            cal2.setTime(stringToDate(fecha));

                            //Verificar que la sesión se encuentre vigente
                            if (cal2.before(cal) ) {
//                            descontarSession(sucursalID,clienteID,sesion);
                                //La sesión existe y se encuentra vigente por lo tanto mostramos pantalla de confirmar asistencia
                                editor.putInt(Variables.SUCURSAL_ID, sucursalID);
                                editor.commit();
                                /**
                                 *	Iniciar activity al detectar beacon
                                 */
//                                Intent intent = new Intent(BuscarBeacon.this, BeaconEncontrado.class);
//                                intent.putExtra(Variables.BEACON, (Serializable) beacon);
//                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            } else {
//                            La sesión no se encuentra vigente por lo tanto no mostramos nada
                                Toast.makeText(getApplicationContext(), "Sin sesiones vigentes", Toast.LENGTH_SHORT).show();
                                MUST_BE_RUNNING = true;
                            }
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Sin sesiones", Toast.LENGTH_SHORT).show();
                        // No tiene ninguna sesión por lo tanto ahora consultaremos si tiene alguna suscripción vigente
                        consultarSuscripciones(sucursalID, clienteID, fecha, beacon);
                    }
                }
            }

            @Override
            public void onFailure(Call<Sesion> call, Throwable t) {

            }
        });
    }

    private void consultarSuscripciones(int sucursalID, int clienteID, String fecha, Beacon beacon) {
        Call<SubscripcionMes> mesCall = service.obtenerSubscripcionesClienteSucursal(clienteID,sucursalID,true);
        mesCall.enqueue(new Callback<SubscripcionMes>() {
            @Override
            public void onResponse(Call<SubscripcionMes> call, Response<SubscripcionMes> response) {
                if (response.isSuccessful()) {

                    if (response.body().getResults().isEmpty()) {
                        //No tiene suscripciones por lo tanto no mostrará pantalla para confirmar su asistencia
                        MUST_BE_RUNNING = true;
                    } else {
                        //Tiene subscripciones. Verificaremos que sea vigente;
                        SubscripcionMes suscripcionesMes = response.body().getResults().get(0);
                        //Caducidad de la sesión
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( stringToDate(suscripcionesMes.getFechaRenovacion()));

                        //Fecha del día de hoy
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTime(stringToDate(fecha));

                        //Verificar que la subscripción se encuentre vigente
                        if (cal2.before(cal)) {
//                            descontarSession(sucursalID,clienteID,sesion);
                            //La subscripcion existe y se encuentra vigente por lo tanto mostramos pantalla de confirmar asistencia
                            editor.putInt(Variables.SUCURSAL_ID, sucursalID);
                            editor.commit();
                            /**
                             *	Iniciar activity al detectar beacon
                             */
                            Intent intent = new Intent(BuscarBeacon.this, BeaconEncontrado.class);
                            intent.putExtra(Variables.BEACON, (Serializable) beacon);
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
                            // Hay una subscripción pero ya no se encuentra vigente por tanto tenemos que borrarla de la BD
                            Toast.makeText(getApplicationContext(), "Sin sesiones vigentes", Toast.LENGTH_SHORT).show();
                            borrarSuscripcion(suscripcionesMes.getId());
                        }
                    }
                } else {
                    MUST_BE_RUNNING = true;
                }
            }

            @Override
            public void onFailure(Call<SubscripcionMes> call, Throwable t) {
                MUST_BE_RUNNING = true;
            }
        });
    }

    public Date stringToDate(String string) {
        String dtStart = string;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(dtStart);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Excepción", Toast.LENGTH_SHORT).show();
        }
        return new Date("");
    }

    private void descontarSession(int sucursalID, int clienteID, Sesion sesion) {
        if (sesion.getSesionesRestantes() == 0) {
            Toast.makeText(this, "Agotó su número de sesiones.", Toast.LENGTH_SHORT).show();
        } else {
            sesion.setSesionesRestantes(sesion.getSesionesRestantes()-1);
            Call<Sesion> sesionCall = service.descontarSesion(sesion.getId(),sesion);
            sesionCall.enqueue(new Callback<Sesion>() {
                @Override
                public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Sesion sesionActualizada = response.body();
                            Toast.makeText(getApplicationContext(), "Sesiones restantes: "+sesionActualizada.getSesionesRestantes(), Toast.LENGTH_SHORT).show();
                            registrarAsistencia(clienteID, sucursalID, sesion);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Sesion> call, Throwable t) {

                }
            });
        }
    }

    public void registrarAsistencia(int clienteID, int sucursalID, Sesion sesion) {
//        Call<Visita> visitaCall = service.registrarVisita(clienteID,sucursalID);
        Call<Visita> visitaCall = service.registrarVisita(clienteID,sucursalID);
        visitaCall.enqueue(new Callback<Visita>() {
            @Override
            public void onResponse(Call<Visita> call, Response<Visita> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    final PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    final NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Toast.makeText(getApplicationContext(), "Bienvenido!!!", Toast.LENGTH_SHORT).show();
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                            .setSmallIcon(R.drawable.ic_bluetooth)
                            .setContentTitle("Asistencia registrada")
                            .setContentText("Bienvenido a Fitory!")
                            .setGroup("BEACONS_GROUP_RANGE")
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);
                    mBuilder.setContentIntent(pi);
                    mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                    mNotificationManager.notify(0,mBuilder.build());
                } else {
                    Toast.makeText(getApplicationContext(), "No se pudo registrar su asistencia correctamente. Contacte a un administrador", Toast.LENGTH_SHORT).show();
                    recuperarSession(clienteID, sucursalID, sesion);
                }
            }

            @Override
            public void onFailure(Call<Visita> call, Throwable t) {
            }
        });
    }

    private void recuperarSession(int sucursalID, int clienteID, Sesion sesion) {
        sesion.setSesionesRestantes(sesion.getSesionesRestantes()+1);
        Call<Sesion> sesionCall = service.descontarSesion(sesion.getId(),sesion);
        sesionCall.enqueue(new Callback<Sesion>() {
            @Override
            public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Sesion sesionActualizada = response.body();
                        Toast.makeText(getApplicationContext(), "Sesiones restantes: "+sesionActualizada.getSesionesRestantes(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Sesion> call, Throwable t) {

            }
        });
    }
}
