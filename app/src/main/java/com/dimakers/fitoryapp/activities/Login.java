package com.dimakers.fitoryapp.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Cliente;
import com.dimakers.fitoryapp.api.models.Club;
import com.dimakers.fitoryapp.api.models.RegistrarCelularResponse;
import com.dimakers.fitoryapp.api.models.User;
import com.dimakers.fitoryapp.api.models.UserLogin;
import com.dimakers.fitoryapp.services.BuscarBeacon;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    FitoryService service = API.getApi().create(FitoryService.class);
    EditText etUsuario,etContrasena;
    LoginButton loginButton;
    SignInButton loginButton2;
    private ImageView botonFacebook, botonGoogle;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;
    public static final int SIGN_IN_CODE = 777;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        callbackManager = CallbackManager.Factory.create();
        etUsuario = findViewById(R.id.et_usuario);
        etContrasena = findViewById(R.id.et_contrasena);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton2 = (SignInButton) findViewById(R.id.login_button2);
        botonFacebook = (ImageView) findViewById(R.id.boton_facebook);
        botonGoogle = (ImageView) findViewById(R.id.boton_google);
        botonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonGoogle.setEnabled(false);
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,SIGN_IN_CODE);
            }
        });
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        loginButton.setReadPermissions(permissions);
        //Facebook registro de callback
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String idFacebook = object.getString("id");
                                    String email = object.getString("email");
                                    String first_name = object.getString("first_name");
                                    String last_name = object.getString("last_name");
                                    String profile_pic = object.getString("picture");
                                    handleFbSignInResult(idFacebook, email, first_name, last_name, profile_pic);
                                } catch (JSONException e) {
                                    Toast.makeText(Login.this, "Error al iniciar sesión"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    LoginManager.getInstance().logOut();
                                }
                            }
                        }
                );
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name, last_name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Login.this, "Error. "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ImageView botonFacebook = (ImageView) findViewById(R.id.boton_facebook);
        ImageView botonGoogle = (ImageView) findViewById(R.id.boton_google);
        Glide.with(Login.this).load(R.drawable.boton_iniciar_sesion_fb).into(botonFacebook);
        Glide.with(Login.this).load(R.drawable.boton_iniciar_sesion_google).into(botonGoogle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            } else {
                Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
                botonGoogle.setEnabled(true);
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFbSignInResult(String idFacebook, String email, String first_name, String last_name, String profile_pic) {
        Call<Cliente> clienteCall = service.obtenerClientesFacebook(idFacebook);
        clienteCall.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getResults().isEmpty()) {
                        Cliente cliente = response.body().getResults().get(0);
                        //Iniciar sesión
                        Call<User> userCall = service.obtenerUsuario(cliente.getUser());
//                            Call<User> userCall = service.obtenerUsuario(9);
                        userCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful()) {
                                    User user = response.body();
                                    editor.putString(Variables.IDUSER,String.valueOf(user.getId()));
                                    editor.putString(Variables.USERNAME,user.getUsername());
                                    editor.putString(Variables.EMAIL,user.getEmail());
                                    editor.putInt(Variables.CLIENTEID, cliente.getId());
                                    editor.putString(Variables.CLIENTENOMBRE,cliente.getNombre());
                                    editor.putString(Variables.CLIENTEAPELLIDO,cliente.getApellido());
                                    editor.putString(Variables.TELEFONO,cliente.getTelefono());
                                    if (cliente.isConvivir()) {
                                        editor.putInt(Variables.CLIENTEOBJETIVO,1);
                                    } else if (cliente.isDiversion()) {
                                        editor.putInt(Variables.CLIENTEOBJETIVO,3);
                                    } else if (cliente.isSalud()) {
                                        editor.putInt(Variables.CLIENTEOBJETIVO,0);
                                    } else if (cliente.isVermeBien()) {
                                        editor.putInt(Variables.CLIENTEOBJETIVO, 2);
                                    }
                                    editor.putString(Variables.TELEFONO,cliente.getTelefono());
                                    editor.putString(Variables.FOTO,cliente.getFoto());
                                    editor.commit();
                                    Toast.makeText(Login.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                    verificarObjetivo(cliente.getId());
                                } else {
                                    Toast.makeText(Login.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                                    LoginManager.getInstance().logOut();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                    Toast.makeText(Login.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    clienteCall.cancel();
                                } else if(t instanceof SocketTimeoutException) {
                                    Toast.makeText(Login.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                    clienteCall.cancel();
                                }
                                LoginManager.getInstance().logOut();
                            }
                        });
                    } else {
                        //Crear usuario con token FB
                        showCellphoneDialog(email, idFacebook, first_name, last_name, "FACEBOOK");

                    }
                } else {
                    Toast.makeText(Login.this, "No se pudo obtener el cliente de Facebook", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                    Toast.makeText(Login.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    clienteCall.cancel();
                } else if(t instanceof SocketTimeoutException) {
                    Toast.makeText(Login.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                    clienteCall.cancel();
                }
            }
        });
    }

    private void showCellphoneDialog(String email, String token, String first_name, String last_name, String media) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        builder.setView(R.layout.dialog_actualizar_telefono);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
            ImageView btnClose = alertDialog.findViewById(R.id.btn_close);
            Button btnGuardarTelefono = alertDialog.findViewById(R.id.btn_actualizar_telefono);
            EditText etTelefono = alertDialog.findViewById(R.id.et_telefono);

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    LoginManager.getInstance().logOut();
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

                    if (etTelefono.getText().toString().trim().length() < 10 || !TextUtils.isDigitsOnly(etTelefono.getText().toString().trim())) {
                        etTelefono.setError("Ingrese un teléfono celular válido");
                        etTelefono.requestFocus();
                        return;
                    }
                    registerUser(email, token, first_name, last_name, etTelefono.getText().toString().trim(), media);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }

    private void registerUser(String email, String token, String first_name, String last_name, String cellphone, String media) {
        Call<RegistrarCelularResponse> registrarCelularCall = service.registrarCelular(email,
                token,
                first_name,
                last_name,
                false,
                false,
                cellphone,
                media,
                token);
        registrarCelularCall.enqueue(new Callback<RegistrarCelularResponse>() {
            @Override
            public void onResponse(Call<RegistrarCelularResponse> call, Response<RegistrarCelularResponse> response) {
                if (response.isSuccessful()) {
                    RegistrarCelularResponse registro = response.body();
                    Toast.makeText(Login.this, ""+registro.getMensaje(), Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                    if (registro.isSuccessful()) {
                        loginMedia(email,token);
                    } else {
                        Toast.makeText(Login.this, registro.getMensaje(), Toast.LENGTH_SHORT).show();
                        Log.e("REGISTROX",registro.getMensaje());
                        LoginManager.getInstance().logOut();
                    }
                } else {
                    Toast.makeText(Login.this, "No se pudo actualizar login", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                }
            }

            @Override
            public void onFailure(Call<RegistrarCelularResponse> call, Throwable t) {
                LoginManager.getInstance().logOut();
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


    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Call<Cliente> clienteCall = service.obtenerClientesGoogle(account.getId());
            clienteCall.enqueue(new Callback<Cliente>() {
                @Override
                public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                    if (response.isSuccessful()) {
                        if (!response.body().getResults().isEmpty()) {
                            Cliente cliente = response.body().getResults().get(0);
                            Call<User> userCall = service.obtenerUsuario(cliente.getUser());
                            userCall.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if (response.isSuccessful()) {
                                        User user = response.body();
                                        editor.putString(Variables.IDUSER,String.valueOf(user.getId()));
                                        editor.putString(Variables.USERNAME,user.getUsername());
                                        editor.putString(Variables.EMAIL,user.getEmail());
                                        editor.putInt(Variables.CLIENTEID, cliente.getId());
                                        editor.putString(Variables.CLIENTENOMBRE,cliente.getNombre());
                                        editor.putString(Variables.CLIENTEAPELLIDO,cliente.getApellido());
                                        editor.putString(Variables.TELEFONO,cliente.getTelefono());
                                        if (cliente.isConvivir()) {
                                            editor.putInt(Variables.CLIENTEOBJETIVO,1);
                                        } else if (cliente.isDiversion()) {
                                            editor.putInt(Variables.CLIENTEOBJETIVO,3);
                                        } else if (cliente.isSalud()) {
                                            editor.putInt(Variables.CLIENTEOBJETIVO,0);
                                        } else if (cliente.isVermeBien()) {
                                            editor.putInt(Variables.CLIENTEOBJETIVO, 2);
                                        }
                                        editor.putString(Variables.TELEFONO,cliente.getTelefono());
                                        editor.putString(Variables.FOTO,cliente.getFoto());
                                        editor.commit();
                                        Toast.makeText(Login.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                        verificarObjetivo(cliente.getId());
                                    } else {
                                        Toast.makeText(Login.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                                        botonGoogle.setEnabled(true);
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                        Toast.makeText(Login.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                        clienteCall.cancel();
                                    } else if(t instanceof SocketTimeoutException) {
                                        Toast.makeText(Login.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                        clienteCall.cancel();
                                    }
                                    botonGoogle.setEnabled(true);
                                }
                            });

                        } else {
                            //Crear usuario con token G+
                            showCellphoneDialog(account.getEmail(),  account.getId(), account.getGivenName(), account.getFamilyName(), "GOOGLE");

                        }
                    }
                }

                @Override
                public void onFailure(Call<Cliente> call, Throwable t) {
                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                        Toast.makeText(Login.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        clienteCall.cancel();
                    } else if(t instanceof SocketTimeoutException) {
                        Toast.makeText(Login.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                        clienteCall.cancel();
                    }
                    botonGoogle.setEnabled(true);
                }
            });

        } catch (ApiException e) {
            botonGoogle.setEnabled(true);
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, "Login cancel", Toast.LENGTH_SHORT).show();
        }
    }

    public void seleccionRegistrarme(View view) {
        startActivity(new Intent(Login.this, SignUp.class));
    }

    public void iniciarSesion(View view) {
            boolean correcto = true;
            if (TextUtils.isEmpty(etContrasena.getText())) {
                etContrasena.setError("Ingrese su contraseña");
                etContrasena.requestFocus();
                correcto = false;
            }
            if (TextUtils.isEmpty(etUsuario.getText())) {
                etUsuario.setError("Ingrese su nombre de usuario");
                etUsuario.requestFocus();
                correcto = false;
            }

            if (correcto) {
                login();
            }
    }

    private void login() {
        Call<UserLogin> userLoginCall = service.iniciarSesion(etUsuario.getText().toString().trim(),etContrasena.getText().toString().trim());
        userLoginCall.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                if (response.isSuccessful()) {
                    UserLogin user = response.body();
                    Call<User> userCall = service.obtenerUsuario(Integer.parseInt(user.getIdUser()));
                    userCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                String email = response.body().getEmail();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Variables.USERNAME,user.getUsername());
                                editor.putString(Variables.IDUSER,user.getIdUser());
                                editor.putString(Variables.TOKEN,user.getToken());
                                editor.putString(Variables.EMAIL,email);
                                editor.commit();
                                obtenerCliente(user.getIdUser());

                            } else {
                                Toast.makeText(Login.this, R.string.intentar_nuevamente, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                Toast.makeText(Login.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Usuario/Contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                    Toast.makeText(Login.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginMedia(String username, String password) {
        Call<UserLogin> userLoginCall = service.iniciarSesion(username,password);
        userLoginCall.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                if (response.isSuccessful()) {
                    UserLogin user = response.body();
                    Call<User> userCall = service.obtenerUsuario(Integer.parseInt(user.getIdUser()));
                    userCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                String email = response.body().getEmail();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Variables.USERNAME,user.getUsername());
                                editor.putString(Variables.IDUSER,user.getIdUser());
                                editor.putString(Variables.TOKEN,user.getToken());
                                editor.putString(Variables.EMAIL,email);
                                editor.commit();
                                obtenerCliente(user.getIdUser());
                            } else {
                                Toast.makeText(Login.this, R.string.intentar_nuevamente, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                Toast.makeText(Login.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Usuario/Contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                    Toast.makeText(Login.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verificarObjetivo(int clienteID) {
        Call<Cliente> clienteCall = service.verificarSeleccionObjetivo(clienteID, false,false,false,false);
        clienteCall.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(Login.this, Objetivo.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    //Mostrar ventana de permisos de ubicación
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                                requestPermission();
                            } else if (!sharedPreferences.getBoolean(Variables.PERMISSION_LOCATION, false)) {
                                requestPermission();
                                editor.putBoolean(Variables.PERMISSION_LOCATION, true);
                                editor.commit();
                            } else {
                                if (isGPSEnabled(getApplicationContext())) {
                                    clubMain();
                                } else {
                                    seleccionCiudad();
                                }
                            }

                        }  else {
                            if (isGPSEnabled(getApplicationContext())) {
                                clubMain();
                            } else {
                                seleccionCiudad();
                            }
                        }
                    } else {
                        if (isGPSEnabled(getApplicationContext())) {
                            clubMain();
                        } else {
                            seleccionCiudad();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                    Toast.makeText(Login.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isGPSEnabled(Context applicationContext) {
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void obtenerCliente(String idUser) {
        if (!idUser.equals("")) {
            Call<Cliente> clienteCall = service.obtenerCliente(Integer.parseInt(idUser));
            clienteCall.enqueue(new Callback<Cliente>() {
                @Override
                public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                    if (response.isSuccessful()) {
                        if (!response.body().getResults().isEmpty()) {

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Cliente cliente = response.body().getResults().get(0);
                            editor.putInt(Variables.CLIENTEID, cliente.getId());
                            editor.putString(Variables.CLIENTENOMBRE,cliente.getNombre());
                            editor.putString(Variables.CLIENTEAPELLIDO,cliente.getApellido());
                            editor.putString(Variables.TELEFONO,cliente.getTelefono());
                            if (cliente.isConvivir()) {
                                editor.putInt(Variables.CLIENTEOBJETIVO,1);
                            } else if (cliente.isDiversion()) {
                                editor.putInt(Variables.CLIENTEOBJETIVO,3);
                            } else if (cliente.isSalud()) {
                                editor.putInt(Variables.CLIENTEOBJETIVO,0);
                            } else if (cliente.isVermeBien()) {
                                editor.putInt(Variables.CLIENTEOBJETIVO, 2);
                            }
                            editor.putString(Variables.TELEFONO,cliente.getTelefono());
                            editor.putString(Variables.FOTO,cliente.getFoto());
                            editor.commit();
                            verificarObjetivo(cliente.getId());
//                            Intent serviceIntent = new Intent(Login.this, BuscarBeacon.class);
//                            ContextCompat.startForegroundService(Login.this, serviceIntent);
                        } else {
                            Toast.makeText(Login.this, R.string.administrador, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, R.string.administrador, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Cliente> call, Throwable t) {
                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                        Toast.makeText(Login.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(Login.this, "No se encontró al usuario", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void atras(View view) {
        finish();
    }

    public void signinFb(View view) {
        loginButton.performClick();
    }

    public void recordarContrasena(View view) {
        Uri webpage = Uri.parse("https://www.fitory.com/RestaurarContrasena/");
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "No se pudo iniciar sesión 1", Toast.LENGTH_SHORT).show();
    }

    private void requestPermission () {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled(getApplicationContext())) {
                    clubMain();
                } else {
                    seleccionCiudad();
                }
            } else {
                seleccionCiudad();
            }

        } else {
            seleccionCiudad();
        }
    }


    public void seleccionCiudad() {
        Intent intent = new Intent(getApplicationContext(), SeleccionCiudad.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void clubMain() {
        Intent intent = new Intent(getApplicationContext(), ClubMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
