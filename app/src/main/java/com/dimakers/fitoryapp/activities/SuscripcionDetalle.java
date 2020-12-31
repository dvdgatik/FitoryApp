package com.dimakers.fitoryapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.dimakers.fitoryapp.R;

public class SuscripcionDetalle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suscripcion_detalle);
        TextView como_llegar = (TextView) findViewById(R.id.como_llegar);
    }
}
