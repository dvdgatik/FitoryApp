package com.dimakers.fitoryapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Cliente;
import com.dimakers.fitoryapp.api.models.Club;
import com.dimakers.fitoryapp.api.models.Fecha;
import com.dimakers.fitoryapp.api.models.Metodo;
import com.dimakers.fitoryapp.api.models.MetodosPago;
import com.dimakers.fitoryapp.api.models.SubscripcionPojo;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.SucursalFull;
import com.dimakers.fitoryapp.api.models.Suscripcion;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Token;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuscripcionMensual extends Fragment implements FragmentManager.OnBackStackChangedListener, OSSubscriptionObserver {
    FitoryService service = API.getApi().create(FitoryService.class);
    Button btnAgregarTarjeta;
//    EditText etTarjetaNumero, etTarjetaMes, etTarjetaVencimiento, etTarjetaCVV;
    TextView tvClubPrecioMes;
    SharedPreferences sharedPreferences;
    Spinner spinnerTarjetas;
    ArrayList<Metodo> metodos = new ArrayList<>();
    ArrayAdapter spinnerAdapter, añadirAdapter;
    Sucursal sucursal;
    RelativeLayout tarjetaSpinner;
    FragmentManager fragmentManager;


    private boolean isSpinnerTouched = false;
    Dialog alertDialog;

    public static SuscripcionMensual newInstance() {
        SuscripcionMensual fragment = new SuscripcionMensual();
        return fragment;
    }

    Fragment misTarjetas = new MisTarjetas2();
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        //Obtenemos el objeto ClubDetalle que se envió desde el fragmento club detalle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
        sucursal = (Sucursal) bundle.getSerializable(Variables.SUCURSALDETALLE);
        } else {
            Toast.makeText(getActivity(), "Sin precio de club", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }
        sharedPreferences = getActivity().getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        OneSignal.addSubscriptionObserver(this);

        //Iniciar Conekta
//        Conekta.setPublicKey("key_OsuzVa6QqHW1wF3Zz8Nmbvg");
        Conekta.setPublicKey("key_UfnLiLkr9SquQVHEKco7MLA");
        Conekta.collectDevice(getActivity());
//        etTarjetaNumero = view.findViewById(R.id.et_numero_tarjeta);
//        etTarjetaMes = view.findViewById(R.id.et_tarjeta_mes);
//        etTarjetaVencimiento = view.findViewById(R.id.et_tarjeta_vencimiento);
//        etTarjetaCVV = view.findViewById(R.id.et_tarjeta_cvv);
        spinnerTarjetas = view.findViewById(R.id.spinner_tarjetas);
        tarjetaSpinner = view.findViewById(R.id.tarjeta_spinner);
//        spinnerTarjetas.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
//            }
//        });

        tvClubPrecioMes = view.findViewById(R.id.tv_club_precio_mes);
        tvClubPrecioMes.setText("$"+sucursal.getMensualidad()+" MXN");
        spinnerAdapter =  new ArrayAdapter(getActivity(),R.layout.support_simple_spinner_dropdown_item,metodos);
        añadirAdapter =  new ArrayAdapter(getActivity(),R.layout.support_simple_spinner_dropdown_item,metodos);
        Button buttonDetalle = (Button) view.findViewById(R.id.pagar_button);
        buttonDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Metodo metodo = ((Metodo)spinnerTarjetas.getSelectedItem());
                alertDialog = new Dialog(getContext());
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setCancelable(false);
                alertDialog.setContentView(R.layout.loading_layout);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                ImageView imageView = (ImageView) alertDialog.findViewById(R.id.imageView);
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
                imageView.startAnimation(animation);
                int clienteID = sharedPreferences.getInt(Variables.CLIENTEID,0);
                String direccion = metodo.getId();
                if (direccion == null || direccion.equals("")) {
                    Toast.makeText(getContext(), "No se seleccionó ningún método de pago", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    return;
                }
                if (clienteID == 0) {
                    Toast.makeText(getContext(), "No se encontró el ID de cliente", Toast.LENGTH_SHORT).show();
                } else {
                    Call<ResponseBody> subscripcionCall = service.registrarSubscripcion(clienteID,sucursal.getId(),direccion);
                    subscripcionCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.body().string());
                                    String errorMessage = "";
                                    switch (jObjError.getString("error")) {
                                        case "No se pudo crear la subscripcion.":
                                            errorMessage = "No se pudo crear la subscripción";
                                            break;
                                        case "No existe un plan registrado para esta sucursal.":
                                            errorMessage = "No existe un plan registrado para esta sucursal.";
                                            break;
                                        case "El cliente ya tiene una subscripcion.":
                                            errorMessage = "Usted ya cuenta con una subscripción.";
                                            break;
                                        case "El customer del cliente no tiene metodos de pago registrados.":
                                            errorMessage = "Usted no cuenta con métodos de pago registrados.";
                                            break;
                                        case "Usted no cuenta con un id de cliente.":
                                            errorMessage = "No existe un plan registrado para esta sucursal.";
                                            break;
                                            default:
                                                errorMessage = jObjError.getString("error");
                                    }
                                    Toast.makeText(getContext(), ""+errorMessage, Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "Felicidades! Tu compra se realizó con éxito.", Toast.LENGTH_SHORT).show();
                                    Variables.sesionFulls.clear();
                                    Variables.sesionFullsMes.clear();
                                    //OneSignal
                                    OneSignal.startInit(getContext())
                                            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                                            .unsubscribeWhenNotificationsAreDisabled(true)
                                            .init();
                                    OneSignal.setSubscription(true);
                                    updatePlayerId();
                                    Toast.makeText(getContext(), "Por favor, espere en lo que preparamos su compra. Esto puede demorar unos segundos.", Toast.LENGTH_LONG).show();
                                    final Thread thread = new Thread() {

                                        @Override
                                        public void run() {
                                            try {
                                                sleep(1*17000);
                                                getActivity().runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        alertDialog.dismiss();
                                                        getActivity().getSupportFragmentManager().popBackStack();
                                                    }
                                                });
                                            }
                                            catch (Exception ex) {
                                            }
                                        }
                                    };
                                    thread.start();
                                }
                            } else {
                                Toast.makeText(getContext(), "No se pudo realizar la suscripción", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    });
                }
//                Toast.makeText(getContext(), "Subscribirse: "+metodo.getId(), Toast.LENGTH_SHORT).show();

//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);
//
//                fragmentTransaction.add(R.id.fragmentContainer, suscripcionMensualDetalle, "SUSCRIPCIONMENSUALDETALLE").addToBackStack(null).commit();
            }
        });

//        btnAgregarTarjeta = view.findViewById(R.id.btn_agregar_tarjeta);
//        btnAgregarTarjeta.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                key_IfxhovXydzXZzHPzxBVC5Ew
//                if (!isOnline()) {
//                    Toast.makeText(getActivity(), "Necesita una conexión a Internet", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                btnAgregarTarjeta.setEnabled(false);
//                String nombreCliente = sharedPreferences.getString(Variables.CLIENTENOMBRE,"");
//                String apellidoCliente = sharedPreferences.getString(Variables.CLIENTEAPELLIDO, "");
//                if (TextUtils.isEmpty(etTarjetaNumero.getText()) ||
//                        TextUtils.isEmpty(etTarjetaCVV.getText()) ||
//                        TextUtils.isEmpty(etTarjetaMes.getText()) ||
//                        TextUtils.isEmpty(etTarjetaVencimiento.getText())
//                        ) {
//                    Toast.makeText(getActivity(), "Asegúrese de llenar todos los campos de la tarjeta", Toast.LENGTH_SHORT).show();
//                    btnAgregarTarjeta.setEnabled(true);
//                    return;
//                }
//                if (etTarjetaNumero.getText().length()<15) {
//                    Toast.makeText(getActivity(), "Introduza un número de tarjeta válido", Toast.LENGTH_SHORT).show();
//                    btnAgregarTarjeta.setEnabled(true);
//                    return;
//                }
//                if (etTarjetaVencimiento.length()<4 || vencimientoInvalido()) {
//                    Toast.makeText(getActivity(), "Introduza una fecha de vencimiento válida", Toast.LENGTH_SHORT).show();
//                    btnAgregarTarjeta.setEnabled(true);
//                    return;
//                }
//                if (etTarjetaCVV.getText().length()<3) {
//                    Toast.makeText(getActivity(), "Introduza un código de seguridad válido", Toast.LENGTH_SHORT).show();
//                    btnAgregarTarjeta.setEnabled(true);
//                    return;
//                }
//                try {
//                    Card card = new Card(nombreCliente+apellidoCliente,
//                            etTarjetaNumero.getText().toString().trim(),
//                            etTarjetaCVV.getText().toString().trim(),
//                            etTarjetaMes.getText().toString().trim(),
//                            etTarjetaVencimiento.getText().toString().trim());
//                    Token token = new Token(getActivity());
//                    token.onCreateTokenListener(new Token.CreateToken() {
//                        @Override
//                        public void onCreateTokenReady(JSONObject data) {
//                            try {
//                                String token = data.getString("id");
//                                int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
//                                if (idCliente==0) {
//                                    Toast.makeText(getActivity(), "No se encontró id cliente", Toast.LENGTH_SHORT).show();
//                                    btnAgregarTarjeta.setEnabled(true);
//                                } else {
//                                    Call<ResponseBody> metodoCall = service.añadirMetodoPagoConekta(String.valueOf(idCliente),token);
//                                    metodoCall.enqueue(new Callback<ResponseBody>() {
//                                        @Override
//                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                            if (response.isSuccessful()) {
//                                                Toast.makeText(getActivity(), "Tarjeta agregada correctamente", Toast.LENGTH_SHORT).show();
//                                                metodos.clear();
//                                                etTarjetaCVV.getText().clear();
//                                                etTarjetaMes.getText().clear();
//                                                etTarjetaNumero.getText().clear();
//                                                etTarjetaVencimiento.getText().clear();
//                                                spinnerTarjetas.setAdapter(spinnerAdapter);
//                                                obtenerMetodosPago();
//                                                btnAgregarTarjeta.setEnabled(true);
//                                            } else {
//                                                Toast.makeText(getActivity(), "No se pudo agregar la tarjeta. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show();
//                                                btnAgregarTarjeta.setEnabled(true);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                            if (!InternetConnectionStatus.isConnected(getActivity())) {
//                                                Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                metodoCall.cancel();
//                                            } else if(t instanceof SocketTimeoutException) {
//                                                Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                metodoCall.cancel();
//                                            }
//                                            btnAgregarTarjeta.setEnabled(true);
//                                        }
//                                    });
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Toast.makeText(getActivity(), "No se pudo añadir la tarjeta. Revise la información de la tarjeta. ", Toast.LENGTH_SHORT).show();
//                                btnAgregarTarjeta.setEnabled(true);
//                            }
//
//                        }
//                    });
//                    token.create(card);
//                } catch (Exception e) {
//                    btnAgregarTarjeta.setEnabled(true);
//                    String message = "";
//                    switch (e.getMessage()) {
//                        case "expMonth":
//                            message = "Número de expiración no válido";
//                            break;
//                        case "expYear":
//                            message = "Número de expiración no válido";
//                        default:
//                            message = "Algún dato de la tarjeta es inválido";
//                    }
//                    Toast.makeText(getActivity(), "Error al crear tarjeta. "+message, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        //Llamada al api para obtener métodos de pago
        obtenerMetodosPago();
    }

    private boolean vencimientoInvalido() {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int vencimiento = Integer.parseInt(etTarjetaVencimiento.getText().toString());
//        if (year<vencimiento) {
//            return false;
//        }
        return true;
    }

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

    public void obtenerMetodosPago() {
        int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
        if (idCliente == 0) {
            Toast.makeText(getActivity(), "No se encontró un cliente id", Toast.LENGTH_SHORT).show();
        } else {
            Call<MetodosPago> metodosPagoCall = service.obtenerMetodosPagoConekta(String.valueOf(idCliente));
            metodosPagoCall.enqueue(new Callback<MetodosPago>() {
                @Override
                public void onResponse(Call<MetodosPago> call, Response<MetodosPago> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError()==null) {
                            spinnerTarjetas.setEnabled(true);
                            metodos.clear();
                            metodos.addAll(response.body().getMetodos());
                            try {
                                spinnerTarjetas.setAdapter(spinnerAdapter);
                                spinnerTarjetas.setOnTouchListener(null);
                                } catch (Exception e) {
                                Log.i("FIT",e.getMessage());
                            }
                        } else {
                            metodos.clear();
                            metodos.add(new Metodo("Agregar","Tarjeta"));
                            spinnerTarjetas.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    spinnerTarjetas.setEnabled(false);
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    spinnerTarjetas.setEnabled(true);
                                    if (fragmentManager.findFragmentByTag("TARJETAS") == null) {
                                        fragmentTransaction.replace(R.id.fragmentContainer, misTarjetas, "TARJETAS").addToBackStack(null).commit();
                                    }
                                    return false;
                                }
                            });
                            spinnerTarjetas.setAdapter(añadirAdapter);
                        }

                    } else {
                        Toast.makeText(getActivity(), R.string.administrador, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MetodosPago> call, Throwable t) {
                    if (!InternetConnectionStatus.isConnected(getActivity())) {
                        Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        metodosPagoCall.cancel();
                    } else if(t instanceof SocketTimeoutException) {
                        Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                        metodosPagoCall.cancel();
                    }
                }
            });
        }
    }

    public boolean isOnline () {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suscripcion_mensual, container, false);
    }

    @Override
    public void onBackStackChanged() {
        //Llamada al api para obtener métodos de pago
//        obtenerMetodosPago();
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

    public void updatePlayerId(){
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        status.getPermissionStatus().getEnabled();
        String playerID = status.getSubscriptionStatus().getUserId();
//        Toast.makeText(getActivity(), "playerid: "+playerID, Toast.LENGTH_SHORT).show();
        if (playerID == "") {
            return;
        }
        int clienteID = sharedPreferences.getInt(Variables.CLIENTEID,0);
        if (clienteID == 0) {
            return;
        }
        Call<Cliente> clienteCall = service.actualizarPlayerID(clienteID, playerID);
        clienteCall.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful()) {
//                    Toast.makeText(getActivity(), "Se actualizó el playerID correctamente", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Sucedió un error al actualizar playerID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al actualizar playerID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        updatePlayerId();
    }

    @Override
    public void onStop() {
        super.onStop();
        OneSignal.addSubscriptionObserver(SuscripcionMensual.this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            Toast.makeText(getContext(), "Hello there!", Toast.LENGTH_SHORT).show();
        }
    }
}
