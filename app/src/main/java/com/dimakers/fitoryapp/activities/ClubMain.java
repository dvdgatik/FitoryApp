package com.dimakers.fitoryapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimakers.fitoryapp.FitoryApp;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.CheckPhoneResponse;
import com.dimakers.fitoryapp.api.models.UpdatePhoneResponse;
import com.dimakers.fitoryapp.api.models.VerifyPhoneResponse;
import com.dimakers.fitoryapp.fragments.Calendario;
import com.dimakers.fitoryapp.fragments.Clubes;
import com.dimakers.fitoryapp.fragments.Evaluar;
import com.dimakers.fitoryapp.fragments.Perfil;
import com.dimakers.fitoryapp.services.BuscarBeacon;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.GoogleMap;

import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.design.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED;
import static android.support.design.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED;

public class ClubMain extends AppCompatActivity{

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FitoryService service = API.getApi().create(FitoryService.class);
    BottomNavigationView bottomNavigationView;
    Fragment calendario = new Calendario();
    Fragment clubes = new Clubes();
    GoogleSignInClient mGoogleSignInClient;
    FrameLayout frameLayout;
    private boolean evaluarValue = false;
    private boolean isMapReady = false;
    private String latitud = "";
    private String longitud = "";
    private GoogleMap mMap;
    ImageView ivBotonAtras;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    AlertDialog.Builder builderV;
    AlertDialog alertDialogV;
    String telefono;
    @Override
    public void onResume() {
        super.onResume();
//        Revisar teléfono
        telefono = sharedPreferences.getString(Variables.TELEFONO,"NONE");
        if (!telefono.equals("NONE")) {
            Call<CheckPhoneResponse> checkPhoneCall = service.checkPhone(telefono);
            checkPhoneCall.enqueue(new Callback<CheckPhoneResponse>() {
                @Override
                public void onResponse(Call<CheckPhoneResponse> call, Response<CheckPhoneResponse> response) {
                    if (response.isSuccessful()) {
                        CheckPhoneResponse checkPhoneResponse = response.body();
                        try {
//                            Toast.makeText(ClubMain.this, ""+checkPhoneResponse.getCode(), Toast.LENGTH_SHORT).show();
                            String code = checkPhoneResponse.getCode();
                            if (code.equals("verification_00")) {
                                builder = new AlertDialog.Builder(ClubMain.this);
                                builder.setView(R.layout.dialog_actualizar_telefono);
                                builder.setCancelable(false);
                                alertDialog = builder.create();
                                if (!alertDialog.isShowing()) {
                                    alertDialog.show();
                                    ImageView btnClose = alertDialog.findViewById(R.id.btn_close);
                                    Button btnGuardarTelefono = alertDialog.findViewById(R.id.btn_actualizar_telefono);
                                    EditText etTelefono = alertDialog.findViewById(R.id.et_telefono);

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
                            } else if (code.equals("verification_01") || code.equals("verification_03")) {
                                closeAlertDialogV();
                                closeAlertDialog();
                                builderV = new AlertDialog.Builder(ClubMain.this);
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
                                                    Toast.makeText(ClubMain.this, ""+verifyPhoneResponse.getMensaje(), Toast.LENGTH_SHORT).show();
                                                    if (verifyPhoneResponse.isSuccessful()) {
                                                        closeAlertDialogV();
                                                        closeAlertDialog();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<VerifyPhoneResponse> call, Throwable t) {
                                                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                                } else if(t instanceof SocketTimeoutException) {
                                                    Toast.makeText(getApplicationContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });



                                    }
                                });

                            }

                        } catch (Exception e) {
                            Log.d(ClubMain.class.getSimpleName(), "onResponse: "+e.getMessage());
                            Toast.makeText(ClubMain.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CheckPhoneResponse> call, Throwable t) {
                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    } else if(t instanceof SocketTimeoutException) {
                        Toast.makeText(getApplicationContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ClubMain.this, ""+updatePhoneResponse.getMensaje(), Toast.LENGTH_SHORT).show();
                        closeAlertDialogV();
                        closeAlertDialog();
                        builderV = new AlertDialog.Builder(ClubMain.this);
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
                                            Toast.makeText(ClubMain.this, ""+verifyPhoneResponse.getMensaje(), Toast.LENGTH_SHORT).show();
                                            if (verifyPhoneResponse.isSuccessful()) {
                                                closeAlertDialogV();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<VerifyPhoneResponse> call, Throwable t) {
                                        if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                        } else if(t instanceof SocketTimeoutException) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(ClubMain.this, ""+updatePhoneResponse.getMensaje(), Toast.LENGTH_SHORT).show();
                        closeAlertDialog();
                    }
                } else {
                    Toast.makeText(ClubMain.this, "No se pudo actualizar el teléfono. Comuníquese con su administrador.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdatePhoneResponse> call, Throwable t) {
                closeAlertDialog();
                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                } else if(t instanceof SocketTimeoutException) {
                    Toast.makeText(getApplicationContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_club_main);
        FitoryApp application = ((FitoryApp) this.getApplicationContext());
        application.setMonitoringActivity(this);
        application.enableMonitoring();
        frameLayout = findViewById(R.id.fragmentContainer);
        ivBotonAtras = findViewById(R.id.button_atras);
        Variables.sucursalesFavs.clear();
        Variables.sucursales.clear();
        Variables.metodos.clear();
        Variables.sesionFulls.clear();
        Variables.sesionFullsMes.clear();
        evaluarValue = getIntent().getBooleanExtra("EVALUAR",false);
//        Variables.is_running = false;
//        Intent serviceIntent = new Intent(this, BuscarBeacon.class);
//        stopService(serviceIntent);

//        ContextCompat.startForegroundService(this, serviceIntent);
        SharedPreferences sharedpreferences = getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        //Google Sign Up
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        editor = sharedpreferences.edit();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        if (getResources().getDisplayMetrics().heightPixels<=320) {
            bottomNavigationView.setLabelVisibilityMode(LABEL_VISIBILITY_UNLABELED);
        } else {
            bottomNavigationView.setLabelVisibilityMode(LABEL_VISIBILITY_LABELED);
        }
        //Setting Bottom Navigation view colors
        int[] colors = new int[]{
                Color.WHITE,
                Color.rgb(55, 189, 194)
        };

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled, -android.R.attr.state_checked},
                new int[]{android.R.attr.state_enabled, android.R.attr.state_checked}
        };

        bottomNavigationView.setItemTextColor(new ColorStateList(states, colors));
        bottomNavigationView.setItemIconTintList(new ColorStateList(states, colors));

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, clubes, "CLUBES").commit();
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.add(R.id.fragmentContainer,clubes,"CLUBES");
//            transaction.addToBackStack("CLUBMAIN");
//            transaction.commit();
        }
        bottomNavigationView.setSelectedItemId(R.id.icono_clubs);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.icono_clubs:
                        ivBotonAtras.setVisibility(View.GONE);
                        if (!getVisibleFragment().equals(clubes)) {
                            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                fragmentManager.popBackStack();
                            }
                            fragmentTransaction.replace(R.id.fragmentContainer, clubes, "CLUBES").addToBackStack(null).commit();
                        }

                        break;
                    case R.id.icono_calendario:
                        ivBotonAtras.setVisibility(View.VISIBLE);
//                        if1 (fragmentManager.findFragmentByTag("CALENDARIO")==null) {
                        if (!getVisibleFragment().equals(calendario)) {
                            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                try{
                                    fragmentManager.popBackStack();
                                } catch (IllegalStateException e) {
                                    Log.i("Exception", "onNavigationItemSelected: "+e.getMessage());
                                }
                            }
                            fragmentTransaction.replace(R.id.fragmentContainer, calendario, "CALENDARIO").addToBackStack(null).commit();
                        }

//                        } else {
//                            fragmentTransaction.replace(R.id.fragmentContainer, calendario, "CALENDARIO").addToBackStack(null).commit();
//                        }
//                        fragmentTransaction.replace(R.id.fragmentContainer, calendario, "CALENDARIO").commit();
//                        } else {
//                            fragmentManager.popBackStack("CALENDARIO", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                            fragmentTransaction.remove(calendario);
//                            fragmentTransaction.add(R.id.fragmentContainer, calendario, "CALENDARIO").addToBackStack(null).commit();
//                        }
                        break;
                    case R.id.icono_perfil:
                        ivBotonAtras.setVisibility(View.VISIBLE);
                        Fragment perfil = new Perfil();
                        if (!getVisibleFragment().equals(perfil)) {
                            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                                fragmentManager.popBackStack();
                            }
                            fragmentTransaction.replace(R.id.fragmentContainer, perfil, "PERFIL").addToBackStack(null).commit();
                        } else {
                            fragmentTransaction.detach(perfil);
                            fragmentTransaction.attach(perfil);
                            fragmentTransaction.commit();
//                            fragmentManager.popBackStack();
//                            fragmentTransaction.replace(R.id.fragmentContainer, perfil, "PERFIL").addToBackStack(null).commit();
                        }
//
//                        if (fragmentManager.findFragmentByTag("PERFIL")==null) {
//                            fragmentTransaction.add(R.id.fragmentContainer, perfil, "PERFIL").addToBackStack(null).commit();
//                        } else {

//                        }
//                        fragmentTransaction.replace(R.id.fragmentContainer, perfil, "PERFIL").commit();
//                        } else {
//                            fragmentManager.popBackStack("PERFIL", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                            fragmentTransaction.remove(perfil);
//                            fragmentTransaction.add(R.id.fragmentContainer, perfil, "PERFIL").addToBackStack(null).commit();
//                        }
                        break;
                }
                return true;
            }
        });


//        Dialog alertDialog = new Dialog(this);
//        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        alertDialog.setCancelable(false);
//        alertDialog.setContentView(R.layout.loading_layout);
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.show();
//        ImageView imageView = (ImageView) alertDialog.findViewById(R.id.imageView);
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
//        imageView.startAnimation(animation);
//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        //Code
//                        alertDialog.dismiss();
//                    }
//                }, new IntentFilter(Variables.SUCURSALES)
//        );
        if (evaluarValue) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment evaluar = new Evaluar();
            if (getVisibleFragment() == null) {
                fragmentTransaction.replace(R.id.fragmentContainer, evaluar, "EVALUAR").addToBackStack(null).commit();
            } else {
                if (!getVisibleFragment().equals(evaluar)) {
                    for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                        fragmentManager.popBackStack();
                    }
                    fragmentTransaction.replace(R.id.fragmentContainer, evaluar, "EVALUAR").addToBackStack(null).commit();
                }
            }
        }
    }

    public void vaciarBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    public void atras(View view) {

        getSupportFragmentManager().popBackStack();
        if (getSupportFragmentManager().getBackStackEntryCount()==1) {
            bottomNavigationView.setSelectedItemId(R.id.icono_clubs);
            getSupportFragmentManager().popBackStack();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount()==0) {
            if (doubleBackToExitPressedOnce) {
                finish();
            } else {
                Toast.makeText(this, "Presione de nuevo para salir", Toast.LENGTH_SHORT).show();
            }
            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
//        ClubDetalle detalle = new ClubDetalle();
//        if (getVisibleFragment().equals(detalle)) {
//            getSupportFragmentManager().popBackStack();
//        } else if (bottomNavigationView.getSelectedItemId()!=R.id.icono_clubs) {
//            getSupportFragmentManager().popBackStack();
//            if (getSupportFragmentManager().getFragments().size()==3) {
////                bottomNavigationView.setSelectedItemId(R.id.icono_clubs);
//            }
//        } else {
//            super.onBackPressed();
//
//        }


//            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, "Presione de nuevo para salir", Toast.LENGTH_SHORT).show();
//
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    doubleBackToExitPressedOnce=false;
//                }
//            }, 2000);
//        } else {
//            bottomNavigationView.setSelectedItemId(R.id.icono_clubs);
//        }


    }

    @Override
    public void onBackPressed() {
//        Toast.makeText(this, ""+getSupportFragmentManager().getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
        getSupportFragmentManager().popBackStack();
        if (getSupportFragmentManager().getBackStackEntryCount()==1) {
            bottomNavigationView.setSelectedItemId(R.id.icono_clubs);
            getSupportFragmentManager().popBackStack();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount()==0) {
            if (doubleBackToExitPressedOnce) {
                finish();
            } else {
                Toast.makeText(this, "Presione de nuevo para salir", Toast.LENGTH_SHORT).show();
            }
                this.doubleBackToExitPressedOnce = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
        }
//        ClubDetalle detalle = new ClubDetalle();
//        if (getVisibleFragment().equals(detalle)) {
//            getSupportFragmentManager().popBackStack();
//        } else if (bottomNavigationView.getSelectedItemId()!=R.id.icono_clubs) {
//            getSupportFragmentManager().popBackStack();
//            if (getSupportFragmentManager().getFragments().size()==3) {
////                bottomNavigationView.setSelectedItemId(R.id.icono_clubs);
//            }
//        } else {
//            super.onBackPressed();
//        }


//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        if (fragmentManager.findFragmentByTag("CLUBES") == null) {
//            fragmentTransaction.add(R.id.fragmentContainer, clubes, "CLUBES").addToBackStack(null).commit();
//        } else {
//            super.onBackPressed();
//            bottomNavigationView.setSelectedItemId(R.id.icono_clubs);
//        }

//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.fragmentContainer, clubes, "CLUBES").commit();
////            Fragment buscar = (Fragment)getSupportFragmentManager().findFragmentByTag("BUSCAR");
////            Fragment calendario = (Fragment)getSupportFragmentManager().findFragmentByTag("CALENDARIO");
////        Fragment perfil = (Fragment)getSupportFragmentManager().findFragmentByTag("PERFIL");
////        if (buscar != null && buscar.isVisible()) {
////            bottomNavigationView.setSelectedItemId(R.id.icono_buscar);
////        }
////        else if (perfil != null && perfil.isVisible()){
////            bottomNavigationView.setSelectedItemId(R.id.icono_perfil);
////        }
////        else if (calendario != null && calendario.isVisible()){
////            bottomNavigationView.setSelectedItemId(R.id.icono_calendario);
////        } else {
////            fragmentTransaction.replace(R.id.fragmentContainer, calendario, "CALENDARIO").commit();
////            Toast.makeText(this, "Selected item changed", Toast.LENGTH_SHORT).show();
//        }
////        }

    }

    public void suscripcion(View view) {
        startActivity(new Intent(ClubMain.this, Suscripcion.class));
    }

    public void cerrarSesion(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_logout);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView buttonCancelar = (TextView) dialog.findViewById(R.id.button1);
        TextView buttonConfirmar = (TextView) dialog.findViewById(R.id.button2);
        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove(Variables.USERNAME);
                editor.remove(Variables.EMAIL);
                editor.remove(Variables.TOKEN);
                editor.remove(Variables.IDUSER);
                editor.remove(Variables.CLIENTEID);
                editor.remove(Variables.LOCATION);
                editor.remove(Variables.CIUDADID);
//                        editor.remove(Variables.SELECCIONUBICACION);
                editor.commit();
                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(ClubMain.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                dialog.dismiss();
                startActivity(intent);
            }
        });
        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }




    /**
     *  Método para obtener el fragmento que esta visible
     * @return
     */
    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void testing(View view) {
//        startActivity(new Intent(this, Main2Activity.class));
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment evaluar = new Evaluar();
//        if (!getVisibleFragment().equals(evaluar)) {
//            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
//                fragmentManager.popBackStack();
//            }
//            fragmentTransaction.replace(R.id.fragmentContainer, evaluar, "EVALUAR").addToBackStack(null).commit();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        closeAlertDialog();
        closeAlertDialogV();
//        Fragment clubes = new Clubes();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        if (!getVisibleFragment().equals(clubes)) {
//            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
//                fragmentManager.popBackStack();
//            }
//            fragmentTransaction.replace(R.id.fragmentContainer, clubes, "CLUBES").addToBackStack(null).commit();
//        }
    }

    public void closeAlertDialog() {
        if (alertDialog!=null) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
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
}
