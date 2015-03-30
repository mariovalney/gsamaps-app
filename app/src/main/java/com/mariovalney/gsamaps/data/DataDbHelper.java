package com.mariovalney.gsamaps.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mariovalney.gsamaps.data.DataContract.AmbassadorEntry;

/**
 * Created by Mário Valney on 20/11/14.
 * mail: mariovalney@gmail.com
 */
public class DataDbHelper extends  SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ambassador.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AmbassadorEntry.TABLE_NAME + " ( " +
                    AmbassadorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AmbassadorEntry.COLUMN_NAME_NOME + " TEXT NOT NULL, " +
                    AmbassadorEntry.COLUMN_NAME_INSTITUICAO + " TEXT NOT NULL, " +
                    AmbassadorEntry.COLUMN_NAME_PAIS + " TEXT NOT NULL, " +
                    AmbassadorEntry.COLUMN_NAME_LATITUDE + " REAL NOT NULL, " +
                    AmbassadorEntry.COLUMN_NAME_LONGITUDE + " REAL NOT NULL, " +
                    AmbassadorEntry.COLUMN_NAME_ENDERECO_LINHA_UM + " TEXT NOT NULL, " +
                    AmbassadorEntry.COLUMN_NAME_ENDERECO_LINHA_DOIS + " TEXT NOT NULL" +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AmbassadorEntry.TABLE_NAME;

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}