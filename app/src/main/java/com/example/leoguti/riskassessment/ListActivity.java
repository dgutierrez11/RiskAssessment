package com.example.leoguti.riskassessment;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.leoguti.riskassessment.modelo.Alerta;
import com.example.leoguti.riskassessment.modelo.CustomAdapter;
import com.example.leoguti.riskassessment.modelo.EventDataSource;
import com.example.leoguti.riskassessment.modelo.NivelAlerta;
import com.example.leoguti.riskassessment.vista.DetailAlert;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;

import java.util.ArrayList;
import java.util.Date;

public class ListActivity extends AppCompatActivity {

    private final static String TAG = ListActivity.class.getSimpleName();
    ArrayList<Alerta> dataModels;
    private ListView listaRegistros;
    private static CustomAdapter adapter;
    static final int CREATE_ALERT_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_toolbar_list);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), InsertEventActivity.class);
                startActivityForResult(intent, CREATE_ALERT_REQUEST);
            }
        });

        listaRegistros = (ListView) findViewById(R.id.lista_registro);
        dataModels= new ArrayList<>();

        EventDataSource datasource = new EventDataSource(this);
        try {
            datasource.open();
            dataModels.addAll(datasource.getAllAlerts());
        }catch (SQLException e){
            Log.e(TAG, "-- errorOpenOrQuery: "+ e.getMessage());
        }finally {
            datasource.close();
        }

        adapter = new CustomAdapter(dataModels,getApplicationContext());
        listaRegistros.setAdapter(adapter);
        listaRegistros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                  @Override
                                                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                      Alerta alerta= dataModels.get(position);
                                                      //Toast toast = Toast.makeText(getApplicationContext(), alerta.getDescripcion(), Toast.LENGTH_SHORT);
                                                      //toast.show();
                                                      Intent intent = new Intent(getBaseContext(), DetailAlert.class);
                                                      intent.putExtra("AlertaDetalle", alerta);
                                                      startActivity(intent);
                                                  }
                                              }

        );
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CREATE_ALERT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                //startActivity(new Intent(Intent.ACTION_VIEW, data));
                Alerta alerta = (Alerta) data.getSerializableExtra("newAlert");
                dataModels.add(alerta);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void onDeleteAlerta(View view){
        Toast toast = Toast.makeText(getApplicationContext(), "Delete", Toast.LENGTH_SHORT);
        toast.show();
    }
}
