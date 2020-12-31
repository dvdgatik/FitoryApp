package com.dimakers.fitoryapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.activities.ClubMain;
import com.dimakers.fitoryapp.activities.Login;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.InternetConnectionStatus;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Metodo;
import com.facebook.login.LoginManager;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tarjetas extends RecyclerView.Adapter<Tarjetas.ViewHolder> {
    Context context;
    ArrayList<Metodo> dataset;
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;

    public Tarjetas(Context context) {
        this.context = context;
        this.dataset = new ArrayList<>();
    }

    public void update(List<Metodo> metodos) {
        this.dataset.addAll(metodos);
        notifyDataSetChanged();
    }

    public void add(Metodo metodo) {
        this.dataset.add(metodo);
        notifyDataSetChanged();
    }

    public void clear() {
        this.dataset.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        sharedPreferences = context.getSharedPreferences(Variables.PREFERENCES,Context.MODE_PRIVATE);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tarjeta,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Metodo metodo = dataset.get(i);
        viewHolder.tvLast4.setText("**** **** **** "+metodo.getLast4());
        viewHolder.tvBrand.setText(metodo.getBrand());
        if (metodo.getBrand().equals("AMERICAN_EXPRESS")) {
            viewHolder.tvBrand.setText("AMEX");
        }
        if (metodo.getBrand().equals("MASTERCARD")) {
            viewHolder.tvBrand.setText("MCARD");
        }
        viewHolder.tvEliminarTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(R.layout.dialog_tarjeta);
                AlertDialog dialog = builder.create();
                dialog.show();
                TextView content = (TextView) dialog.findViewById(R.id.content);
                TextView buttonCancelar = (TextView) dialog.findViewById(R.id.button1);
                TextView buttonConfirmar = (TextView) dialog.findViewById(R.id.button2);
                content.setText(metodo.getBrand()+" * * * *  * * * *  * * * * "+metodo.getLast4());
                buttonConfirmar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int clienteID = sharedPreferences.getInt(Variables.CLIENTEID,0);
                        String metodoID = metodo.getId();
//                                Toast.makeText(context, "clienteID: "+clienteID + " metodoID: "+metodoID , Toast.LENGTH_SHORT).show();
                        if (clienteID == 0 || metodoID == null) {
                            Toast.makeText(context, "No se puede remover la tarjeta.", Toast.LENGTH_SHORT).show();
                        } else {
                            Call<ResponseBody> borrarMetodoCall = service.borrarMetodoPagoConekta(clienteID,metodoID);
                            borrarMetodoCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        dataset.remove(i);
                                        notifyItemRemoved(i);
                                        notifyItemRangeChanged(i, getItemCount());
                                        Toast.makeText(context, "Tarjeta eliminada.", Toast.LENGTH_SHORT).show();
                                        Variables.metodos.clear();
                                    } else {
                                        Toast.makeText(context, "No se pudo remover la tarjeta. Intente de nuevo", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    if (!InternetConnectionStatus.isConnected(context)) {
                                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    } else if(t instanceof SocketTimeoutException) {
                                        Toast.makeText(context, context.getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });
                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
//
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrand, tvLast4;
        ImageView tvEliminarTarjeta;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvLast4 = itemView.findViewById(R.id.tv_last4);
            tvEliminarTarjeta = itemView.findViewById(R.id.tv_eliminar_tarjeta);
        }
    }
}
