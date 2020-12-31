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
import com.dimakers.fitoryapp.api.models.Club;
import com.dimakers.fitoryapp.api.models.Horario;
import com.dimakers.fitoryapp.api.models.Sesion;
import com.dimakers.fitoryapp.api.models.SesionFull;
import com.dimakers.fitoryapp.api.models.SesionFullMes;
import com.dimakers.fitoryapp.api.models.SubscripcionFreeFull;
import com.dimakers.fitoryapp.api.models.SubscripcionMes;
import com.dimakers.fitoryapp.api.models.SubscripcionesGratis;
import com.dimakers.fitoryapp.api.models.Sucursal;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class ObtenerSuscripciones extends IntentService {
    private static final String TAG = "SESIONSERVICE";
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    public ObtenerSuscripciones() {
        super("ObtenerSuscripcionesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES,MODE_PRIVATE);
        int clienteID = sharedPreferences.getInt(Variables.CLIENTEID,0);
        if (clienteID == 0) {
        }
        Call<Sesion> sesionCall = service.obtenerSesionesCliente(clienteID,true);
        try {
            ArrayList<Sesion> sesiones = sesionCall.execute().body().getResults();
            Variables.sesionFulls.clear();
            for (Sesion sesion : sesiones) {
                SesionFull sesionFull = new SesionFull();
                sesionFull.setId(sesion.getId());
                sesionFull.setSesion(sesion);
                Call<Sucursal> sucursalCall = service.obtenerSucursal(sesion.getSucursal(),true);
                Sucursal sucursal = sucursalCall.execute().body();
                sesionFull.setSucursal(sucursal);
                if (sucursal!= null ) {
                    Call<Club> clubCall = service.obtenerClub(sucursal.getClub(),true);
                    Club club = clubCall.execute().body();
                    sesionFull.setClub(club);
//                    Call<Horario> horarioCall = service.consultarHorarioSucursal(sucursal.getId());
//                    Horario horario = horarioCall.execute().body().getResults().get(0);
//                    sesionFull.setHorario(horario);
                    Variables.sesionFulls.add(sesionFull);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Intent intentBroadcast = new Intent(Variables.SESIONES);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        }
        //Traer subscripciones
        Call<SubscripcionMes> subscripcionMesCall = service.obtenerSubscripcionesCliente(clienteID,true);
        try {
            ArrayList<SubscripcionMes> subscripciones = subscripcionMesCall.execute().body().getResults();
            Variables.sesionFullsMes.clear();
            for (SubscripcionMes subscripcion : subscripciones) {
                SesionFullMes sesionFullMes = new SesionFullMes();
                sesionFullMes.setSubscripcionMes(subscripcion);
                Call<Sucursal> sucursalCall = service.obtenerSucursal(subscripcion.getSucursal(),true);
                Sucursal sucursal = sucursalCall.execute().body();
                sesionFullMes.setSucursal(sucursal);
                if (sucursal!= null) {
                    Call<Club> clubCall = service.obtenerClub(sucursal.getClub(),true);
                    Club club = clubCall.execute().body();
                    sesionFullMes.setClub(club);
//                    Call<Horario> horarioCall = service.consultarHorarioSucursal(sucursal.getId());
//                    Horario horario = horarioCall.execute().body().getResults().get(0);
//                    sesionFullMes.setHorario(horario);
                    Variables.sesionFullsMes.add(sesionFullMes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Intent intentBroadcast = new Intent(Variables.SESIONES);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        }

        //Traer subscripciones gratuitas
        Call<SubscripcionesGratis> subscripcionesGratisCall = service.obtenerSubscripcionesGratis(clienteID,true);
        try {
            ArrayList<SubscripcionesGratis> subscripcionesGratis = subscripcionesGratisCall.execute().body().getResults();
            Variables.subscripcionFreeFulls.clear();
            for(SubscripcionesGratis subscripcionGratis: subscripcionesGratis) {
                SubscripcionFreeFull subscripcionFreeFull = new SubscripcionFreeFull();
                subscripcionFreeFull.setSubscripcionMes(subscripcionGratis);
                Call<Sucursal> sucursalCall = service.obtenerSucursal(subscripcionGratis.getSucursal(),true);
                Sucursal sucursal = sucursalCall.execute().body();
                subscripcionFreeFull.setSucursal(sucursal);
                if (sucursal!=null) {
                    Call<Club> clubCall = service.obtenerClub(sucursal.getClub(),true);
                    Club club = clubCall.execute().body();
                    subscripcionFreeFull.setClub(club);
                    Variables.subscripcionFreeFulls.add(subscripcionFreeFull);
                }
            }
            Intent intentBroadcast = new Intent(Variables.SESIONES);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        } catch (Exception e) {
            Intent intentBroadcast = new Intent(Variables.SESIONES);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
        }
    }
}
