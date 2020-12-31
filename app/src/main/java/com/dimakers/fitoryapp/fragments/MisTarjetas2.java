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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.adapters.Tarjetas;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.MetodosPago;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.Calendar;

import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Token;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisTarjetas2 extends Fragment {
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    RecyclerView rvTarjetas;
    Tarjetas adapter;
    Button btnAgregarTarjeta;
    EditText etTarjetaNumero, etTarjetaMes, etTarjetaVencimiento, etTarjetaCVV;
    Dialog alertDialog;

    public static MisTarjetas2 newInstance(){
        MisTarjetas2 tarjetas = new MisTarjetas2();
        return tarjetas;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        rvTarjetas = view.findViewById(R.id.rv_tarjetas);
        etTarjetaNumero = view.findViewById(R.id.et_numero_tarjeta);
        etTarjetaMes = view.findViewById(R.id.et_tarjeta_mes);
        etTarjetaVencimiento = view.findViewById(R.id.et_tarjeta_vencimiento);
        etTarjetaCVV = view.findViewById(R.id.et_tarjeta_cvv);
        adapter = new Tarjetas(getContext());
        rvTarjetas.setAdapter(adapter);
        rvTarjetas.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        rvTarjetas.setLayoutManager(layoutManager);
        //Iniciar Conekta
//        Conekta.setPublicKey("key_OsuzVa6QqHW1wF3Zz8Nmbvg");
        Conekta.setPublicKey("key_UfnLiLkr9SquQVHEKco7MLA");
        Conekta.collectDevice(getActivity());
        //Llamada al api para obtener métodos de pago
        if (Variables.metodos.isEmpty()) {
            obtenerMetodosPago();
        } else {
            adapter.clear();
            adapter.update(Variables.metodos);
        }
        btnAgregarTarjeta = view.findViewById(R.id.btn_agregar_tarjeta);
        btnAgregarTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                key_IfxhovXydzXZzHPzxBVC5Ew
                if (!isOnline()) {
                    Toast.makeText(getActivity(), "Necesita una conexión a Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                btnAgregarTarjeta.setEnabled(false);
                String nombreCliente = sharedPreferences.getString(Variables.CLIENTENOMBRE,"");
                String apellidoCliente = sharedPreferences.getString(Variables.CLIENTEAPELLIDO, "");
                if (TextUtils.isEmpty(etTarjetaNumero.getText()) ||
                        TextUtils.isEmpty(etTarjetaCVV.getText()) ||
                        TextUtils.isEmpty(etTarjetaMes.getText()) ||
                        TextUtils.isEmpty(etTarjetaVencimiento.getText())
                        ) {
                    Toast.makeText(getActivity(), "Asegúrese de llenar todos los campos de la tarjeta", Toast.LENGTH_SHORT).show();
                    btnAgregarTarjeta.setEnabled(true);
                    return;
                }
                if (etTarjetaNumero.getText().length()<15) {
                    Toast.makeText(getActivity(), "Introduzca un número de tarjeta válido", Toast.LENGTH_SHORT).show();
                    btnAgregarTarjeta.setEnabled(true);
                    return;
                }
                if (etTarjetaVencimiento.length()<4 || vencimientoInvalido()) {
                    Toast.makeText(getActivity(), "Introduzca una fecha de vencimiento válida", Toast.LENGTH_SHORT).show();
                    btnAgregarTarjeta.setEnabled(true);
                    return;
                }
                if (etTarjetaCVV.getText().length()<3) {
                    Toast.makeText(getActivity(), "Introduzca un código de seguridad válido", Toast.LENGTH_SHORT).show();
                    btnAgregarTarjeta.setEnabled(true);
                    return;
                }
                try {
                    Card card = new Card(nombreCliente+apellidoCliente,
                            etTarjetaNumero.getText().toString().trim(),
                            etTarjetaCVV.getText().toString().trim(),
                            etTarjetaMes.getText().toString().trim(),
                            etTarjetaVencimiento.getText().toString().trim());
                    Token token = new Token(getActivity());
                    token.onCreateTokenListener(new Token.CreateToken() {
                        @Override
                        public void onCreateTokenReady(JSONObject data) {
                            try {
                                String token = data.getString("id");
                                int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
                                if (idCliente==0) {
                                    Toast.makeText(getActivity(), "No se encontró id cliente", Toast.LENGTH_SHORT).show();
                                    btnAgregarTarjeta.setEnabled(true);
                                } else {
                                    Call <ResponseBody> metodoCall = service.añadirMetodoPagoConekta(String.valueOf(idCliente),token);
                                    startLoadingAnimation();
                                    metodoCall.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            alertDialog.dismiss();
                                            if (response.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Tarjeta agregada correctamente", Toast.LENGTH_SHORT).show();
                                                etTarjetaCVV.getText().clear();
                                                etTarjetaMes.getText().clear();
                                                etTarjetaNumero.getText().clear();
                                                etTarjetaVencimiento.getText().clear();
                                                obtenerMetodosPago();
                                                btnAgregarTarjeta.setEnabled(true);
                                                getActivity().getSupportFragmentManager().popBackStack();
                                            } else {
                                                Toast.makeText(getActivity(), "No se pudo agregar la tarjeta. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show();
                                                btnAgregarTarjeta.setEnabled(true);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            alertDialog.dismiss();
                                            if (!InternetConnectionStatus.isConnected(getActivity())) {
                                                Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                                metodoCall.cancel();
                                            } else if(t instanceof SocketTimeoutException) {
                                                Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                                metodoCall.cancel();
                                            }
                                            btnAgregarTarjeta.setEnabled(true);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "No se pudo añadir la tarjeta. Revise la información de la tarjeta. ", Toast.LENGTH_SHORT).show();
                                btnAgregarTarjeta.setEnabled(true);
                            }

                        }
                    });
                    token.create(card);
                } catch (Exception e) {
                    btnAgregarTarjeta.setEnabled(true);
                    String message = "";
                    switch (e.getMessage()) {
                        case "expMonth":
                            message = "Número de expiración no válido";
                            break;
                        case "expYear":
                            message = "Número de expiración no válido";
                            default:
                                message = "Algún dato de la tarjeta es inválido";
                    }
                    Toast.makeText(getActivity(), "Error al crear tarjeta. "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_tarjetas,container,false);
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
                            adapter.clear();
                            Variables.metodos.addAll(response.body().getMetodos());
                            adapter.update(response.body().getMetodos());
                        } else {
//                            Toast.makeText(getActivity(), ""+response.body().getError(), Toast.LENGTH_SHORT).show();
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

    private boolean vencimientoInvalido() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int vencimiento = Integer.parseInt(etTarjetaVencimiento.getText().toString());
        if (year<=vencimiento) {
            return false;
        }
        return true;
    }
}
