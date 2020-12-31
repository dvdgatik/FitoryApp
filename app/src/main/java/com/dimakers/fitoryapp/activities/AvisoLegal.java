package com.dimakers.fitoryapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dimakers.fitoryapp.R;

public class AvisoLegal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso_legal);
    }

    public void atras(View view) {
        finish();
    }
}
