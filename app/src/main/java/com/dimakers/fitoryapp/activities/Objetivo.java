package com.dimakers.fitoryapp.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Club;
import com.dimakers.fitoryapp.api.models.Objetivos;
import com.dimakers.fitoryapp.services.BuscarBeacon;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Objetivo extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    FitoryService service = API.getApi().create(FitoryService.class);
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private GoogleApiClient googleApiClient;
    private Location ultimaUbicacion;
    GoogleSignInClient mGoogleSignInClient;
    private boolean salud, convivir,vermeBien,  diversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetivo);
//        Intent serviceIntent = new Intent(this, BuscarBeacon.class);
//        stopService(serviceIntent);
//        ContextCompat.startForegroundService(this, serviceIntent);
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        googleApiClient = new GoogleApiClient.Builder(Objetivo.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        SharedPreferences sharedpreferences = getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        //Google Sign Up
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        editor = sharedpreferences.edit();
        ImageView iv_salud = (ImageView) findViewById(R.id.objetivo_salud);
        ImageView iv_convivir = (ImageView) findViewById(R.id.objetivo_convivir);
        ImageView iv_verme_bien = (ImageView) findViewById(R.id.objetivo_verme_bien);
        ImageView iv_diversion = (ImageView) findViewById(R.id.objetivo_diversion);
        ImageView tv_tu_objetivo = (ImageView) findViewById(R.id.tu_objetivo);
        Glide.with(Objetivo.this).load(R.drawable.objetivo_salud).into(iv_salud);
        Glide.with(Objetivo.this).load(R.drawable.objetivo_convivir).into(iv_convivir);
        Glide.with(Objetivo.this).load(R.drawable.objetivo_verme_bien).into(iv_verme_bien);
        Glide.with(Objetivo.this).load(R.drawable.obj_diversion).into(iv_diversion);
        Glide.with(Objetivo.this).load(R.drawable.tu_objetivo).into(tv_tu_objetivo);
    }

    public void actualizarObjetivos(boolean salud, boolean convivir, boolean vermeBien, boolean diversion) {
        int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
        if (idCliente!=0) {
            Call<ResponseBody> call = service.actualizarObjetivo(String.valueOf(idCliente),new Objetivos(salud,convivir,vermeBien,diversion));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
//                        if (isGPSEnabled(Objetivo.this)) {
                            Intent intent = new Intent(Objetivo.this,ClubMain.class);
                            startActivity(intent);
                            finish();
                        if (convivir) {
                            editor.putInt(Variables.CLIENTEOBJETIVO,1);
                        } else if (diversion) {
                            editor.putInt(Variables.CLIENTEOBJETIVO,3);
                        } else if (salud) {
                            editor.putInt(Variables.CLIENTEOBJETIVO,0);
                        } else if (vermeBien) {
                            editor.putInt(Variables.CLIENTEOBJETIVO, 2);
                        }
                        editor.commit();
//                        } else {
//                            Intent intent = new Intent(Objetivo.this,SeleccionCiudad.class);
//                            startActivity(intent);
//                            finish();
//                        }

                    } else {
                        Toast.makeText(Objetivo.this, "Error. Contacte al administrador.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(Objetivo.this, "No internet", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            cerrarSesion();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "No se pudo obtener su ID de cliente. Inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cerrarSesion() {
        editor.remove(Variables.USERNAME);
        editor.remove(Variables.EMAIL);
        editor.remove(Variables.TOKEN);
        editor.remove(Variables.IDUSER);
        editor.remove(Variables.CLIENTEID);
        editor.commit();
        LoginManager.getInstance().logOut();
        mGoogleSignInClient.signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void requestPermission () {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},123);
    }

    public boolean activateBluetooth()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
//        if (false ){
            // Device does not support Bluetooth
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            mostrarAlertaUbicacion();
            return false;
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert);
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.THEME);
                AlertDialog dialog;
                alertDialog.setTitle("Solicitud de Permiso");
                alertDialog.setMessage("Para mejorar tu experiencia \nFitory requiere que actives tu Bluetooth.");
//                alertDialog.setView(R.layout.permiso_bluetooth);
                alertDialog.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bluetoothAdapter.enable();
                        dialog.dismiss();
//                        startActivity(new Intent(Objetivo.this, SeleccionCiudad.class));
                        mostrarAlertaUbicacion();
                    }
                });
                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = alertDialog.create();
                dialog.show();
//                ImageView ivPermisosCerrar =  dialog.findViewById(R.id.permission_cerrar);
//                ivPermisosCerrar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                Button btnCancelar = dialog.findViewById(R.id.permission_cancelar);
//                btnCancelar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                Button btnActivar = dialog.findViewById(R.id.permission_activar);
//                btnActivar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        bluetoothAdapter.enable();
////                        startActivity(new Intent(Objetivo.this, SeleccionCiudad.class));
//                        dialog.dismiss();
//                        mostrarAlertaUbicacion();
//                    }
//                });
//                dialog.getWindow().setLayout(800,570);
            } else {

                mostrarAlertaUbicacion();
//                startActivity(new Intent(Objetivo.this,SeleccionCiudad.class));
            }
          return true;
        }
    }

    public void mostrarAlertaUbicacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(Objetivo.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        requestPermission();
//                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
////                        alertDialog.setView(R.layout.permiso_ubicacion);
//                        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//                        alertDialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                requestPermission();
//                            }
//                        });
//                        AlertDialog dialog = alertDialog.create();
//                        ivCancelar.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//                        btnNoPermitir.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
////                                startActivity(new Intent(Objetivo.this, SeleccionCiudad.class));
//                            }
//                        });
//                        btnPermitir.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
////                        dialog.getWindow().setLayout(800, 545);
//                                                dialog.show();

                    } else if (!sharedPreferences.getBoolean(Variables.PERMISSION_LOCATION, false)) {
                        requestPermission();
                        editor.putBoolean(Variables.PERMISSION_LOCATION, true);
                        editor.commit();
                    } else {
                        Toast.makeText(getApplicationContext(), "Por favor otorge los permisos necesarios a la aplicación", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }

                }  else {
                    //Continue
//                    finish();
//                    startActivity(new Intent(Objetivo.this, ClubMain.class));
//                    startActivity(new Intent(Objetivo.this,SeleccionCiudad.class));
                    actualizarObjetivos(salud,convivir,vermeBien,diversion);

                }
        } else {
            if (!isGPSEnabled(getApplicationContext())) {
                Toast.makeText(this, "Por favor active la ubicación de su dispositivo", Toast.LENGTH_SHORT).show();
            } else {
//                finish();
//                Intent intent = new Intent(this, ClubMain.class);
//                startActivity(intent);
                actualizarObjetivos(salud,convivir,vermeBien,diversion);
            }
        }
    }

    private boolean isGPSEnabled(Context applicationContext) {
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Presione de nuevo para salir", Toast.LENGTH_SHORT).show();
        }
        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        switch (requestCode) {
//            case 456:
//                if (resultCode == -1) {
////                    startActivity(new Intent(Objetivo.this, SeleccionCiudad.class));
//                }
//                break;
//        }
//    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled(getApplicationContext())) {
                    finish();
                    actualizarObjetivos(salud,convivir,vermeBien,diversion);
                } else {
                    Toast.makeText(this, "Por favor active el servicio de ubicación de su dispositivo", Toast.LENGTH_SHORT).show();
                }
            } else {
                startActivity(new Intent(Objetivo.this, SeleccionCiudad.class));
            }

        } else {
        }
    }

    public void salud(View view) {
        salud = true;
        convivir = false;
        vermeBien = false;
        diversion = false;
        activateBluetooth();

    }


    public void convivir(View view) {
        salud = false;
        convivir = true;
        vermeBien = false;
        diversion = false;
        activateBluetooth();
    }

    public void vermeBien(View view) {
        salud = false;
        convivir = false;
        vermeBien = true;
        diversion = false;
        activateBluetooth();
    }

    public void diversion(View view) {
        salud = false;
        convivir = false;
        vermeBien = false;
        diversion = true;
        activateBluetooth();
    }

    public void stopTouch(View view) {
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Objetivo.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        ultimaUbicacion = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (ultimaUbicacion != null) {
//            Toast.makeText(Objetivo.this, ""+ultimaUbicacion.getLatitude()+" "+ultimaUbicacion.getLongitude(), Toast.LENGTH_SHORT).show();
//            editor.putString(Variables.LONGITUD,String.valueOf(ultimaUbicacion.getLongitude()));
//            editor.putString(Variables.LATITUD,String.valueOf(ultimaUbicacion.getLatitude()));
//            editor.commit();
//            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//            try {
//                List<Address> addresses = geocoder.getFromLocation(ultimaUbicacion.getLatitude(),ultimaUbicacion.getLongitude(),1);
//                String cityName = addresses.get(0).getLocality();
//                String stateName = addresses.get(0).getAdminArea();
//                Toast.makeText(this, "Ciudad: "+cityName+ " Estado: "+stateName, Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Status changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
