package com.dimakers.fitoryapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class DialogUtils extends AppCompatActivity {


    public static Dialog alertDialog;

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public static Dialog getAlertDialog(Context context) {
        alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.setContentView(R.layout.loading_layout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        ImageView imageView = (ImageView) alertDialog.findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        imageView.startAnimation(animation);
        return  alertDialog;
    }
}
