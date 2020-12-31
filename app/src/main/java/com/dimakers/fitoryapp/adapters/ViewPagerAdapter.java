package com.dimakers.fitoryapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dimakers.fitoryapp.Main3Activity;
import com.dimakers.fitoryapp.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> imageUrls = new ArrayList<>();

    public ViewPagerAdapter(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls.addAll(imageUrls);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Main3Activity.class);
                intent.putStringArrayListExtra("galeria",imageUrls);
                context.startActivity(intent);
            }
        });
        Glide.with(context).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(imageUrls.get(position)).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
