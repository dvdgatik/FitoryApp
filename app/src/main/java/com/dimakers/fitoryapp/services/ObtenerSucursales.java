package com.dimakers.fitoryapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Actividad;
import com.dimakers.fitoryapp.api.models.ActividadClub;
import com.dimakers.fitoryapp.api.models.ActividadFull;
import com.dimakers.fitoryapp.api.models.ActividadHorario;
import com.dimakers.fitoryapp.api.models.Club;
import com.dimakers.fitoryapp.api.models.Favorito;
import com.dimakers.fitoryapp.api.models.Horario;
import com.dimakers.fitoryapp.api.models.Servicio;
import com.dimakers.fitoryapp.api.models.ServicioClub;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.SucursalFull;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class ObtenerSucursales extends IntentService {
    private static final String TAG = "SERVICESUCURSAL";
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    public ObtenerSucursales() {
        super("ObtenerSucursalesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES,MODE_PRIVATE);
        int idCiudad = sharedPreferences.getInt(Variables.CIUDADID,0);
        Variables.sucursalFulls.clear();
        Call<Sucursal> sucursalCall = service.obtenerSucursales(true,true,idCiudad);
        try {
            Variables.sucursales = sucursalCall.execute().body().getResults();
            for (Sucursal sucursal : Variables.sucursales) {
                Call<Club> clubCall = service.obtenerClub(sucursal.getClub(),true);
                Club club = clubCall.execute().body();

                // Consultar si el club es favorito del cliente
                int clienteId = sharedPreferences.getInt(Variables.CLIENTEID,0);
                //Llamada a API para saber si una sucursal es favorita
                Call<Favorito> favoritoCall = service.obtenerFavorito(clienteId,sucursal.getId());
                ArrayList<Favorito> favoritos = favoritoCall.execute().body().getResults();
                SucursalFull sucursalFull = new SucursalFull();
                if (favoritos.size()>0) {
                    Favorito favorito = favoritos.get(0);
                    sucursalFull.setIsFavorito(false);
                    if (favorito != null) {
                        sucursalFull.setIsFavorito(true);
                        sucursalFull.setFavoritoID(favorito.getId());
                    }
                }

                //Llamada al API para obtener Servicios
                Call<ServicioClub> servicioCall = service.obtenerServicios(sucursal.getId());
                ArrayList<ServicioClub> servicios = new ArrayList<>();
                servicios = servicioCall.execute().body().getResults();
                for (ServicioClub servicio : servicios ) {
                    Call<Servicio> servicioCall2 = service.obtenerServicio(servicio.getServicio());
                    sucursalFull.getServicios().add(servicioCall2.execute().body());
                }

                //Llamada al API para obtener Actividades
                Call<ActividadClub> actividadClubCall = service.obtenerActividades(sucursal.getId());
                ArrayList<ActividadClub> actividades = new ArrayList<>();
                actividades = actividadClubCall.execute().body().getResults();
                for (ActividadClub actividadClub : actividades) {
                    ActividadFull actividadFull = new ActividadFull();
                    Call<Actividad> actividadCall = service.obtenerActividad(actividadClub.getActividad());
                    actividadFull.setActividad(actividadCall.execute().body());
                    sucursalFull.getActividades().add(actividadFull);
//                    Call<Horario> horarioCall = service.consultarHorarioSucursal(sucursal.getId());
//                    sucursalFull.setHorario(horarioCall.execute().body().getResults().get(0));
                }
                sucursalFull.setSucursal(sucursal);
                sucursalFull.setClub(club);
                Variables.sucursalFulls.add(sucursalFull);
            }
            Intent intentBroadcast = new Intent(Variables.SUCURSALES);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
