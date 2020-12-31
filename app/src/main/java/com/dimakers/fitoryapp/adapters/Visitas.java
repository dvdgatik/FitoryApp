package com.dimakers.fitoryapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.api.models.Visita;

import java.util.ArrayList;

public class Visitas extends RecyclerView.Adapter<Visitas.ViewHolder> {
    private ArrayList<Visita> dataset;
    private Context context;

    public Visitas(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
    }

    public void update(ArrayList<Visita> visitas) {
        this.dataset.addAll(visitas);
        notifyDataSetChanged();
    }

    public void clear() {
        dataset.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHora, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora = itemView.findViewById(R.id.tv_hora);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_asistencia,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Visita visita = dataset.get(i);
        viewHolder.tvFecha.setText(visita.getFecha());
        viewHolder.tvHora.setText(to12(visita.getHora()));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public String to12(String hora) {
        String[] horaArray = hora.split(":");
        String time;
        int hour = Integer.parseInt(horaArray[0]);
        if (hour>=13) {
            hour = hour - 12;
            time = hour + ":" + horaArray[1] + " P.M.";
        } else {
            time = hour + ":" + horaArray[1] + " A.M.";
        }

        return time;
    }
}

