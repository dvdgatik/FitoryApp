package com.dimakers.fitoryapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dimakers.fitoryapp.DialogUtils;
import com.dimakers.fitoryapp.PathUtils;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.RealPathUtil;
import com.dimakers.fitoryapp.TerminosCondicionesActivity;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.activities.ClubMain;
import com.dimakers.fitoryapp.activities.Login;
import com.dimakers.fitoryapp.activities.MainActivity;
import com.dimakers.fitoryapp.activities.Objetivo;
import com.dimakers.fitoryapp.activities.SeleccionCiudad;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Cliente;
import com.dimakers.fitoryapp.api.models.Objetivos;
import com.dimakers.fitoryapp.api.models.User;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static com.facebook.FacebookSdk.getApplicationContext;

public class Informacion extends Fragment {
    Fragment tarjetas = new MisTarjetas();
    String menu = "";
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageView imageView;
    Bitmap bitmap;
//    Button botonMisTarjetas;
    Button botonCambiarUbicacion;
    Button botonCerrarSesion;
    Spinner spObjetivos;
    GoogleSignInClient mGoogleSignInClient;
    LinearLayout cambiarFoto;
    int eleccion;
    String userId;
    int clienteId;
    Dialog alertDialog;
    TextView terminos;
    EditText etClienteNombre, etClienteApellido, etClienteTelefono, etClienteCorreoEletronico, etClienteContrasena;
    private boolean correcto = true;
    public static Informacion newInstance(){
        Informacion informacion = new Informacion();
        return informacion;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        imageView = view.findViewById(R.id.imagen);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
//        botonMisTarjetas = view.findViewById(R.id.boton_mis_tarjetas);
        botonCambiarUbicacion = view.findViewById(R.id.boton_cambiar_ubicacion);
        botonCerrarSesion = view.findViewById(R.id.boton_cerrar_sesion);
        terminos = view.findViewById(R.id.terminos);
        terminos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),TerminosCondicionesActivity.class);
                startActivity(intent);
            }
        });
        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        editor.remove(Variables.LONGITUD);
                        editor.remove(Variables.LATITUD);
//                        editor.remove(Variables.SELECCIONUBICACION);
                        editor.commit();
                        LoginManager.getInstance().logOut();
                        mGoogleSignInClient.signOut();
                        Intent intent = new Intent(getContext(), Login.class);
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
        });
        cambiarFoto = view.findViewById(R.id.cambiar_foto);
        cambiarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu = "CAMARA";
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext(),R.style.DialogCambiarFoto);
//                alertDialog.setView(R.layout.dialog_cambiar_foto);
                alertDialog.setMessage("Cambiar foto");
                alertDialog.setPositiveButton("Tomar foto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        elegirTomarFoto();
                        eleccion = 0;
                        menu = "CAMARA";
                    }
                });
                alertDialog.setNegativeButton("Elegir una foto existente", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        menu = "GALERIA";
                        elegirFotoExistente();
                        eleccion = 1;
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
//                TextView tvTomarFoto = dialog.findViewById(R.id.tv_tomar_foto);
//                tvTomarFoto.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "Tomar foto", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                TextView tvElegirFotoExistente = dialog.findViewById(R.id.tv_elegir_foto_existente);
//                tvElegirFotoExistente.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "Elegir foto existente", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
        etClienteNombre = view.findViewById(R.id.et_cliente_nombre);
        etClienteApellido = view.findViewById(R.id.et_cliente_apellido);
        etClienteTelefono = view.findViewById(R.id.et_cliente_telefono);
        etClienteCorreoEletronico =view.findViewById(R.id.et_cliente_correo_electronico);
        etClienteContrasena = view.findViewById(R.id.et_cliente_contrasena);
        spObjetivos = view.findViewById(R.id.sp_objetivos);
        //Iniciar spinner de objetivos
        ArrayList<String> objetivos = new ArrayList<>();
        objetivos.add("Salud");
        objetivos.add("Convivir");
        objetivos.add("Verme bien");
        objetivos.add("Diversión");
        spObjetivos.setAdapter(new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item, objetivos){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);

                return view;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.GRAY);

                return view;
            }
        });

        Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(R.drawable.profile_preview).into(imageView);
        String clienteNombre = sharedPreferences.getString(Variables.CLIENTENOMBRE,"");
        String clienteApellido = sharedPreferences.getString(Variables.CLIENTEAPELLIDO,"");
        String clienteEmail = sharedPreferences.getString(Variables.EMAIL,"");
        String clienteTelefono = sharedPreferences.getString(Variables.TELEFONO,"");
        String clienteFotografia = sharedPreferences.getString(Variables.FOTO,"");
        int clienteObjetivo = sharedPreferences.getInt(Variables.CLIENTEOBJETIVO,0);
        etClienteNombre.setText(clienteNombre);
        etClienteApellido.setText(clienteApellido);
        etClienteCorreoEletronico.setText(clienteEmail);
        etClienteTelefono.setText(clienteTelefono);
        Glide.with(getActivity()).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(clienteFotografia).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new Dialog(getContext());
                alertDialog.setContentView(R.layout.dialog_foto);
                alertDialog.show();
                ImageView profilePicture = alertDialog.findViewById(R.id.profile_picture);
                Glide.with(getActivity()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(clienteFotografia).into(profilePicture);
            }
        });
        Button botonGuardarCambios = view.findViewById(R.id.boton_guardar_cambios);
        userId = sharedPreferences.getString(Variables.IDUSER,"");
        clienteId = sharedPreferences.getInt(Variables.CLIENTEID,0);
        botonGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clienteId == 0 || userId.equals("")) {
                    Toast.makeText(getContext(), "No se puede actualizar el usuario", Toast.LENGTH_SHORT).show();
                } else {
                    //Actualizar objetivos del cliente
                    switch (spObjetivos.getSelectedItem().toString()) {
                        case "Salud":
                            actualizarObjetivos(true, false, false, false);
                            break;
                        case "Convivir":
                            actualizarObjetivos(false, true, false, false);
                            break;
                        case "Verme bien":
                            actualizarObjetivos(false, false, true, false);
                            break;
                        case "Diversión":
                            actualizarObjetivos(false, false, false, true);
                            break;
                    }
                }
            }
        });

        botonCambiarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGPSEnabled(getApplicationContext())) {
                    Intent intent = new Intent(getContext(), SeleccionCiudad.class);
                    int ciudadId = sharedPreferences.getInt(Variables.CIUDADID,0);
                    if (ciudadId!=0) {
                        intent.putExtra(Variables.CIUDADID,ciudadId);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Desactive su servicio de ubicación para continuar.", Toast.LENGTH_SHORT).show();
                }
//                if (!isGPSEnabled(getActivity())) {
//                    editor.remove(Variables.CIUDADID);
//                    editor.commit();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Desactive el servicio de localización para cambiar su ubicación manualmente.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        spObjetivos.setSelection(clienteObjetivo);
    }

    public boolean isGPSEnabled(Context mContext)
    {
        String locationProviders = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void elegirTomarFoto() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            tomarFoto();
        } else {
            if (checkSelfPermission(getContext(),Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ) {
                if (this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                    requestPermissionCamara();
                } else if (!sharedPreferences.getBoolean("PERMISSION_CAMERA",false)) {
                    requestPermissionCamara();
                    editor.putBoolean("PERMISSION_CAMERA", true);
                    editor.commit();
                } else {
                    Toast.makeText(getContext(), "Por favor otorge los permisos necesarios a la aplicación", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            } else {
                tomarFoto();
            }
        }
    }

    private void elegirFotoExistente() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            startActivityForResult(intent,Variables.REQUEST_ACTION_GET_CONTENT);
        } else {
            if (checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (this.shouldShowRequestPermissionRationale( Manifest.permission.WRITE_EXTERNAL_STORAGE) || this.shouldShowRequestPermissionRationale( Manifest.permission.READ_EXTERNAL_STORAGE) ) {
                    requestPermissionAlmacenamiento();
//                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
//                    builder.setMessage("Es necesario otorgar el permiso de lectura de archivos para continuar.");
//                    builder.setTitle("Permisos");
//                    builder.setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            requestPermission();
//                        }
//                    });
//
//                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    android.support.v7.app.AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
                } else if (!sharedPreferences.getBoolean("PERMISSION_STORAGE",false)) {
                    requestPermissionAlmacenamiento();
                    editor.putBoolean("PERMISSION_STORAGE", true);
                    editor.commit();
                } else {
                    Toast.makeText(getContext(), "Por favor otorge los permisos necesarios a la aplicación", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent,Variables.REQUEST_ACTION_GET_CONTENT);
            }
        }
    }

    private void requestPermissionAlmacenamiento () {
        this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},123);
    }

    private void requestPermissionCamara () {
        this.requestPermissions(new String[]{Manifest.permission.CAMERA},456);
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Aguillon");
        if (storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
//             Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(), "Ocurrió un error al tomar la fotografía. Por favor intente de nuevo.", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.dimakers.fitoryapp",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Variables.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_informacion,container,false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Uri imageUri = data.getData();

//        Log.e("FITORYFILESEXCEPTION: ", PathUtils.getPath(getContext(), imageUri));
//        if (data != null && mCurrentPhotoPath!=null) {
        if (data != null || mCurrentPhotoPath!=null) {
            switch (requestCode) {
                case Variables.REQUEST_ACTION_GET_CONTENT:
                    Uri returnUri = data.getData();
                    try {
//                        String realPath = RealPathUtil.getRealPathFromURI_API19(getActivity(), data.getData());
                        String realPath = PathUtils.getPath(getContext(), data.getData());
                        DialogUtils.getAlertDialog(getActivity()).show();
                        uploadImage(returnUri, realPath);
                    } catch (Exception e) {
                        Log.e("FITORYFILESEXCEPTION: ", e.getMessage());
                        Toast.makeText(getActivity(), "No se pudo actualizar la foto de perfil.", Toast.LENGTH_SHORT).show();
                        if (DialogUtils.alertDialog!=null) {
                            DialogUtils.alertDialog.dismiss();
                        }
                    }
                    break;
                case Variables.REQUEST_IMAGE_CAPTURE:
                    if (resultCode == RESULT_OK) {
                        if (!mCurrentPhotoPath.equals("")) {
                            File f = new File(mCurrentPhotoPath);
                            Uri contentUri = Uri.fromFile(f);
                            DialogUtils.getAlertDialog(getActivity()).show();
                            uploadImage2(contentUri,mCurrentPhotoPath);
                            mCurrentPhotoPath = "";
                        }
                    }
                    break;
            }
        }
    }

    private void uploadImage2(Uri filePath, String realPath) {

//        Crear objeto RequestBody de file
//        File file = new File(realPath);
////        File file = new File("/storage/F88A-1B00/te creo... pero mi metralleta no.jpg");
//
        Log.i("FITORYAPP","filepath: "+filePath+"\n");
        Log.i("FITORYAPP","realpath: "+realPath+"\n");
////        Log.i("FITORYAPP","sdpath: "+Environment.getExternalStorageDirectory()+"\n");
//
//        file = saveBitmapToFile(file);

        try {


            int clienteId = sharedPreferences.getInt(Variables.CLIENTEID,0);

            if (clienteId != 0) {


//
//                RequestBody clienteIdPart =
//                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(clienteId));
//
//
//

                RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM,
                        String.valueOf(clienteId));

                File originalFile = new File(realPath);
                if (!originalFile.exists()) {
                    final String docId = DocumentsContract.getDocumentId(filePath);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    String storageDefinition;
                    Log.e("FILESheh", split[0] + " " + split[1]);
                    originalFile = new File("/storage/"+split[0]+"/"+split[1]);
                }

                if (!originalFile.exists()) {
                    String sdcardpath = "";

                    //Datas
                    if (new File("/data/sdext4/").exists() && new File("/data/sdext4/").canRead()){
                        sdcardpath = "/data/sdext4/";
                    }
                    if (new File("/data/sdext3/").exists() && new File("/data/sdext3/").canRead()){
                        sdcardpath = "/data/sdext3/";
                    }
                    if (new File("/data/sdext2/").exists() && new File("/data/sdext2/").canRead()){
                        sdcardpath = "/data/sdext2/";
                    }
                    if (new File("/data/sdext1/").exists() && new File("/data/sdext1/").canRead()){
                        sdcardpath = "/data/sdext1/";
                    }
                    if (new File("/data/sdext/").exists() && new File("/data/sdext/").canRead()){
                        sdcardpath = "/data/sdext/";
                    }

                    //MNTS

                    if (new File("mnt/sdcard/external_sd/").exists() && new File("mnt/sdcard/external_sd/").canRead()){
                        sdcardpath = "mnt/sdcard/external_sd/";
                    }
                    if (new File("mnt/extsdcard/").exists() && new File("mnt/extsdcard/").canRead()){
                        sdcardpath = "mnt/extsdcard/";
                    }
                    if (new File("mnt/external_sd/").exists() && new File("mnt/external_sd/").canRead()){
                        sdcardpath = "mnt/external_sd/";
                    }
                    if (new File("mnt/emmc/").exists() && new File("mnt/emmc/").canRead()){
                        sdcardpath = "mnt/emmc/";
                    }
                    if (new File("mnt/sdcard0/").exists() && new File("mnt/sdcard0/").canRead()){
                        sdcardpath = "mnt/sdcard0/";
                    }
                    if (new File("mnt/sdcard1/").exists() && new File("mnt/sdcard1/").canRead()){
                        sdcardpath = "mnt/sdcard1/";
                    }
                    if (new File("mnt/sdcard/").exists() && new File("mnt/sdcard/").canRead()){
                        sdcardpath = "mnt/sdcard/";
                    }

                    //Storages
                    if (new File("/storage/removable/sdcard1/").exists() && new File("/storage/removable/sdcard1/").canRead()){
                        sdcardpath = "/storage/removable/sdcard1/";
                    }
                    if (new File("/storage/external_SD/").exists() && new File("/storage/external_SD/").canRead()){
                        sdcardpath = "/storage/external_SD/";
                    }
                    if (new File("/storage/ext_sd/").exists() && new File("/storage/ext_sd/").canRead()){
                        sdcardpath = "/storage/ext_sd/";
                    }
                    if (new File("/storage/sdcard1/").exists() && new File("/storage/sdcard1/").canRead()){
                        sdcardpath = "/storage/sdcard1/";
                    }
                    if (new File("/storage/sdcard0/").exists() && new File("/storage/sdcard0/").canRead()){
                        sdcardpath = "/storage/sdcard0/";
                    }
                    if (new File("/storage/sdcard/").exists() && new File("/storage/sdcard/").canRead()){
                        sdcardpath = "/storage/sdcard/";
                    }
                    if (sdcardpath.contentEquals("")){
                        sdcardpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    }

                    final String docId = DocumentsContract.getDocumentId(filePath);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    String storageDefinition;
                    Log.e("FILESheh", split[0] + " " + split[1]);
                    originalFile = new File(sdcardpath+split[0]+"/"+split[1]);
                }

                originalFile = saveBitmapToFile(originalFile);


                /**
                 * motog4 100, moto e5 falla sd, j7 falla sd
                 */
//                RequestBody filePart = RequestBody.create(
//                        MediaType.parse("multipart/form-data"),
//                        originalFile
//                        );


                RequestBody filePart = RequestBody.create(
                        MediaType.parse(getMimeType(realPath)),
                        originalFile
                );




                MultipartBody.Part file = MultipartBody.Part.createFormData("foto",
                        originalFile.getName(),
                        filePart);

//                Call<ResponseBody> fotoCall = service.actualizarFoto(clienteIdPart, fotoPart);
                Call<ResponseBody> fotoCall = service.actualizarFoto(descriptionPart, file);
                fotoCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        DialogUtils.alertDialog.dismiss();
                        if (response.isSuccessful()) {
//                        Cliente cliente = response.body();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                String fotoUrl = Variables.MEDIA_URL+jsonObject.getString("fotoUrl");
                                String message = jsonObject.getString("mensaje");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                editor = sharedPreferences.edit();
                                editor.putString(Variables.FOTO, fotoUrl);
                                editor.commit();
                                Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(fotoUrl).into(imageView);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog = new Dialog(getContext());
                                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        alertDialog.setContentView(R.layout.dialog_foto);
                                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertDialog.show();
                                        ImageView profilePicture = alertDialog.findViewById(R.id.profile_picture);
                                        Glide.with(getActivity()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(fotoUrl).into(profilePicture);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.administrador, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Toast.makeText(getActivity(), "No se pudo actualizar su foto de perfil. Intente nuevamente.", Toast.LENGTH_LONG).show();
                        DialogUtils.alertDialog.dismiss();
                    }
                });
            } else {
                Toast.makeText(getActivity(),   "No se encontró cliente id", Toast.LENGTH_SHORT).show();
                DialogUtils.alertDialog.dismiss();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "No se pudo actualizar la foto de perfil", Toast.LENGTH_SHORT).show();
            Log.e("EXCEPTIONFILE", e.getMessage());
            DialogUtils.alertDialog.dismiss();
        }



    }


    private void uploadImage(Uri filePath, String realPath) {

//        Crear objeto RequestBody de file
//        File file = new File(realPath);
////        File file = new File("/storage/F88A-1B00/te creo... pero mi metralleta no.jpg");
//
        Log.i("FITORYAPP","filepath: "+filePath+"\n");
        Log.i("FITORYAPP","realpath: "+realPath+"\n");
////        Log.i("FITORYAPP","sdpath: "+Environment.getExternalStorageDirectory()+"\n");
//
//        file = saveBitmapToFile(file);

        try {


            int clienteId = sharedPreferences.getInt(Variables.CLIENTEID,0);

            if (clienteId != 0) {


//
//                RequestBody clienteIdPart =
//                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(clienteId));
//
//
//

                RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM,
                        String.valueOf(clienteId));

                File originalFile = new File(realPath);
                if (!originalFile.exists()) {
                    final String docId = DocumentsContract.getDocumentId(filePath);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    String storageDefinition;
                    Log.e("FILESheh", split[0] + " " + split[1]);
                    originalFile = new File("/storage/"+split[0]+"/"+split[1]);
                }

                if (!originalFile.exists()) {
                    String sdcardpath = "";

                    //Datas
                    if (new File("/data/sdext4/").exists() && new File("/data/sdext4/").canRead()){
                        sdcardpath = "/data/sdext4/";
                    }
                    if (new File("/data/sdext3/").exists() && new File("/data/sdext3/").canRead()){
                        sdcardpath = "/data/sdext3/";
                    }
                    if (new File("/data/sdext2/").exists() && new File("/data/sdext2/").canRead()){
                        sdcardpath = "/data/sdext2/";
                    }
                    if (new File("/data/sdext1/").exists() && new File("/data/sdext1/").canRead()){
                        sdcardpath = "/data/sdext1/";
                    }
                    if (new File("/data/sdext/").exists() && new File("/data/sdext/").canRead()){
                        sdcardpath = "/data/sdext/";
                    }

                    //MNTS

                    if (new File("mnt/sdcard/external_sd/").exists() && new File("mnt/sdcard/external_sd/").canRead()){
                        sdcardpath = "mnt/sdcard/external_sd/";
                    }
                    if (new File("mnt/extsdcard/").exists() && new File("mnt/extsdcard/").canRead()){
                        sdcardpath = "mnt/extsdcard/";
                    }
                    if (new File("mnt/external_sd/").exists() && new File("mnt/external_sd/").canRead()){
                        sdcardpath = "mnt/external_sd/";
                    }
                    if (new File("mnt/emmc/").exists() && new File("mnt/emmc/").canRead()){
                        sdcardpath = "mnt/emmc/";
                    }
                    if (new File("mnt/sdcard0/").exists() && new File("mnt/sdcard0/").canRead()){
                        sdcardpath = "mnt/sdcard0/";
                    }
                    if (new File("mnt/sdcard1/").exists() && new File("mnt/sdcard1/").canRead()){
                        sdcardpath = "mnt/sdcard1/";
                    }
                    if (new File("mnt/sdcard/").exists() && new File("mnt/sdcard/").canRead()){
                        sdcardpath = "mnt/sdcard/";
                    }

                    //Storages
                    if (new File("/storage/removable/sdcard1/").exists() && new File("/storage/removable/sdcard1/").canRead()){
                        sdcardpath = "/storage/removable/sdcard1/";
                    }
                    if (new File("/storage/external_SD/").exists() && new File("/storage/external_SD/").canRead()){
                        sdcardpath = "/storage/external_SD/";
                    }
                    if (new File("/storage/ext_sd/").exists() && new File("/storage/ext_sd/").canRead()){
                        sdcardpath = "/storage/ext_sd/";
                    }
                    if (new File("/storage/sdcard1/").exists() && new File("/storage/sdcard1/").canRead()){
                        sdcardpath = "/storage/sdcard1/";
                    }
                    if (new File("/storage/sdcard0/").exists() && new File("/storage/sdcard0/").canRead()){
                        sdcardpath = "/storage/sdcard0/";
                    }
                    if (new File("/storage/sdcard/").exists() && new File("/storage/sdcard/").canRead()){
                        sdcardpath = "/storage/sdcard/";
                    }
                    if (sdcardpath.contentEquals("")){
                        sdcardpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    }

                    final String docId = DocumentsContract.getDocumentId(filePath);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    String storageDefinition;
                    Log.e("FILESheh", split[0] + " " + split[1]);
                    originalFile = new File(sdcardpath+split[0]+"/"+split[1]);
                }

//                originalFile = saveBitmapToFile(originalFile);


                /**
                 * motog4 100, moto e5 falla sd, j7 falla sd
                 */
//                RequestBody filePart = RequestBody.create(
//                        MediaType.parse("multipart/form-data"),
//                        originalFile
//                        );


                RequestBody filePart = RequestBody.create(
                        MediaType.parse(getMimeType(realPath)),
                        originalFile
                        );




                MultipartBody.Part file = MultipartBody.Part.createFormData("foto",
                        originalFile.getName(),
                        filePart);

//                Call<ResponseBody> fotoCall = service.actualizarFoto(clienteIdPart, fotoPart);
                Call<ResponseBody> fotoCall = service.actualizarFoto(descriptionPart, file);
                fotoCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        DialogUtils.alertDialog.dismiss();
                        if (response.isSuccessful()) {
//                        Cliente cliente = response.body();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                String fotoUrl = Variables.MEDIA_URL+jsonObject.getString("fotoUrl");
                                String message = jsonObject.getString("mensaje");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                editor = sharedPreferences.edit();
                                editor.putString(Variables.FOTO, fotoUrl);
                                editor.commit();
                                Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(fotoUrl).into(imageView);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog = new Dialog(getContext());
                                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        alertDialog.setContentView(R.layout.dialog_foto);
                                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertDialog.show();
                                        ImageView profilePicture = alertDialog.findViewById(R.id.profile_picture);
                                        Glide.with(getActivity()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(fotoUrl).into(profilePicture);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.administrador, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Toast.makeText(getActivity(), "No se pudo actualizar su foto de perfil. Intente nuevamente.", Toast.LENGTH_LONG).show();
                        DialogUtils.alertDialog.dismiss();
                    }
                });
            } else {
                Toast.makeText(getActivity(),   "No se encontró cliente id", Toast.LENGTH_SHORT).show();
                DialogUtils.alertDialog.dismiss();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "No se pudo actualizar la foto de perfil", Toast.LENGTH_SHORT).show();
            Log.e("EXCEPTIONFILE", e.getMessage());
            DialogUtils.alertDialog.dismiss();
        }



    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void actualizarObjetivos(boolean salud, boolean convivir, boolean vermeBien, boolean diversion) {
        int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
        if (idCliente!=0) {

            if (TextUtils.isEmpty(etClienteTelefono.getText())) {
                etClienteTelefono.setError("Ingrese su teléfono celular");
                etClienteTelefono.requestFocus();
                correcto = false;
            }

            if (etClienteTelefono.getText().toString().trim().length()<10 || !TextUtils.isDigitsOnly(etClienteTelefono.getText().toString().trim())) {
                etClienteTelefono.setError("Ingrese un teléfono celular válido");
                etClienteTelefono.requestFocus();
                correcto = false;
            }

            if (correcto) {
                Call<ResponseBody> call = service.actualizarObjetivo(String.valueOf(idCliente),new Objetivos(salud,convivir,vermeBien,diversion));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Call<Cliente> clienteCall1 = service.actualizarCliente(clienteId,
                                    etClienteNombre.getText().toString().trim(),
                                    etClienteApellido.getText().toString().trim(),
                                    etClienteTelefono.getText().toString().trim());
                            clienteCall1.enqueue(new Callback<Cliente>() {
                                @Override
                                public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Información actualizada con éxito", Toast.LENGTH_SHORT).show();
                                        Cliente cliente = response.body();
                                        editor.putString(Variables.CLIENTENOMBRE,cliente.getNombre());
                                        editor.putString(Variables.CLIENTEAPELLIDO, cliente.getApellido());
                                        editor.putString(Variables.TELEFONO, cliente.getTelefono());
                                        editor.putInt(Variables.CLIENTEOBJETIVO, spObjetivos.getSelectedItemPosition());
                                        editor.commit();

                                        // Llamada al api para actualizar la información del usuario
                                        Call<User> userCall = service.actualizarContrasena(Integer.parseInt(userId), etClienteContrasena.getText().toString().trim());
                                        userCall.enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                if (!response.isSuccessful()) {
//                                Toast.makeText(getContext(), "No se pudo actualizar el usuario. Intente de nuevo.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    etClienteContrasena.setText("");
                                                    etClienteContrasena.requestFocus();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {
                                                Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        Toast.makeText(getActivity(), "No se pudo actualizar su información", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Cliente> call, Throwable t) {
                                    Toast.makeText(getActivity(), "Error al actualizar usuar", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Error. Contacte al administrador.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "No internet", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                correcto = true;
            }

        }
    }


    public File saveBitmapToFile(File file){
        try {

            ExifInterface oldExif = new ExifInterface(file.getPath());
            String exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            if (exifOrientation != null) {
                ExifInterface newExif = new ExifInterface(file.getPath());
                newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
                newExif.saveAttributes();
            }

            return file;
        } catch (Exception e) {
            return null;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0, len = permissions.length; i < len; i++) {
            switch (requestCode) {
            case 123:
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    elegirFotoExistente();
                    return;
                }
                break;
            case 456:
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    elegirTomarFoto();
                    return;
                }
                break;

        }
        }
    }

}
