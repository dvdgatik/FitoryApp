package com.dimakers.fitoryapp.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.dimakers.fitoryapp.SingleShotLocationProvider;
import com.dimakers.fitoryapp.activities.ClubMain;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.activities.SeleccionCiudad;
import com.dimakers.fitoryapp.adapters.Sucursales;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Ciudad;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Clubes extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private final String TAG = "FITORYOUTPUT";
    FitoryService service = API.getApi().create(FitoryService.class);
    RecyclerView rvClubes;
    Sucursales adapter;
    ImageView ivBuscadorCancel;
    EditText etBusquedaUsuario;
    TextView tvSinSucursales;
    Dialog alertDialog;
    private View view;
    private float longitud;
    private float latitud;
    private FusedLocationProviderClient fusedLocationClient;
    LocationManager mlocManager;
    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 1000 * 10 * 1; // 1 minutol
    //Minima distancia para updates en metros.
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = 1; // 1.5 metros
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    private Handler mHandler;
    private static int firstVisibleInListview;
    private TextView tvClubesTitle;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        startLoadingAnimation();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (isGPSEnabled(getApplicationContext())) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                editor.putFloat(Variables.LATITUD,(float)location.getLatitude());
                                editor.putFloat(Variables.LONGITUD,(float)location.getLongitude());
                                latitud = (float) location.getLatitude();
                                longitud = (float) location.getLongitude();
                                if (Variables.sucursales.isEmpty()) {
                                    cargarSucursalesLocation(view);
                                }
                            } else {
                                Toast.makeText(getActivity(), "No se pudo obtener ubicación", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getActivity(),ClubMain.class);
//                                startActivity(intent);
//                                getActivity().finish();
                            }
                        }
                    });
//            mLocationClient = new GoogleApiClient.Builder(getApplicationContext())
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
////            mHandler = new Handler();
////            mHandler.postDelayed(mExpiredRunnable, 10 * 1000);
//            mLocationClient.connect();
//            mLocationRequest.setInterval(10000);
//            mLocationRequest.setFastestInterval(6000);
//            mLocationClient.connect();
        }

        int ciudadId = sharedPreferences.getInt(Variables.CIUDADID, 0);
        if (ciudadId != 0 && isGPSEnabled(getApplicationContext())) {
            if (alertDialog != null) {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        }

        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


//        if (isGPSEnabled(getApplicationContext())) {
//            mlocManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//
//        }

        this.view = view;
        adapter = new Sucursales(getActivity());

//        int ciudadID = sharedPreferences.getInt(Variables.CIUDADID, 0);
//        if (ciudadID == 0 && !isGPSEnabled(getApplicationContext())) {
//            getActivity().finish();
//            Intent intent = new Intent(getActivity(), SeleccionCiudad.class);
//            startActivity(intent);
//            return;
//        }
        tvSinSucursales = view.findViewById(R.id.tv_sin_sucursales);
        rvClubes = view.findViewById(R.id.rv_sucursales);
        rvClubes.setHasFixedSize(true);

        rvClubes.setAdapter(adapter);
        tvClubesTitle = view.findViewById(R.id.tv_clubes_title);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        rvClubes.setLayoutManager(layoutManager);
//        rvClubes.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (recyclerView.getScrollY() ==0 )
//                    Toast.makeText(getApplicationContext(), "TOP!", Toast.LENGTH_SHORT).show();
//            }
//        });
        rvClubes.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layout = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layout.findFirstCompletelyVisibleItemPosition() == 0 &&
                        layout.findFirstVisibleItemPosition() == 0 &&
                        layout.findLastCompletelyVisibleItemPosition() == 0 &&
                        layout.findLastVisibleItemPosition() == 1) {
                    tvClubesTitle.setVisibility(View.VISIBLE);
                } else {
                    tvClubesTitle.setVisibility(View.INVISIBLE);
                }
            }
        });
//        adapterFull.clear();
//        sucursales.clear();
//        adapter.update(Variables.sucursales);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        if (Variables.sucursales.isEmpty()) {
            if (isGPSEnabled(getApplicationContext())) {
                cargarSucursalesLocation(view);
            } else {
                cargarSucursales(view);
            }
        }

        if (Variables.sucursales.isEmpty()) {
            cargarSucursales(view);
            //Obtener sucursales
//            Call<Sucursal> sucursalCall = service.obtenerSucursales(true,ciudadID);
//            startLoadingAnimation();
//            sucursalCall.enqueue(new Callback<Sucursal>() {
//                @Override
//                public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
//                    if (response.isSuccessful()) {
//                        if (!response.body().getResults().isEmpty()) {
//                            Variables.sucursales = response.body().getResults();
//                            adapter.update(Variables.sucursales);
//                        }
//                        alertDialog.dismiss();
//                    } else {
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Sucursal> call, Throwable t) {
//
//                }
//            });
//            if (!isGPSEnabled(getApplicationContext())) {
//            }

        } else {
            if (alertDialog != null) {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
//            adapter.update(Variables.sucursales);
//            cargarSucursales(view);
//            adapter.update(Variables.sucursales);
            adapter.update(Variables.sucursales);
//            cargarSucursales(view);
        }

        etBusquedaUsuario = view.findViewById(R.id.et_busqueda_usuario);
        ivBuscadorCancel = view.findViewById(R.id.iv_buscador_cancel);
        etBusquedaUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userInput = s.toString().toLowerCase();
                ArrayList<Sucursal> nuevaLista = new ArrayList<>();
                for (Sucursal sucursal : Variables.sucursales) {
                    String resultado = Normalizer.normalize(sucursal.getNombre().toLowerCase(), Normalizer.Form.NFD);
                    resultado = resultado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                    if (resultado.contains(userInput)) {
                        nuevaLista.add(sucursal);
                    }
                }
//                adapter.clear();
                adapter.clear();
                adapter.update(nuevaLista);
                if (adapter.getItemCount() == 0) {
                    tvSinSucursales.setVisibility(View.VISIBLE);
                } else {
                    tvSinSucursales.setVisibility(View.INVISIBLE);
                }
//                adapterFull.update(nuevaLista);
//                nuevaLista.clear();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivBuscadorCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etBusquedaUsuario.setText("");
                InputMethodManager inputManager = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                try {
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (NullPointerException e) {

                }
            }
        });
//        adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
//            @Override
//            public void onBottomReached(int position) {
//                Toast.makeText(getApplicationContext(), "Reached BOTTOM", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void cargarSucursalesLocation(View view) {
        try {
            Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitud, longitud, 1);
//            Toast.makeText(getApplicationContext(), "GEO LAUNCHED $$", Toast.LENGTH_SHORT).show();
            if (addresses.isEmpty()) {
                //Waiting for location
                obtenerSucursalesActivas();
            } else {
                if (addresses.size() > 0) {
                    //                        addres.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                    editor.putString(Variables.CIUDAD, addresses.get(0).getLocality());
                    editor.commit();
                    String cityName = addresses.get(0).getLocality();
//                    Toast.makeText(getApplicationContext(), addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
//                                Log.d("LOCATION",addresses.get(0).getLocality());
                    if (cityName != null) {
                        if (!cityName.equals("")) {
                            //Llamar al método que trae a las sucursales con el cityid que se encuentre
                            Call<Ciudad> ciudadCall = service.obtenerCiudadId(cityName);
                            ciudadCall.enqueue(new Callback<Ciudad>() {
                                @Override
                                public void onResponse(Call<Ciudad> call, Response<Ciudad> response) {

                                    if (response.isSuccessful()) {
                                        if (!response.body().getResults().isEmpty()) {
                                            Ciudad ciudad = response.body().getResults().get(0);
                                            //                                                Toast.makeText(getApplicationContext(), ""+ciudad.getId(), Toast.LENGTH_SHORT).show();
                                            editor.putInt(Variables.CIUDADID, ciudad.getId());
                                            editor.commit();
                                            cargarSucursales(view);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "No se pudo encontrar su ubicación. Cambia tu ubicación manualmente", Toast.LENGTH_SHORT).show();
//                                                        Intent intent = new Intent(getContext(), SeleccionCiudad.class);
//                                                        startActivity(intent);
                                            obtenerSucursalesActivas();
                                            if (alertDialog != null) {
                                                if (alertDialog.isShowing()) {
                                                    alertDialog.dismiss();
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Ciudad> call, Throwable t) {
                                    if (alertDialog != null) {
                                        if (alertDialog.isShowing()) {
                                            alertDialog.dismiss();
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(), "No se pudo encontrar su ubicación. Cambia tu ubicación manualmente", Toast.LENGTH_SHORT).show();
                                    obtenerSucursalesActivas();
//
//                                                Intent intent = new Intent(getContext(), SeleccionCiudad.class);
//                                                startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "No se pudo encontrar su ubicación. Cambia tu ubicación manualmente", Toast.LENGTH_SHORT).show();
                            if (alertDialog != null) {
                                if (alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                }
                            }
                            obtenerSucursalesActivas();
//                                        Intent intent = new Intent(getContext(), SeleccionCiudad.class);
//                                        startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No se pudo encontrar su ubicación. Cambia tu ubicación manualmente", Toast.LENGTH_SHORT).show();

////Llamar al método que trae a las sucursales con el cityid que se encuentre
                        String ciudad = sharedPreferences.getString(Variables.CIUDAD, "");
                        Call<Ciudad> ciudadCall = service.obtenerCiudadId(ciudad);
                        ciudadCall.enqueue(new Callback<Ciudad>() {
                            @Override
                            public void onResponse(Call<Ciudad> call, Response<Ciudad> response) {
                                if (response.isSuccessful()) {
                                    if (!response.body().getResults().isEmpty()) {
                                        Ciudad ciudad = response.body().getResults().get(0);
                                        //                                                Toast.makeText(getApplicationContext(), ""+ciudad.getId(), Toast.LENGTH_SHORT).show();
                                        editor.putInt(Variables.CIUDADID, ciudad.getId());
                                        editor.commit();
                                        cargarSucursales(view);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "No se pudo encontrar su ubicación. Cambia tu ubicación manualmente", Toast.LENGTH_SHORT).show();
//                                                        Intent intent = new Intent(getContext(), SeleccionCiudad.class);
//                                                        startActivity(intent);
                                        obtenerSucursalesActivas();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Ciudad> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "No se pudo encontrar su ubicación. Cambia tu ubicación manualmente", Toast.LENGTH_SHORT).show();
                                if (alertDialog != null) {
                                    if (alertDialog.isShowing()) {
                                        alertDialog.dismiss();
                                    }
                                }
//
//                                                Intent intent = new Intent(getContext(), SeleccionCiudad.class);
//                                                startActivity(intent);
                            }
                        });
//                                    Intent intent = new Intent(getContext(), SeleccionCiudad.class);
//                                    startActivity(intent);
                    }
                } else {
                    obtenerSucursalesActivas();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
//            Toast.makeText(getApplicationContext(), "Exception", Toast.LENGTH_SHORT).show();
            obtenerSucursalesActivas();
        }
    }

    private void cargarSucursales(View view) {
        adapter = new Sucursales(getContext());
        int ciudadID = sharedPreferences.getInt(Variables.CIUDADID, 0);
        if (ciudadID == 0) {
            if (!isGPSEnabled(getApplicationContext())) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), SeleccionCiudad.class);
                startActivity(intent);
                return;
            }
        }
        rvClubes.setHasFixedSize(true);
        rvClubes.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        rvClubes.setLayoutManager(layoutManager);
        Call<Sucursal> sucursalCall = service.obtenerSucursales(true,true, ciudadID);
        sucursalCall.enqueue(new Callback<Sucursal>() {
            @Override
            public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getResults().isEmpty()) {
                        adapter.clear();
//                        adapterFull.clear();
                        Variables.sucursales = response.body().getResults();
                        adapter.update(Variables.sucursales);
                        tvSinSucursales.setVisibility(View.INVISIBLE);
//                        adapterFull.update();
                    } else {
                        tvSinSucursales.setVisibility(View.VISIBLE);
//                        Toast.makeText(getContext(), "Testing", Toast.LENGTH_SHORT).show();
                        obtenerSucursalesActivas();
                    }
                } else {
                    Toast.makeText(getContext(), "Error. Comuníquese con el administrador", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Sucursal> call, Throwable t) {
                Toast.makeText(getContext(), "No internet" + t.getMessage(), Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });

    }

    private void obtenerSucursalesActivas() {
        Call<Sucursal> sucursalCall = service.obtenerSucursalesV2(true,true);
        sucursalCall.enqueue(new Callback<Sucursal>() {
            @Override
            public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                if (response.isSuccessful()) {
                    adapter.clear();
                    Variables.sucursales = response.body().getResults();
                    adapter.update(Variables.sucursales);
                    tvSinSucursales.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getContext(), "Error. Comuníquese con el administrador", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sucursal> call, Throwable t) {
                Toast.makeText(getContext(), "No internet" + t.getMessage(), Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
//        int ciudadID = sharedPreferences.getInt(Variables.CIUDADID, 0);
//        if (ciudadID == 0) {
        //Inflate the layout for this fragment or reuse the existing one
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_clubs, container, false);
        return view;
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


    public boolean isGPSEnabled(Context mContext) {
        String locationProviders = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    boolean mRequestingLocationUpdates;




//    private final Runnable mExpiredRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (alertDialog != null) {
//                if (alertDialog.isShowing()) {
//                    try {
//                        alertDialog.dismiss();
//                    } catch (Exception e) {
//                        Log.d("Exc",e.getMessage());
//                    }
//                }
//            }
//            if (isGPSEnabled(getApplicationContext())) {
//                int ciudadID = sharedPreferences.getInt(Variables.CIUDADID, 0);
//                if (ciudadID==0) {
//                    Toast.makeText(getApplicationContext(), "No se pudo obtener su ubicación.", Toast.LENGTH_SHORT).show();
//                    try {
//                        Intent intent = new Intent(getApplicationContext(), SeleccionCiudad.class);
//                        startActivity(intent);
//                        getActivity().finish();
//                    } catch (IllegalStateException e) {
//                        Log.d(TAG, e.getMessage());
//                    }
//                }
//            }
//        }
//    };



//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
////      mlocManager.requestLocationUpdates(NETWORK_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, getActivity());
//        SingleShotLocationProvider.requestSingleUpdate(getContext(),
//                new SingleShotLocationProvider.LocationCallback() {
//                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
//
//                    }
//                });
//    }



//    @Override
//    public void onLocationChanged(Location location) {
//        Toast.makeText(getApplicationContext(), "Location changed automatically!", Toast.LENGTH_SHORT).show();
//        Log.d("LOCATION CHANGED: ","LOCATION CHANGED");
//        mHandler.removeCallbacks(mExpiredRunnable);
//        mLocationClient.unregisterConnectionCallbacks(this);
//        this.latitud = location.getLatitude();
//        this.longitud = location.getLongitude();
//        if (Variables.sucursales.isEmpty()) {
//            cargarSucursalesLocation(view);
//        }
////        mlocManager.removeUpdates(Clubes.this);
//    }
}
