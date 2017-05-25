package com.example.leoguti.riskassessment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leoguti.riskassessment.modelo.Alerta;
import com.example.leoguti.riskassessment.modelo.EventDataSource;
import com.example.leoguti.riskassessment.modelo.NivelAlerta;
import com.example.leoguti.riskassessment.vista.DateTimePickerFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Arrays;

public class InsertEventActivity extends AppCompatActivity
        implements DateTimePickerFragment.DateTimePickerInterface, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private final static String TAG = InsertEventActivity.class.getSimpleName();

    /** Variables para el API de localizacion de GOOGLE*/
    private GoogleApiClient mGoogleApiClient;
    private Location sLastLocation;
    private LocationCallback mCallback;
    final private static int MY_PERMISSION_FINE_LOCATION = 101;

    private ConstraintLayout mCLayout;
    private EventDataSource datasource;
    private Alerta newAlert;
    EditText etFechaAlerta;
    EditText etNivelAlerta;
    EditText etDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_event);

        mCLayout = (ConstraintLayout) findViewById(R.id.constraint_layout);
        // Se obtiene los elementos del formulario
        etFechaAlerta = (EditText) findViewById(R.id.etFechaAlerta);
        etFechaAlerta.setTag(etFechaAlerta.getKeyListener());
        etFechaAlerta.setKeyListener(null);

        etNivelAlerta = (EditText) findViewById(R.id.etNivelAlerta);
        etDescripcion = (EditText) findViewById(R.id.etDescripcion);
        etDescripcion.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        newAlert = new Alerta();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void showDateTimePicker(View view) {
        DialogFragment newFragment = new DateTimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onSelectedDateTime(String dateTimeSelected) {
        EditText et_fecha = ((EditText) findViewById(R.id.etFechaAlerta));
        et_fecha.setText(dateTimeSelected);
    }

    public void onSelectedAlert(View view) {
        // Initializing a new alert dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the alert dialog title
        builder.setTitle("Choose an alert.");

        // Initializing an array
        final String[] alertas = Arrays.toString(NivelAlerta.values()).replaceAll("^.|.$", "").split(", ");

        // Set a single choice items list for alert dialog
        builder.setSingleChoiceItems(
                alertas, // Items list
                -1, // Index of checked item (-1 = no selection)
                new DialogInterface.OnClickListener() // Item click listener
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Get the alert dialog selected item's text
                        String selectedItem = Arrays.asList(alertas).get(i);
                        etNivelAlerta.setText(selectedItem);
                        // Display the selected item's text on snack bar
                        Snackbar.make(
                                mCLayout,
                                "Checked : " + selectedItem,
                                Snackbar.LENGTH_INDEFINITE
                        ).show();
                    }
                });
        // Set the a;ert dialog positive button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Just dismiss the alert dialog after selection
                // Or do something now
            }
        });

        // Create the alert dialog
        AlertDialog dialog = builder.create();

        // Finally, display the alert dialog
        dialog.show();
    }

    public void onSaveEvent(View view) {
        // Se obtienen los datos del formulario
        String nivelAlerta = etNivelAlerta.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String fechaEvento = etFechaAlerta.getText().toString();

        if (fechaEvento.isEmpty()) {
            new AlertDialog.Builder(this).setMessage(R.string.error_date_event_empty)
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
            return;
        }

        if (nivelAlerta.isEmpty()) {
            new AlertDialog.Builder(this).setMessage(R.string.error_level_alert_empty)
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
            return;
        }

        if (descripcion.isEmpty()) {
            new AlertDialog.Builder(this).setMessage(R.string.error_description_empty)
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
            return;
        }

        newAlert.setFechaRegistro(Alerta.parseDate(etFechaAlerta.getText().toString()));
        newAlert.setDescripcion(etDescripcion.getText().toString());
        newAlert.setNivelAlerta(NivelAlerta.getByName(etNivelAlerta.getText().toString()));

        // Se instancia y abre el datasource
        datasource = new EventDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            Log.e(TAG, "-- errorOpen: " + e.getMessage());
        }

        // Se crea la alerta
        final Alerta alerta = datasource.createAlert(newAlert);
        new AlertDialog.Builder(this).setMessage(R.string.message_create_record)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Return data
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("newAlert", alerta);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }).create().show();
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (datasource != null) {
            datasource.close();
        }
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "-- Caused Suspended: "+ connectionResult.getErrorMessage());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast toast = Toast.makeText(getApplicationContext(), "Error permisos ", Toast.LENGTH_SHORT);
            toast.show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
            //checkIfLocationServicesEnabled();
        } else {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                newAlert.setLatitud(mLastLocation.getLatitude());
                newAlert.setLongitud(mLastLocation.getLongitude());
                Toast toast = Toast.makeText(getApplicationContext(), R.string.msj_location, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), R.string.msj_permisos, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                        if (mLastLocation != null) {
                            newAlert.setLatitud(mLastLocation.getLatitude());
                            newAlert.setLongitud(mLastLocation.getLongitude());
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.msj_location, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "-- Caused Suspended: "+ i);
    }
}
