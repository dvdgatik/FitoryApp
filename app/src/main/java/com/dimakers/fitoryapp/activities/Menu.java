package com.dimakers.fitoryapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.dimakers.fitoryapp.R;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
//        Rotate Animation
//        ImageView image= (ImageView) findViewById(R.id.imageView);
//        image.startAnimation(AnimationUtils.loadAnimation(Menu.this,R.anim.rotate));
    }
}
