package com.example.leoguti.riskassessment.modelo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by leoguti on 12/05/2017.
 */

public class AppDataDbHelper extends SQLiteOpenHelper {

    private final static String TAG = AppDataDbHelper.class.getSimpleName();
    // If you change the database schema, you must increment the database version.
    private static final String DATABASE_NAME = "eventsDB.db";
    private static final int DATABASE_VERSION = 1;


    public AppDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        Log.i(TAG, "-- start: onCreate");
        database.execSQL(Alerta.SQL_CREATE_ENTRY);

        Log.i(TAG, "-- end: onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "-- start: onUpgrade");
        Log.w(AppDataDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(Alerta.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
