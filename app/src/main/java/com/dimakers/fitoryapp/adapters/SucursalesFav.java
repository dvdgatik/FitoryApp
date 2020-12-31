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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.Variables;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.api.models.Ciudad;
import com.dimakers.fitoryapp.api.models.Club;
import com.dimakers.fitoryapp.api.models.Estado;
import com.dimakers.fitoryapp.api.models.Favorito;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.fragments.ClubDetalle;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SucursalesFav extends RecyclerView.Adapter<SucursalesFav.ViewHolder> {
    private ArrayList<Sucursal> dataset;
    private Context context;
    FitoryService service = API.getApi().create(FitoryService.class);
    SharedPreferences sharedPreferences;
    float latitud, longitud;

    public SucursalesFav(Context context) {
        this.context = context;
        this.dataset = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(Variables.PREFERENCES,Context.MODE_PRIVATE);
    }

    public void update(ArrayList<Sucursal> sucursales) {
        dataset.addAll(sucursales);
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<Sucursal> sucursales) {
        dataset = new ArrayList<>();
        dataset.addAll(sucursales);
        notifyDataSetChanged();
    }

    public void add(Sucursal sucursal) {
        dataset.add(sucursal);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_club2,viewGroup,false);
        latitud = sharedPreferences.getFloat(Variables.LATITUD,0);
        longitud = sharedPreferences.getFloat(Variables.LONGITUD,0);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Sucursal sucursal = dataset.get(i);
        //Calcular distancia entre el usuario y la sucursal
//        Location loc1 = new Location("");
//        loc1.setLatitude(Float.parseFloat(sucursal.getLatitud()));
//        loc1.setLongitude(Float.parseFloat(sucursal.getLongitud()));
//        Location loc2 = new Location("");
//        String latitud = sharedPreferences.getString(Variables.LATITUD,"");
//        String longitud = sharedPreferences.getString(Variables.LONGITUD,"");
//        float distanceInMeters = 0;
//        if (!latitud.equals("") && !longitud.equals("")) {
//            loc2.setLatitude(Float.parseFloat(latitud));
//            loc2.setLongitude(Float.parseFloat(longitud));
//            distanceInMeters = loc1.distanceTo(loc2)/1000;
//            viewHolder.tvSucursalDistancia.setText(round(distanceInMeters,2)+" KM");
//        }
//        viewHolder.rbCalificacion.setRating((float)sucursal.getCalificacion());
        //Star rating bar
        viewHolder.star1.setImageResource(R.drawable.star_bg);
        viewHolder.star2.setImageResource(R.drawable.star_bg);
        viewHolder.star3.setImageResource(R.drawable.star_bg);
        viewHolder.star4.setImageResource(R.drawable.star_bg);
        viewHolder.star5.setImageResource(R.drawable.star_bg);
        double star = sucursal.getCalificacion();
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
        viewHolder.tvSucursalDireccion.setText(sucursal.getCalle()+" "+sucursal.getNumExt()+" "+sucursal.getColonia()+" "+sucursal.getMunicipio());
        if (sucursal.getNumExt()!=null) {
            viewHolder.tvSucursalDireccion.setText(sucursal.getCalle()+" "+sucursal.getNumExt()+" "+sucursal.getColonia()+" "+sucursal.getMunicipio());
        }
        if (sucursal.getNumInt()!=null) {
            viewHolder.tvSucursalDireccion.setText(sucursal.getCalle()+" "+sucursal.getNumInt()+" "+sucursal.getColonia()+" "+sucursal.getMunicipio());
        }
        viewHolder.tvNombreSucursal.setText(sucursal.getNombre());
        try {
            Glide.with((context).getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation().diskCacheStrategy(DiskCacheStrategy.ALL)).load(sucursal.getLogo()).into(viewHolder.ivImagenSucursal);
        } catch (NullPointerException e) {
            Log.e("Exception",e.getMessage());
        }

//        Glide.with(context).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(sucursal.getLogo()).into(viewHolder.ivImagenSucursal);
//        Call<Club> clubCall = service.obtenerClub(sucursal.getClub(),true);
//        clubCall.enqueue(new Callback<Club>() {
//            @Override
//            public void onResponse(Call<Club> call, Response<Club> response) {
//                if (response.isSuccessful()) {
//                    if (response.body()!=null) {
//                        Club club = response.body();
//                        Glide.with(context.getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(club.getFoto()).into(viewHolder.ivImagenSucursal);
//                    } else {
//                        Toast.makeText(context, "No se pudo obtener el club de la sucursal", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Club> call, Throwable t) {
//
//            }
//        });
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
                    bundle.putInt(Variables.SUCURSAL_ID,sucursal.getId());
                    Fragment clubDetalle = new ClubDetalle();
                    clubDetalle.setArguments(bundle);
//                    fragmentTransaction.replace(R.id.fragmentContainer, clubDetalle, "CLUBDETALLE").addToBackStack(null).commit();
                    fragmentTransaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down);
                    fragmentTransaction.replace(R.id.fragmentContainer, clubDetalle, "CLUBDETALLE").addToBackStack(null).commit();
                }
            }
        });

//        //Llamada al API para obtener una ciudad
//        Call<Ciudad> ciudadCall = service.obtenerCiudad(sucursal.getCiudad());
//        ciudadCall.enqueue(new Callback<Ciudad>() {
//            @Override
//            public void onResponse(Call<Ciudad> call, Response<Ciudad> response) {
//                if (response.isSuccessful()) {
//                    Ciudad ciudad = response.body();
//                    //Llamada al API para obtener un estado
//                    Call<Estado> estadoCall = service.obtenerEstado(sucursal.getEstado());
//                    estadoCall.enqueue(new Callback<Estado>() {
//                        @Override
//                        public void onResponse(Call<Estado> call, Response<Estado> response) {
//                            if (response.isSuccessful()) {
//                                Estado estado = response.body();
//                                if (sucursal.getNumExt().equals("")) {
//                                    viewHolder.tvSucursalDireccion.setText(sucursal.getCalle()+" "+sucursal.getNumInt()+" "+sucursal.getColonia()+" "+ciudad.getNombre()+", "+estado.getNombre());
//                                } else {
//                                    viewHolder.tvSucursalDireccion.setText(sucursal.getCalle()+" "+sucursal.getNumExt()+" "+sucursal.getColonia()+" "+ciudad.getNombre()+", "+estado.getNombre());
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Estado> call, Throwable t) {
//                            Toast.makeText(context, R.string.administrador, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Ciudad> call, Throwable t) {
//                Toast.makeText(context, R.string.administrador, Toast.LENGTH_SHORT).show();
//            }
//        });

//        // Consultar si el club es favorito del cliente
//        int clienteId = sharedPreferences.getInt(Variables.CLIENTEID,0);
//        if (clienteId != 0) {
//            Call<Favorito> call = service.obtenerFavorito(clienteId, sucursal.getId());
//            call.enqueue(new Callback<Favorito>() {
//                @Override
//                public void onResponse(Call<Favorito> call, Response<Favorito> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getResults().isEmpty()) {
//                            Glide.with(context).load(R.drawable.icono_favoritos_inactivo).into(viewHolder.ivIconoFavoritos);
//                            viewHolder.tvFavoritoValue.setText("false");
//                        } else {
//                            Favorito favorito = response.body().getResults().get(0);
//                            viewHolder.tvFavoritoId.setText(favorito.getId()+"");
//                            Glide.with(context).load(R.drawable.icono_favoritos_activo).into(viewHolder.ivIconoFavoritos);
//                            viewHolder.tvFavoritoValue.setText("true");
//                        }
//                    }
//                }
//                @Override
//                public void onFailure(Call<Favorito> call, Throwable t) {
//                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
//                }
//            });
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
//                                Toast.makeText(context, "Removido de favoritos", Toast.LENGTH_SHORT).show();
//                                dataset.remove(i);
//                                notifyItemRemoved(i);
//                                notifyItemRangeChanged(i, getItemCount());
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
//                    Call<Favorito> favoritoCall = service.agregarFavorito(cliente_id,sucursal.getId());
//                    favoritoCall.enqueue(new Callback<Favorito>() {
//                        @Override
//                        public void onResponse(Call<Favorito> call, Response<Favorito> response) {
//                            if (response.isSuccessful()) {
//                                Favorito favorito = response.body();
//                                Glide.with(context).load(R.drawable.icono_favoritos_activo).into(viewHolder.ivIconoFavoritos);
//                                viewHolder.tvFavoritoValue.setText("true");
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
        try {
            if (latitud != 0 && longitud != 0 && !sucursal.getLatitud().isEmpty() && !sucursal.getLongitud().isEmpty()) {
                float latitudSucursal = Float.parseFloat(sucursal.getLatitud());
                float longituducursal = Float.parseFloat(sucursal.getLongitud());
                Location myLocation = new Location("mylocation");
                Location sucursalLocation = new Location("sucursallocation");
                myLocation.setLatitude(latitud);
                myLocation.setLongitude(longitud);
                sucursalLocation.setLongitude(longituducursal);
                sucursalLocation.setLatitude(latitudSucursal);
                float distancia = myLocation.distanceTo(sucursalLocation);
                DecimalFormat df = new DecimalFormat();
                if (distancia>=1000) {
                    df.setMaximumFractionDigits(2);
                    distancia = distancia/1000;
                    viewHolder.tvDistancia.setText(df.format(distancia)+"Km");
                } else {
                    df.setMaximumFractionDigits(0);
                    viewHolder.tvDistancia.setText(df.format(distancia)+"Mts");
                }
                viewHolder.tvDistancia.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagenSucursal;
//        ivIconoFavoritos;
        TextView tvNombreSucursal, tvSucursalDireccion, tvSucursalDistancia, tvClubHorario, tvFavoritoValue, tvFavoritoId,tvDistancia;
        RatingBar rbCalificacion;
        View viewClubes;
        ImageView star1,star2,star3,star4,star5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagenSucursal = itemView.findViewById(R.id.iv_imagen_sucursal);
            tvNombreSucursal = itemView.findViewById(R.id.tv_nombre_club);
            tvSucursalDireccion = itemView.findViewById(R.id.tv_sucursal_direccion);
            rbCalificacion = itemView.findViewById(R.id.rb_calificacion);
//            ivIconoFavoritos = itemView.findViewById(R.id.iv_icono_favoritos);
            tvFavoritoValue = itemView.findViewById(R.id.tv_favorito_value);
            tvFavoritoId = itemView.findViewById(R.id.tv_favorito_id);
            viewClubes = itemView.findViewById(R.id.club);
            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);
            star4 = itemView.findViewById(R.id.star4);
            star5 = itemView.findViewById(R.id.star5);
            tvDistancia = itemView.findViewById(R.id.tv_distancia);

        }
    }

    public float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}