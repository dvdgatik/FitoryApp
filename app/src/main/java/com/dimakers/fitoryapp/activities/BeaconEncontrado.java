package com.dimakers.fitoryapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Club;
import com.dimakers.fitoryapp.api.models.EvaluacionSucursal;
import com.dimakers.fitoryapp.api.models.Fecha;
import com.dimakers.fitoryapp.api.models.Sesion;
import com.dimakers.fitoryapp.api.models.SubscripcionMes;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.Visita;
import com.dimakers.fitoryapp.fragments.Evaluar;
import com.dimakers.fitoryapp.services.BuscarBeacon;

import org.altbeacon.beacon.Beacon;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeaconEncontrado extends AppCompatActivity {
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String fechaStr = "";
    Button button;
    private boolean evaluar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_encontrado);
        BuscarBeacon.MUST_BE_RUNNING = false;
        sharedPreferences = getSharedPreferences(Variables.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        button = findViewById(R.id.button_confirmar);
        int clienteID = sharedPreferences.getInt(Variables.CLIENTEID, 0);
        if (clienteID == 0) {
            Toast.makeText(this, "Sin inicio de sesión", Toast.LENGTH_SHORT).show();
            finish();
        } else {
//            Toast.makeText(this, "CLIENTEID: "+clienteID, Toast.LENGTH_LONG).show();
            Intent intentBeacon = getIntent();
            Beacon beacon = (Beacon) intentBeacon.getSerializableExtra(Variables.BEACON);
            Sucursal sucursal = (Sucursal) intentBeacon.getSerializableExtra("SUCURSAL");
            TextView sucursalNombre = (TextView) findViewById(R.id.sucursal_nombre);
            ImageView sucursalLogo = (ImageView) findViewById(R.id.sucursal_logo);
            sucursalNombre.setText(sucursalNombre.getText()+sucursal.getNombre());
            String fecha = (String) intentBeacon.getSerializableExtra("FECHA");
            Call<Club> clubCall = service.obtenerClub(sucursal.getClub(),true);
            clubCall.enqueue(new Callback<Club>() {
                @Override
                public void onResponse(Call<Club> call, Response<Club> response) {
                    if (response.isSuccessful()) {
                        if (response.body()!=null) {
                            Club club = response.body();
                            Glide.with(getApplicationContext()).load(club.getFoto()).into(sucursalLogo);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Club> call, Throwable t) {

                }
            });
            String str= "BEACON id: "+beacon.getId1()+"\nBEACON mayor: "+beacon.getId2()+"\nBEACON menor: "+beacon.getId3()+"\nDistancia: "+beacon.getDistance();
//            Toast.makeText(this, "Mayor: "+beacon.getId2()+" Menor: "+beacon.getId3(), Toast.LENGTH_LONG).show();
            //Punto de partida. Presiona botón "Confirmar"
            button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            confirmarAsistencia(beacon,clienteID);
                            //Comprobar que tenga sesiones vigentes o suscripciones
                            consultarSesiones(sucursal.getId(),clienteID, fecha, beacon);
                        }
                    });
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
//            Intent intent = new Intent(BeaconEncontrado.this,BuscarBeacon.class);
//            stopService(intent);
            TextView beaconText = (TextView) findViewById(R.id.beacon_text);
            beaconText.setText(str);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

    }

//    private void comprobarAsistencia(int clienteID, int sucursalID, String fecha) {
//        Call<Visita> visitaCall = service.comprobarVisita(clienteID,sucursalID,fecha);
//        visitaCall.enqueue(new Callback<Visita>() {
//            @Override
//            public void onResponse(Call<Visita> call, Response<Visita> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getResults().isEmpty()) {
//                        consultarSesiones(sucursalID, clienteID);
//                    } else {
//                        Toast.makeText(BeaconEncontrado.this, "Ya tiene la asistencia del día", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Visita> call, Throwable t) {
//
//            }
//        });
//    }

//    public void consultarFecha(int clienteID, int sucursalID){
//        Call<Fecha> fechaCall = service.consultarFecha();
//        fechaCall.enqueue(new Callback<Fecha>() {
//            @Override
//            public void onResponse(Call<Fecha> call, Response<Fecha> response) {
//                if (response.isSuccessful()) {
//                    Fecha fecha = response.body();
//                    fechaStr = fecha.getFecha();
//                    Toast.makeText(BeaconEncontrado.this, ""+fecha.getFecha(), Toast.LENGTH_SHORT).show();
//                    comprobarAsistencia(clienteID, sucursalID, fecha.getFecha());
//                } else {
//                    Toast.makeText(BeaconEncontrado.this, "No se pudo obtener fecha del servidor", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Fecha> call, Throwable t) {
//
//            }
//        });
//
//    }
//
//    private void consultarSesiones(int sucursalID, int clienteID) {
//        Call<Sesion> sesionCall = service.comprobarSesionesSucursal(String.valueOf(sucursalID),String.valueOf(clienteID),true);
//        sesionCall.enqueue(new Callback<Sesion>() {
//            @Override
//            public void onResponse(Call<Sesion> call, Response<Sesion> response) {
//                if (response.isSuccessful()) {
//                    if (!response.body().getResults().isEmpty()) {
//                        Sesion sesion = response.body().getResults().get(0);
//                        Toast.makeText(BeaconEncontrado.this, "Sesiones: "+sesion.getSesionesRestantes(), Toast.LENGTH_SHORT).show();
//                        //Caducidad de la sesión
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime( stringToDate(sesion.getCaducidad()));
//
//                        //Fecha del día de hoy
//                        Calendar cal2 = Calendar.getInstance();
//                        cal2.setTime(stringToDate(fechaStr));
//
//                        //Verificar que la sesión se encuentre vigente
//                        if (cal2.before(cal)) {
//                            descontarSession(sucursalID,clienteID,sesion);
//                        } else {
//                            Toast.makeText(BeaconEncontrado.this, "Sin sesiones vigentes", Toast.LENGTH_SHORT).show();
//                            borrarSesion(sesion.getId());
//                        }
//
//                    } else {
//                        Toast.makeText(BeaconEncontrado.this, "Sin sesiones", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Sesion> call, Throwable t) {
//
//            }
//        });
//    }

    private void consultarSesiones(int sucursalID, int clienteID , String fecha, Beacon beacon) {
        Call<Sesion> sesionCall = service.comprobarSesionesSucursal(String.valueOf(sucursalID),String.valueOf(clienteID),true);
        sesionCall.enqueue(new Callback<Sesion>() {
            @Override
            public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getResults().isEmpty()) {
                        Sesion sesion = response.body().getResults().get(0);
//                        Toast.makeText(getApplicationContext(), "Sesiones: "+sesion.getSesionesRestantes(), Toast.LENGTH_SHORT).show();
                        //Tiene sesiones pero debemos evaluar cuantas tiene restantes
                        if (sesion.getSesionesRestantes()==0) {
                            // El número de sesiones restantes se termino por la tanto borraremos dicha paquete de sesiones y ahora verificaremos si tiene alguna suscripción vigente
                            borrarSesion(sesion.getId());
                            consultarSuscripciones(sucursalID, clienteID, fecha, beacon);
                        } else {
                            //Checar que la sesion este vigente
//                        descontarSession(sucursalID,clienteID,sesion);
                            //Caducidad de la sesión
                            Calendar cal = Calendar.getInstance();
                            cal.setTime( stringToDate(sesion.getCaducidad()));

                            //Fecha del día de hoy
                            Calendar cal2 = Calendar.getInstance();
                            cal2.setTime(stringToDate(fecha));

                            //Verificar que la sesión se encuentre vigente
                            if (cal2.before(cal) || cal2.equals(cal)) {
                                //La sesión existe y se encuentra vigente por lo tanto mostramos pantalla de confirmar asistencia
                                /**
                                 * DESCONTAR SESION Y MARCAR ASISTENCIA
                                 */

                                    descontarSession(sucursalID,clienteID,sesion);
//                                editor.putInt(Variables.SUCURSAL_ID, sucursalID);
//                                editor.commit();
//                                /**
//                                 *	Iniciar activity al detectar beacon
//                                 */
//                                Intent intent = new Intent(getApplicationContext(), BeaconEncontrado.class);
//                                intent.putExtra(Variables.BEACON, (Serializable) beacon);
//                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            } else {
//                            La sesión no se encuentra vigente por lo tanto no mostramos nada y eliminamos la sesión
//                                Toast.makeText(getApplicationContext(), "No cuenta con ningún paquete o subscripción disponible.", Toast.LENGTH_SHORT).show();
                                borrarSesion(sesion.getId());
                                //Sin embargo seguiremos consultado para saber si existe alguna subscripción en existencia
                                consultarSuscripciones(sucursalID, clienteID, fecha, beacon);

                                finish();
                            }
                        }

                    } else {
//                        Toast.makeText(getApplicationContext(), "No cuenta con ningún paquete o subscripción disponible.", Toast.LENGTH_SHORT).show();
                        // No tiene ninguna sesión por lo tanto ahora consultaremos si tiene alguna suscripción vigente
                        consultarSuscripciones(sucursalID, clienteID, fecha, beacon);
                    }
                }
            }

            @Override
            public void onFailure(Call<Sesion> call, Throwable t) {

            }
        });
    }


    private void borrarSesion(int sesionID) {
        Call<ResponseBody> sesionCall = service.removerSesion(sesionID);
        sesionCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
//                    Toast.makeText(BeaconEncontrado.this, "Sesión vigente fue borrada con éxito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(BeaconEncontrado.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recuperarSession(int sucursalID, int clienteID, Sesion sesion) {
        sesion.setSesionesRestantes(sesion.getSesionesRestantes()+1);
        Call<Sesion> sesionCall = service.descontarSesion(sesion.getId(),sesion);
        sesionCall.enqueue(new Callback<Sesion>() {
            @Override
            public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Sesion sesionActualizada = response.body();
//                        Toast.makeText(BeaconEncontrado.this, "Sesiones restantes: "+sesionActualizada.getSesionesRestantes(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Sesion> call, Throwable t) {
                Toast.makeText(BeaconEncontrado.this, "Error al confirmar la asistencia.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void descontarSession(int sucursalID, int clienteID, Sesion sesion) {
        if (sesion.getSesionesRestantes() == 0) {
            Toast.makeText(this, "Agotó su número de sesiones.", Toast.LENGTH_SHORT).show();
            //Por tanto borramos el paquete de sesiones
            borrarSesion(sesion.getId());
        } else {
            sesion.setSesionesRestantes(sesion.getSesionesRestantes()-1);
            Call<Sesion> sesionCall = service.descontarSesion(sesion.getId(),sesion);
            sesionCall.enqueue(new Callback<Sesion>() {
                @Override
                public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Sesion sesionActualizada = response.body();
                            Toast.makeText(BeaconEncontrado.this, "Sesiones restantes: "+sesionActualizada.getSesionesRestantes(), Toast.LENGTH_SHORT).show();
                            registrarAsistencia(clienteID, sucursalID, sesion);
                            Variables.sesionFulls.clear();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Sesion> call, Throwable t) {
                    Toast.makeText(BeaconEncontrado.this, "Error al confirmar asistencia. Comuníquese ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    public void registrarAsistenciaSubscripcion(int clienteID, int sucursalID) {
        Call<Visita> visitaCall = service.registrarVisita(clienteID,sucursalID);
        visitaCall.enqueue(new Callback<Visita>() {
            @Override
            public void onResponse(Call<Visita> call, Response<Visita> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BeaconEncontrado.this, "Bienvenido!!!", Toast.LENGTH_SHORT).show();
                    Variables.sesionFullsMes.clear();
                    Variables.sesionFulls.clear();
                    //Revisaremos si el cliente ya ha hecho una calificación de sucursal, de lo contrario mostraremos la pantalla para evaluación
                    Call<EvaluacionSucursal> evaluacionSucursalCall = service.verificarEvaluacionUsuario(clienteID,sucursalID);
                    evaluacionSucursalCall.enqueue(new Callback<EvaluacionSucursal>() {
                        @Override
                        public void onResponse(Call<EvaluacionSucursal> call, Response<EvaluacionSucursal> response) {
                            finish();
                            if (response.isSuccessful()) {
                                if (response.body().getResults().isEmpty()) {
                                    Intent intent = new Intent(getApplicationContext(), ClubMain.class);
                                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("EVALUAR",true);
                                    startActivity(intent);
                                } else {
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<EvaluacionSucursal> call, Throwable t) {
                            finish();
                        }
                    });
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    Fragment evaluar = new Evaluar();
//                    for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
//                        fragmentManager.popBackStack();
//                    }
//                    fragmentTransaction.replace(R.id.fragmentContainer, evaluar, "EVALUAR").addToBackStack(null).commit();
                } else {
                    Toast.makeText(BeaconEncontrado.this, "No se pudo registrar su asistencia correctamente. Contacte a un administrador", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Visita> call, Throwable t) {
            }
        });
    }

    public void registrarAsistencia(int clienteID, int sucursalID, Sesion sesion) {
//        Call<Visita> visitaCall = service.registrarVisita(clienteID,sucursalID);
        Call<Visita> visitaCall = service.registrarVisita(clienteID,sucursalID);
        visitaCall.enqueue(new Callback<Visita>() {
            @Override
            public void onResponse(Call<Visita> call, Response<Visita> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BeaconEncontrado.this, "Bienvenido!!!", Toast.LENGTH_SHORT).show();
                    Variables.sesionFullsMes.clear();
                    Variables.sesionFulls.clear();
                    //Revisaremos si el cliente ya ha hecho una calificación de sucursal, de lo contrario mostraremos la pantalla para evaluación
                    Call<EvaluacionSucursal> evaluacionSucursalCall = service.verificarEvaluacionUsuario(clienteID,sucursalID);
                    evaluacionSucursalCall.enqueue(new Callback<EvaluacionSucursal>() {
                        @Override
                        public void onResponse(Call<EvaluacionSucursal> call, Response<EvaluacionSucursal> response) {
                            finish();
                            if (response.isSuccessful()) {
                                if (response.body().getResults().isEmpty()) {
                                    Intent intent = new Intent(getApplicationContext(), ClubMain.class);
                                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("EVALUAR",true);
                                    startActivity(intent);
                                } else {
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<EvaluacionSucursal> call, Throwable t) {
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(BeaconEncontrado.this, "No se pudo registrar su asistencia correctamente. Contacte a un administrador", Toast.LENGTH_SHORT).show();
                    recuperarSession(clienteID, sucursalID, sesion);
                }
            }

            @Override
            public void onFailure(Call<Visita> call, Throwable t) {
                Toast.makeText(BeaconEncontrado.this, "Error al confirmar asistencia.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public Date stringToDate(String string) {
        String dtStart = string;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(dtStart);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Excepción", Toast.LENGTH_SHORT).show();
        }
        return new Date("");
    }

//    public void confirmarAsistencia(Beacon beacon, int clienteID) {
//        Toast.makeText(this, "Asistencia confirmada", Toast.LENGTH_SHORT).show();
//        Call<Sucursal> sucursalCall = service.obtenerSucursalMayorMenor(beacon.getId2().toString(),beacon.getId3().toString(),true);
//            sucursalCall.enqueue(new Callback<Sucursal>() {
//                @Override
//                public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
//                    if (response.isSuccessful()) {
//                        if (!response.body().getResults().isEmpty()) {
//                            Sucursal sucursal = response.body().getResults().get(0);
//                            editor.putInt(Variables.SUCURSAL_ID, sucursal.getId());
//                            editor.commit();
//                            consultarFecha(clienteID, sucursal.getId());
//
//                        } else {
//
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Sucursal> call, Throwable t) {
//
//                }
//            });
//    }

    public void cerrarActivity(View view) {
        BuscarBeacon.MUST_BE_RUNNING = true;
        finish();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Intent intent = new Intent(BeaconEncontrado.this,BuscarBeacon.class);
//        startService(intent);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BuscarBeacon.MUST_BE_RUNNING = true;
//        Intent intent = new Intent(BeaconEncontrado.this,BuscarBeacon.class);
//        startService(intent);
//        if (evaluar) {
//            Toast.makeText(this, "Evaluar desde onDestroy", Toast.LENGTH_SHORT).show();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            Fragment evaluar = new Evaluar();
//                for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
//                    fragmentManager.popBackStack();
//                }
//                fragmentTransaction.replace(R.id.fragmentContainer, evaluar, "EVALUAR").addToBackStack(null).commit();
//        }
    }

    private void consultarSuscripciones(int sucursalID, int clienteID, String fecha, Beacon beacon) {
        Call<SubscripcionMes> mesCall = service.obtenerSubscripcionesClienteSucursal(clienteID,sucursalID,true);
        mesCall.enqueue(new Callback<SubscripcionMes>() {
            @Override
            public void onResponse(Call<SubscripcionMes> call, Response<SubscripcionMes> response) {
                if (response.isSuccessful()) {

                    if (response.body().getResults().isEmpty()) {
                        //No tiene suscripciones por lo tanto no mostrará pantalla para confirmar su asistencia
                        BuscarBeacon.MUST_BE_RUNNING = true;
                        Toast.makeText(getApplicationContext(), "No cuenta con ningún paquete o subscripción disponible.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Tiene subscripciones. Verificaremos que sea vigente;
//                        Toast.makeText(BeaconEncontrado.this, "Tiene suscripciones. Verificaremos qu sea vigente", Toast.LENGTH_SHORT).show();
                        SubscripcionMes suscripcionesMes = response.body().getResults().get(0);
                        //Caducidad de la sesión
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( stringToDate(suscripcionesMes.getFechaRenovacion()));

                        //Fecha del día de hoy
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTime(stringToDate(fecha));

                        //Verificar que la subscripción se encuentre vigente
                        if (cal2.before(cal) || cal2.equals(cal)) {
                            //La subscripcion existe y se encuentra vigente por lo tanto mostramos pantalla de confirmar asistencia
                            /**
                             * MARCAR ASISTENCIA
                             */
                            registrarAsistenciaSubscripcion(clienteID,sucursalID);
//                            descontarSession(sucursalID,clienteID,sesion);
//                            Toast.makeText(BeaconEncontrado.this, "La subscripcion eixste y se encuentre vigente. ", Toast.LENGTH_SHORT).show();
//                            editor.putInt(Variables.SUCURSAL_ID, sucursalID);
//                            editor.commit();
//                            /**
//                             *	Iniciar activity al detectar beacon
//                             */
//                            Intent intent = new Intent(getApplicationContext(), BeaconEncontrado.class);
//                            intent.putExtra(Variables.BEACON, (Serializable) beacon);
//                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
                            // Hay una subscripción pero ya no se encuentra vigente por tanto tenemos que borrarla de la BD
//                            Toast.makeText(getApplicationContext(), "Sin subscripcion vigentes", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "No cuenta con ningún paquete o subscripción disponible.", Toast.LENGTH_SHORT).show();
//                            borrarSuscripcion(suscripcionesMes.getId());
                        }
                    }
                } else {
                    BuscarBeacon.MUST_BE_RUNNING = true;
                }
            }

            @Override
            public void onFailure(Call<SubscripcionMes> call, Throwable t) {
                BuscarBeacon.MUST_BE_RUNNING = true;
            }
        });
    }

    private void borrarSuscripcion(int subscripcionID) {
        Call<ResponseBody> sesionCall = service.removerSubscripcion(subscripcionID);
        sesionCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                BuscarBeacon.MUST_BE_RUNNING = true;
                if (response.isSuccessful()) {
//                    Toast.makeText(getApplicationContext(), "Subscripción vigente fue borrada con éxito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                BuscarBeacon.MUST_BE_RUNNING = true;
            }
        });
    }

}
