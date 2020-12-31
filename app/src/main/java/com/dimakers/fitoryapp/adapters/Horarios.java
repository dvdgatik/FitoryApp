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
import com.dimakers.fitoryapp.api.models.FormatoHorario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Horarios extends RecyclerView.Adapter<Horarios.ViewHolder> {
    private ArrayList<FormatoHorario> dataset;
    private Context context;

    public Horarios(Context context){
        this.context = context;
        dataset = new ArrayList<>();
    }

    public ArrayList<FormatoHorario> getDataset() {
        return dataset;
    }

    public void update(ArrayList<FormatoHorario> horarios) {
        dataset.addAll(horarios);
        notifyDataSetChanged();
    }

    public void add(FormatoHorario horario) {
        dataset.add(horario);
        notifyDataSetChanged();
    }

    public void empty() {
        dataset.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_horario,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        FormatoHorario horario = dataset.get(i);
        Glide.with(context).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(horario.getIcono()).into(viewHolder.ivHorarioIcono);
        viewHolder.tvHorarioNombre.setText(horario.getNombre());
        viewHolder.tvHorarioLunes.setText(horario.getLunes());
        viewHolder.tvHorarioMartes.setText(horario.getMartes());
        viewHolder.tvHorarioMiercoles.setText(horario.getMiercoles());
        viewHolder.tvHorarioJueves.setText(horario.getJueves());
        viewHolder.tvHorarioViernes.setText(horario.getViernes());
//        viewHolder.tvHorarioLunes.setText("Lunes");
//        viewHolder.tvHorarioMartes.setText("Martes");
//        viewHolder.tvHorarioMiercoles.setText("Miercoles");
//        viewHolder.tvHorarioJueves.setText("Jueves");
//        viewHolder.tvHorarioViernes.setText("Viernes");
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivHorarioIcono;
        TextView tvHorarioNombre, tvHorarioLunes, tvHorarioMartes, tvHorarioMiercoles, tvHorarioJueves, tvHorarioViernes, tvHorarioSabado, tvHorarioDomingo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHorarioIcono = itemView.findViewById(R.id.iv_horario_icono);
            tvHorarioNombre = itemView.findViewById(R.id.tv_horario_nombre);
//            tvHorarioLunes = itemView.findViewById(R.id.tv_horario_lunes);
//            tvHorarioMartes = itemView.findViewById(R.id.tv_horario_martes);
//            tvHorarioMiercoles = itemView.findViewById(R.id.tv_horario_miercoles);
//            tvHorarioJueves = itemView.findViewById(R.id.tv_horario_jueves);
//            tvHorarioViernes = itemView.findViewById(R.id.tv_horario_viernes);
        }
    }
}
