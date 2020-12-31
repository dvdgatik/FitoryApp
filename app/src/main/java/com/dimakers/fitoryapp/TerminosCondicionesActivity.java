package com.dimakers.fitoryapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.barteksc.pdfviewer.PDFView;

public class TerminosCondicionesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos_condiciones);
        PDFView pdfView = findViewById(R.id.pdf1);
        pdfView.fromAsset("terminos.pdf").load();
    }
}
