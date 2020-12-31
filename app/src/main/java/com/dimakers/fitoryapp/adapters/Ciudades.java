package com.dimakers.fitoryapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.activities.ClubMain;
import com.dimakers.fitoryapp.activities.Login;
import com.dimakers.fitoryapp.activities.SeleccionCiudad;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Ciudad;
import com.dimakers.fitoryapp.api.models.Cliente;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ciudades extends RecyclerView.Adapter<Ciudades.ViewHolder> {
    private ArrayList<Ciudad> dataset;
    private Context context;
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    
    public Ciudades(Context context) {
        this.context = context;
        this.dataset = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(Variables.PREFERENCES,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void empty() {
        dataset.clear();
        notifyDataSetChanged();
    }

    public void update(ArrayList<Ciudad> ciudades) {
        dataset.addAll(ciudades);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ciudad_bco,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Ciudad ciudad = dataset.get(i);
        viewHolder.btnCiudad.setText(ciudad.getNombre());
        viewHolder.btnCiudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idCliente = sharedPreferences.getInt(Variables.CLIENTEID,0);
                if (idCliente == 0) {
                    Toast.makeText(context, "No se encontro un cliente id", Toast.LENGTH_SHORT).show();
                    ((Activity)context).finish();
                } else {
                    Call<Cliente> clienteCall = service.actualizarCiudadEstadoCliente(idCliente,ciudad.getId(),ciudad.getEstado());
                    clienteCall.enqueue(new Callback<Cliente>() {
                        @Override
                        public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                            if (response.isSuccessful()) {
//                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                editor.putBoolean(Variables.SELECCIONUBICACION, true);
                                editor.putInt(Variables.CIUDADID,ciudad.getId());
                                editor.commit();
                                Intent intent = new Intent(context, ClubMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                                ((Activity)context).finish();
                            } else {
                                Toast.makeText(context, "No se pudo actualizar su ubicación. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Cliente> call, Throwable t) {
                            if (!InternetConnectionStatus.isConnected(context)) {
                                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                clienteCall.cancel();
                            } else if(t instanceof SocketTimeoutException) {
                                Toast.makeText(context, context.getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                clienteCall.cancel();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private Button btnCiudad;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnCiudad = itemView.findViewById(R.id.ciudad);
        }
    }
}
