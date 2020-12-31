package com.dimakers.fitoryapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.adapters.Ciudades;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Ciudad;
import com.dimakers.fitoryapp.api.models.Estado;
import com.dimakers.fitoryapp.services.BuscarBeacon;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SeleccionCiudad extends AppCompatActivity {
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_ciudad);
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Intent intent = getIntent();
        int ciudadID;
        try {
            ciudadID = intent.getIntExtra(Variables.CIUDADID,0);
            if (ciudadID != 0) {
                editor.putInt(Variables.CIUDADID,ciudadID);
            }
        } catch (Exception e) {

        }
//        if (isGPSEnabled(getApplicationContext())) {
//            startActivity(new Intent(this,ClubMain.class));
//            finish();
//            return;
//        }
//        Intent serviceIntent = new Intent(this, BuscarBeacon.class);
//        stopService(serviceIntent);
//        ContextCompat.startForegroundService(this, serviceIntent);
        Spinner spinnerEstados = findViewById(R.id.spinner_estados);
        ArrayList<Estado> spinnerArray = new ArrayList<>();
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,spinnerArray) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        //Creación de RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_ciudad);
        Ciudades adapter = new Ciudades(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        spinnerEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int estadoId = ((Estado)parent.getItemAtPosition(position)).getId();
                Call<Ciudad> ciudadCall = service.obtenerCiudades(estadoId);
                ciudadCall.enqueue(new Callback<Ciudad>() {
                    @Override
                    public void onResponse(Call<Ciudad> call, Response<Ciudad> response) {
                        if (response.isSuccessful()) {
                            if (!response.body().getResults().isEmpty()) {
                                adapter.empty();
                                adapter.update(response.body().getResults());
                            }
                        } else {
                            Toast.makeText(SeleccionCiudad.this, "Error. Comuníquese con el administrador", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Ciudad> call, Throwable t) {
                        Toast.makeText(SeleccionCiudad.this, "No internet", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Call<Estado> estadoCall = service.obtenerEstados();
        estadoCall.enqueue(new Callback<Estado>() {
            @Override
            public void onResponse(Call<Estado> call, Response<Estado> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getResults().isEmpty()) {
                        spinnerArray.add(new Estado(0,"Seleccione un estado"));
                        spinnerArray.addAll(response.body().getResults());
                        spinnerEstados.setAdapter(spinnerAdapter);
                    }
                } else {
                    Toast.makeText(SeleccionCiudad.this, "Error. Comuníquese con el administrador.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Estado> call, Throwable t) {
                Toast.makeText(SeleccionCiudad.this, "No internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void clubDetalle(View view) {
        Intent intent = new Intent(SeleccionCiudad.this, ClubMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void atras(View view) {finish();
    }

    private boolean isGPSEnabled(Context applicationContext) {
        LocationManager lm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
