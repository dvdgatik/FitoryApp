package com.dimakers.fitoryapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.api.models.Actividad;
import com.dimakers.fitoryapp.api.models.ActividadHorario;
import com.dimakers.fitoryapp.api.models.FormatoHorario;
import com.dimakers.fitoryapp.api.models.HorarioActividad;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.ArrayList;

public class Actividades extends RecyclerView.Adapter<Actividades.ViewHolder> {
    private Context context;
    private ArrayList<Actividad> dataset = new ArrayList<>();

    public Actividades(Context context) {
        this.context = context;
        this.dataset = new ArrayList<>();
    }

    public void update(ArrayList<Actividad> actividades) {
        this.dataset.addAll(actividades);
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
        Actividad actividad = dataset.get(i);
        viewHolder.expandable.toggle();
        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.expandable.isExpanded()) {
                    viewHolder.ivDesplegarInformacion.setRotation(360);
                } else {
                    viewHolder.ivDesplegarInformacion.setRotation(180);
                }
                viewHolder.expandable.toggle();
            }
        });
        Glide.with(context).load(actividad.getIcono()).into(viewHolder.ivHorarioIcono);
        viewHolder.tvHorarioNombre.setText(actividad.getNombre().toUpperCase());
        //Cargar Servicios
        if (!actividad.getHorarios().isEmpty()) {
            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);
            viewHolder.expandable.addView(layout);
            for(HorarioActividad horario: actividad.getHorarios()) {
                //Crear horario textview
                TextView tvHorario = new TextView(context);
                tvHorario.setText(horario.getInicio()+" - "+horario.getFin());
                //Rojoclose
                tvHorario.setTextColor(context.getResources().getColor(R.color.rojoClose));
                Typeface face = Typeface.createFromAsset(context.getAssets(),
                        "ralewaymedium.ttf");
                tvHorario.setTypeface(face);
                tvHorario.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
                tvHorario.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                ViewGroup.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                //Crear checkbox para los dias de la semana
                CheckBox cbLunes = new CheckBox(context);
                cbLunes.setClickable(false);
                if (horario.getLunes().equals("True")){
                    cbLunes.setChecked(true);
                }
                CheckBox cbMartes = new CheckBox(context);
                cbMartes.setClickable(false);
                if (horario.getMartes().equals("True")){
                    cbMartes.setChecked(true);
                }
                CheckBox cbMiercoles = new CheckBox(context);
                cbMiercoles.setClickable(false);
                if (horario.getMiercoles().equals("True")){
                    cbMiercoles.setChecked(true);
                }
                CheckBox cbJueves = new CheckBox(context);
                cbJueves.setClickable(false);
                if (horario.getJueves().equals("True")){
                    cbJueves.setChecked(true);
                }
                CheckBox cbViernes = new CheckBox(context);
                cbViernes.setClickable(false);
                if (horario.getViernes().equals("True")){
                    cbViernes.setChecked(true);
                }
                CheckBox cbSabado = new CheckBox(context);
                cbSabado.setClickable(false);
                if (horario.getSabado().equals("True")){
                    cbSabado.setChecked(true);
                }
                CheckBox cbDomingo = new CheckBox(context);
                cbDomingo.setClickable(false);
                if (horario.getDomingo().equals("True")){
                    cbDomingo.setChecked(true);
                }
                LinearLayout layoutCheckBox = new LinearLayout(context);
                layoutCheckBox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutCheckBox.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout layoutCHL = new LinearLayout(context);
                layoutCHL.setLayoutParams(params);
                layoutCHL.setOrientation(LinearLayout.HORIZONTAL);
                layoutCHL.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutCHL.addView(cbLunes);

                LinearLayout layoutCHM = new LinearLayout(context);
                layoutCHM.setLayoutParams(params);
                layoutCHM.setOrientation(LinearLayout.HORIZONTAL);
                layoutCHM.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutCHM.addView(cbMartes);

                LinearLayout layoutCHMi = new LinearLayout(context);
                layoutCHMi.setLayoutParams(params);
                layoutCHMi.setOrientation(LinearLayout.HORIZONTAL);
                layoutCHMi.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutCHMi.addView(cbMiercoles);

                LinearLayout layoutCHJ = new LinearLayout(context);
                layoutCHJ.setLayoutParams(params);
                layoutCHJ.setOrientation(LinearLayout.HORIZONTAL);
                layoutCHJ.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutCHJ.addView(cbJueves);

                LinearLayout layoutCHV = new LinearLayout(context);
                layoutCHV.setLayoutParams(params);
                layoutCHV.setOrientation(LinearLayout.HORIZONTAL);
                layoutCHV.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutCHV.addView(cbViernes);

                LinearLayout layoutCHS = new LinearLayout(context);
                layoutCHS.setLayoutParams(params);
                layoutCHS.setOrientation(LinearLayout.HORIZONTAL);
                layoutCHS.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutCHS.addView(cbSabado);

                LinearLayout layoutCHD = new LinearLayout(context);
                layoutCHD.setLayoutParams(params);
                layoutCHD.setOrientation(LinearLayout.HORIZONTAL);
                layoutCHD.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutCHD.addView(cbDomingo);

                layoutCheckBox.addView(layoutCHL);
                layoutCheckBox.addView(layoutCHM);
                layoutCheckBox.addView(layoutCHMi);
                layoutCheckBox.addView(layoutCHJ);
                layoutCheckBox.addView(layoutCHV);
                layoutCheckBox.addView(layoutCHS);
                layoutCheckBox.addView(layoutCHD);

                //Cargar etiquetas de dias de la semana
                LinearLayout layoutDias = new LinearLayout(context);
                layoutDias.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutDias.setOrientation(LinearLayout.HORIZONTAL);
                TextView tvLunes = new TextView(context);
                tvLunes.setText("L");
                tvLunes.setTypeface(face);
                tvLunes.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tvLunes.setLayoutParams(params);
                TextView tvMartes = new TextView(context);
                tvMartes.setText("M");
                tvMartes.setTypeface(face);
                tvMartes.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tvMartes.setLayoutParams(params);
                TextView tvMiercoles = new TextView(context);
                tvMiercoles.setText("M");
                tvMiercoles.setTypeface(face);
                tvMiercoles.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tvMiercoles.setLayoutParams(params);
                TextView tvJueves = new TextView(context);
                tvJueves.setText("J");
                tvJueves.setTypeface(face);
                tvJueves.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tvJueves.setLayoutParams(params);
                TextView tvViernes = new TextView(context);
                tvViernes.setText("V");
                tvViernes.setTypeface(face);
                tvViernes.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tvViernes.setLayoutParams(params);
                TextView tvSabado = new TextView(context);
                tvSabado.setText("S");
                tvSabado.setTypeface(face);
                tvSabado.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tvSabado.setLayoutParams(params);
                TextView tvDomingo = new TextView(context);
                tvDomingo.setText("D");
                tvDomingo.setTypeface(face);
                tvDomingo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tvDomingo.setLayoutParams(params);
                layoutDias.addView(tvLunes);
                layoutDias.addView(tvMartes);
                layoutDias.addView(tvMiercoles);
                layoutDias.addView(tvJueves);
                layoutDias.addView(tvViernes);
                layoutDias.addView(tvSabado);
                layoutDias.addView(tvDomingo);

                layout.addView(tvHorario);
                layout.addView(layoutDias);
                layout.addView(layoutCheckBox);

            }
        } else {
            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);
            viewHolder.expandable.addView(layout);
            TextView tvHorario = new TextView(context);
            tvHorario.setText("Sin horarios disponibles");
            Typeface face = Typeface.createFromAsset(context.getAssets(),
                    "ralewaymedium.ttf");
            tvHorario.setTypeface(face);
            tvHorario.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(tvHorario);
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ExpandableRelativeLayout expandable;
        RelativeLayout relativeLayout;
        ImageView ivDesplegarInformacion;
        ImageView ivHorarioIcono;
        TextView tvHorarioNombre;
//        TextView tvHorarioLunes, tvHorarioMartes, tvHorarioMiercoles, tvHorarioJueves, tvHorarioViernes, tvHorarioSabado, tvHorarioDomingo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDesplegarInformacion = itemView.findViewById(R.id.iv_desplegar_informacion);
            expandable = itemView.findViewById(R.id.expandableLayout);
            relativeLayout = itemView.findViewById(R.id.desplegar_informacion);
            ivHorarioIcono = itemView.findViewById(R.id.iv_horario_icono);
            tvHorarioNombre = itemView.findViewById(R.id.tv_horario_nombre);
//            tvHorarioLunes = itemView.findViewById(R.id.tv_horario_lunes);
//            tvHorarioMartes = itemView.findViewById(R.id.tv_horario_martes);
//            tvHorarioMiercoles = itemView.findViewById(R.id.tv_horario_miercoles);
//            tvHorarioJueves = itemView.findViewById(R.id.tv_horario_jueves);
//            tvHorarioViernes = itemView.findViewById(R.id.tv_horario_viernes);
//            tvHorarioSabado = itemView.findViewById(R.id.tv_horario_sabado);
//            tvHorarioDomingo = itemView.findViewById(R.id.tv_horario_domingo);

        }
    }
}
