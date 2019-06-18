package com.amco.cidnews.Utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {


    public ConexionSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Utilidades.CREAR_TABLA_NOTICA);
        db.execSQL(Utilidades.CREAR_TABLA_PREFERENCIA);
        db.execSQL(Utilidades.CREAR_TABLA_RECUPERAR);
        db.execSQL(Utilidades.DATABASE_TEMP);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS noticias");
        db.execSQL("DROP TABLE IF EXISTS temporal");

        onCreate(db);

    }
}
