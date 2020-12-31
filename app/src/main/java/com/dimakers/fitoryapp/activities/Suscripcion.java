package com.dimakers.fitoryapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dimakers.fitoryapp.R;

public class Suscripcion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suscripcion);
    }

    public void detallesusc(View view) {
        startActivity(new Intent(Suscripcion.this,SuscripcionDetalle.class));
    }
}
