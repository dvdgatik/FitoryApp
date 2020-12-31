package com.dimakers.fitoryapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.ActivarSubscripcionFree;
import com.dimakers.fitoryapp.api.models.CheckPhoneResponse;
import com.dimakers.fitoryapp.api.models.EvaluacionSucursal;
import com.dimakers.fitoryapp.api.models.PromedioEvaluacion;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.UpdatePhoneResponse;
import com.dimakers.fitoryapp.api.models.VerifyPhoneResponse;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DiasPrueba extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FitoryService service = API.getApi().create(FitoryService.class);
    Sucursal sucursal;
    Button btnDiasGratis;
    AlertDialog.Builder builder2;
    AlertDialog alertDialog2;
    AlertDialog.Builder builderV;
    AlertDialog alertDialogV;
    TextView tvDiasPrueba;
    public static DiasPrueba newInstance() {
        DiasPrueba fragment = new DiasPrueba();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        btnDiasGratis = view.findViewById(R.id.btn_dias_gratis);
        tvDiasPrueba = view.findViewById(R.id.tv_dias_prueba);
        //Obtenemos el objeto ClubDetalle que se envió desde el fragmento club detalle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sucursal = (Sucursal) bundle.getSerializable(Variables.SUCURSALDETALLE);
            mostrarDiasPrueba(sucursal.getId());
            btnDiasGratis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnDiasGratis.setEnabled(false);
                    String celular = sharedPreferences.getString(Variables.TELEFONO,"");
                    if (celular.equals("") || celular.equals(' ') || celular.equals("...") || celular == null) {
                        Toast.makeText(getContext(), "Por favor ingrese su número celular antes de continuar con la activación del paquete", Toast.LENGTH_SHORT).show();
                        btnDiasGratis.setEnabled(true);
                        checkPhone();
                        return;
                    } else {
                        Call<CheckPhoneResponse> checkPhoneCall = service.checkPhone(celular);
                        checkPhoneCall.enqueue(new Callback<CheckPhoneResponse>() {
                            @Override
                            public void onResponse(Call<CheckPhoneResponse> call, Response<CheckPhoneResponse> response) {
                                if (response.isSuccessful()) {
                                    CheckPhoneResponse checkPhoneResponse = response.body();
                                    String code = checkPhoneResponse.getCode();
                                    if (code.equals("verification_01") || code.equals("verification_03")) {
                                        openConfirmationCode(celular);
                                    } else if (code.equals("verification_04")) {
                                        openAlertDialog();
                                    } else {
                                        Toast.makeText(getActivity(), "Error al activar paquete gratuito", Toast.LENGTH_SHORT).show();
                                        btnDiasGratis.setEnabled(true);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<CheckPhoneResponse> call, Throwable t) {

                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Sin precio de club", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }
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
                        Toast.makeText(getActivity(), "Error al activar el paquete", Toast.LENGTH_SHORT).show();
                        closeAlertDialogV();
                        closeAlertDialog();
                    }
                }
            });

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
                        openConfirmationCode(telefono);

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dias_prueba, container, false);
    }

    public void openAlertDialog() {
        AlertDialog.Builder builderP = new AlertDialog.Builder(getActivity());
        builderP.setTitle("Activar promoción");
        builderP.setCancelable(false);
        builderP.setMessage("Esta promoción sólo será válida a los días habilitados por la sucursal");
        builderP.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                btnDiasGratis.setEnabled(true);
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
                            Toast.makeText(getContext(), activarSubscripcionFree.getMsg(), Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                            getActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            btnDiasGratis.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<ActivarSubscripcionFree> call, Throwable t) {
                        btnDiasGratis.setEnabled(true);
                        if (!InternetConnectionStatus.isConnected(getActivity())) {
                            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        } else if(t instanceof SocketTimeoutException) {
                            Toast.makeText(getActivity(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.administrador), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        AlertDialog alertDialogP = builderP.create();
        alertDialogP.show();
    }

    public void openConfirmationCode(String telefono){
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
    }

    private void mostrarDiasPrueba(int idSucursal) {
        Call<Sucursal> sucursalCall = service.obtenerSucursal(idSucursal,true);
        sucursalCall.enqueue(new Callback<Sucursal>() {
            @Override
            public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                if (response.isSuccessful()) {
                    Sucursal sucursal = response.body();
                    tvDiasPrueba.setText(tvDiasPrueba.getText().toString()+" "+sucursal.getDiasPrueba());
                }
            }

            @Override
            public void onFailure(Call<Sucursal> call, Throwable t) {

            }
        });
    }
}
