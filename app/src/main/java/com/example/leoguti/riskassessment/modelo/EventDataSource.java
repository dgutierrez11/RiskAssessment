package com.example.leoguti.riskassessment.modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leoguti on 12/05/2017.
 */

public class EventDataSource {
    // Database fields
    private SQLiteDatabase database;
    private AppDataDbHelper dbHelper;
    private String[] allColumns = { Alerta.EventEntry.COLUMN_ID,
            Alerta.EventEntry.COLUMN_EVENT_DATE,  Alerta.EventEntry.COLUMN_LEVEL_ALERT,
            Alerta.EventEntry.COLUMN_DESCRIPTION, Alerta.EventEntry.COLUMN_LATITUDE, Alerta.EventEntry.COLUMN_LONGITUDE};

    public EventDataSource(Context context) {
        dbHelper = new AppDataDbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Alerta createAlert(Alerta alerta) {
        ContentValues values = new ContentValues();
        values.put(Alerta.EventEntry.COLUMN_EVENT_DATE, Alerta.formatDateTime(alerta.getFechaRegistro()));
        values.put(Alerta.EventEntry.COLUMN_LEVEL_ALERT, alerta.getNivelAlerta().name());
        values.put(Alerta.EventEntry.COLUMN_DESCRIPTION, alerta.getDescripcion());
        values.put(Alerta.EventEntry.COLUMN_LATITUDE, alerta.getLatitud());
        values.put(Alerta.EventEntry.COLUMN_LONGITUDE, alerta.getLongitud());
        long insertId = database.insert(Alerta.EventEntry.TABLE_NAME, null,
                values);
        Cursor cursor = database.query(Alerta.EventEntry.TABLE_NAME,
                allColumns, Alerta.EventEntry.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Alerta newAlert = cursorToAlert(cursor);
        cursor.close();
        return newAlert;
    }

    public void deleteAlert(Alerta alerta) {
        long id = alerta.getId();
        System.out.println("Alert deleted with id: " + id);
        database.delete(Alerta.EventEntry.TABLE_NAME, Alerta.EventEntry.COLUMN_ID
                + " = " + id, null);
    }

    public List<Alerta> getAllAlerts() {
        List<Alerta> alertas = new ArrayList<Alerta>();

        Cursor cursor = database.query(Alerta.EventEntry.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Alerta alerta = cursorToAlert(cursor);
            alertas.add(alerta);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return alertas;
    }

    private Alerta cursorToAlert(Cursor cursor) {
        Alerta alerta = new Alerta();
        alerta.setId(cursor.getLong(0));
        alerta.setFechaRegistro(Alerta.parseDate(cursor.getString(1)));
        alerta.setDescripcion(cursor.getString(3));
        alerta.setLatitud(cursor.getDouble(4));
        alerta.setLongitud(cursor.getDouble(5));
        if(cursor.getString(2) != null){
            alerta.setNivelAlerta(NivelAlerta.getByName(cursor.getString(2)));
        }

        return alerta;
    }
}
