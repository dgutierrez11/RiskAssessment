package com.example.leoguti.riskassessment.vista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.example.leoguti.riskassessment.R;
import com.example.leoguti.riskassessment.modelo.Alerta;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;

public class DetailAlert extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final private static int MY_PERMISSION_FINE_LOCATION = 101;
    ZoomControls zoom;
    private double longitud, latitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_alert);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        Intent intent = getIntent();

        Alerta alerta = (Alerta) intent.getSerializableExtra("AlertaDetalle");

        TextView tvFecha = (TextView) findViewById(R.id.tveFechaRegistro);
        TextView tvNivel = (TextView) findViewById(R.id.tveNivelAlerta);
        TextView tvDescripcion = (TextView) findViewById(R.id.tveDescripcion);
        tvFecha.setText(sdf.format(alerta.getFechaRegistro().getTime()));
        tvNivel.setText(alerta.getNivelAlerta().name());
        tvDescripcion.setText(alerta.getDescripcion());

        // Se setea el punto de ubicacion
        longitud = alerta.getLongitud();
        latitud = alerta.getLatitud();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        zoom = (ZoomControls) findViewById(R.id.zcZoom);

        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());

            }
        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Agrega un marcador en la ubicacion de la alerta and move the camera
        LatLng ubicacionAlerta;
        if (latitud != 0 && longitud != 0){
            // LatLng sydney = new LatLng(-34, 151);
            ubicacionAlerta = new LatLng(latitud, longitud);
        } else {
            // Si no tiene ubicacion se coloca una ubicacion por defecto
            ubicacionAlerta = new LatLng(4.61, -74.08);
        }
        mMap.addMarker(new MarkerOptions().position(ubicacionAlerta).title("Ubicación alerta"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionAlerta, 15.0f));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "La aplicacion requiere permisos", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }
}
