package com.dimakers.fitoryapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.EvaluacionSucursal;
import com.dimakers.fitoryapp.api.models.PromedioEvaluacion;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.SucursalFull;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Evaluar extends Fragment {
    ImageView star1,star2,star3,star4,star5;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FitoryService service = API.getApi().create(FitoryService.class);
    Button btnEvaluear;
    int calificacion = 0;
    public static Evaluar newInstance() {
        Evaluar fragment = new Evaluar();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);
        star4 = view.findViewById(R.id.star4);
        star5 = view.findViewById(R.id.star5);
        btnEvaluear = view.findViewById(R.id.btn_enviar_evaluacion);
        btnEvaluear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calificar(calificacion);
            }
        });
        star1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recolorear(1);
//                        calificar(1);
                        calificacion = 1;
                    }
                });
                star2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recolorear(2);
//                        calificar(2);
                        calificacion = 2;
                    }
                });
                star3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recolorear(3);
//                        calificar(3);
                        calificacion = 3;
                    }
                });
                star4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recolorear(4);
//                        calificar(4);
                        calificacion = 4;
                    }
                });
                star5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recolorear(5);
//                        calificar(5);
                        calificacion = 5;
                    }
                });
//            }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_evaluar, container, false);
    }

    private void recolorear(int star) {
        star1.setImageResource(R.drawable.star_bg);
        star2.setImageResource(R.drawable.star_bg);
        star3.setImageResource(R.drawable.star_bg);
        star4.setImageResource(R.drawable.star_bg);
        star5.setImageResource(R.drawable.star_bg);
        switch (star) {
            case 1:
                star1.setImageResource(R.drawable.star_on);
                break;
            case 2:
                star1.setImageResource(R.drawable.star_on);
                star2.setImageResource(R.drawable.star_on);
                break;
            case 3:
                star1.setImageResource(R.drawable.star_on);
                star2.setImageResource(R.drawable.star_on);
                star3.setImageResource(R.drawable.star_on);
                break;
            case 4:
                star1.setImageResource(R.drawable.star_on);
                star2.setImageResource(R.drawable.star_on);
                star3.setImageResource(R.drawable.star_on);
                star4.setImageResource(R.drawable.star_on);
                break;
            case 5:
                star1.setImageResource(R.drawable.star_on);
                star2.setImageResource(R.drawable.star_on);
                star3.setImageResource(R.drawable.star_on);
                star4.setImageResource(R.drawable.star_on);
                star5.setImageResource(R.drawable.star_on);
                break;
        }
    }

    private void calificar(int rating) {
        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        EvaluacionSucursal evaluacionSucursal = (EvaluacionSucursal) mBundle.getSerializable(Variables.EVALUACION);

        if (evaluacionSucursal==null) {
            Toast.makeText(getActivity(), "NA", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
            return;
        }
        int idCliente = evaluacionSucursal.getCliente();
        int idSucursal = evaluacionSucursal.getSucursal();
        Call<EvaluacionSucursal> evaluacionSucursalCall = service.verificarEvaluacionUsuario(idCliente,idSucursal);
        evaluacionSucursalCall.enqueue(new Callback<EvaluacionSucursal>() {
            @Override
            public void onResponse(Call<EvaluacionSucursal> call, Response<EvaluacionSucursal> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getResults().isEmpty()) {
                        Toast.makeText(getActivity(), "Usted ya ha calificado esta sucursal. Muchas gracias!", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        EvaluacionSucursal evaluacionSucursal = new EvaluacionSucursal(idCliente,idSucursal, rating);
                        Call<EvaluacionSucursal> crearEvaluacionCall = service.crearEvaluacion(evaluacionSucursal);
                        crearEvaluacionCall.enqueue(new Callback<EvaluacionSucursal>() {
                            @Override
                            public void onResponse(Call<EvaluacionSucursal> call, Response<EvaluacionSucursal> response) {
                                if (response.isSuccessful()) {
                                    Variables.sucursales.clear();
                                    Toast.makeText(getActivity(), "Calificación guardada. Muchas gracias!", Toast.LENGTH_SHORT).show();
                                    Call<PromedioEvaluacion> promedioEvaluacionCall = service.calcularPromedioSucursal(idSucursal);
                                    promedioEvaluacionCall.enqueue(new Callback<PromedioEvaluacion>() {
                                        @Override
                                        public void onResponse(Call<PromedioEvaluacion> call, Response<PromedioEvaluacion> response) {
                                            getActivity().getSupportFragmentManager().popBackStack();
                                            if (response.isSuccessful()) {
                                            } else {
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<PromedioEvaluacion> call, Throwable t) {
                                            getActivity().getSupportFragmentManager().popBackStack();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "No pudimos guardar su calificación. Intente de nuevo", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<EvaluacionSucursal> call, Throwable t) {
                                getActivity().getSupportFragmentManager().popBackStack();
                                if (!InternetConnectionStatus.isConnected(getContext())) {
                                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                } else if(t instanceof SocketTimeoutException) {
                                    Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<EvaluacionSucursal> call, Throwable t) {
                getActivity().getSupportFragmentManager().popBackStack();
                if (!InternetConnectionStatus.isConnected(getContext())) {
                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                } else if(t instanceof SocketTimeoutException) {
                    Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

}
