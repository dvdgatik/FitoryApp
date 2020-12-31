package com.dimakers.fitoryapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.TerminosCondicionesActivity;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeleccionRegistrar extends AppCompatActivity {
    FitoryService service = API.getApi().create(FitoryService.class);
    ImageView btnGoogle;
    LoginButton loginButton;
    GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 321;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_registrar);
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //Google Sign Up
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogle = findViewById(R.id.btn_google);
        Glide.with(this).load(R.drawable.boton_registro_google).into(btnGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
//        437431816927-j2n5nuc6fr56d2dklujaujj1n46ehqjp.apps.googleusercontent.com
        ImageView imageView = (ImageView) findViewById(R.id.iv_registro);
        Glide.with(SeleccionRegistrar.this).load(R.drawable.foto_registro).into(imageView);
        Intent intent = getIntent();
        String first_name = intent.getStringExtra(Variables.CLIENTENOMBRE);
        String last_name = intent.getStringExtra(Variables.CLIENTEAPELLIDO);
        String email = intent.getStringExtra(Variables.EMAIL);
        ImageView btnFacebook = findViewById(R.id.btn_facebook);
        Glide.with(this).load(R.drawable.boton_registro_fb).into(btnFacebook);
        ImageView btnCorreo = findViewById(R.id.btn_correo);
        Glide.with(this).load(R.drawable.boton_registro_correo).into(btnCorreo);
//        Toast.makeText(this, ""+username+" "+email, Toast.LENGTH_SHORT).show();
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
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
                                    Toast.makeText(SeleccionRegistrar.this, "Error al iniciar sesión"+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SeleccionRegistrar.this, "Error. "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void signup(View view) {
        startActivity(new Intent(SeleccionRegistrar.this, SignUp.class));
    }

    public void terminos(View view) {
//        Uri webpage = Uri.parse("https://fitory.com/Legal/");
//        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
        Intent intent = new Intent(this, TerminosCondicionesActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
                default:
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
                                    editor.putString(Variables.FOTO,cliente.getFoto());
                                    editor.commit();
                                    Toast.makeText(SeleccionRegistrar.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SeleccionRegistrar.this, Objetivo.class));
                                    finish();
                                } else {
                                    Toast.makeText(SeleccionRegistrar.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                    Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    clienteCall.cancel();
                                } else if(t instanceof SocketTimeoutException) {
                                    Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                    clienteCall.cancel();
                                }
                            }
                        });
                    } else {
                        //Crear usuario
//                        Intent intent = new Intent(SeleccionRegistrar.this, SignUp.class);
//                        intent.putExtra(Variables.EMAIL,email);
//                        intent.putExtra(Variables.CLIENTENOMBRE,first_name);
//                        intent.putExtra(Variables.CLIENTEAPELLIDO,last_name);
//                        intent.putExtra(Variables.FBID, idFacebook);
//                        intent.putExtra(Variables.PROFILE_PIC, profile_pic);
//                        intent.putExtra(Variables.SOCIAL_MEDIA,"FACEBOOK");
//                        LoginManager.getInstance().logOut();
//                        startActivity(intent);
                        //Crear usuario con token FB

                        Call<RegistrarCelularResponse> registrarCelularCall = service.registrarCelular(email,
                                idFacebook,
                                first_name,
                                last_name,
                                false,
                                false,
                                "",
                                "FACEBOOK",
                                idFacebook);
                        registrarCelularCall.enqueue(new Callback<RegistrarCelularResponse>() {
                            @Override
                            public void onResponse(Call<RegistrarCelularResponse> call, Response<RegistrarCelularResponse> response) {
                                if (response.isSuccessful()) {
                                    RegistrarCelularResponse registro = response.body();
                                    Toast.makeText(SeleccionRegistrar.this, ""+registro.getMensaje(), Toast.LENGTH_SHORT).show();
                                    LoginManager.getInstance().logOut();
                                    if (registro.isSuccessful()) {
                                        loginMedia(email,idFacebook);
                                    } else {
                                        Toast.makeText(SeleccionRegistrar.this, "No se pudo crear al usuario. Contacte al administrador.", Toast.LENGTH_SHORT).show();
                                        LoginManager.getInstance().logOut();
                                    }
                                } else {
                                    Toast.makeText(SeleccionRegistrar.this, "No se pudo actualizar login", Toast.LENGTH_SHORT).show();
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

//                        Call<User> userCall = service.crearUsuario(email,email,idFacebook);
//                        userCall.enqueue(new Callback<User>() {
//                            @Override
//                            public void onResponse(Call<User> call, Response<User> response) {
//                                if (response.isSuccessful()) {
//                                    User user = response.body();
//                                    Call<Cliente> clienteCall = service.crearClienteFacebook(user.getId(),first_name,last_name, false, false, idFacebook);
//                                    clienteCall.enqueue(new Callback<Cliente>() {
//                                        @Override
//                                        public void onResponse(Call<Cliente> call, Response<Cliente> response) {
//                                            if (response.isSuccessful()) {
//                                                Cliente cliente = response.body();
//                                                Call<ResponseBody> conektaCall = service.crearCustomerConekta(String.valueOf(cliente.getId()));
//                                                conektaCall.enqueue(new Callback<ResponseBody>() {
//                                                    @Override
//                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                                        if (response.isSuccessful()) {
//                                                            Toast.makeText(SeleccionRegistrar.this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
//                                                            /**
//                                                             *  Al descomentar el siguiente bloque de código se enviará a la pantalla de iniciar sesión
//                                                             *  al crearse un nuevo usuario
//                                                             */
////                                                                Intent intent = new Intent(SeleccionRegistrar.this, Login.class);
////                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////                                                                startActivity(intent);
//                                                            /**
//                                                             * Ahora se hara inicio de sesión directamente
//                                                             */
//                                                            loginMedia(email,idFacebook);
//                                                        } else {
//                                                            Toast.makeText(SeleccionRegistrar.this, "No se pudo crear al usuario. Contacte al administrador.", Toast.LENGTH_SHORT).show();
//                                                            LoginManager.getInstance().logOut();
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                                        if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                            Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                        } else if(t instanceof SocketTimeoutException) {
//                                                            Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                        }
//                                                        LoginManager.getInstance().logOut();
//                                                    }
//                                                });
//                                            } else {
//                                                Toast.makeText(SeleccionRegistrar.this, "Error. Consulte al administrador", Toast.LENGTH_SHORT).show();
//                                                LoginManager.getInstance().logOut();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<Cliente> call, Throwable t) {
//                                            if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                            } else if(t instanceof SocketTimeoutException) {
//                                                Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                            }
//                                            LoginManager.getInstance().logOut();
//                                        }
//                                    });
//                                } else {
//                                    //Actualizar user con token de FB
//                                    Call<User> userCall1 = service.obtenerUsuario(email);
//                                    userCall1.enqueue(new Callback<User>() {
//                                        @Override
//                                        public void onResponse(Call<User> call, Response<User> response) {
//                                            if (response.isSuccessful()) {
//                                                User user = response.body().getResults().get(0);
//                                                Call<Cliente> clienteCall = service.obtenerCliente(user.getId());
//                                                clienteCall.enqueue(new Callback<Cliente>() {
//                                                    @Override
//                                                    public void onResponse(Call<Cliente> call, Response<Cliente> response) {
//                                                        if (response.isSuccessful()) {
//                                                            Cliente cliente = response.body().getResults().get(0);
//                                                            Call<Cliente> tokenCall = service.actualizarFacebookToken(cliente.getId(),idFacebook);
//                                                            tokenCall.enqueue(new Callback<Cliente>() {
//                                                                @Override
//                                                                public void onResponse(Call<Cliente> call, Response<Cliente> response) {
//                                                                    if (response.isSuccessful()) {
//                                                                        loginMedia(email,cliente.getIdGoogle());
//
//
//                                                                    } else {
//                                                                        Toast.makeText(SeleccionRegistrar.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
//                                                                        LoginManager.getInstance().logOut();
//                                                                    }
//                                                                }
//
//                                                                @Override
//                                                                public void onFailure(Call<Cliente> call, Throwable t) {
//                                                                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                                        Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                                    } else if(t instanceof SocketTimeoutException) {
//                                                                        Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                    LoginManager.getInstance().logOut();
//                                                                }
//                                                            });
//                                                        } else {
//                                                            Toast.makeText(SeleccionRegistrar.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
//                                                            LoginManager.getInstance().logOut();
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(Call<Cliente> call, Throwable t) {
//                                                        if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                            Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                        } else if(t instanceof SocketTimeoutException) {
//                                                            Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                        }
//                                                        LoginManager.getInstance().logOut();
//                                                    }
//                                                });
//
//                                            } else {
//                                                Toast.makeText(SeleccionRegistrar.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
//                                                LoginManager.getInstance().logOut();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<User> call, Throwable t) {
//                                            if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                            } else if(t instanceof SocketTimeoutException) {
//                                                Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                            }
//                                            LoginManager.getInstance().logOut();
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<User> call, Throwable t) {
//                                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                    Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                } else if(t instanceof SocketTimeoutException) {
//                                    Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                }
//                                LoginManager.getInstance().logOut();
//                            }
//                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                    Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    clienteCall.cancel();
                } else if(t instanceof SocketTimeoutException) {
                    Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                    clienteCall.cancel();
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
                                        editor.putString(Variables.FOTO,cliente.getFoto());
                                        editor.commit();
                                        Toast.makeText(SeleccionRegistrar.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SeleccionRegistrar.this, Objetivo.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SeleccionRegistrar.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                        Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                        clienteCall.cancel();
                                    } else if(t instanceof SocketTimeoutException) {
                                        Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                        clienteCall.cancel();
                                    }
                                }
                            });
                        } else {
                            //Crear usuario
//                            Intent intent = new Intent(SeleccionRegistrar.this, SignUp.class);
//                            intent.putExtra(Variables.EMAIL,account.getEmail());
//                            intent.putExtra(Variables.CLIENTENOMBRE,account.getGivenName());
//                            intent.putExtra(Variables.CLIENTEAPELLIDO,account.getFamilyName());
//                            intent.putExtra(Variables.GOOGLEID, account.getId());
//                            intent.putExtra(Variables.SOCIAL_MEDIA,"GOOGLE");
//                            startActivity(intent);

                            //Crear usuario con token G+

                            Call<RegistrarCelularResponse> registrarCelularCall = service.registrarCelular(account.getEmail(),
                                    account.getId(),
                                    account.getGivenName(),
                                    account.getFamilyName(),
                                    false,
                                    false,
                                    "",
                                    "GOOGLE",
                                    account.getId());
                            registrarCelularCall.enqueue(new Callback<RegistrarCelularResponse>() {
                                @Override
                                public void onResponse(Call<RegistrarCelularResponse> call, Response<RegistrarCelularResponse> response) {
                                    if (response.isSuccessful()) {
                                        RegistrarCelularResponse registro = response.body();
                                        Toast.makeText(SeleccionRegistrar.this, ""+registro.getMensaje(), Toast.LENGTH_SHORT).show();
                                        btnGoogle.setEnabled(true);
                                        if (registro.isSuccessful()) {
                                            loginMedia(account.getEmail(),account.getId());
                                        }
                                    } else {
                                        Toast.makeText(SeleccionRegistrar.this, "No se pudo actualizar login", Toast.LENGTH_SHORT).show();
                                        btnGoogle.setEnabled(true);
                                    }
                                }

                                @Override
                                public void onFailure(Call<RegistrarCelularResponse> call, Throwable t) {
                                    btnGoogle.setEnabled(true);
                                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    } else if(t instanceof SocketTimeoutException) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

//                            Call<User> userCall = service.crearUsuario(account.getEmail(),account.getEmail(),account.getId());
//                            userCall.enqueue(new Callback<User>() {
//                                @Override
//                                public void onResponse(Call<User> call, Response<User> response) {
//                                    if (response.isSuccessful()) {
//                                        User user = response.body();
//                                        Call<Cliente> clienteCall = service.crearClienteGoogle(user.getId(),account.getGivenName(),account.getFamilyName(), false, false, account.getId());
//                                        clienteCall.enqueue(new Callback<Cliente>() {
//                                            @Override
//                                            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
//                                                if (response.isSuccessful()) {
//                                                    Cliente cliente = response.body();
//                                                    Call<ResponseBody> conektaCall = service.crearCustomerConekta(String.valueOf(cliente.getId()));
//                                                    conektaCall.enqueue(new Callback<ResponseBody>() {
//                                                        @Override
//                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                                            if (response.isSuccessful()) {
//                                                                Toast.makeText(SeleccionRegistrar.this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
//                                                                /**
//                                                                 *  Al descomentar el siguiente bloque de código se enviará a la pantalla de iniciar sesión
//                                                                 *  al crearse un nuevo usuario
//                                                                 */
////                                                                Intent intent = new Intent(SignUp.this, Login.class);
////                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////                                                                startActivity(intent);
//                                                                /**
//                                                                 * Ahora se hara inicio de sesión directamente
//                                                                 */
//                                                                loginMedia(account.getEmail(),account.getId());
//                                                            } else {
//                                                                Toast.makeText(SeleccionRegistrar.this, "No se pudo crear al usuario. Contacte al administrador.", Toast.LENGTH_SHORT).show();
//                                                                btnGoogle.setEnabled(true);
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                                            if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                                Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                            } else if(t instanceof SocketTimeoutException) {
//                                                                Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                            }
//                                                            btnGoogle.setEnabled(true);
//                                                        }
//                                                    });
//                                                } else {
//                                                    Toast.makeText(SeleccionRegistrar.this, "Error. Consulte al administrador", Toast.LENGTH_SHORT).show();
//                                                    btnGoogle.setEnabled(true);
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onFailure(Call<Cliente> call, Throwable t) {
//                                                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                    Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                } else if(t instanceof SocketTimeoutException) {
//                                                    Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                }
//                                                btnGoogle.setEnabled(true);
//                                            }
//                                        });
//                                    } else {
//                                        //Actualizar user con token de Google
//                                        Call<User> userCall1 = service.obtenerUsuario(account.getEmail());
//                                        userCall1.enqueue(new Callback<User>() {
//                                            @Override
//                                            public void onResponse(Call<User> call, Response<User> response) {
//                                                if (response.isSuccessful()) {
//                                                    User user = response.body().getResults().get(0);
//                                                    Call<Cliente> clienteCall = service.obtenerCliente(user.getId());
//                                                    clienteCall.enqueue(new Callback<Cliente>() {
//                                                        @Override
//                                                        public void onResponse(Call<Cliente> call, Response<Cliente> response) {
//                                                            if (response.isSuccessful()) {
//                                                                Cliente cliente = response.body().getResults().get(0);
//                                                                Call<Cliente> tokenCall = service.actualizarGoogleToken(cliente.getId(),account.getId());
//                                                                tokenCall.enqueue(new Callback<Cliente>() {
//                                                                    @Override
//                                                                    public void onResponse(Call<Cliente> call, Response<Cliente> response) {
//                                                                        if (response.isSuccessful()) {
//                                                                            loginMedia(account.getEmail(),cliente.getIdFacebook());
//
//
//                                                                        } else {
//                                                                            Toast.makeText(SeleccionRegistrar.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
//                                                                            btnGoogle.setEnabled(true);
//                                                                        }
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onFailure(Call<Cliente> call, Throwable t) {
//                                                                        if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                                            Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                                        } else if(t instanceof SocketTimeoutException) {
//                                                                            Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                                        }
//                                                                        btnGoogle.setEnabled(true);
//                                                                    }
//                                                                });
//                                                            } else {
//                                                                Toast.makeText(SeleccionRegistrar.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
//                                                                btnGoogle.setEnabled(true);
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onFailure(Call<Cliente> call, Throwable t) {
//                                                            if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                                Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                            } else if(t instanceof SocketTimeoutException) {
//                                                                Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                            }
//                                                            btnGoogle.setEnabled(true);
//                                                        }
//                                                    });
//
//                                                } else {
//                                                    Toast.makeText(SeleccionRegistrar.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
//                                                    btnGoogle.setEnabled(true);
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onFailure(Call<User> call, Throwable t) {
//                                                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                    Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                } else if(t instanceof SocketTimeoutException) {
//                                                    Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                }
//                                                btnGoogle.setEnabled(true);
//                                            }
//                                        });
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<User> call, Throwable t) {
//                                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                        Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                    } else if(t instanceof SocketTimeoutException) {
//                                        Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                    }
//                                    btnGoogle.setEnabled(true);
//                                }
//                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<Cliente> call, Throwable t) {
                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                        Toast.makeText(SeleccionRegistrar.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        clienteCall.cancel();
                    } else if(t instanceof SocketTimeoutException) {
                        Toast.makeText(SeleccionRegistrar.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                        clienteCall.cancel();
                    }
                }
            });

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

    public void iniciarSesionFb(View view) {
        loginButton.performClick();
    }

    public void atras(View view) {
        finish();
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
                                Toast.makeText(SeleccionRegistrar.this, R.string.intentar_nuevamente, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                Toast.makeText(SeleccionRegistrar.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SeleccionRegistrar.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SeleccionRegistrar.this, "Usuario/Contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                    Toast.makeText(SeleccionRegistrar.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SeleccionRegistrar.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
//                            Intent serviceIntent = new Intent(SeleccionRegistrar.this, BuscarBeacon.class);
//                            ContextCompat.startForegroundService(SeleccionRegistrar.this, serviceIntent);
                        } else {
                            Toast.makeText(SeleccionRegistrar.this, R.string.administrador, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SeleccionRegistrar.this, R.string.administrador, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Cliente> call, Throwable t) {
                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                        Toast.makeText(SeleccionRegistrar.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SeleccionRegistrar.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(SeleccionRegistrar.this, "No se encontró al usuario", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void verificarObjetivo(int clienteID) {
        Toast.makeText(SeleccionRegistrar.this, "Bienvenido", Toast.LENGTH_SHORT).show();
        Call<Cliente> clienteCall = service.verificarSeleccionObjetivo(clienteID, false,false,false,false);
        clienteCall.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(SeleccionRegistrar.this, Objetivo.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(SeleccionRegistrar.this, Objetivo.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SeleccionRegistrar.this, ClubMain.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(SeleccionRegistrar.this, ClubMain.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {

            }
        });
    }
}
