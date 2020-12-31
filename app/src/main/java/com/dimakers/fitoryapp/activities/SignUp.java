package com.dimakers.fitoryapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.w3c.dom.Text;

import java.net.SocketTimeoutException;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
    FitoryService service = API.getApi().create(FitoryService.class);
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private CheckBox chHombre, chMujer;
    private EditText etNombre, etApellido, etCorreoElectronico, etContrasena, etCelular;
    private String idGoogle, idFacebook;
    private String social_media;
    private SharedPreferences sharedPreferences;
    private static final String FACEBOOK = "FACEBOOK";
    private static final String GOOGLE = "GOOGLE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        chHombre = findViewById(R.id.ch_hombre);
        chMujer = findViewById(R.id.ch_mujer);
        etNombre = findViewById(R.id.et_nombre);
        etApellido = findViewById(R.id.et_apellido);
        etCorreoElectronico = findViewById(R.id.et_correoelectronico);
        etContrasena = findViewById(R.id.et_contrasena);
        etCelular = findViewById(R.id.et_celular);
        //Cargar datos de inicio de sesión de Google y Facebook
        Intent intent = getIntent();
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES, MODE_PRIVATE);
        etNombre.setText(intent.getStringExtra(Variables.CLIENTENOMBRE));
        etApellido.setText(intent.getStringExtra(Variables.CLIENTEAPELLIDO));
        etCorreoElectronico.setText(intent.getStringExtra(Variables.EMAIL));
        idGoogle = intent.getStringExtra(Variables.GOOGLEID);
        idFacebook = intent.getStringExtra(Variables.FBID);
        social_media = intent.getStringExtra(Variables.SOCIAL_MEDIA);
        if (etNombre.getText().toString().equals("null")) {
            etNombre.getText().clear();
        }
        if (etApellido.getText().toString().equals("null")) {
            etApellido.getText().clear();
        }
        if (etCorreoElectronico.getText().toString().equals("null")) {
            etCorreoElectronico.getText().clear();
        }
        chHombre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chMujer.setChecked(!isChecked);
            }
        });
        chMujer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chHombre.setChecked(!isChecked);
            }
        });
    }

    public void atras(View view) {
        finish();
    }

    public void registrarCliente(View view) {
        view.setEnabled(false);
        boolean correcto = true;

        if (TextUtils.isEmpty(etCelular.getText())) {
            etCelular.setError("Ingrese su teléfono celular");
            etCelular.requestFocus();
            correcto = false;
        }

        if (etCelular.getText().toString().trim().length()<10 || !TextUtils.isDigitsOnly(etCelular.getText().toString().trim())) {
            etCelular.setError("Ingrese un teléfono celular válido");
            etCelular.requestFocus();
            correcto = false;
        }

        if (TextUtils.isEmpty(etContrasena.getText())) {
            etContrasena.setError("Ingrese su contraseña");
            etContrasena.requestFocus();
            correcto = false;
        }

        if (!isEmailValid(etCorreoElectronico.getText().toString().trim())) {
            etCorreoElectronico.setError("Ingrese un correo electrónico válido");
            etCorreoElectronico.requestFocus();
            correcto = false;
        }

        if (TextUtils.isEmpty(etCorreoElectronico.getText())) {
            etCorreoElectronico.setError("Ingrese un correo eletrónico");
            etCorreoElectronico.requestFocus();
            correcto = false;
        }

        if (TextUtils.isEmpty(etApellido.getText())) {
            etApellido.setError("Ingrese su apellido");
            etApellido.requestFocus();
            correcto = false;
        }

        if (TextUtils.isEmpty(etNombre.getText())) {
            etNombre.setError("Ingrese su nombre");
            etNombre.requestFocus();
            correcto = false;
        }

        if (correcto) {
            Call<RegistrarCelularResponse> registrarCelularCall;
            if (social_media==null) {
                registrarCelularCall = service.registrarCelular(etCorreoElectronico.getText().toString().trim(),
                        etContrasena.getText().toString().trim(),
                        etNombre.getText().toString().trim(),
                        etApellido.getText().toString().trim(),
                        chHombre.isChecked(),
                        chMujer.isChecked(),
                        etCelular.getText().toString().trim(),
                        "",
                        "");
            } else {
                if (social_media.equals("FACEBOOK")) {
                    registrarCelularCall = service.registrarCelular(etCorreoElectronico.getText().toString().trim(),
                            etContrasena.getText().toString().trim(),
                            etNombre.getText().toString().trim(),
                            etApellido.getText().toString().trim(),
                            chHombre.isChecked(),
                            chMujer.isChecked(),
                            etCelular.getText().toString().trim(),
                            "FACEBOOK",
                            idFacebook);
                } else if (social_media.equals("GOOGLE")) {
                    registrarCelularCall = service.registrarCelular(etCorreoElectronico.getText().toString().trim(),
                            etContrasena.getText().toString().trim(),
                            etNombre.getText().toString().trim(),
                            etApellido.getText().toString().trim(),
                            chHombre.isChecked(),
                            chMujer.isChecked(),
                            etCelular.getText().toString().trim(),
                            "GOOGLE",
                            idGoogle);
                } else {
                    registrarCelularCall = service.registrarCelular(etCorreoElectronico.getText().toString().trim(),
                            etContrasena.getText().toString().trim(),
                            etNombre.getText().toString().trim(),
                            etApellido.getText().toString().trim(),
                            chHombre.isChecked(),
                            chMujer.isChecked(),
                            etCelular.getText().toString().trim(),
                            "",
                            "");
                }
            }
            registrarCelularCall.enqueue(new Callback<RegistrarCelularResponse>() {
                @Override
                public void onResponse(Call<RegistrarCelularResponse> call, Response<RegistrarCelularResponse> response) {
                    if (response.isSuccessful()) {
                        RegistrarCelularResponse registro = response.body();
                        Toast.makeText(SignUp.this, ""+registro.getMensaje(), Toast.LENGTH_SHORT).show();
                        view.setEnabled(true);
                        if (registro.isSuccessful()) {
                            login();
                        }
                    } else {
                        Toast.makeText(SignUp.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                        view.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<RegistrarCelularResponse> call, Throwable t) {
                    view.setEnabled(true);
                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    } else if(t instanceof SocketTimeoutException) {
                        Toast.makeText(getApplicationContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
//            Call<User> userCall2 = service.obtenerUsuario(etCorreoElectronico.getText().toString().trim());
//            userCall2.enqueue(new Callback<User>() {
//                @Override
//                public void onResponse(Call<User> call, Response<User> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getResults().isEmpty()) {
//
//                            Call<User> userCall = service.crearUsuario(getUsuario(),getUsuario(),etContrasena.getText().toString().trim());
//                            userCall.enqueue(new Callback<User>() {
//                                @Override
//                                public void onResponse(Call<User> call, Response<User> response) {
//                                    if (response.isSuccessful()) {
//                                        User user = response.body();
//                                        Call<Cliente> clienteCall = null;
//                                        if (social_media==null) {
//                                            clienteCall = service.crearCliente(user.getId(),etNombre.getText().toString().trim(),etApellido.getText().toString().trim(), chHombre.isChecked(), chMujer.isChecked(), etCelular.getText().toString().trim());
//                                        } else {
//                                            if (social_media.equals("FACEBOOK")) {
//                                                clienteCall = service.crearClienteFacebook(user.getId(),etNombre.getText().toString().trim(),etApellido.getText().toString().trim(), chHombre.isChecked(), chMujer.isChecked(), idFacebook);
//                                            } else if (social_media.equals("GOOGLE")) {
//                                                clienteCall = service.crearClienteGoogle(user.getId(),etNombre.getText().toString().trim(),etApellido.getText().toString().trim(), chHombre.isChecked(), chMujer.isChecked(), idGoogle);
//                                            }
//                                        }
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
//                                                                Call<RegistrarCelularResponse> registrarCelularCall = service.registrarCelular(etCelular.getText().toString().trim());
//                                                                registrarCelularCall.enqueue(new Callback<RegistrarCelularResponse>() {
//                                                                    @Override
//                                                                    public void onResponse(Call<RegistrarCelularResponse> call, Response<RegistrarCelularResponse> response) {
//                                                                        if (response.isSuccessful()) {
//                                                                            if (response.body().isSuccessful()) {
//                                                                                Toast.makeText(SignUp.this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
//                                                                                /**
//                                                                                 *  Al descomentar el siguiente bloque de código se enviará a la pantalla de iniciar sesión
//                                                                                 *  al crearse un nuevo usuario
//                                                                                 */
//                //                                                                Intent intent = new Intent(SignUp.this, Login.class);
//                //                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                //                                                                startActivity(intent);
//                                                                                /**
//                                                                                 * Ahora se hara inicio de sesión directamente
//                                                                                 */
//                                                                                login();
//                                                                            } else {
//                                                                                Toast.makeText(SignUp.this, "No se pudo crear al usuario. Contacte al administrador.", Toast.LENGTH_SHORT).show();
//                                                                            }
//                                                                        }
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onFailure(Call<RegistrarCelularResponse> call, Throwable t) {
//                                                                        if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                                        } else if(t instanceof SocketTimeoutException) {
//                                                                            Toast.makeText(getApplicationContext(), getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                                        } else {
//                                                                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//                                                                        }
//                                                                    }
//                                                                });
//                                                            } else {
//                                                                Toast.makeText(SignUp.this, "No se pudo crear al usuario. Contacte al administrador.", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                                            if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                                Toast.makeText(SignUp.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                            } else if(t instanceof SocketTimeoutException) {
//                                                                Toast.makeText(SignUp.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    });
//                                                } else {
//                                                    Toast.makeText(SignUp.this, "Error. Consulte al administrador", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onFailure(Call<Cliente> call, Throwable t) {
//                                                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                                    Toast.makeText(SignUp.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                                } else if(t instanceof SocketTimeoutException) {
//                                                    Toast.makeText(SignUp.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                                    } else {
//                                        Toast.makeText(SignUp.this, "Error. Consulte al administrador", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<User> call, Throwable t) {
//                                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
//                                        Toast.makeText(SignUp.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                        userCall.cancel();
//                                    } else if(t instanceof SocketTimeoutException) {
//                                        Toast.makeText(SignUp.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
//                                        userCall.cancel();
//                                    }
//                                }
//                            });
//                        } else {
//                            Toast.makeText(SignUp.this, "El usuario ya se encuentra registrado", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<User> call, Throwable t) {
//
//                }
//            });
        } else {
            view.setEnabled(true);
        }
    }

    private boolean isEmailValid(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }

    public String getUsuario() {
        String s = Normalizer.normalize(etCorreoElectronico.getText().toString().toLowerCase(),Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    public void terminos(View view) {
        Intent intent = new Intent(this, TerminosCondicionesActivity.class);
        startActivity(intent);
//        Uri webpage = Uri.parse("https://fitory.com/Legal/");
//        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
    }

    private void login() {
        Call<UserLogin> userLoginCall = service.iniciarSesion(etCorreoElectronico.getText().toString().trim(),etContrasena.getText().toString().trim());
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
                                Toast.makeText(SignUp.this, R.string.intentar_nuevamente, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                                Toast.makeText(SignUp.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUp.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SignUp.this, "Usuario/Contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                    Toast.makeText(SignUp.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUp.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
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
//                            Intent serviceIntent = new Intent(SignUp.this, BuscarBeacon.class);
//                            ContextCompat.startForegroundService(SignUp.this, serviceIntent);
                        } else {
                            Toast.makeText(SignUp.this, R.string.administrador, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUp.this, R.string.administrador, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Cliente> call, Throwable t) {
                    if (!InternetConnectionStatus.isConnected(getApplicationContext())) {
                        Toast.makeText(SignUp.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUp.this, ""+R.string.administrador, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(SignUp.this, "No se encontró al usuario", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void verificarObjetivo(int clienteID) {
        Toast.makeText(SignUp.this, "Bienvenido", Toast.LENGTH_SHORT).show();
        Call<Cliente> clienteCall = service.verificarSeleccionObjetivo(clienteID, false,false,false,false);
        clienteCall.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(SignUp.this, Objetivo.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(SignUp.this, Objetivo.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SignUp.this, ClubMain.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(SignUp.this, ClubMain.class);
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
