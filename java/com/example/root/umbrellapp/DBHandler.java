package com.example.root.umbrellapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "umbrellapp.db";

    // Table names
    private static final String TABLE_FAVS = "favorites";

    // Needs Table Columns names
    private static final String KEY_FAV_LOC = "favoriteLoc";
    private static final String KEY_NEED_NAME = "needName";

    // Table Create Statements
    private static final String CREATE_FAVS_TABLE = "CREATE TABLE " + TABLE_FAVS + "("
            + KEY_FAV_LOC + " TEXT" + ")";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // An einai na kanoume update tote prepei na kanoume copy ta dedomena
        // kai meta na kanoume drop thn vash wste na mhn xa8oun
        // Drop older table if existed

        // Create tables again
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVS);
        onCreate(db);
    }

    public void deleteFavs() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVS);
        db.execSQL(CREATE_FAVS_TABLE);
    }

    /*---------------- favorite locations functions ----------------------------*/
    // Adding new favorite location
    public void addFavorite(String town) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FAV_LOC, town);
        //values.put(KEY_NEED_NAME, needName);

        db.insert(TABLE_FAVS, null, values);
        db.close();
    }

    // Vazoume ola ta favorite locations ston adapter
    public void getAllFavs(FavoriteAdapter favoriteAdapter) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_FAVS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                favoriteAdapter.add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
    }
}
