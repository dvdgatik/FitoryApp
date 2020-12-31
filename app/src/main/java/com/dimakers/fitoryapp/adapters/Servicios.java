package com.dimakers.fitoryapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.api.models.Servicio;

import java.util.ArrayList;

public class Servicios extends RecyclerView.Adapter<Servicios.ViewHolder> {
    ArrayList<Servicio> dataset;
    Context context;

    public Servicios(Context context) {
        this.context = context;
        this.dataset = new ArrayList<>();
    }

    public void update(ArrayList<Servicio> servicios) {
        dataset.addAll(servicios);
        notifyDataSetChanged();
    }

    public void add(Servicio servicio) {
        dataset.add(servicio);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_servicio,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Servicio servicio = dataset.get(i);
        viewHolder.tvServicioText.setText(servicio.getNombre());
        Glide.with(context).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(servicio.getIcono()).into(viewHolder.ivServicioImagen);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServicioText;
        ImageView ivServicioImagen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServicioText = itemView.findViewById(R.id.tv_servicio_texto);
            ivServicioImagen = itemView.findViewById(R.id.iv_servicio_imagen);
        }
    }
}
