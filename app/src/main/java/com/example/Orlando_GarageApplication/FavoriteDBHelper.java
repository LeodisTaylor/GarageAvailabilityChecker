package com.example.Orlando_GarageApplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FavoriteDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favoriteGarage.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creates the SQLiteDatabase for the user to add favorite garages
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITEGARAGE_TABLE = "CREATE TABLE " +
                FavoriteGarage.FavoriteGarageEntry.TABLE_NAME + " (" +
                FavoriteGarage.FavoriteGarageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteGarage.FavoriteGarageEntry.COLUMN_PERSONID + " TEXT NOT NULL, " +
                FavoriteGarage.FavoriteGarageEntry.COLUMN_GARAGENAME + " TEXT NOT NULL, " +
                FavoriteGarage.FavoriteGarageEntry.COLUMN_FAVORITETITLE + " TEXT NOT NULL, " +
                FavoriteGarage.FavoriteGarageEntry.COLUMN_ACTIVATIONDATE + " TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_FAVORITEGARAGE_TABLE);
    }

    //Activates if DATABASE_VERSION is changed manually, this is changed when the database scheme is changed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteGarage.FavoriteGarageEntry.TABLE_NAME);
        onCreate(db);
    }
}
