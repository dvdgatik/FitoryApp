package com.dimakers.fitoryapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Favorito;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.SucursalFull;
import com.dimakers.fitoryapp.fragments.ClubDetalle;

import java.math.BigDecimal;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SucursalesFull extends RecyclerView.Adapter<SucursalesFull.ViewHolder> {
    private ArrayList<SucursalFull> dataset;
    private Context context;
    private boolean favoritos;
    private SharedPreferences sharedPreferences;
    FitoryService service = API.getApi().create(FitoryService.class);
    public SucursalesFull(Context context, boolean favoritos) {
        this.context = context;
        this.favoritos = favoritos;
        this.dataset = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(Variables.PREFERENCES,Context.MODE_PRIVATE);
    }

    public void update(ArrayList<SucursalFull> sucursalesFulls){
        this.dataset.clear();
        if (favoritos) {
            for (SucursalFull sucursal : sucursalesFulls) {
                if (sucursal.isFavorito()) {
                    this.dataset.add(sucursal);
                }
            }
        } else {
            this.dataset.addAll(sucursalesFulls);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        dataset.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_club,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SucursalFull sucursal = dataset.get(i);
        //Calcular distancia entre el usuario y la sucursal
//        if (!sucursal.getSucursal().getLatitud().equals("") && !sucursal.getSucursal().getLongitud().equals("")) {
//            Location loc1 = new Location("");
//            loc1.setLatitude(Float.parseFloat(sucursal.getSucursal().getLatitud()));
//            loc1.setLongitude(Float.parseFloat(sucursal.getSucursal().getLongitud()));
//            Location loc2 = new Location("");
//            String latitud = sharedPreferences.getString(Variables.LATITUD,"");
//            String longitud = sharedPreferences.getString(Variables.LONGITUD,"");
//            float distanceInMeters = 0;
//            if (!latitud.equals("") && !longitud.equals("")) {
//                loc2.setLatitude(Float.parseFloat(latitud));
//                loc2.setLongitude(Float.parseFloat(longitud));
//                distanceInMeters = loc1.distanceTo(loc2)/1000;
//                viewHolder.tvSucursalDistancia.setText(round(distanceInMeters,2)+" KM");
//            }
//        }

//        viewHolder.rbCalificacion.setRating((float)sucursal.getSucursal().getCalificacion());
        //Set rating bars
        viewHolder.star1.setImageResource(R.drawable.star_bg);
        viewHolder.star2.setImageResource(R.drawable.star_bg);
        viewHolder.star3.setImageResource(R.drawable.star_bg);
        viewHolder.star4.setImageResource(R.drawable.star_bg);
        viewHolder.star5.setImageResource(R.drawable.star_bg);
        double star = sucursal.getSucursal().getCalificacion();
        Toast.makeText(context, "stars"+star+"", Toast.LENGTH_SHORT).show();
        if (star==0) {
            /*
            No stars
             */
        } else if (star<1) {
            viewHolder.star1.setImageResource(R.drawable.star_half);
        } else if (star<1.5) {
            viewHolder.star1.setImageResource(R.drawable.star_on);
        } else if (star<2.0) {
            viewHolder.star1.setImageResource(R.drawable.star_on);
            viewHolder.star2.setImageResource(R.drawable.star_half);
        } else if (star<2.5) {
            viewHolder.star1.setImageResource(R.drawable.star_on);
            viewHolder.star2.setImageResource(R.drawable.star_on);
        } else if (star<3) {
            viewHolder.star1.setImageResource(R.drawable.star_on);
            viewHolder.star2.setImageResource(R.drawable.star_on);
            viewHolder.star3.setImageResource(R.drawable.star_half);
        } else if (star<3.5) {
            viewHolder.star1.setImageResource(R.drawable.star_on);
            viewHolder.star2.setImageResource(R.drawable.star_on);
            viewHolder.star3.setImageResource(R.drawable.star_on);
        } else if (star<4) {
            viewHolder.star1.setImageResource(R.drawable.star_on);
            viewHolder.star2.setImageResource(R.drawable.star_on);
            viewHolder.star3.setImageResource(R.drawable.star_on);
            viewHolder.star4.setImageResource(R.drawable.star_half);
        } else if (star<4.5) {
            viewHolder.star1.setImageResource(R.drawable.star_on);
            viewHolder.star2.setImageResource(R.drawable.star_on);
            viewHolder.star3.setImageResource(R.drawable.star_on);
            viewHolder.star4.setImageResource(R.drawable.star_on);
        } else if (star<5) {
            viewHolder.star1.setImageResource(R.drawable.star_on);
            viewHolder.star2.setImageResource(R.drawable.star_on);
            viewHolder.star3.setImageResource(R.drawable.star_on);
            viewHolder.star4.setImageResource(R.drawable.star_on);
            viewHolder.star5.setImageResource(R.drawable.star_half);
        } else if (star==5) {
            viewHolder.star1.setImageResource(R.drawable.star_on);
            viewHolder.star2.setImageResource(R.drawable.star_on);
            viewHolder.star3.setImageResource(R.drawable.star_on);
            viewHolder.star4.setImageResource(R.drawable.star_on);
            viewHolder.star5.setImageResource(R.drawable.star_on);
        }
            if (sucursal.getSucursal().getNumExt()!=null) {
            viewHolder.tvSucursalDireccion.setText(sucursal.getSucursal().getCalle()+" "+sucursal.getSucursal().getNumExt()+" "+sucursal.getSucursal().getColonia()+" "+sucursal.getSucursal().getMunicipio());
        }
        if (sucursal.getSucursal().getNumInt()!=null) {
            viewHolder.tvSucursalDireccion.setText(sucursal.getSucursal().getCalle()+" "+sucursal.getSucursal().getNumInt()+" "+sucursal.getSucursal().getColonia()+" "+sucursal.getSucursal().getMunicipio());
        }
        viewHolder.tvNombreSucursal.setText(sucursal.getSucursal().getNombre());
        Glide.with(context).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(sucursal.getClub().getFoto()).into(viewHolder.ivImagenSucursal);
        viewHolder.viewClubes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity)context ).getSupportFragmentManager();
                /**
                 * Con esta condición, verificamos que el fragmento sólo se añada si no existe en el backStack
                 */
                if (fragmentManager.findFragmentByTag("CLUBDETALLE")==null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //Pasamos un objeto clubDetalle con toda la información hacia el siguiente fragmento.
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Variables.CLUBDETALLE,sucursal);
                    Fragment clubDetalle = new ClubDetalle();
                    clubDetalle.setArguments(bundle);
//                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_right);
                    fragmentTransaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down);
                    fragmentTransaction.replace(R.id.fragmentContainer, clubDetalle, "CLUBDETALLE").addToBackStack(null).commit();
                }
            }
        });

//        if (sucursal.isFavorito()) {
//            Glide.with(context).load(R.drawable.icono_favoritos_activo).into(viewHolder.ivIconoFavoritos);
//            viewHolder.tvFavoritoValue.setText("true");
//            viewHolder.tvFavoritoId.setText(sucursal.getFavoritoID()+"");
//        } else {
//            Glide.with(context).load(R.drawable.icono_favoritos_inactivo).into(viewHolder.ivIconoFavoritos);
//            viewHolder.tvFavoritoValue.setText("false");
//        }



//        viewHolder.ivIconoFavoritos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (viewHolder.tvFavoritoValue.getText().equals("true")) {
//                    int favorito_id = Integer.parseInt(viewHolder.tvFavoritoId.getText().toString());
//                    Call<ResponseBody> favoritoCall = service.removerFavorito(favorito_id);
//                    favoritoCall.enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            if (response.isSuccessful()) {
//                                sucursal.setIsFavorito(false);
//                                Toast.makeText(context, "Removido de favoritos", Toast.LENGTH_SHORT).show();
//                                Glide.with(context).load(R.drawable.icono_favoritos_inactivo).into(viewHolder.ivIconoFavoritos);
//                                viewHolder.tvFavoritoValue.setText("false");
//                                if (favoritos) {
//                                    dataset.remove(i);
//                                    notifyItemRemoved(i);
//                                    notifyItemRangeChanged(i, getItemCount());
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                        }
//                    });
//                } else {
//                    int cliente_id = sharedPreferences.getInt(Variables.CLIENTEID,0);
//                    Call<Favorito> favoritoCall = service.agregarFavorito(cliente_id,sucursal.getSucursal().getId());
//                    favoritoCall.enqueue(new Callback<Favorito>() {
//                        @Override
//                        public void onResponse(Call<Favorito> call, Response<Favorito> response) {
//                            if (response.isSuccessful()) {
//                                Favorito favorito = response.body();
//                                Glide.with(context).load(R.drawable.icono_favoritos_activo).into(viewHolder.ivIconoFavoritos);
//                                viewHolder.tvFavoritoValue.setText("true");
//                                sucursal.setIsFavorito(true);
//                                sucursal.setFavoritoID(favorito.getId());
//                                viewHolder.tvFavoritoId.setText(favorito.getId()+"");
//                                Toast.makeText(context, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Favorito> call, Throwable t) {
//
//                        }
//                    });
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagenSucursal;
//        ImageView ivIconoFavoritos;
        TextView tvNombreSucursal, tvSucursalDireccion, tvSucursalDistancia, tvClubHorario, tvFavoritoValue, tvFavoritoId;
        RatingBar rbCalificacion;
        RelativeLayout viewClubes;
        ImageView star1,star2,star3,star4,star5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagenSucursal = itemView.findViewById(R.id.iv_imagen_sucursal);
            tvNombreSucursal = itemView.findViewById(R.id.tv_nombre_club);
            tvSucursalDireccion = itemView.findViewById(R.id.tv_sucursal_direccion);
//            tvSucursalDistancia = itemView.findViewById(R.id.tv_sucursal_distancia);
//            tvClubHorario = itemView.findViewById(R.id.tv_club_horario);
            rbCalificacion = itemView.findViewById(R.id.rb_calificacion);
//            ivIconoFavoritos = itemView.findViewById(R.id.iv_icono_favoritos);
            viewClubes = itemView.findViewById(R.id.club);
            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);
            star4 = itemView.findViewById(R.id.star4);
            star5 = itemView.findViewById(R.id.star5);
//            tvFavoritoValue = itemView.findViewById(R.id.tv_favorito_value);
//            tvFavoritoId = itemView.findViewById(R.id.tv_favorito_id);
        }
    }


    public float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
//        star1.setImageResource(R.drawable.star_half);
//        star2.setImageResource(R.drawable.star_half);
//        star3.setImageResource(R.drawable.star_half);
//        star4.setImageResource(R.drawable.star_half);
//        star5.setImageResource(R.drawable.star_half);

}
