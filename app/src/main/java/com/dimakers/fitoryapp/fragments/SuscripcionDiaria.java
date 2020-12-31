package com.dimakers.fitoryapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.activities.ClubMain;
import com.dimakers.fitoryapp.activities.Login;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.ActivarSubscripcionFree;
import com.dimakers.fitoryapp.api.models.Cliente;
import com.dimakers.fitoryapp.api.models.Club;
import com.dimakers.fitoryapp.api.models.Fecha;
import com.dimakers.fitoryapp.api.models.Metodo;
import com.dimakers.fitoryapp.api.models.MetodosPago;
import com.dimakers.fitoryapp.api.models.RevisionSubscripcionFree;
import com.dimakers.fitoryapp.api.models.Sesion;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.SucursalFull;
import com.dimakers.fitoryapp.api.models.UpdatePhoneResponse;
import com.dimakers.fitoryapp.api.models.VerifyPhoneResponse;
import com.onesignal.OSNotification;
import com.onesignal.OSPermissionObserver;
import com.onesignal.OSPermissionStateChanges;
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
import java.time.ZonedDateTime;
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

public class SuscripcionDiaria extends Fragment implements FragmentManager.OnBackStackChangedListener, OSSubscriptionObserver {
    FitoryService service = API.getApi().create(FitoryService.class);
    Button btnAgregarTarjeta;
//    EditText etTarjetaNumero, etTarjetaMes, etTarjetaVencimiento, etTarjetaCVV;
//    TextView tvClubPrecioDia;
    AlertDialog.Builder builder2;
    AlertDialog alertDialog2;
    AlertDialog.Builder builderV;
    AlertDialog alertDialogV;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LinearLayout radioButton11,radioButton12, radioButton21, radioButton22, radioButton31, radioButton32, radioButton41, radioButton42;
    Spinner spinnerTarjetas;
    ArrayList<Metodo> metodos = new ArrayList<>();
    ArrayAdapter spinnerAdapter, añadirAdapter;
    Sucursal sucursal;
    FragmentManager fragmentManager;
    RadioGroup rgSesiones;
    Button btnPagar;
    String fecha;
    RadioButton rbPaquete1, rbPaquete2, rbPaquete3, rbPaquete4;
    EditText etCodigoCelular;
    int diasDisponibles;
    int diasRestantes;
    boolean btnEnabled = false;
    Dialog alertDialog;
    TextView tvPaquete1Precio,tvPaquete2Precio,tvPaquete3Precio, tvPaquete4Precio, tvDiasPrueba;
    int clienteID;

    public static SuscripcionDiaria newInstance() {
        SuscripcionDiaria fragment = new SuscripcionDiaria();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        tvDiasPrueba = view.findViewById(R.id.tv_dias_prueba);
        rgSesiones = view.findViewById(R.id.rg_sesiones);
        rbPaquete1 = view.findViewById(R.id.rb_paquete1);
        rbPaquete2 = view.findViewById(R.id.rb_paquete2);
        rbPaquete3 = view.findViewById(R.id.rb_paquete3);
        rbPaquete4 = view.findViewById(R.id.rb_paquete4);
        tvPaquete1Precio = view.findViewById(R.id.tv_paquete1_precio);
        tvPaquete2Precio = view.findViewById(R.id.tv_paquete2_precio);
        tvPaquete3Precio = view.findViewById(R.id.tv_paquete3_precio);
        tvPaquete4Precio = view.findViewById(R.id.tv_paquete4_precio);
        radioButton11 = view.findViewById(R.id.radio_button_1_1);
        radioButton12 = view.findViewById(R.id.radio_button_1_2);
        radioButton21 = view.findViewById(R.id.radio_button_2_1);
        radioButton22 = view.findViewById(R.id.radio_button_2_2);
        radioButton31 = view.findViewById(R.id.radio_button_3_1);
        radioButton32 = view.findViewById(R.id.radio_button_3_2);
        radioButton41 = view.findViewById(R.id.radio_button_4_1);
        radioButton42 = view.findViewById(R.id.radio_button_4_2);
//        layoutCodigo = view.findViewById(R.id.layout_codigo);
//        etCodigoCelular = view.findViewById(R.id.et_codigo_celular);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        OneSignal.addSubscriptionObserver(this);
        rbPaquete1.toggle();
        radioButton11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbPaquete1.isEnabled()) {
                    rbPaquete1.performClick();
                }
            }
        });
        radioButton12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbPaquete1.isEnabled()) {
                    rbPaquete1.performClick();
                }
            }
        });
        radioButton21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbPaquete2.isEnabled()) {
                    rbPaquete2.performClick();
                }
            }
        });
        radioButton22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbPaquete2.isEnabled()) {
                    rbPaquete2.performClick();
                }
            }
        });
        radioButton31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbPaquete3.isEnabled()) {
                    rbPaquete3.performClick();
                }
            }
        });
        radioButton32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbPaquete3.isEnabled()) {
                    rbPaquete3.performClick();
                }
            }
        });
        rbPaquete4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String celular = sharedPreferences.getString(Variables.TELEFONO,"");
                if (celular.equals("") || celular.equals(' ') || celular.equals("...") || celular == null) {
                    Toast.makeText(getContext(), "Por favor ingrese su número celular antes de continuar con la activación del paquete", Toast.LENGTH_SHORT).show();
                    checkPhone();
                }
            }
        });
        radioButton41.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String celular = sharedPreferences.getString(Variables.TELEFONO,"");
                if (celular.equals("") || celular.equals(' ') || celular.equals("...") || celular == null) {
                    Toast.makeText(getContext(), "Por favor ingrese su número celular antes de continuar con la activación del paquete", Toast.LENGTH_SHORT).show();
                    checkPhone();
                    return;
                }
                if (rbPaquete4.isEnabled()) {
                    rbPaquete4.performClick();
                }
            }
        });
        radioButton42.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String celular = sharedPreferences.getString(Variables.TELEFONO,"");
                if (celular.equals("") || celular.equals(' ') || celular.equals("...") || celular == null) {
                    Toast.makeText(getContext(), "Por favor ingrese su número celular antes de continuar con la activación del paquete", Toast.LENGTH_SHORT).show();
                    checkPhone();
                    return;
                }
                if (rbPaquete4.isEnabled()) {
                    rbPaquete4.performClick();
                }
            }
        });
        //Obtenemos el objeto ClubDetalle que se envió desde el fragmento club detalle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sucursal = (Sucursal) bundle.getSerializable(Variables.SUCURSALDETALLE);
        } else {
            Toast.makeText(getActivity(), "Sin precio de club", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }
        //Iniciar Conekta
//        Conekta.setPublicKey("key_OsuzVa6QqHW1wF3Zz8Nmbvg");
        Conekta.setPublicKey("key_UfnLiLkr9SquQVHEKco7MLA");
        Conekta.collectDevice(getActivity());
        tvPaquete1Precio.setText("$"+round(Double.parseDouble(sucursal.getDia())*4,4)+"MXN");
        tvPaquete2Precio.setText("$"+round(Double.parseDouble(sucursal.getDia())*8,2)+"MXN");
        tvPaquete3Precio.setText("$"+round(Double.parseDouble(sucursal.getDia())*12,2)+"MXN");

//        etTarjetaNumero = view.findViewById(R.id.et_numero_tarjeta);
//        etTarjetaMes = view.findViewById(R.id.et_tarjeta_mes);
//        etTarjetaVencimiento = view.findViewById(R.id.et_tarjeta_vencimiento);
//        etTarjetaCVV = view.findViewById(R.id.et_tarjeta_cvv);
        spinnerTarjetas = view.findViewById(R.id.spinner_tarjetas);
        spinnerAdapter =  new ArrayAdapter(getActivity(),R.layout.support_simple_spinner_dropdown_item,metodos);
//        tvClubPrecioDia = view.findViewById(R.id.tv_club_precio_dia);
//        tvClubPrecioDia.setText("$"+sucursal.getDia()+" MXN");
        añadirAdapter =  new ArrayAdapter(getActivity(),R.layout.support_simple_spinner_dropdown_item,metodos);
        btnPagar = view.findViewById(R.id.pagar_button);
//        btnActivar = view.findViewById(R.id.activar_button);

//        if (diasDisponibles == 0 || diasRestantes == 0) {
////            getFragmentManager().popBackStack();
//            btnPagar.setEnabled(false);
//        } else {
//            btnPagar.setEnabled(true);
//        }

        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String celular = sharedPreferences.getString(Variables.TELEFONO,"");
                if (celular.equals("") || celular.equals(' ') || celular.equals("...") || celular == null) {
                    Toast.makeText(getContext(), "Por favor ingrese su número celular antes de continuar con la activación del paquete", Toast.LENGTH_SHORT).show();
                    checkPhone();
                    return;
                }
                btnPagar.setEnabled(false);
                int clienteID = sharedPreferences.getInt(Variables.CLIENTEID,0);
                int sucursalID = sucursal.getId();
                if (sucursalID == 0 || clienteID == 0) {
                    Toast.makeText(getContext(), "No se puede continuar con el pago.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rbPaquete4.isChecked()) {
                    AlertDialog.Builder builderP = new AlertDialog.Builder(getActivity());
                    builderP.setTitle("Activar promoción");
                    builderP.setCancelable(false);
                    builderP.setMessage("Esta promoción sólo será válida a los días habilitados por la sucursal");
                    builderP.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            btnPagar.setEnabled(true);
                        }
                    });
                    builderP.setPositiveButton("ACTIVAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Llamada a api
                            int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
                            if (idCliente==0) {
                                Toast.makeText(getActivity(), "Error al activar el paquete", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Call<ActivarSubscripcionFree> activarSubscripcionFreeCall = service.activarSubscripcionFree(idCliente, sucursal.getId());
                            activarSubscripcionFreeCall.enqueue(new Callback<ActivarSubscripcionFree>() {
                                @Override
                                public void onResponse(Call<ActivarSubscripcionFree> call, Response<ActivarSubscripcionFree> response) {
                                    if (response.isSuccessful()) {
                                        ActivarSubscripcionFree activarSubscripcionFree = response.body();
                                        rbPaquete1.setChecked(true);
                                        Toast.makeText(getContext(), activarSubscripcionFree.getMsg(), Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ActivarSubscripcionFree> call, Throwable t) {
                                    if (!InternetConnectionStatus.isConnected(getActivity())) {
                                        Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    } else if(t instanceof SocketTimeoutException) {
                                        Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.administrador), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            btnPagar.setEnabled(true);
                        }
                    });
                    AlertDialog alertDialogP = builderP.create();
                    alertDialogP.show();
                } else {
                    alertDialog = new Dialog(getContext());
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setCancelable(false);
                    alertDialog.setContentView(R.layout.loading_layout);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                    ImageView imageView = (ImageView) alertDialog.findViewById(R.id.imageView);
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
                    imageView.startAnimation(animation);
                    if (btnEnabled) {
                        Metodo metodo = (Metodo) spinnerTarjetas.getSelectedItem();
                        if (rbPaquete1.isChecked()) {
//                        Toast.makeText(getContext(), "paq1 "+metodo.getId(), Toast.LENGTH_SHORT).show();
                            cobrarPorSesion(clienteID,sucursalID,metodo.getId(),4);
                        } else if (rbPaquete2.isChecked()) {
//                        Toast.makeText(getContext(), "paq2"+metodo.getId(), Toast.LENGTH_SHORT).show();
                            cobrarPorSesion(clienteID,sucursalID,metodo.getId(),8);

                        } else if (rbPaquete3.isChecked()) {
//                        Toast.makeText(getContext(), "paq3"+metodo.getId(), Toast.LENGTH_SHORT).show();
                            cobrarPorSesion(clienteID,sucursalID,metodo.getId(),12);

                        } else {
                            Toast.makeText(getContext(), "Para continuar con la compra elija un paquete.", Toast.LENGTH_SHORT).show();
                            btnPagar.setEnabled(true);
                            alertDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(getContext(), "No tiene métodos de pago registrados.", Toast.LENGTH_SHORT).show();
                        btnPagar.setEnabled(true);
                        alertDialog.dismiss();
                    }
                }

//                Fragment suscripcionDiaria = new SuscripcionDiariaDetalle();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);
//                fragmentTransaction.add(R.id.fragmentContainer, suscripcionDiaria, "SUSCRIPCIONDIARIADETALLE").addToBackStack(null).commit();
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
//                                    Call <ResponseBody> metodoCall = service.añadirMetodoPagoConekta(String.valueOf(idCliente),token);
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
//                            default:
//                                message = "Algún dato de la tarjeta es inválido";
//                    }
//                    Toast.makeText(getActivity(), "Error al crear tarjeta. "+message, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        //Llamada al api para obtener métodos de pago
        obtenerMetodosPago();

        //Llamada al api para verificar si aplica la prompción gratuita
        clienteID = sharedPreferences.getInt(Variables.CLIENTEID,0);
        if (clienteID == 0) {
            return;
        }
        verificarPromocion(clienteID,sucursal.getId());
    }

    private void checkPhone() {
        builder2 = new AlertDialog.Builder(getContext());
        builder2.setView(R.layout.dialog_actualizar_telefono);
        builder2.setCancelable(false);
        alertDialog2 = builder2.create();
        if (!alertDialog2.isShowing()) {
            alertDialog2.show();
            ImageView btnClose = alertDialog2.findViewById(R.id.btn_close);
            Button btnGuardarTelefono = alertDialog2.findViewById(R.id.btn_actualizar_telefono);
            EditText etTelefono = alertDialog2.findViewById(R.id.et_telefono);

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeAlertDialogV();
                    closeAlertDialog();
                }
            });
            btnGuardarTelefono.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(etTelefono.getText())) {
                        etTelefono.setError("Ingrese su teléfono celular");
                        etTelefono.requestFocus();
                        return;
                    }

                    if (etTelefono.getText().toString().trim().length()<10 || !TextUtils.isDigitsOnly(etTelefono.getText().toString().trim())) {
                        etTelefono.setError("Ingrese un teléfono celular válido");
                        etTelefono.requestFocus();
                        return;
                    }
                    int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
                    if (idCliente!=0) {
                        updatePhone(etTelefono.getText().toString().trim(), idCliente);
                    } else {
                        closeAlertDialogV();
                        closeAlertDialog();
                    }
                }
            });

        }
    }

    public void closeAlertDialog() {
        if (alertDialog2!=null) {
            if (alertDialog2.isShowing()) {
                alertDialog2.dismiss();
            }
        }
    }

    public void closeAlertDialogV() {
        if (alertDialogV!=null) {
            if (alertDialogV.isShowing()) {
                alertDialogV.dismiss();
            }
        }
    }

    private void updatePhone(String telefono, int idCliente) {
        Call<UpdatePhoneResponse> updatePhoneCall = service.updatePhone(telefono, idCliente);
        updatePhoneCall.enqueue(new Callback<UpdatePhoneResponse>() {
            @Override
            public void onResponse(Call<UpdatePhoneResponse> call, Response<UpdatePhoneResponse> response) {
                if (response.isSuccessful()) {
                    UpdatePhoneResponse updatePhoneResponse = response.body();
                    if (updatePhoneResponse.isSuccessful()) {
                        editor.putString(Variables.TELEFONO,updatePhoneResponse.getCelular());
                        editor.commit();
                        Toast.makeText(getContext(), ""+updatePhoneResponse.getMensaje(), Toast.LENGTH_SHORT).show();
                        closeAlertDialogV();
                        closeAlertDialog();
                        builderV = new AlertDialog.Builder(getContext());
                        builderV.setView(R.layout.dialog_validar_telefono);
                        builderV.setCancelable(false);
                        alertDialogV = builderV.create();
                        if (!alertDialogV.isShowing()) {
                            alertDialogV.show();
                        }
                        ImageView btnClose = alertDialogV.findViewById(R.id.btn_close);
                        Button btnValidarCodigo = alertDialogV.findViewById(R.id.btn_validar_codigo);
                        EditText etCodigo = alertDialogV.findViewById(R.id.et_codigo);
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                closeAlertDialogV();
                                closeAlertDialog();
                            }
                        });

                        btnValidarCodigo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (TextUtils.isEmpty(etCodigo.getText())) {
                                    etCodigo.setError("Ingrese su código de validación");
                                    etCodigo.requestFocus();
                                    return;
                                }
                                Call<VerifyPhoneResponse> verifyPhoneCall = service.verifyPhone(telefono, etCodigo.getText().toString().trim());
                                verifyPhoneCall.enqueue(new Callback<VerifyPhoneResponse>() {
                                    @Override
                                    public void onResponse(Call<VerifyPhoneResponse> call, Response<VerifyPhoneResponse> response) {
                                        if (response.isSuccessful()) {
                                            VerifyPhoneResponse verifyPhoneResponse = response.body();
                                            Toast.makeText(getContext(), ""+verifyPhoneResponse.getMensaje(), Toast.LENGTH_SHORT).show();
                                            if (verifyPhoneResponse.isSuccessful()) {
                                                closeAlertDialogV();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<VerifyPhoneResponse> call, Throwable t) {
                                        if (!InternetConnectionStatus.isConnected(getContext())) {
                                            Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                        } else if(t instanceof SocketTimeoutException) {
                                            Toast.makeText(getContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), ""+updatePhoneResponse.getMensaje(), Toast.LENGTH_SHORT).show();
                        closeAlertDialog();
                    }
                } else {
                    Toast.makeText(getContext(), "No se pudo actualizar el teléfono. Comuníquese con su administrador.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdatePhoneResponse> call, Throwable t) {
                closeAlertDialog();
                if (!InternetConnectionStatus.isConnected(getContext())) {
                    Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                } else if(t instanceof SocketTimeoutException) {
                    Toast.makeText(getContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verificarPromocion(int clienteID, int sucursalID) {
        Call<RevisionSubscripcionFree> revisionSubscripcionFreeCall = service.revisarSubscripcionFree(clienteID,sucursalID);
        revisionSubscripcionFreeCall.enqueue(new Callback<RevisionSubscripcionFree>() {
            @Override
            public void onResponse(Call<RevisionSubscripcionFree> call, Response<RevisionSubscripcionFree> response) {
                if (response.isSuccessful()) {
                    RevisionSubscripcionFree revisionSubscripcionFree = response.body();
                    if (revisionSubscripcionFree.isMostrar()) {
                        rbPaquete4.setVisibility(View.VISIBLE);
                        radioButton41.setVisibility(View.VISIBLE);
                        radioButton42.setVisibility(View.VISIBLE);
                        mostrarDiasPrueba(sucursalID);
                    }
                }
            }

            @Override
            public void onFailure(Call<RevisionSubscripcionFree> call, Throwable t) {

            }
        });
    }

    private void mostrarDiasPrueba(int idSucursal) {
        Call<Sucursal> sucursalCall = service.obtenerSucursal(idSucursal,true);
        sucursalCall.enqueue(new Callback<Sucursal>() {
            @Override
            public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                if (response.isSuccessful()) {
                    Sucursal sucursal = response.body();
                    tvDiasPrueba.setText(sucursal.getDiasPrueba()+tvDiasPrueba.getText().toString());
                }
            }

            @Override
            public void onFailure(Call<Sucursal> call, Throwable t) {

            }
        });
    }

    private void cobrarPorSesion(int clienteID, int sucursalID, String metodoPagoID, int numSesiones) {

        Call<ResponseBody> cobrarSesionCall = service.cobrarPorSesion(clienteID,sucursalID,metodoPagoID,numSesiones);
        cobrarSesionCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.body().string());
                        String errorMessage = jObjError.getString("error");
                        Toast.makeText(getContext(), ""+errorMessage, Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        btnPagar.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( stringToDate(fecha) );
//                        cal.setTime( stringToDate("2018-12-21") );
                        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                        if (diasRestantes<=7) {
                            cal.add(Calendar.MONTH,1);
                        }
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String reportDate = df.format(cal.getTime());
                        Sesion sesion = new Sesion();
                        sesion.setCliente(clienteID);
                        sesion.setSucursal(sucursalID);
                        double total = Double.parseDouble(sucursal.getDia())*numSesiones;
                        sesion.setTotal( total);
                        sesion.setSesiones(numSesiones);
                        sesion.setSesionesRestantes(numSesiones);
                        sesion.setCaducidad(reportDate);
                        sesion.setActivo(true);
                        Call<Sesion> sesionCall = service.crearSesion(sesion);
                        sesionCall.enqueue(new Callback<Sesion>() {
                            @Override
                            public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                                btnPagar.setEnabled(true);
                                if (response.isSuccessful()) {
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
                                } else {
                                    alertDialog.dismiss();
                                    Toast.makeText(getContext(), "Algo salió mal con su compra. Contacte a su administrador.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Sesion> call, Throwable t) {
                                Toast.makeText(getContext(), "Algo salió mal con su compra. Contacte a su administrador.", Toast.LENGTH_SHORT).show();
                                btnPagar.setEnabled(true);
                                alertDialog.dismiss();
                            }
                        });


//                        Call<Sesion> sesionCall = service.crearSesion();
                    }
                } else {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    btnPagar.setEnabled(true);
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
                btnPagar.setEnabled(true);
                alertDialog.dismiss();
            }
        });
    }

    private void updatePlayerId() {
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

    private boolean vencimientoInvalido() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
//        int vencimiento = Integer.parseInt(etTarjetaVencimiento.getText().toString());
//        if (year<vencimiento) {
//            return false;
//        }
        return true;
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
                            btnEnabled = true;
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
                                    btnEnabled = false;
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    spinnerTarjetas.setEnabled(true);
                                    if (fragmentManager.findFragmentByTag("TARJETAS") == null) {
                                        Fragment misTarjetas = new MisTarjetas2();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suscripcion_diaria, container, false);
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
    public void onBackStackChanged() {
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

    /**
     *  Validación para la compra de los paquetes de Fitory
     */
    public Calendar firstDayOfLastWeek(Calendar c)
    {
        c = (Calendar) c.clone();
        // last week
        c.set(Calendar.WEEK_OF_MONTH, c.getActualMaximum(Calendar.WEEK_OF_MONTH));
        // first day
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c;
    }

    public Calendar lastDayOfLastWeek(Calendar c)
    {
        c = (Calendar) c.clone();
        // last day
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c;
    }

    @Override
    public void onResume() {
        super.onResume();
        verificarPromocion(clienteID,sucursal.getId());
        metodos.clear();
        spinnerTarjetas.setAdapter(spinnerAdapter);
        alertDialog = new Dialog(getContext());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.setContentView(R.layout.loading_layout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        ImageView imageView = (ImageView) alertDialog.findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        imageView.startAnimation(animation);
        Call<ResponseBody> diasDisponiblesCall = service.obtenerDiasDisponibles(sucursal.getId());
        diasDisponiblesCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        diasDisponibles = jsonObject.getInt("diasDisponibles");
                        diasRestantes = jsonObject.getInt("diasRestantes");
                        Call<Fecha> fechaCall = service.consultarFecha();
                        fechaCall.enqueue(new Callback<Fecha>() {
                            @Override
                            public void onResponse(Call<Fecha> call, Response<Fecha> response) {
                                alertDialog.dismiss();
                                if (response.isSuccessful()) {
                                      fecha = response.body().getFecha();
//                                    Toast.makeText(getActivity(), "Dias disponibles: "+diasDisponibles, Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getActivity(), "Dias restantes: "+diasRestantes, Toast.LENGTH_SHORT).show();
                                    if (diasDisponibles<=4) {
                                        rbPaquete1.setEnabled(false);
                                        rbPaquete2.setEnabled(false);
                                        rbPaquete3.setEnabled(false);
                                    } else if (diasDisponibles<=8) {
                                        rbPaquete2.setEnabled(false);
                                        rbPaquete3.setEnabled(false);
                                    } else if (diasDisponibles<=12) {
                                        rbPaquete3.setEnabled(false);
                                    }

                                    if (diasRestantes<=7) {
                                        rbPaquete1.setEnabled(true);
                                        rbPaquete2.setEnabled(true);
                                        rbPaquete3.setEnabled(true);
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Unsuccesful", Toast.LENGTH_SHORT).show();
                                    getFragmentManager().popBackStack();
                                }
                            }

                            @Override
                            public void onFailure(Call<Fecha> call, Throwable t) {
                                Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack();
                            }
                        });
                    } catch (Exception e) {
                        alertDialog.dismiss();
                        getFragmentManager().popBackStack();
                        Toast.makeText(getContext(), "No se pudieron recuperar los paquetes de servicio. Intente de nuevo.", Toast.LENGTH_SHORT).show();
                    }
//                    Toast.makeText(getContext(), "Días de la semana:"+sucursal.getHorario().getNumDias(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getContext(), "Not succesful", Toast.LENGTH_SHORT).show();
                          alertDialog.dismiss();
                        getFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        updatePlayerId();
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

    @Override
    public void onStop() {
        super.onStop();
        OneSignal.addSubscriptionObserver(SuscripcionDiaria.this);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            Toast.makeText(getContext(), "Hello there!", Toast.LENGTH_SHORT).show();
        }
    }
}
