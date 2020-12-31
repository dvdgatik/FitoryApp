package com.dimakers.fitoryapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.adapters.Visitas;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Datos;
import com.dimakers.fitoryapp.api.models.Horario;
import com.dimakers.fitoryapp.api.models.PerfilSucursal;
import com.dimakers.fitoryapp.api.models.RegistroHorario;
import com.dimakers.fitoryapp.api.models.Sesion;
import com.dimakers.fitoryapp.api.models.SesionFull;
import com.dimakers.fitoryapp.api.models.Visita;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuscripcionDetalle extends Fragment {
    TextView tvLunes, tvLunesHorario, tvMartes, tvMartesHorario, tvMiercoles, tvMiercolesHorario, tvJueves, tvJuevesHorario, tvViernes, tvViernesHorario, tvSabado, tvSabadoHorario, tvDomingo, tvDomingoHorario;
    FitoryService service = API.getApi().create(FitoryService.class);
    RecyclerView rvVisitas;
    Visitas adapter;
    public static SuscripcionDetalle newInstance() {
        SuscripcionDetalle misSuscripciones = new SuscripcionDetalle();
        return misSuscripciones;
    }
    TextView tvSuscripcionTipo, tvSuscripcionSesiones, tvSuscripcionPrecio, tvSuscripcionSesionesRestantes, tvSuscripcionComoLlegar, tvSuscripcionDireccion, tvSuscripcionTelefono, tvSuscripcionCorreo, tvSuscripcionHorario, tvPlanMensual;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvSuscripcionTipo = view.findViewById(R.id.tv_suscripcion_tipo);
        tvSuscripcionSesiones = view.findViewById(R.id.tv_suscripcion_sesiones);
        tvSuscripcionPrecio =  view.findViewById(R.id.tv_suscripcion_precio);
        tvSuscripcionSesionesRestantes = view.findViewById(R.id.tv_suscripcion_sesiones_restantes);
        tvSuscripcionComoLlegar = view.findViewById(R.id.tv_suscripcion_como_llegar);
        tvSuscripcionDireccion = view.findViewById(R.id.tv_suscripcion_direccion);
        tvSuscripcionTelefono  = view.findViewById(R.id.tv_suscripcion_telefono);
        tvSuscripcionCorreo = view.findViewById(R.id.tv_suscripcion_correo);
        tvSuscripcionHorario = view.findViewById(R.id.tv_suscripcion_horario);
        tvPlanMensual = view.findViewById(R.id.plan_mensual);
        tvLunes = view.findViewById(R.id.tv_lunes);
        tvMartes = view.findViewById(R.id.tv_martes);
        tvMiercoles = view.findViewById(R.id.tv_miercoles);
        tvJueves = view.findViewById(R.id.tv_jueves);
        tvViernes = view.findViewById(R.id.tv_viernes_horario);
        tvSabado = view.findViewById(R.id.tv_sabado);
        tvDomingo = view.findViewById(R.id.tv_domingo);
        tvLunesHorario = view.findViewById(R.id.tv_lunes_horario);
        tvMartesHorario= view.findViewById(R.id.tv_martes_horario);
        tvMiercolesHorario = view.findViewById(R.id.tv_miercoles_horario);
        tvJuevesHorario = view.findViewById(R.id.tv_jueves_horario);
        tvViernesHorario = view.findViewById(R.id.tv_viernes_horario);
        tvSabadoHorario = view.findViewById(R.id.tv_sabado_horario);
        tvDomingoHorario = view.findViewById(R.id.tv_domingo_horario);
        rvVisitas = view.findViewById(R.id.rv_asistencias);
        adapter = new Visitas(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        rvVisitas.setLayoutManager(layoutManager);
        rvVisitas.setHasFixedSize(true);
        rvVisitas.setAdapter(adapter);
        TextView tvDesplegarInformacion = view.findViewById(R.id.tv_desplegar_informacion);
        ImageView ivDesplegarInformacion = view.findViewById(R.id.iv_desplegar_informacion);
        ExpandableRelativeLayout expandable = view.findViewById(R.id.expandableLayout);
        RelativeLayout relativeLayout = view.findViewById(R.id.desplegar_informacion);
        expandable.toggle();
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandable.isExpanded()) {
                    tvDesplegarInformacion.setText("VER HORARIO");
                    ivDesplegarInformacion.setRotation(360);
                } else {
                    tvDesplegarInformacion.setText("OCULTAR HORARIO");
                    ivDesplegarInformacion.setRotation(180);
                }
                expandable.toggle();
            }
        });
        if (getResources().getDisplayMetrics().heightPixels<=320) {
            tvPlanMensual.setScaleY(0.87f);
        }
        //Obtenemos el objeto ClubDetalle que se envió desde el adapter de sucursales
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Sesion sesion = (Sesion) bundle.getSerializable(Variables.SUSCRIPCION);
            Call<Visita> visitaCall = service.obtenerVisitas(sesion.getSucursal(),sesion.getCliente());
            visitaCall.enqueue(new Callback<Visita>() {
                @Override
                public void onResponse(Call<Visita> call, Response<Visita> response) {
                    if (response.isSuccessful()) {
                        if (!response.body().getResults().isEmpty()) {
                            adapter.update(response.body().getResults());
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al cargar las visitas", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Visita> call, Throwable t) {
                    Toast.makeText(getContext(), "Fallo con la conexión a Internet", Toast.LENGTH_SHORT).show();
                }
            });
            switch (sesion.getSesiones()) {
                case 4:
                    tvSuscripcionTipo.setText("BÁSICO");
                    break;
                case 8:
                    tvSuscripcionTipo.setText("MEDIO");
                    break;
                case 12:
                    tvSuscripcionTipo.setText("AVANZADO");

            }
            tvSuscripcionSesiones.setText(sesion.getSesiones()+" Sesiones");
            tvSuscripcionPrecio.setText("$"+sesion.getTotal()+" MXN");
            tvSuscripcionSesionesRestantes.setText("Sesiones Restantes: "+sesion.getSesionesRestantes());
            tvSuscripcionComoLlegar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String latitud = sesion.getSucursalLatitud();
                    String longitud = sesion.getSucursalLongitud();
                    Uri gmmIntentUri = Uri.parse("geo:"+latitud+","+longitud+"?q="+latitud+","+longitud+"(Ubicación)");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });


            //Detalles del club
            tvSuscripcionDireccion.setText(sesion.getSucursalDireccion());
            tvSuscripcionTelefono.setText(sesion.getSucursalTelefono());
            tvSuscripcionCorreo.setText(sesion.getSucursalCorreo());

            Call<PerfilSucursal> perfilSucursalCall = service.obtenerPerfilSucursal(sesion.getSucursal(),sesion.getCliente());
            perfilSucursalCall.enqueue(new Callback<PerfilSucursal>() {
                @Override
                public void onResponse(Call<PerfilSucursal> call, Response<PerfilSucursal> response) {
                    if (response.isSuccessful()) {
                        PerfilSucursal perfilSucursal = response.body();
                        if (perfilSucursal.getDatos() != null) {
                            Datos datos = perfilSucursal.getDatos().get(0);

                            //Cargar horarios
                            //Cargar horarios
                            Horario horario = null;
                            if (datos.getHorario()!=null) {
                                horario = datos.getHorario().get(0);
                            }
                            for ( int i = 0; i<datos.getRegistroHorario().size(); i++) {
                                //Cargar horario sucursal
                                RegistroHorario registroHorario = datos.getRegistroHorario().get(i);
                                try {
                                    switch (registroHorario.getDia()) {
                                        case "Lunes":
                                            tvLunesHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            if (horario.getTipo().equals("Mixto")) {
                                                i++;
                                                registroHorario = datos.getRegistroHorario().get(i);
                                                tvLunesHorario.setText(tvLunesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            }
                                            break;
                                        case "Martes":
                                            tvMartesHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            if (horario.getTipo().equals("Mixto")) {
                                                i++;
                                                registroHorario = datos.getRegistroHorario().get(i);
                                                tvMartesHorario.setText(tvMartesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            }
                                            break;
                                        case "Miércoles":
                                            tvMiercolesHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            if (horario.getTipo().equals("Mixto")) {
                                                i++;
                                                registroHorario = datos.getRegistroHorario().get(i);
                                                tvMiercolesHorario.setText(tvMiercolesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            }
                                            break;
                                        case "Jueves":
                                            tvJuevesHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            if (horario.getTipo().equals("Mixto")) {
                                                i++;
                                                registroHorario = datos.getRegistroHorario().get(i);
                                                tvJuevesHorario.setText(tvJuevesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            }
                                            break;
                                        case "Viernes":
                                            tvViernesHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            if (horario.getTipo().equals("Mixto")) {
                                                i++;
                                                registroHorario = datos.getRegistroHorario().get(i);
                                                tvViernesHorario.setText(tvViernesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            }
                                            break;
                                        case "Sábado":
                                            tvSabadoHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            if (horario.getTipo().equals("Mixto")) {
                                                i++;
                                                registroHorario = datos.getRegistroHorario().get(i);
                                                tvSabadoHorario.setText(tvSabadoHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            }
                                            break;
                                        case "Domingo":
                                            tvDomingoHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            if (horario.getTipo().equals("Mixto")) {
                                                i++;
                                                registroHorario = datos.getRegistroHorario().get(i);
                                                tvViernesHorario.setText(tvViernesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                            }
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();

                                }
                            }




                        } else {
                            getActivity().getSupportFragmentManager().popBackStack();
                            Toast.makeText(getContext(), "No coinciden los datos registrados", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Unsuccesful", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PerfilSucursal> call, Throwable t) {
                    Toast.makeText(getActivity(), "Failure"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
//            tvSuscripcionHorario.setText(suscripcion.getHorario().getLunesA().substring(0,5)+"-"+suscripcion.getHorario().getLunesC().substring(0,5));

        } else {
            getActivity().finish();
        }
//        ArrayList<Suscripcion> suscripcions = new ArrayList<>();
//        suscripcions.add(new Suscripcion("TRX Centro","","","Lunes a Viernes de 8am-10pm Sábados de 10am-5pm","Teléfono: 01 (812) 132 2896","Básico","4 sesiones","$132.00 MxN","Sesiones Restantes: 2"));
//        suscripcions.add(new Suscripcion("BOX CAPITAL","","","Lunes a Viernes de 8am-10pm Sábados de 10am-5pm","Teléfono: 01 (812) 132 2896","Medio","8 sesiones","$264.00 MxN","Sesiones Restantes: 4"));
//        adapter.update(suscripcions);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suscripcion_detalle,container,false);
    }

    public String parseTo12(String fecha){
//        SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
//        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm:ss");
//        Date date = null;
//        try {
//            date = parseFormat.parse(fecha);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        String output = fecha.substring(0,5);

        return output;
    }
}
