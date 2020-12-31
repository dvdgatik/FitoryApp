package com.dimakers.fitoryapp.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Actividad;
import com.dimakers.fitoryapp.api.models.ActividadClub;
import com.dimakers.fitoryapp.api.models.ActividadHorario;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class ObtenerActividades extends IntentService {
    FitoryService service = API.getApi().create(FitoryService.class);
    public ObtenerActividades() {
        super("ObtenerActividadesService");
    }

    ArrayList<ActividadClub> actividades;
    ArrayList<ActividadHorario> horarios;
    ArrayList<Actividad> actividads = new ArrayList<>();
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Sucursal sucursal = (Sucursal) intent.getSerializableExtra(Variables.CLUBDETALLE);
        if (sucursal != null) {
            //Traer todas las actividades de un respectivo club
            Call<ActividadClub> actividadClubCall = service.obtenerActividades(sucursal.getId());
            try {
                actividades = actividadClubCall.execute().body().getResults();
                //Obtener cada actividad de un club respectivo
                for(ActividadClub actividad : actividades) {
                    Call<Actividad> actividadCall = service.obtenerActividad(actividad.getActividad());
                    Actividad actividadResult =  actividadCall.execute().body();
                    actividads.add(actividadResult);
                            Call<ActividadHorario> actividadHorarioCall = service.obtenerHorarioActividad(actividad.getId());
                             horarios = actividadHorarioCall.execute().body().getResults();
                    Gson gson = new Gson();
                    String jsonHorarios = gson.toJson(horarios);
                    String jsonActividades = gson.toJson(actividads);
                    Intent intentBroadcast = new Intent(Variables.HORARIO);
                    intentBroadcast.putExtra(Variables.HORARIO,jsonHorarios);
                    intentBroadcast.putExtra(Variables.ACTIVIDADES,jsonActividades);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
                    actividads.clear();
                }
                try {
                    horarios.clear();
                } catch (NullPointerException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Sucursal sin horarios", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                try {
                    actividads.clear();
                    actividades.clear();
                } catch (NullPointerException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Sucursal sin actividades", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                stopSelf();
            } catch (IOException e) {
                Toast.makeText(this, "ex:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        } else {
            Toast.makeText(this, "No se pudo cargar la sucursal", Toast.LENGTH_SHORT).show();
        }
    }
}
