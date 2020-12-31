package com.dimakers.fitoryapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.EvaluacionSucursal;
import com.dimakers.fitoryapp.api.models.RegistroAsistencia;
import com.dimakers.fitoryapp.api.models.SesionFullMes;
import com.dimakers.fitoryapp.api.models.SubscripcionFreeFull;
import com.dimakers.fitoryapp.api.models.SubscripcionMes;
import com.dimakers.fitoryapp.api.models.SubscripcionesGratis;
import com.dimakers.fitoryapp.api.models.Visita;
import com.dimakers.fitoryapp.fragments.ClubDetalle;
import com.dimakers.fitoryapp.fragments.Evaluar;
import com.dimakers.fitoryapp.fragments.SuscripcionDetalleMes;
import com.dimakers.fitoryapp.fragments.SuscripcionGratisDetalle;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuscripcionesFree extends RecyclerView.Adapter<SuscripcionesFree.ViewHolder> {
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<SubscripcionesGratis> dataset;
    Context context;
    public SuscripcionesFree(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
    }

    public void update(ArrayList<SubscripcionesGratis> suscripciones) {
        Collections.reverse(suscripciones);
        dataset.addAll(suscripciones);
        notifyDataSetChanged();
    }

    public void clear() {
        this.dataset.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        sharedPreferences = context.getSharedPreferences(Variables.PREFERENCES,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_suscripcion_mes_gratis,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SubscripcionesGratis subscripcionGratis = dataset.get(i);
        viewHolder.tvSuscripcionNombre.setText(subscripcionGratis.getSucursalNombre());
//        viewHolder.tvSuscripcionComoLlegar
//        viewHolder.tvSuscripcionHorario.setText(suscripcion.getHorario().getLunesA().substring(0,5)+"-"+suscripcion.getHorario().getLunesC().substring(0,5));
        viewHolder.tvSuscripcionTelefono.setText("Teléfono: "+subscripcionGratis.getSucursalTelefono());
//        switch (suscripcion.getSesion().getSesiones()) {
//            case 4:
//                viewHolder.tvSuscripcionTipo.setText("BÁSICO");
//                break;
//            case 8:
//                viewHolder.tvSuscripcionTipo.setText("MEDIO");
//                break;
//            case 12:
//                viewHolder.tvSuscripcionTipo.setText("AVANZADO");
//
//        }
//        viewHolder.tvSuscripcionSesiones.setText(suscripcion.getSesion().getSesiones()+" Sesiones");
        viewHolder.tvSuscripcionPrecio.setText("GRATIS");
        viewHolder.tvSuscripcionSesionesRestantes.setText("");
        viewHolder.tvSuscripcionHorario.setText("Vigencia: "+subscripcionGratis.getFechaFin());
        viewHolder.tvSuscripcionComoLlegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitud = subscripcionGratis.getSucursalLatitud();
                String longitud = subscripcionGratis.getSucursalLongitud();
                Uri gmmIntentUri = Uri.parse("geo:"+latitud+","+longitud+"?q="+latitud+","+longitud+"(Ubicación)");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
        viewHolder.btnSuscripcionVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity)context ).getSupportFragmentManager();
                /**
                 * Con esta condición, verificamos que el fragmento sólo se añada si no existe en el backStack
                 */
                if (fragmentManager.findFragmentByTag("CLUBDETALLE")==null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //Pasamos un objeto clubDetalle con toda la información hacia el siguiente fragmento.
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Variables.SUSCRIPCION,subscripcionGratis);
                    SuscripcionGratisDetalle suscripcionDetalle = new SuscripcionGratisDetalle();
                    suscripcionDetalle.setArguments(bundle);
//                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);
//                    fragmentTransaction.setCustomAnimations(R.animator.going_up, R.animator.going_down, R.animator.going_up, R.animator.going_down);
                    fragmentTransaction.setCustomAnimations(R.animator.going_up, R.animator.going_down, 0, 0);
                    fragmentTransaction.replace(R.id.fragmentContainer, suscripcionDetalle, "SUSCRIPCIONDETALLE").addToBackStack(null).commit();
                }
            }
        });
        if (context.getResources().getDisplayMetrics().heightPixels<=320) {
            viewHolder.tvSuscripcionTipo.setScaleY(0.88f);
        }
        viewHolder.btnAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setView(R.layout.dialog_asistencia);
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
                TextView buttonCancelar = (TextView) dialog.findViewById(R.id.button1);
                TextView buttonConfirmar = (TextView) dialog.findViewById(R.id.button2);
                buttonConfirmar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        viewHolder.btnAsistencia.setEnabled(false);
                        int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
                        if (idCliente!=0) {
                            Call<RegistroAsistencia> registroAsistenciaCall = service.registrarAsistencia(idCliente,subscripcionGratis.getSucursal());
                            registroAsistenciaCall.enqueue(new Callback<RegistroAsistencia>() {
                                @Override
                                public void onResponse(Call<RegistroAsistencia> call, Response<RegistroAsistencia> response) {
                                    if (response.isSuccessful()) {
                                        RegistroAsistencia registroAsistencia = response.body();
                                        try {
                                            switch (registroAsistencia.getMessage()) {
                                                case "Visita registrada":
                                                    Toast.makeText(context, "Visita registrada", Toast.LENGTH_SHORT).show();
                                                    comprobarAsistencia(idCliente,subscripcionGratis.getSucursal(), subscripcionGratis);
                                                    //Llamada al api de visitas para traer la última visita del cliente
                                                    Call<Visita> visitaCall = service.comprobarVisita(subscripcionGratis.getCliente(),subscripcionGratis.getSucursal());
                                                    visitaCall.enqueue(new Callback<Visita>() {
                                                        @Override
                                                        public void onResponse(Call<Visita> call, Response<Visita> response) {
                                                            if (response.isSuccessful()) {
                                                                if (!response.body().getResults().isEmpty()) {
                                                                    Visita visita = response.body().getResults().get(response.body().getResults().size()-1);
                                                                    viewHolder.tvChekin.setText( visita.getFecha()+ "    " + to12(visita.getHora()));
                                                                    viewHolder.tvChekinTitle.setVisibility(View.VISIBLE);
                                                                    viewHolder.tvChekin.setVisibility(View.VISIBLE);
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Visita> call, Throwable t) {
                                                            if (!InternetConnectionStatus.isConnected(context)) {
                                                                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                                            } else if(t instanceof SocketTimeoutException) {
                                                                Toast.makeText(context, context.getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    break;
                                                case "El cliente ya cuenta con una visita registrada":
                                                    Toast.makeText(context, "Usted ya cuenta con una visita registrada", Toast.LENGTH_SHORT).show();
                                                    break;
                                                default:
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(context, ""+registroAsistencia.getError(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    viewHolder.btnAsistencia.setEnabled(true);
                                }

                                @Override
                                public void onFailure(Call<RegistroAsistencia> call, Throwable t) {
                                    if (!InternetConnectionStatus.isConnected(context)) {
                                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    } else if(t instanceof SocketTimeoutException) {
                                        Toast.makeText(context, context.getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                    }
                                    viewHolder.btnAsistencia.setEnabled(true);
                                }
                            });
                        }
                    }
                });
                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });//
            }
        });
        //Llamada al api de visitas para traer la última visita del cliente
        Call<Visita> visitaCall = service.comprobarVisita(subscripcionGratis.getCliente(),subscripcionGratis.getSucursal());
        visitaCall.enqueue(new Callback<Visita>() {
            @Override
            public void onResponse(Call<Visita> call, Response<Visita> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getResults().isEmpty()) {
                        Visita visita = response.body().getResults().get(response.body().getResults().size()-1);
                        viewHolder.tvChekin.setText(visita.getFecha()+ "    " + to12(visita.getHora()));
                        viewHolder.tvChekinTitle.setVisibility(View.VISIBLE);
                        viewHolder.tvChekin.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Visita> call, Throwable t) {
                if (!InternetConnectionStatus.isConnected(context)) {
                    Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                } else if(t instanceof SocketTimeoutException) {
                    Toast.makeText(context, context.getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvSuscripcionNombre, tvSuscripcionComoLlegar, tvSuscripcionHorario, tvSuscripcionTelefono, tvSuscripcionTipo, tvSuscripcionSesiones, tvSuscripcionPrecio, tvSuscripcionSesionesRestantes, btnSuscripcionVer, tvChekin, tvChekinTitle;
        Button btnAsistencia;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSuscripcionNombre = itemView.findViewById(R.id.tv_suscripcion_nombre);
            tvSuscripcionComoLlegar = itemView.findViewById(R.id.tv_suscripcion_como_llegar);
            tvSuscripcionHorario = itemView.findViewById(R.id.tv_suscripcion_horario);
            tvSuscripcionTelefono = itemView.findViewById(R.id.tv_suscripcion_telefono);
            tvSuscripcionTipo = itemView.findViewById(R.id.tv_suscripcion_tipo);
            tvSuscripcionSesiones = itemView.findViewById(R.id.tv_suscripcion_sesiones);
            tvSuscripcionPrecio = itemView.findViewById(R.id.tv_suscripcion_precio);
            tvSuscripcionSesionesRestantes = itemView.findViewById(R.id.tv_suscripcion_sesiones_restantes);
            btnSuscripcionVer = itemView.findViewById(R.id.btn_suscripcion_ver);
            btnAsistencia = itemView.findViewById(R.id.btn_asistencia_manual);
            tvChekin = itemView.findViewById(R.id.tv_ultimo_checkin);
            tvChekinTitle = itemView.findViewById(R.id.tv_ultimo_checkin_title);
        }
    }

    private void comprobarAsistencia(int clienteID, int sucursalID,  SubscripcionesGratis subscripcionGratis) {
        //Revisaremos si el cliente ya ha hecho una calificación de sucursal, de lo contrario mostraremos la pantalla para evaluación
        Call<EvaluacionSucursal> evaluacionSucursalCall = service.verificarEvaluacionUsuario(subscripcionGratis.getCliente(),subscripcionGratis.getSucursal());
        evaluacionSucursalCall.enqueue(new Callback<EvaluacionSucursal>() {
            @Override
            public void onResponse(Call<EvaluacionSucursal> call, Response<EvaluacionSucursal> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResults().isEmpty()) {
                        FragmentManager fragmentManager = ((AppCompatActivity)context ).getSupportFragmentManager();
                        /**
                         * Con esta condición, verificamos que el fragmento sólo se añada si no existe en el backStack
                         */
                        if (fragmentManager.findFragmentByTag("EVALUAR")==null) {
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //Pasamos un objeto clubDetalle con toda la información hacia el siguiente fragmento.
                            Bundle bundle = new Bundle();
                            EvaluacionSucursal evaluacionSucursal = new EvaluacionSucursal(subscripcionGratis.getCliente(),subscripcionGratis.getSucursal(),5);
                            bundle.putSerializable(Variables.EVALUACION,evaluacionSucursal);
                            Evaluar favoritos = new Evaluar();
                            favoritos.setArguments(bundle);
                            //                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);
                            //                    fragmentTransaction.setCustomAnimations(R.animator.going_up, R.animator.going_down, R.animator.going_up, R.animator.going_down);
                            fragmentTransaction.setCustomAnimations(R.animator.going_up, R.animator.going_down, 0, 0);
                            fragmentTransaction.replace(R.id.fragmentContainer, favoritos, "EVALUAR").addToBackStack(null).commit();
                        }
                    } else {
                    }
                }

            }

            @Override
            public void onFailure(Call<EvaluacionSucursal> call, Throwable t) {

            }
        });
    }

    public String to12(String hora) {
        String[] horaArray = hora.split(":");
        String time;
        int hour = Integer.parseInt(horaArray[0]);
        if (hour>=13) {
            hour = hour - 12;
            time = hour + ":" + horaArray[1] + " P.M.";
        } else {
            time = hour + ":" + horaArray[1] + " A.M.";
        }

        return time;
    }
}
