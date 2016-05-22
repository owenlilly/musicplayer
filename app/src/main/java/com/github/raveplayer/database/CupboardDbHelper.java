package com.github.raveplayer.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.raveplayer.models.PlayingState;
import com.github.raveplayer.models.SongDetails;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class CupboardDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RavePlayer.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteDatabase database;

    static {
        cupboard().register(PlayingState.class);
        cupboard().register(SongDetails.class);
    }

    public CupboardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteDatabase getConnection(Context context){
        if(database == null){
            synchronized (CupboardDbHelper.class) {
                database = new CupboardDbHelper(context.getApplicationContext()).getWritableDatabase();
            }
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
