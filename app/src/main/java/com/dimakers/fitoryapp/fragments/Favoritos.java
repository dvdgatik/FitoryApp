package com.dimakers.fitoryapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.adapters.SucursalesFav;
import com.dimakers.fitoryapp.adapters.SucursalesFull;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Favorito;
import com.dimakers.fitoryapp.api.models.Sucursal;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Favoritos extends Fragment {

    FitoryService service = API.getApi().create(FitoryService.class);
    Fragment clubDetalle = new ClubDetalle();
    SharedPreferences sharedPreferences;
    public static Favoritos newInstance() {
        Favoritos fragment = new Favoritos();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvSinSucursales = view.findViewById(R.id.tv_sin_sucursales);
        sharedPreferences = getActivity().getSharedPreferences(Variables.PREFERENCES,Context.MODE_PRIVATE);
        //Preparar Recycler view para obtener sucursales favoritas
        RecyclerView rvClubes = view.findViewById(R.id.rv_sucursales);
        SucursalesFav adapter = new SucursalesFav(getContext());
        rvClubes.setHasFixedSize(true);
        rvClubes.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        rvClubes.setLayoutManager(layoutManager);
//        adapter.update(Variables.sucursalFulls);
        int clienteId = sharedPreferences.getInt(Variables.CLIENTEID,0);
        if (clienteId == 0) {
        } else {
            if (Variables.sucursalesFavs.isEmpty()) {
                Call<Favorito> favoritoCall = service.obtenerFavoritos(clienteId);
                favoritoCall.enqueue(new Callback<Favorito>() {
                    @Override
                    public void onResponse(Call<Favorito> call, Response<Favorito> response) {
                        if (response.isSuccessful()) {
                            if (!response.body().getResults().isEmpty()) {
                                ArrayList<Favorito> favoritos = response.body().getResults();
                                tvSinSucursales.setVisibility(View.INVISIBLE);
                                for (Favorito favorito : favoritos) {
                                    Call<Sucursal> sucursalCall = service.obtenerSucursal(favorito.getSucursal(), true);
                                    sucursalCall.enqueue(new Callback<Sucursal>() {
                                        @Override
                                        public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                                            if (response.isSuccessful()) {
                                                Sucursal sucursal = response.body();
                                                Variables.sucursalesFavs.add(sucursal);
                                                adapter.add(sucursal);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Sucursal> call, Throwable t) {
                                        }
                                    });
                                }
                            } else {
                                tvSinSucursales.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Favorito> call, Throwable t) {
                    }
                });

            } else {
                adapter.update(Variables.sucursalesFavs);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favoritos, container, false);
    }
}
