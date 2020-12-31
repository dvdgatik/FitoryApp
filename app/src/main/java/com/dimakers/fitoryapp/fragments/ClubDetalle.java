package com.dimakers.fitoryapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.adapters.Actividades;
import com.dimakers.fitoryapp.adapters.Servicios;
import com.dimakers.fitoryapp.adapters.ViewPagerAdapter;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Actividad;
import com.dimakers.fitoryapp.api.models.Ciudad;
import com.dimakers.fitoryapp.api.models.Club;
import com.dimakers.fitoryapp.api.models.Datos;
import com.dimakers.fitoryapp.api.models.Estado;
import com.dimakers.fitoryapp.api.models.EvaluacionSucursal;
import com.dimakers.fitoryapp.api.models.Favorito;
import com.dimakers.fitoryapp.api.models.Horario;
import com.dimakers.fitoryapp.api.models.PerfilSucursal;
import com.dimakers.fitoryapp.api.models.PromedioEvaluacion;
import com.dimakers.fitoryapp.api.models.RegistroHorario;
import com.dimakers.fitoryapp.api.models.RevisionSubscripcionFree;
import com.dimakers.fitoryapp.api.models.Servicio;
import com.dimakers.fitoryapp.api.models.Sesion;
import com.dimakers.fitoryapp.api.models.SubscripcionMes;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.SucursalFull;
import com.dimakers.fitoryapp.services.ObtenerActividades;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.uncopt.android.widget.text.justify.JustifiedEditText;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClubDetalle extends Fragment {
    FitoryService service = API.getApi().create(FitoryService.class);
    Fragment suscripcionMensual = new SuscripcionMensual();
    Fragment suscripcionDiaria = new SuscripcionDiaria();
    Button btnPlanDiario;
    Button btnPlanMensual;
    Button btnDiasPrueba;
    TextView tvClubNombre, tvClubPrecioDia, tvClubPrecioMes, tvClubDireccion, tvClubTelefono, tvClubCorreo, tvFavoritoValue, tvFavoritoId,tvClubInfoTitle;
    TextView tvComoLlegar;
    TextView tvLunes, tvLunesHorario, tvMartes, tvMartesHorario, tvMiercoles, tvMiercolesHorario, tvJueves, tvJuevesHorario, tvViernes, tvViernesHorario, tvSabado, tvSabadoHorario, tvDomingo, tvDomingoHorario, redesSociales ;
    LinearLayout rbClubCalificacion;
    ImageView ivInstagram, ivTwitter, ivFacebook, ivLinkedn, ivIconoFavoritos, ivLogo;
    JustifiedTextView tvClubInfo;
    ViewPager ivClubFoto;
    Servicios adapter;
    RecyclerView recyclerViewHorarios, rvSucursalHorario;
    Actividades horariosAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageView star1,star2,star3,star4,star5;
    View line1, line2;
    Dialog alertDialog;
    ArrayList<String> galeria;
    RelativeLayout servicios_adicionales, actividades_incluidas;
    Timer timer;
    int clienteID, sucursalID;
    @Override
    public void onResume() {
        super.onResume();
        verificarPromocion(clienteID,sucursalID);
        timer  = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (galeria != null) {

                                if (ivClubFoto.getCurrentItem() == galeria.size() - 1) {

                                    ivClubFoto.setCurrentItem(0, true); //getItem(-1) for previous
                                } else {
                                    ivClubFoto.setCurrentItem(ivClubFoto.getCurrentItem() + 1, true); //getItem(-1) for previous
                                }

                            }
                        }
                    });

                }
            }, 4000,4000);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Variables.PREFERENCES,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        startLoadingAnimation();
        tvClubInfo = view.findViewById(R.id.tv_club_info);
        tvClubInfoTitle = view.findViewById(R.id.tv_club_info_title);
        line2 = view.findViewById(R.id.line1);
        line1 = view.findViewById(R.id.line2);
        redesSociales = view.findViewById(R.id.redes_sociales);
        tvComoLlegar = view.findViewById(R.id.tv_como_llegar);
        servicios_adicionales = view.findViewById(R.id.servicios_adicionales);
        actividades_incluidas = view.findViewById(R.id.actividades_incluidas);
//        rvSucursalHorario = view.findViewById(R.id.rv_sucursal_horario);
        tvClubNombre = view.findViewById(R.id.tv_club_nombre);
        rbClubCalificacion = view.findViewById(R.id.rb_club_calificacion);
        ivLogo = view.findViewById(R.id.iv_logo);
        ivClubFoto = view.findViewById(R.id.iv_club_foto);
        ivClubFoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                timer.cancel();
                timer  = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (galeria != null) {

                                    if (ivClubFoto.getCurrentItem() == galeria.size() - 1) {

                                        ivClubFoto.setCurrentItem(0, true); //getItem(-1) for previous
                                    } else {
                                        ivClubFoto.setCurrentItem(ivClubFoto.getCurrentItem() + 1, true); //getItem(-1) for previous
                                    }

                                }
                            }
                        });

                    }
                }, 4000,4000);
                return false;
            }
        });
        tvClubPrecioDia = view.findViewById(R.id.tv_club_precio_dia);
        tvClubPrecioMes = view.findViewById(R.id.tv_club_precio_mes);
        tvClubDireccion = view.findViewById(R.id.tv_club_direccion);
        tvClubTelefono = view.findViewById(R.id.tv_club_telefono);
        tvClubCorreo = view.findViewById(R.id.tv_club_correo);
//        rvClubHorario = view.findViewById(R.id.tv_club_horario);
        ivFacebook = view.findViewById(R.id.iv_facebook);
        ivInstagram = view.findViewById(R.id.iv_instagram);
        ivTwitter = view.findViewById(R.id.iv_twitter);
        ivLinkedn = view.findViewById(R.id.iv_linkedn);
        ivIconoFavoritos = view.findViewById(R.id.iv_icono_favoritos);
        tvFavoritoValue = view.findViewById(R.id.tv_favorito_value);
        tvFavoritoId = view.findViewById(R.id.tv_favorito_id);
        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);
        star4 = view.findViewById(R.id.star4);
        star5 = view.findViewById(R.id.star5);
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
        TextView tvDesplegarInformacion = view.findViewById(R.id.tv_desplegar_informacion);
        ImageView ivDesplegarInformacion = view.findViewById(R.id.iv_desplegar_informacion);
        btnPlanDiario = (Button) view.findViewById(R.id.plan_diario);
        btnPlanMensual = (Button) view.findViewById(R.id.plan_mensual);
        btnDiasPrueba = view.findViewById(R.id.btn_dias_gratis);
        ExpandableRelativeLayout expandable = view.findViewById(R.id.expandableLayout);
        RelativeLayout relativeLayout = view.findViewById(R.id.desplegar_informacion);
        expandable.toggle();
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandable.isExpanded()) {
                    tvDesplegarInformacion.setText("VER INFORMACIÓN");
                    ivDesplegarInformacion.setRotation(360);
                } else {
                    tvDesplegarInformacion.setText("OCULTAR INFORMACIÓN");
                    ivDesplegarInformacion.setRotation(180);
                }
                expandable.toggle();
            }
        });

        /**
         *  Llamada al api para traer toda la información de la sucursal
         */
        
        //Obtenemos el objeto ClubDetalle que se envió desde el adapter de sucursales
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sucursalID = bundle.getInt(Variables.SUCURSAL_ID);
            clienteID = sharedPreferences.getInt(Variables.CLIENTEID, 0);
            if (clienteID == 0) {
                alertDialog.dismiss();
                Toast.makeText(getActivity(), "No se pudo obtener el cliente id", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            } else {
                Call<PerfilSucursal> perfilSucursalCall = service.obtenerPerfilSucursal(sucursalID,clienteID); 
                perfilSucursalCall.enqueue(new Callback<PerfilSucursal>() {
                    @Override
                    public void onResponse(Call<PerfilSucursal> call, Response<PerfilSucursal> response) {
                        if (response.isSuccessful()) {
                            PerfilSucursal perfilSucursal = response.body();
                            if (perfilSucursal.getDatos() != null) {
                                Datos datos = perfilSucursal.getDatos().get(0);
                                Club club = datos.getClub().get(0);
                                ArrayList<Actividad> actividades = datos.getActividad();
                                ArrayList<Servicio> servicios = datos.getServicio();
                                adapter = new Servicios(getContext());
                                tvComoLlegar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Uri gmmIntentUri = Uri.parse("geo:"+datos.getLatitud()+","+datos.getLongitud()+"?q="+datos.getLatitud()+","+datos.getLongitud()+"(Ubicación)");
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        getContext().startActivity(mapIntent);
                                    }
                                });
                                //Cargar Servicios
                                if (servicios != null) {
                                    RecyclerView recyclerView = view.findViewById(R.id.rv_servicios);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setHasFixedSize(true);
                                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
                                    recyclerView.setLayoutManager(layoutManager);
                                    adapter.update(servicios);
                                }

                                if (adapter.getItemCount()==0) {
                                    line1.setVisibility(View.GONE);
                                    servicios_adicionales.setVisibility(View.GONE);
                                }
                                //Cargar horarios
                                Horario horario = null;
                                if (datos.getActividad()!=null) {
                                    horariosAdapter = new Actividades(getContext());
                                    RecyclerView recyclerViewHorarios = view.findViewById(R.id.rv_horarios);
                                    recyclerViewHorarios.setAdapter(horariosAdapter);
                                    recyclerViewHorarios.setHasFixedSize(true);
                                    GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
                                    recyclerViewHorarios.setLayoutManager(layoutManager);
                                    horariosAdapter.update(actividades);
                                } else {
                                    actividades_incluidas.setVisibility(View.GONE);
                                    line2.setVisibility(View.GONE);
                                }
                                if (datos.getHorario()!=null) {
                                    horario = datos.getHorario().get(0);
                                    //Botónes de plan diario y suscripción mensual
                                    //Antes tenemos que ver si ya tiene sesiones de diario
                                    Call<Sesion> sesionCall = service.obtenerSesionesClienteSucursal(clienteID,sucursalID,true);
                                    sesionCall.enqueue(new Callback<Sesion>() {
                                        @Override
                                        public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                                            if (response.isSuccessful()) {
                                                if (response.body().getResults().isEmpty()) {
                                                    btnPlanDiario.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                            if (fragmentManager.findFragmentByTag("SUSCRIPCIONDIARIA")==null) {
                                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                                //                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);/
                                                                fragmentTransaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down);
                                                                //Pasamos un objeto clubDetalle con toda la información hacia el siguiente fragmento.
                                                                Bundle bundle = new Bundle();
                                                                Sucursal sucursal = new Sucursal();
                                                                sucursal.setDia(datos.getDia()+"");
                                                                sucursal.setMensualidad(datos.getMensualidad()+"");
                                                                sucursal.setId(sucursalID);
                                                                bundle.putSerializable(Variables.SUCURSALDETALLE, sucursal);
                                                                suscripcionDiaria.setArguments(bundle);
                                                                timer.cancel();
                                                                fragmentTransaction.add(R.id.fragmentContainer, suscripcionDiaria, "SUSCRIPCIONDIARIA").addToBackStack(null).commit();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    btnPlanDiario.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Toast.makeText(getActivity(), "Usted ya cuenta con sesiones disponibles", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Sesion> call, Throwable t) {

                                        }
                                    });

                                    //Botónes de suscripción mensual
                                    //Antes tenemos que ver si ya tiene alguna suscripción
                                    Call<SubscripcionMes> suscripcionCall = service.obtenerSubscripcionesClienteSucursal(clienteID,sucursalID,true);
                                    suscripcionCall.enqueue(new Callback<SubscripcionMes>() {
                                        @Override
                                        public void onResponse(Call<SubscripcionMes> call, Response<SubscripcionMes> response) {
                                            if (response.isSuccessful()) {
                                                if (response.body().getResults().isEmpty()) {
                                                    btnPlanMensual.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                            if (fragmentManager.findFragmentByTag("SUSCRIPCIONMENSUAL")==null) {
                                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                                fragmentTransaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down);
                                                                //Pasamos un objeto clubDetalle con toda la información hacia el siguiente fragmento.
                                                                Bundle bundle = new Bundle();
                                                                Sucursal sucursal = new Sucursal();
                                                                sucursal.setDia(datos.getDia()+"");
                                                                sucursal.setMensualidad(datos.getMensualidad()+"");
                                                                sucursal.setId(sucursalID);
                                                                bundle.putSerializable(Variables.SUCURSALDETALLE,sucursal);
                                                                suscripcionMensual.setArguments(bundle);
                                                                timer.cancel();
                                                                fragmentTransaction.add(R.id.fragmentContainer, suscripcionMensual, "SUSCRIPCIONMENSUAL").addToBackStack(null).commit();
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    btnPlanMensual.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Toast.makeText(getActivity(), "Usted ya cuenta con suscripción vigente", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<SubscripcionMes> call, Throwable t) {

                                        }
                                    });
                                    Horario finalHorario = horario;

                                }
//                                if (horariosAdapter.getItemCount()==0) {
//                                    line1.setVisibility(View.GONE);
//                                    servicios_adicionales.setVisibility(View.GONE);
//                                }
                                tvClubNombre.setText(datos.getNombre());
                                recolorear(datos.getCalificacion());
                                Glide.with(getContext()).load(club.getFoto()).into(ivLogo);
//                                Double d = datos.getCalificacion();
//                                Integer calificaion = d.intValue();
//                            Glide.with(getContext()).load(club.getFoto()).into(ivClubFoto);
                                if ( (club.getFacebook().equals("") || club.getFacebook().equals("...") )
                                        && ( club.getTwitter().equals("") || club.getTwitter().equals("...") ) &&
                                        ( club.getInstagram().equals("") || club.getInstagram().equals("...") ) ) {
                                    redesSociales.setVisibility(View.GONE);
                                }

                                //Redes sociales
                                ivFacebook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Uri webpage = Uri.parse("fb://facewebmodal/f?href="+club.getFacebook());
                                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                                            startActivity(intent);
                                        } else {
                                            webpage = Uri.parse(club.getFacebook());
                                            intent = new Intent(Intent.ACTION_VIEW, webpage);
                                            startActivity(intent);
                                        }
                                    }
                                });
                                if (club.getFacebook().equals("") || club.getFacebook().equals("...")) {
                                    ivFacebook.setVisibility(View.GONE);
                                }

                                ivInstagram.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        Uri webpage = Uri.parse(club.getInstagram());
//                                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//                                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
//                                            startActivity(intent);
//                                        }
                                        String instagram = club.getInstagram();
                                        instagram = instagram.split("/")[instagram.split("/").length-2];
                                        Uri webpage = Uri.parse("https://instagram.com/_u/"+ instagram+"/");
//                                        Uri webpage = Uri.parse("https://www.instagram.com/fleur.cdmx/?hl=es-la");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                                            startActivity(intent);
                                        } else {
                                            webpage = Uri.parse(club.getTwitter());
                                            intent = new Intent(Intent.ACTION_VIEW, webpage);
                                            startActivity(intent);
                                        }
                                    }
                                });

                                if (club.getInstagram().equals("") || club.getInstagram().equals("...")) {
                                    ivInstagram.setVisibility(View.GONE);
                                }

                                ivTwitter.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String twitter = club.getTwitter();
                                        twitter = twitter.split("/")[twitter.split("/").length-1];
                                        Uri webpage = Uri.parse("twitter://user?screen_name="+ twitter);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                                            startActivity(intent);
                                        } else {
                                            webpage = Uri.parse(club.getTwitter());
                                            intent = new Intent(Intent.ACTION_VIEW, webpage);
                                            startActivity(intent);
                                        }
                                    }
                                });

                                if (club.getTwitter().equals("") || club.getTwitter().equals("...")) {
                                    ivTwitter.setVisibility(View.GONE);
                                }
//
//                                ivLinkedn.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Uri webpage = Uri.parse("https://www.linkedin.com/company/linkedin/");
//                                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//                                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
//                                            startActivity(intent);
//                                        }
//                                    }
//                                });
//
                                ivLinkedn.setVisibility(View.GONE);

                                if (datos.getRegistroHorario()!=null) {
                                    for ( int i = 0; i<datos.getRegistroHorario().size(); i++) {
                                        //Cargar horario sucursal
                                        RegistroHorario registroHorario = datos.getRegistroHorario().get(i);
                                        try {
                                            switch (registroHorario.getDia()) {
                                                case "Lunes":
                                                    tvLunesHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                    if (horario.getTipo().equals("Mixto")) {
                                                        i++;
                                                        try {
                                                            registroHorario = datos.getRegistroHorario().get(i);
                                                            tvLunesHorario.setText(tvLunesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                        } catch (IndexOutOfBoundsException e) {
                                                            Log.e(ClubDetalle.class.getSimpleName(), "Horario mixto incompleto");
                                                        }
                                                    }
                                                    break;
                                                case "Martes":
                                                    tvMartesHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                    if (horario.getTipo().equals("Mixto")) {
                                                        i++;
                                                        try {
                                                            registroHorario = datos.getRegistroHorario().get(i);
                                                            tvMartesHorario.setText(tvMartesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                        } catch (IndexOutOfBoundsException e) {
                                                            Log.e(ClubDetalle.class.getSimpleName(), "Horario mixto incompleto");
                                                        }
                                                    }
                                                    break;
                                                case "Miércoles":
                                                    tvMiercolesHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                    if (horario.getTipo().equals("Mixto")) {
                                                        i++;
                                                        try {
                                                            registroHorario = datos.getRegistroHorario().get(i);
                                                            tvMiercolesHorario.setText(tvMiercolesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                        } catch (IndexOutOfBoundsException e) {
                                                            Log.e(ClubDetalle.class.getSimpleName(), "Horario mixto incompleto");
                                                        }
                                                    }
                                                    break;
                                                case "Jueves":
                                                    tvJuevesHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                    if (horario.getTipo().equals("Mixto")) {
                                                        i++;
                                                        try {
                                                            registroHorario = datos.getRegistroHorario().get(i);
                                                            tvJuevesHorario.setText(tvJuevesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                        } catch (IndexOutOfBoundsException e) {
                                                            Log.e(ClubDetalle.class.getSimpleName(), "Horario mixto incompleto");
                                                        }
                                                    }
                                                    break;
                                                case "Viernes":
                                                    tvViernesHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                    if (horario.getTipo().equals("Mixto")) {
                                                        i++;
                                                        try {
                                                            registroHorario = datos.getRegistroHorario().get(i);
                                                            tvViernesHorario.setText(tvViernesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                        } catch (IndexOutOfBoundsException e) {
                                                            Log.e(ClubDetalle.class.getSimpleName(), "Horario mixto incompleto");
                                                        }
                                                    }
                                                    break;
                                                case "Sábado":
                                                    tvSabadoHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                    if (horario.getTipo().equals("Mixto")) {
                                                        i++;
                                                        try {
                                                            registroHorario = datos.getRegistroHorario().get(i);
                                                            tvSabadoHorario.setText(tvSabadoHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                        } catch (IndexOutOfBoundsException e) {
                                                            Log.e(ClubDetalle.class.getSimpleName(), "Horario mixto incompleto");
                                                        }
                                                    }
                                                    break;
                                                case "Domingo":
                                                    tvDomingoHorario.setText(parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                    if (horario.getTipo().equals("Mixto")) {
                                                        i++;
                                                        try {
                                                            registroHorario = datos.getRegistroHorario().get(i);
                                                            tvViernesHorario.setText(tvViernesHorario.getText()+"     "+parseTo12(registroHorario.getApertura())+" - "+parseTo12(registroHorario.getCierre()));
                                                        } catch (IndexOutOfBoundsException e) {
                                                            Log.e(ClubDetalle.class.getSimpleName(), "Horario mixto incompleto");
                                                        }
                                                    }
                                            }
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();

                                        }
                                    }
                                }

                                tvClubNombre.setText(datos.getNombre());
                                try {
//                                Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(datos.getLogo()).into(ivClubFoto);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                tvClubTelefono.setText(datos.getTelefono());
                                tvClubCorreo.setText(datos.getCorreo());
                                try {
                                    if (datos.getTips()==null) {
                                        tvClubInfo.setVisibility(View.GONE);
                                        tvClubInfoTitle.setVisibility(View.GONE);
                                    } else if(datos.getTips().equals("...") || datos.getTips().equals("")) {
                                        tvClubInfo.setVisibility(View.GONE);
                                        tvClubInfoTitle.setVisibility(View.GONE);
                                    } else {
                                        tvClubInfo.setText(datos.getTips());
                                    }
                                } catch (Exception e) {

                                }
                                NumberFormat nf = NumberFormat.getCurrencyInstance();

                                tvClubPrecioDia.setText(nf.format(new BigDecimal(String.valueOf(datos.getDia())))+" MXN");
                                tvClubPrecioMes.setText(nf.format(new BigDecimal(String.valueOf(datos.getMensualidad())))+" MXN");
//                            tvClubDireccion.setText(datos.getDireccion());
                                if (datos.getNumExt()!=null) {
                                    tvClubDireccion.setText(datos.getCalle()+" "+datos.getNumExt()+" "+datos.getColonia()+" "+datos.getMunicipio());
                                }
                                if (datos.getNumInt()!=null) {
                                    tvClubDireccion.setText(datos.getCalle()+" "+datos.getNumInt()+" "+datos.getColonia()+" "+datos.getMunicipio());
                                }
                                tvClubTelefono.setText(datos.getTelefono());
                                tvClubCorreo.setText(datos.getCorreo());
                                tvFavoritoId.setText(datos.getFavorito()+"");
//                            if (sucursal.isFavorito()) {
//                                tvFavoritoId.setText(sucursal.getFavoritoID()+"");
//                                tvFavoritoValue.setText(sucursal.isFavorito()+"");
//                            } else {
//                                tvFavoritoValue.setText("false");
//                            }
//                            obtenerDireccion(sucursal);
//                            obtenerInformacionClub(sucursal);
//                            obtenerServicios(sucursal);
                                tvFavoritoId.setText(datos.getFavorito()+"");
                                if (datos.getFavorito()!=0) {
                                    Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.icono_favoritos_activo).into(ivIconoFavoritos);
                                } else {
                                    Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.icono_favoritos_inactivo).into(ivIconoFavoritos);
                                }
                                alertDialog.dismiss();

                                // Cargar imágenes de la sucursal
//                            ivClubFoto.setFactory(new ViewSwitcher.ViewFactory() {
//                                @Override
//                                public View makeView() {
//                                    ImageView myView = new ImageView(getContext());
//                                    return myView;
//                                }
//                            });
                                // Stuff that updates the UI
                                if (datos.getGaleria()!=null) {
                                    galeria = datos.getGaleria();
                                    ViewPagerAdapter adapter = new ViewPagerAdapter(getContext(), galeria);
                                    ivClubFoto.setAdapter(adapter);
//                                Animation in = AnimationUtils.loadAnimation(getContext(),android.R.anim.slide_out_right);
//                                Animation out = AnimationUtils.loadAnimation(getContext(),android.R.anim.slide_in_left);
//                                ivClubFoto.setInAnimation(in);
//                                ivClubFoto.setOutAnimation(out);
//                                timer.schedule(new TimerTask()
//                                {
//                                    @Override
//                                    public void run()
//                                    {
//
//                                        // TODO do your thing
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            int counter = 0;
//
//                                            @Override
//                                            public void run() {
////                                                ivClubFoto.setImageURI(Uri.parse(galeria.get(0)));
////                                                Glide.with(getActivity()).load(galeria.get(counter)).into((ImageView)ivClubFoto.getCurrentView());
////                                                ivClubFoto.setImageURI(Uri.parse(galeria.get(counter)));
////                                                counter++;
////                                                if (galeria.size()-1==counter) {
////                                                    counter=0;
////                                                }
//                                                Glide.with(getContext()).load(galeria.get(counter)).listener(new RequestListener<Drawable>() {
//                                                    @Override
//                                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                                        return false;
//                                                    }
//
//                                                    @Override
//                                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                                        counter++;
//                                                        Toast.makeText(getContext(), ""+galeria.get(counter), Toast.LENGTH_SHORT).show();
//                                                        if (counter==galeria.size()-1) {
//                                                            counter=0;
//                                                        }
//                                                        ivClubFoto.setImageDrawable(resource);
//                                                        return true;
//
//                                                    }
//                                                }).into((ImageView) ivClubFoto.getNextView());
//                                            }
//                                        });
//                                    }
//                                }, 5000, 5000);


                                    ivIconoFavoritos.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Variables.sucursalesFavs.clear();
                                            if (!tvFavoritoId.getText().equals("0")) {
                                                int favorito_id = Integer.parseInt(tvFavoritoId.getText().toString());
                                                Call<ResponseBody> favoritoCall = service.removerFavorito(favorito_id);
                                                favoritoCall.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        if (response.isSuccessful()) {
//                                                          sucursal.setIsFavorito(false);
                                                            Toast.makeText(getActivity(), "Removido de favoritos", Toast.LENGTH_SHORT).show();
                                                            Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.icono_favoritos_inactivo).into(ivIconoFavoritos);
                                                            tvFavoritoId.setText("0");
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                    }
                                                });
                                            } else {
                                                int cliente_id = sharedPreferences.getInt(Variables.CLIENTEID, 0);
                                                Call<Favorito> favoritoCall = service.agregarFavorito(cliente_id, sucursalID);
                                                favoritoCall.enqueue(new Callback<Favorito>() {
                                                    @Override
                                                    public void onResponse(Call<Favorito> call, Response<Favorito> response) {
                                                        if (response.isSuccessful()) {
                                                            Favorito favorito = response.body();
                                                            Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.icono_favoritos_activo).into(ivIconoFavoritos);
                                                            tvFavoritoId.setText(favorito.getId()+"");
//                                    sucursal.setIsFavorito(true);
//                                    sucursal.setFavoritoID(favorito.getId());
                                                            tvFavoritoId.setText(favorito.getId() + "");
                                                            Toast.makeText(getContext(), "Agregado a favoritos", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Favorito> call, Throwable t) {
                                                    }
                                                });
                                            }
                                        }
                                    });


                                }

                            } else {
                                getActivity().getSupportFragmentManager().popBackStack();
                                Toast.makeText(getContext(), "No coinciden los datos registrados", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Unsuccesful", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<PerfilSucursal> call, Throwable t) {
                        Toast.makeText(getActivity(), "Failure"+t.getMessage(), Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });


//                // Consultar si el club es favorito del cliente
//                int clienteId = sharedPreferences.getInt(Variables.CLIENTEID,0);
//                if (clienteId != 0) {
//                    Call<Favorito> call = service.obtenerFavorito(clienteId, sucursalID);
//                    call.enqueue(new Callback<Favorito>() {
//                        @Override
//                        public void onResponse(Call<Favorito> call, Response<Favorito> response) {
//                            if (response.isSuccessful()) {
//                                if (response.body().getResults().isEmpty()) {
//                                    Glide.with(getActivity()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.icono_favoritos_inactivo).into(ivIconoFavoritos);
//                                    tvFavoritoValue.setText("false");
//                                } else {
//                                    Favorito favorito = response.body().getResults().get(0);
//                                    tvFavoritoId.setText(favorito.getId() + "");
//                                    Glide.with(getActivity()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.icono_favoritos_activo).into(ivIconoFavoritos);
//                                    tvFavoritoValue.setText("true");
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Favorito> call, Throwable t) {
//                            Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
                }
        } else {
            alertDialog.dismiss();
            Toast.makeText(getActivity(), "No se pudo obtener la información del club", Toast.LENGTH_SHORT).show();
            getFragmentManager().popBackStack();
        }
//
//
//
//            SucursalFull sucursal = (SucursalFull) bundle.getSerializable(Variables.CLUBDETALLE);
//



//




//            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
//                    new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context context, Intent intent) {
//                            //Code
//                            Gson gson = new Gson();
//                            String horariosAsString = intent.getStringExtra(Variables.HORARIO);
//                            String actividadesAsString = intent.getStringExtra(Variables.ACTIVIDADES);
//                            Type typeHorarios = new TypeToken<ArrayList<ActividadHorario>>(){}.getType();
//                            ArrayList<ActividadHorario> horarios = gson.fromJson(horariosAsString,typeHorarios);
//                            Type typeActividades = new TypeToken<ArrayList<Actividad>>(){}.getType();
//                            ArrayList<Actividad> actividades = gson.fromJson(actividadesAsString,typeActividades);
//                            FormatoHorario horario = new FormatoHorario();
//                            for(ActividadHorario horarioItem : horarios) {
//                                for (Actividad actividad : actividades) {
//                                    horario.setIcono(actividad.getIcono());
//                                    horario.setNombre(actividad.getNombre());
//                                    switch (horarioItem.getDia()) {
//                                        case "Lunes":
//                                            horario.setLunes(horarioItem.getHora());
//                                            break;
//                                        case "Martes":
//                                            horario.setMartes(horarioItem.getHora());
//                                            break;
//                                        case "Miércoles":
//                                            horario.setMiercoles(horarioItem.getHora());
//                                            break;
//                                        case "Jueves":
//                                            horario.setJueves(horarioItem.getHora());
//                                            break;
//                                        case "Viernes":
//                                            horario.setViernes(horarioItem.getHora());
//                                            break;
//                                        case "Sábado":
//                                            horario.setSabado(horarioItem.getHora());
//                                            break;
//                                        case "Domingo":
//                                            horario.setDomingo(horarioItem.getHora());
//                                            break;
//                                            default:
//                                    }
//                                }
//                                //Cargar Recycler view
//                                horariosAdapter.add(horario);
//                            }
//                        }
//                    }, new IntentFilter(Variables.HORARIO)
//            );
//            obtenerActividades(sucursal);


//                rbClubCalificacion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//                    @Override
//                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                        rbClubCalificacion.setEnabled(false);
//                        int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
//                        Call<EvaluacionSucursal> evaluacionSucursalCall = service.verificarEvaluacionUsuario(idCliente,sucursal.getId());
//                        evaluacionSucursalCall.enqueue(new Callback<EvaluacionSucursal>() {
//                            @Override
//                            public void onResponse(Call<EvaluacionSucursal> call, Response<EvaluacionSucursal> response) {
//                                if (response.isSuccessful()) {
//                                    if (!response.body().getResults().isEmpty()) {
//                                        Toast.makeText(getContext(), "Usted ya ha calificado esta sucursal. Muchas gracias!", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        EvaluacionSucursal evaluacionSucursal = new EvaluacionSucursal(idCliente,sucursal.getId(), rating);
//                                        Call<EvaluacionSucursal> crearEvaluacionCall = service.crearEvaluacion(evaluacionSucursal);
//                                        crearEvaluacionCall.enqueue(new Callback<EvaluacionSucursal>() {
//                                            @Override
//                                            public void onResponse(Call<EvaluacionSucursal> call, Response<EvaluacionSucursal> response) {
//                                                if (response.isSuccessful()) {
//                                                    Toast.makeText(getActivity(), "Calificación guardada. Muchas gracias!", Toast.LENGTH_SHORT).show();
//                                                    Call<PromedioEvaluacion> promedioEvaluacionCall = service.calcularPromedioSucursal(sucursal.getId());
//                                                    promedioEvaluacionCall.enqueue(new Callback<PromedioEvaluacion>() {
//                                                        @Override
//                                                        public void onResponse(Call<PromedioEvaluacion> call, Response<PromedioEvaluacion> response) {
//                                                            if (response.isSuccessful()) {
//                                                                Toast.makeText(getContext(), ""+response.body().getCalificacionSucursal(), Toast.LENGTH_SHORT).show();
//                                                            } else {
//                                                                Toast.makeText(getContext(), "Unsuccesful", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onFailure(Call<PromedioEvaluacion> call, Throwable t) {
//                                                        }
//                                                    });
//                                                } else {
//                                                    Toast.makeText(getActivity(), "No pudimos guardar su calificación. Intente de nuevo", Toast.LENGTH_SHORT).show();
//                                                }
//                                                rbClubCalificacion.setEnabled(true);
//                                            }
//
//                                            @Override
//                                            public void onFailure(Call<EvaluacionSucursal> call, Throwable t) {
//                                                rbClubCalificacion.setEnabled(true);
//                                                if (!InternetConnectionStatus.isConnected(getContext())) {
//                                                    Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                } else if(t instanceof SocketTimeoutException) {
//                                                    Toast.makeText(getContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                                    }
//                                }
//                                rbClubCalificacion.setEnabled(true);
//                            }
//
//                            @Override
//                            public void onFailure(Call<EvaluacionSucursal> call, Throwable t) {
//                                rbClubCalificacion.setEnabled(true);
//                                if (!InternetConnectionStatus.isConnected(getContext())) {
//                                    Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                } else if(t instanceof SocketTimeoutException) {
//                                    Toast.makeText(getContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                });
//                star1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        recolorear(1);
//                        calificar(1, sucursal);
//                    }
//                });
//                star2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        recolorear(2);
//                        calificar(2, sucursal);
//                    }
//                });
//                star3.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        recolorear(3);
//                        calificar(3, sucursal);
//                    }
//                });
//                star4.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        recolorear(4);
//                        calificar(4, sucursal);
//                    }
//                });
//                star5.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        recolorear(5);
//                        calificar(5, sucursal);
//                    }
//                });
////            }
//            if (sucursal.getSucursal().getCalificacion()!=0) {
//                recolorear((int)Math.round(sucursal.getSucursal().getCalificacion()));
//            }


//

//


//        } else {
//            getActivity().finish();
//        }
    }

    private void verificarPromocion(int clienteID, int sucursalID) {
        Call<RevisionSubscripcionFree> revisionSubscripcionFreeCall = service.revisarSubscripcionFree(clienteID,sucursalID);
        revisionSubscripcionFreeCall.enqueue(new Callback<RevisionSubscripcionFree>() {
            @Override
            public void onResponse(Call<RevisionSubscripcionFree> call, Response<RevisionSubscripcionFree> response) {
                if (response.isSuccessful()) {
                    RevisionSubscripcionFree revisionSubscripcionFree = response.body();
                    if (revisionSubscripcionFree.isMostrar()) {
                        btnDiasPrueba.setVisibility(View.VISIBLE);
                        btnDiasPrueba.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Call<PerfilSucursal> perfilSucursalCall = service.obtenerPerfilSucursal(sucursalID,clienteID);
                                perfilSucursalCall.enqueue(new Callback<PerfilSucursal>() {
                                    @Override
                                    public void onResponse(Call<PerfilSucursal> call, Response<PerfilSucursal> response) {
                                        if (response.isSuccessful()) {
                                            PerfilSucursal perfilSucursal = response.body();
                                            if (perfilSucursal.getDatos() != null) {
                                                Datos datos = perfilSucursal.getDatos().get(0);
                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                if (fragmentManager.findFragmentByTag("DIASPRUEBA")==null) {
                                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                    fragmentTransaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down);
                                                    //Pasamos un objeto clubDetalle con toda la información hacia el siguiente fragmento.
                                                    DiasPrueba diasPrueba = new DiasPrueba();
                                                    Bundle bundle = new Bundle();
                                                    Sucursal sucursal = new Sucursal();
                                                    sucursal.setDia(datos.getDia()+"");
                                                    sucursal.setMensualidad(datos.getMensualidad()+"");
                                                    sucursal.setId(sucursalID);
                                                    bundle.putSerializable(Variables.SUCURSALDETALLE,sucursal);
                                                    diasPrueba.setArguments(bundle);
                                                    timer.cancel();
                                                    fragmentTransaction.add(R.id.fragmentContainer, diasPrueba, "DIASPRUEBA").addToBackStack(null).commit();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<PerfilSucursal> call, Throwable t) {

                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<RevisionSubscripcionFree> call, Throwable t) {

            }
        });
    }

    private void calificar(int rating, SucursalFull sucursal) {
        rbClubCalificacion.setEnabled(false);
        rbClubCalificacion.setClickable(false);
        int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
        Call<EvaluacionSucursal> evaluacionSucursalCall = service.verificarEvaluacionUsuario(idCliente,sucursal.getSucursal().getId());
        evaluacionSucursalCall.enqueue(new Callback<EvaluacionSucursal>() {
            @Override
            public void onResponse(Call<EvaluacionSucursal> call, Response<EvaluacionSucursal> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getResults().isEmpty()) {
                        Toast.makeText(getActivity(), "Usted ya ha calificado esta sucursal. Muchas gracias!", Toast.LENGTH_SHORT).show();
                    } else {
                        EvaluacionSucursal evaluacionSucursal = new EvaluacionSucursal(idCliente,sucursal.getSucursal().getId(), rating);
                        Call<EvaluacionSucursal> crearEvaluacionCall = service.crearEvaluacion(evaluacionSucursal);
                        crearEvaluacionCall.enqueue(new Callback<EvaluacionSucursal>() {
                            @Override
                            public void onResponse(Call<EvaluacionSucursal> call, Response<EvaluacionSucursal> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Calificación guardada. Muchas gracias!", Toast.LENGTH_SHORT).show();
                                    Call<PromedioEvaluacion> promedioEvaluacionCall = service.calcularPromedioSucursal(sucursal.getSucursal().getId());
                                    promedioEvaluacionCall.enqueue(new Callback<PromedioEvaluacion>() {
                                        @Override
                                        public void onResponse(Call<PromedioEvaluacion> call, Response<PromedioEvaluacion> response) {
                                            if (response.isSuccessful()) {
                                            } else {
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<PromedioEvaluacion> call, Throwable t) {
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "No pudimos guardar su calificación. Intente de nuevo", Toast.LENGTH_SHORT).show();
                                }
                                rbClubCalificacion.setEnabled(true);
                                rbClubCalificacion.setClickable(true);
                            }

                            @Override
                            public void onFailure(Call<EvaluacionSucursal> call, Throwable t) {
                                rbClubCalificacion.setEnabled(true);
                                rbClubCalificacion.setClickable(true);
                                if (!InternetConnectionStatus.isConnected(getContext())) {
                                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                } else if(t instanceof SocketTimeoutException) {
                                    Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                rbClubCalificacion.setEnabled(true);
                rbClubCalificacion.setClickable(true);
            }

            @Override
            public void onFailure(Call<EvaluacionSucursal> call, Throwable t) {
                rbClubCalificacion.setEnabled(true);
                if (!InternetConnectionStatus.isConnected(getContext())) {
                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                } else if(t instanceof SocketTimeoutException) {
                    Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void recolorear(double star) {
        star1.setImageResource(R.drawable.star_bg);
        star2.setImageResource(R.drawable.star_bg);
        star3.setImageResource(R.drawable.star_bg);
        star4.setImageResource(R.drawable.star_bg);
        star5.setImageResource(R.drawable.star_bg);
        if (star==0) {
            /*
            No stars
             */
        } else if (star<1) {
            star1.setImageResource(R.drawable.star_half);
        } else if (star<1.5) {
            star1.setImageResource(R.drawable.star_on);
        } else if (star<2.0) {
            star1.setImageResource(R.drawable.star_on);
            star2.setImageResource(R.drawable.star_half);
        } else if (star<2.5) {
            star1.setImageResource(R.drawable.star_on);
            star2.setImageResource(R.drawable.star_on);
        } else if (star<3) {
            star1.setImageResource(R.drawable.star_on);
            star2.setImageResource(R.drawable.star_on);
            star3.setImageResource(R.drawable.star_half);
        } else if (star<3.5) {
            star1.setImageResource(R.drawable.star_on);
            star2.setImageResource(R.drawable.star_on);
            star3.setImageResource(R.drawable.star_on);
        } else if (star<4) {
            star1.setImageResource(R.drawable.star_on);
            star2.setImageResource(R.drawable.star_on);
            star3.setImageResource(R.drawable.star_on);
            star4.setImageResource(R.drawable.star_half);
        } else if (star<4.5) {
            star1.setImageResource(R.drawable.star_on);
            star2.setImageResource(R.drawable.star_on);
            star3.setImageResource(R.drawable.star_on);
            star4.setImageResource(R.drawable.star_on);
        } else if (star<5) {
            star1.setImageResource(R.drawable.star_on);
            star2.setImageResource(R.drawable.star_on);
            star3.setImageResource(R.drawable.star_on);
            star4.setImageResource(R.drawable.star_on);
            star5.setImageResource(R.drawable.star_half);
        } else if (star==5) {
            star1.setImageResource(R.drawable.star_on);
            star2.setImageResource(R.drawable.star_on);
            star3.setImageResource(R.drawable.star_on);
            star4.setImageResource(R.drawable.star_on);
            star5.setImageResource(R.drawable.star_on);
        }
//        star1.setImageResource(R.drawable.star_half);
//        star2.setImageResource(R.drawable.star_half);
//        star3.setImageResource(R.drawable.star_half);
//        star4.setImageResource(R.drawable.star_half);
//        star5.setImageResource(R.drawable.star_half);
    }


    private void obtenerActividades(Sucursal sucursal) {
        Intent intent = new Intent(getContext(), ObtenerActividades.class);
        intent.putExtra(Variables.CLUBDETALLE, sucursal);
        getContext().startService(intent);
        // Llamada al api para obtener las actividades de la sucursal
//        Call<ActividadClub> actividadClubCall = service.obtenerActividades(sucursal.getId());
//        actividadClubCall.enqueue(new Callback<ActividadClub>() {
//            @Override
//            public void onResponse(Call<ActividadClub> call, Response<ActividadClub> response) {
//                if (response.isSuccessful()) {
//                    if (!response.body().getResults().isEmpty()) {
//                        for(ActividadClub actividad : response.body().getResults()) {
//                            Call<Actividad> actividadCall = service.obtenerActividad(actividad.getActividad());
//                            actividadCall.enqueue(new Callback<Actividad>() {
//                                @Override
//                                public void onResponse(Call<Actividad> call, Response<Actividad> response) {
//                                    if (response.isSuccessful()) {
//                                        Actividad actividad = response.body();
//                                        Toast.makeText(getActivity(), ""+actividad.getNombre(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<Actividad> call, Throwable t) {
//
//                                }
//                            });
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ActividadClub> call, Throwable t) {
//
//            }
//        });
    }

    private void obtenerServicios(SucursalFull sucursal) {

        //Llama al API para obtener los servicios
//        Call<ServicioClub> servicioCall = service.obtenerServicios(sucursal.getId());
//        servicioCall.enqueue(new Callback<ServicioClub>() {
//            @Override
//            public void onResponse(Call<ServicioClub> call, Response<ServicioClub> response) {
//                if (response.isSuccessful()) {
//                    if (!response.body().getResults().isEmpty()) {
//                        ArrayList<ServicioClub> servicios = response.body().getResults();
//                        for (ServicioClub servicio : servicios ) {
//                            Call<Servicio> servicioCall2 = service.obtenerServicio(servicio.getServicio());
//                            servicioCall2.enqueue(new Callback<Servicio>() {
//                                @Override
//                                public void onResponse(Call<Servicio> call, Response<Servicio> response) {
//                                    if (response.isSuccessful()) {
//                                        adapter.add(response.body());
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<Servicio> call, Throwable t) {
//                                    if (!InternetConnectionStatus.isConnected(getContext())) {
//                                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                    } else if(t instanceof SocketTimeoutException) {
//                                        Toast.makeText(getContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//                    } else {
//                        ArrayList<Servicio> servicios = new ArrayList<>();
//                        Servicio servicio = new Servicio("Sin servicios",Variables.HOST+ Variables.MEDIA_URL+"default/servicio.png");
//                        servicios.add(servicio);
//                        adapter.update(servicios);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ServicioClub> call, Throwable t) {
//                if (!InternetConnectionStatus.isConnected(getContext())) {
//                    Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                } else if(t instanceof SocketTimeoutException) {
//                    Toast.makeText(getContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_club_detalle,container,false);
    }

    public void obtenerDireccion(Sucursal sucursal) {
        //Llamada al API para obtener una ciudad
        Call<Ciudad> ciudadCall = service.obtenerCiudad(sucursal.getCiudad());
        ciudadCall.enqueue(new Callback<Ciudad>() {
            @Override
            public void onResponse(Call<Ciudad> call, Response<Ciudad> response) {
                if (response.isSuccessful()) {
                    Ciudad ciudad = response.body();
                    //Llamada al API para obtener un estado
                    Call<Estado> estadoCall = service.obtenerEstado(sucursal.getEstado());
                    estadoCall.enqueue(new Callback<Estado>() {
                        @Override
                        public void onResponse(Call<Estado> call, Response<Estado> response) {
                            if (response.isSuccessful()) {
                                Estado estado = response.body();
                                if (sucursal.getNumExt().equals("")) {
                                    tvClubDireccion.setText(sucursal.getCalle()+" "+sucursal.getNumInt()+" "+sucursal.getColonia()+" "+ciudad.getNombre()+", "+estado.getNombre());
                                } else {
                                    tvClubDireccion.setText(sucursal.getCalle()+" "+sucursal.getNumExt()+" "+sucursal.getColonia()+" "+ciudad.getNombre()+", "+estado.getNombre());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Estado> call, Throwable t) {
                            if (!InternetConnectionStatus.isConnected(getContext())) {
                                Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            } else if(t instanceof SocketTimeoutException) {
                                Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Ciudad> call, Throwable t) {
                if (!InternetConnectionStatus.isConnected(getContext())) {
                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                } else if(t instanceof SocketTimeoutException) {
                    Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                }
            }
        });
        ivIconoFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvFavoritoValue.getText().equals("true")) {
                    int favorito_id = Integer.parseInt(tvFavoritoId.getText().toString());
                    Call<ResponseBody> favoritoCall = service.removerFavorito(favorito_id);
                    favoritoCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getActivity(), "Removido de favoritos", Toast.LENGTH_SHORT).show();
                                Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.icono_favoritos_inactivo).into(ivIconoFavoritos);
                                tvFavoritoValue.setText("false");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                } else {
                    int cliente_id = sharedPreferences.getInt(Variables.CLIENTEID, 0);
                    Call<Favorito> favoritoCall = service.agregarFavorito(cliente_id, sucursal.getId());
                    favoritoCall.enqueue(new Callback<Favorito>() {
                        @Override
                        public void onResponse(Call<Favorito> call, Response<Favorito> response) {
                            if (response.isSuccessful()) {
                                Favorito favorito = response.body();
                                Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.icono_favoritos_activo).into(ivIconoFavoritos);
                                tvFavoritoValue.setText("true");
                                tvFavoritoId.setText(favorito.getId() + "");
                                Toast.makeText(getActivity(), "Agregado a favoritos", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Favorito> call, Throwable t) {

                        }
                    });
                }
            }
        });
    }

//    private void obtenerInformacionClub(Sucursal sucursal) {
//        // Llamada al API para obtener la información del club al que pertenece la sucursal
//        //Consultar club por sucursal
//        Call<Club> clubCall = service.obtenerClub(sucursal.getClub(),true);
//        clubCall.enqueue(new Callback<Club>() {
//            @Override
//            public void onResponse(Call<Club> call, Response<Club> response) {
//                if (response.isSuccessful()) {
//                    Club club = response.body();
//                    tvClubPrecioDia.setText(sucursal.getDia()+" MXN");
//                    tvClubPrecioMes.setText(sucursal.getMensualidad()+" MXN");
////                    tvClubHorario.setText(sucursal.getHorario().getLunesA().substring(0,5)+"-"+sucursal.getHorario().getLunesC().substring(0,5));
//                    //Remover saltos de línea
////                    String str = club.getHorario();
////                    if (str!=null) {
////                        str = str.replaceAll("(\\r|\\n)","  ");
////                        tvClubHorario.setText(str);
////                    }
//                    btnPlanDiario.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                            if (fragmentManager.findFragmentByTag("SUSCRIPCIONDIARIA")==null) {
//                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);/
//                                fragmentTransaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down);
//                                //Pasamos un objeto clubDetalle con toda la información hacia el siguiente fragmento.
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable(Variables.SUCURSALDETALLE, sucursal);
//                                suscripcionDiaria.setArguments(bundle);
//                                fragmentTransaction.add(R.id.fragmentContainer, suscripcionDiaria, "SUSCRIPCIONDIARIA").addToBackStack(null).commit();
//                            }
//                        }
//                    });
//                    btnPlanMensual.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                            if (fragmentManager.findFragmentByTag("SUSCRIPCIONMENSUAL")==null) {
//                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                fragmentTransaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down);
//                                //Pasamos un objeto clubDetalle con toda la información hacia el siguiente fragmento.
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable(Variables.SUCURSALDETALLE,sucursal);
//                                suscripcionMensual.setArguments(bundle);
//                                fragmentTransaction.add(R.id.fragmentContainer, suscripcionMensual, "SUSCRIPCIONMENSUAL").addToBackStack(null).commit();
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Club> call, Throwable t) {
//                if (!InternetConnectionStatus.isConnected(getContext())) {
//                    Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                } else if(t instanceof SocketTimeoutException) {
//                    Toast.makeText(getContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    public void startLoadingAnimation() {
        alertDialog = new Dialog(getContext());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.setContentView(R.layout.loading_layout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        ImageView imageView = (ImageView) alertDialog.findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        imageView.startAnimation(animation);
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

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }
}
