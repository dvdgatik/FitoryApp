package com.dimakers.fitoryapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.adapters.Suscripciones;
import com.dimakers.fitoryapp.adapters.SuscripcionesFree;
import com.dimakers.fitoryapp.adapters.SuscripcionesMes;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.GetSubscripcionesResponse;
import com.dimakers.fitoryapp.api.models.Suscripcion;
import com.dimakers.fitoryapp.services.ObtenerSucursales;
import com.dimakers.fitoryapp.services.ObtenerSuscripciones;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisSuscripciones extends Fragment {
    RecyclerView rvMisSuscripciones,rvMisSuscripcionesMes, rvMisSuscripcionesGratis;
    Suscripciones adapter;
    SuscripcionesMes adapterMes;
    SuscripcionesFree adapterMesGratis;
    private FitoryService service = API.getApi().create(FitoryService.class);
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static MisSuscripciones newInstance() {
        MisSuscripciones misSuscripciones = new MisSuscripciones();
        return misSuscripciones;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Variables.PREFERENCES,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        rvMisSuscripciones = view.findViewById(R.id.rv_mis_suscripciones);
        rvMisSuscripcionesMes = view.findViewById(R.id.rv_mis_suscripciones_mes);
        rvMisSuscripcionesGratis = view.findViewById(R.id.rv_mis_suscripciones_gratis);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(getContext(), ObtenerSuscripciones.class);
                getContext().startService(intent);

            }
        });
        adapter = new Suscripciones(getContext());
        rvMisSuscripciones.setHasFixedSize(true);
        rvMisSuscripciones.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        GridLayoutManager layoutManager2 = new GridLayoutManager(getContext(),1);
        GridLayoutManager layoutManager3 = new GridLayoutManager(getContext(),1);
        rvMisSuscripcionesMes.setLayoutManager(layoutManager2);
        adapterMes = new SuscripcionesMes(getContext());
        rvMisSuscripcionesMes.setHasFixedSize(true);
        rvMisSuscripcionesMes.setAdapter(adapterMes);
        rvMisSuscripciones.setLayoutManager(layoutManager);
        adapterMesGratis = new SuscripcionesFree(getContext());
        rvMisSuscripcionesGratis.setHasFixedSize(true);
        rvMisSuscripcionesGratis.setAdapter(adapterMesGratis);
        rvMisSuscripcionesGratis.setLayoutManager(layoutManager3);
        int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
        if (idCliente!=0) {
            Call<GetSubscripcionesResponse> getSubscripcionesCall = service.obtenerSubscripciones(idCliente);
            getSubscripcionesCall.enqueue(new Callback<GetSubscripcionesResponse>() {
                @Override
                public void onResponse(Call<GetSubscripcionesResponse> call, Response<GetSubscripcionesResponse> response) {
                    if (response.isSuccessful()) {
                        GetSubscripcionesResponse subscripcionesResponse = response.body();
                        adapter.clear();
                        adapter.update(subscripcionesResponse.getSesiones());
                        adapterMes.clear();
                        adapterMes.update(subscripcionesResponse.getSubscripciones());
                        adapterMesGratis.clear();
                        adapterMesGratis.update(subscripcionesResponse.getSubscripcionesGratis());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<GetSubscripcionesResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "No se pudieron obtener las subscripciones: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No se pudieron obtener las subscripciones", Toast.LENGTH_SHORT).show();
        }
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
//                new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        //Code
//                        adapter.clear();
//                        adapter.update(Variables.sesionFulls);
//                        adapterMes.clear();
//                        adapterMes.update(Variables.sesionFullsMes);
//                        adapterMesGratis.clear();
//                        adapterMesGratis.update(Variables.subscripcionFreeFulls);
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }, new IntentFilter(Variables.SESIONES)
//        );


//            Intent intent = new Intent(getContext(), ObtenerSuscripciones.class);
//            getContext().startService(intent);

//        ArrayList<Suscripcion> suscripcions = new ArrayList<>();
//        suscripcions.add(new Suscripcion("TRX Centro","","","Lunes a Viernes de 8am-10pm Sábados de 10am-5pm","Teléfono: 01 (812) 132 2896","Básico","4 sesiones","$132.00 MxN","Sesiones Restantes: 2"));
//        suscripcions.add(new Suscripcion("BOX CAPITAL","","","Lunes a Viernes de 8am-10pm Sábados de 10am-5pm","Teléfono: 01 (812) 132 2896","Medio","8 sesiones","$264.00 MxN","Sesiones Restantes: 4"));
//        adapter.update(suscripcions);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_suscripciones,container,false);
    }

}
