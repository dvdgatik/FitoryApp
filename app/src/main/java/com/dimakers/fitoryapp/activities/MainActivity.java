package com.dimakers.fitoryapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Cliente;

import java.net.SocketTimeoutException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    FitoryService service = API.getApi().create(FitoryService.class);
    boolean seleccionUbicacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        int clienteID = sharedpreferences.getInt(Variables.CLIENTEID, 0);
        seleccionUbicacion = sharedpreferences.getBoolean(Variables.SELECCIONUBICACION, false);
        final Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(1*3000);
                    if (clienteID == 0) {
                        Intent intent = new Intent(MainActivity.this, Intro.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                verificarObjetivo(clienteID);
                            }
                        });
                    }
                }
                catch (Exception ex) {
                }
            }
        };
        thread.start();
    }

    private void verificarObjetivo(int clienteID) {
        Call<Cliente> clienteCall = service.verificarSeleccionObjetivo(clienteID, false,false,false,false);
        clienteCall.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, Objetivo.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    //Mostrar ventana de permisos de ubicación
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                                requestPermission();
                            } else if (!sharedpreferences.getBoolean(Variables.PERMISSION_LOCATION, false)) {
                                requestPermission();
                                editor.putBoolean(Variables.PERMISSION_LOCATION, true);
                                editor.commit();
                            } else {
                                if (isGPSEnabled(getApplicationContext())) {
                                    clubMain();
                                } else {
                                    seleccionCiudad();
                                }
                            }

                        }  else {
                            if (isGPSEnabled(getApplicationContext())) {
                                clubMain();
                            } else {
                                seleccionCiudad();
                            }
                        }
                    } else {
                        if (isGPSEnabled(getApplicationContext())) {
                            clubMain();
                        } else {
                            seleccionCiudad();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                    Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isGPSEnabled(Context applicationContext) {
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public void seleccionCiudad() {
        int ciudadID = sharedpreferences.getInt(Variables.CIUDADID, 0);
        if (ciudadID != 0) {
            Intent intent = new Intent(this, ClubMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        } else {
            Intent intent = new Intent(getApplicationContext(), SeleccionCiudad.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void clubMain() {
        Intent intent = new Intent(getApplicationContext(), ClubMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void requestPermission () {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled(getApplicationContext())) {
                    clubMain();
                } else {
                    seleccionCiudad();
                }
            } else {
                seleccionCiudad();
            }

        } else {
            seleccionCiudad();
        }
    }
}
