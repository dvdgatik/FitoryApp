package com.dimakers.fitoryapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Fecha;
import com.dimakers.fitoryapp.api.models.Visita;
import com.roomorama.caldroid.CaldroidFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Calendario extends android.support.v4.app.Fragment {
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    String fecha;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        CaldroidFragment caldroidFragment = new CaldroidFragment();
        sharedPreferences = getContext().getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        int clienteID = sharedPreferences.getInt(Variables.CLIENTEID,0);
        int sucursalID = sharedPreferences.getInt(Variables.SUCURSAL_ID, 0);
        if (clienteID==0) {
//            Toast.makeText(getContext(), "No se pudieron obtener las visitas", Toast.LENGTH_SHORT).show();
            consultarFechaCalendario();

        } else {
            consultarFecha(clienteID, sucursalID);
        }
    }

    private void consultarFechaCalendario() {
        Call<Fecha> fechaCall = service.consultarFecha();
        fechaCall.enqueue(new Callback<Fecha>() {
            private static final String TAG = "Calendar";

            @Override
            public void onResponse(Call<Fecha> call, Response<Fecha> response) {
                try {
                    if (response.isSuccessful()) {
                        fecha = response.body().getFecha();
                        CaldroidFragment caldroidFragment = new CaldroidSampleCustomFragment();
                        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.custom_cell,null);
                        Bundle args = new Bundle();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( stringToDate(fecha) );
                        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
                        args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);
                        caldroidFragment.setBackgroundDrawableForDate(drawable,cal.getTime());
                        caldroidFragment.setArguments(args);
                        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                        t.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);
//                    for (Visita visita : response.body().getResults()) {
////                    cal.add(Calendar.DATE, 1);
//                        Date newDate = stringToDate(visita.getFecha());
//                        caldroidFragment.setBackgroundDrawableForDate(getActivity().getDrawable(R.drawable.asistencia), newDate);
//                        caldroidFragment.refreshView();
//                    }
                        t.replace(R.id.calendar, caldroidFragment);
                        t.commit();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Fecha> call, Throwable t) {

            }
        });
    }

    private void consultarFecha(int clienteID, int sucursalID) {
        Call<Fecha> fechaCall = service.consultarFecha();
        fechaCall.enqueue(new Callback<Fecha>() {
            @Override
            public void onResponse(Call<Fecha> call, Response<Fecha> response) {
                if (response.isSuccessful()) {
                    try {
                        fecha = response.body().getFecha();
                        CaldroidFragment caldroidFragment = new CaldroidSampleCustomFragment();
                        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.custom_cell,null);
                        Bundle args = new Bundle();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( stringToDate(fecha) );
                        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
                        args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);
                        caldroidFragment.setBackgroundDrawableForDate(drawable,cal.getTime());
                        caldroidFragment.setArguments(args);
                        consultarVisitas(clienteID, caldroidFragment);
                    } catch (Exception e) {
                    }
                } else {
                    Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Fecha> call, Throwable t) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void consultarVisitas(int clienteID, CaldroidFragment caldroidFragment) {
        Call<Visita> visitaCall = service.obtenerVisitasCalendario(clienteID);
        visitaCall.enqueue(new Callback<Visita>() {
            @Override
            public void onResponse(Call<Visita> call, Response<Visita> response) {
                if (response.isSuccessful()) {
                    try {
                        if (!response.body().getResults().isEmpty()) {
//                        Toast.makeText(getContext(), "Se encontraron visitas", Toast.LENGTH_SHORT).show();
                            FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                            t.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);
                            for (Visita visita : response.body().getResults()) {
//                          cal.add(Calendar.DAfaZZTE, 1);
                                Date newDate = stringToDate(visita.getFecha());
                                caldroidFragment.setBackgroundDrawableForDate(getActivity().getDrawable(R.drawable.asistencia3), newDate);
                                caldroidFragment.refreshView();
                            }
                            t.replace(R.id.calendar, caldroidFragment);
                            t.commit();
                        } else {
                            FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                            t.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);
                            t.replace(R.id.calendar, caldroidFragment);
                            t.commit();
                        }
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Visita> call, Throwable t) {

            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendario,container,false);
    }

    public Date stringToDate(String string) {
        String dtStart = string;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(dtStart);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Excepción", Toast.LENGTH_SHORT).show();
        }
        return new Date("");
    }
}
