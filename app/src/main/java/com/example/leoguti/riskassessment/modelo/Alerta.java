package com.example.leoguti.riskassessment.modelo;

import android.provider.BaseColumns;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by leoguti on 09/05/2017.
 */

public class Alerta implements Serializable{

    private long id;
    private Date fechaRegistro;
    private String descripcion;
    private NivelAlerta nivelAlerta;
    private double latitud;
    private double longitud;

    public Alerta() {
    }

    public Alerta(long id, Date fechaRegistro, String descripcion, NivelAlerta nivelAlerta, double latitud, double longitud) {
        this.id = id;
        this.fechaRegistro = fechaRegistro;
        this.descripcion = descripcion;
        this.nivelAlerta = nivelAlerta;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public NivelAlerta getNivelAlerta() {
        return nivelAlerta;
    }

    public void setNivelAlerta(NivelAlerta nivelAlerta) {
        this.nivelAlerta = nivelAlerta;
    }


    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return "Alerta{" +
                "id=" + id +
                ", fechaRegistro=" + fechaRegistro +
                ", descripcion='" + descripcion + '\'' +
                ", nivelAlerta=" + nivelAlerta +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                '}';
    }

    // metadata model
    public static final String SQL_CREATE_ENTRY = "create table " +  EventEntry.TABLE_NAME + "( "  +
            EventEntry.COLUMN_ID + " integer primary key autoincrement, " +
            EventEntry.COLUMN_EVENT_DATE + " text, " +
            EventEntry.COLUMN_DESCRIPTION + " text not null, " +
            EventEntry.COLUMN_LEVEL_ALERT + " text not null, " +
            EventEntry.COLUMN_LATITUDE + " real, " +
            EventEntry.COLUMN_LONGITUDE + " real "
            + " );";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME +" ;";

    /* Inner class that defines the table contents */
    public static abstract class EventEntry implements BaseColumns {
        public static final String TABLE_NAME          = "Event";
        public static final String COLUMN_ID           = "id";
        public static final String COLUMN_LEVEL_ALERT   = "levelAlert";
        public static final String COLUMN_DESCRIPTION  = "description";
        public static final String COLUMN_EVENT_DATE   = "eventDate";
        public static final String COLUMN_LATITUDE   = "latitude";
        public static final String COLUMN_LONGITUDE  = "longitude";
    }

    public static String formatDateTime(Date fechaRegistro) {

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        return iso8601Format.format(fechaRegistro);
    }

    public static Date parseDate(String fechaRegistro) {

        if(fechaRegistro == null){
            return null;
        }
        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm");
        Date date;
        try {
            date = iso8601Format.parse(fechaRegistro);
        } catch (ParseException e) {
            date = null;
        }

        return date;
    }
}
