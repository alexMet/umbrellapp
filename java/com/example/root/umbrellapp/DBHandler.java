package com.example.root.umbrellapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "umbrellapp.db";

    // Table names
    private static final String TABLE_FAVS = "favorites";

    // Favorites Table Columns names
    private static final String KEY_FAV_NAME = "favName";
    private static final String KEY_FAV_ID = "favId";
    private static final String KEY_FAV_DATE = "favDate";

    private static final String KEY_MAX0 = "fav_max0";
    private static final String KEY_MIN0 = "fav_min0";
    private static final String KEY_ICON0 = "fav_icon0";

    private static final String KEY_MAX1 = "fav_max1";
    private static final String KEY_MIN1 = "fav_min1";
    private static final String KEY_ICON1 = "fav_icon1";

    private static final String KEY_MAX2 = "fav_max2";
    private static final String KEY_MIN2 = "fav_min2";
    private static final String KEY_ICON2 = "fav_icon2";

    private static final String KEY_MAX3 = "fav_max3";
    private static final String KEY_MIN3 = "fav_min3";
    private static final String KEY_ICON3 = "fav_icon3";

    private static final String KEY_MAX4 = "fav_max4";
    private static final String KEY_MIN4 = "fav_min4";
    private static final String KEY_ICON4 = "fav_icon4";

    private static final String KEY_MAX5 = "fav_max5";
    private static final String KEY_MIN5 = "fav_min5";
    private static final String KEY_ICON5 = "fav_icon5";

    // Table Create Statements
    private static final String CREATE_FAVS_TABLE = "CREATE TABLE " + TABLE_FAVS + "("
            + KEY_FAV_NAME + " TEXT PRIMARY KEY, " + KEY_FAV_ID + " TEXT, " + KEY_FAV_DATE + " TEXT, "
            + KEY_MAX0 + " TEXT, " + KEY_MIN0 + " TEXT, " + KEY_ICON0 + " TEXT, "
            + KEY_MAX1 + " TEXT, " + KEY_MIN1 + " TEXT, " + KEY_ICON1 + " TEXT, "
            + KEY_MAX2 + " TEXT, " + KEY_MIN2 + " TEXT, " + KEY_ICON2 + " TEXT, "
            + KEY_MAX3 + " TEXT, " + KEY_MIN3 + " TEXT, " + KEY_ICON3 + " TEXT, "
            + KEY_MAX4 + " TEXT, " + KEY_MIN4 + " TEXT, " + KEY_ICON4 + " TEXT, "
            + KEY_MAX5 + " TEXT, " + KEY_MIN5 + " TEXT, " + KEY_ICON5 + " TEXT" + ")";

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
        // Drop older table if existed
        // Create tables again
    }

    public void deleteFavs() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVS);
        db.execSQL(CREATE_FAVS_TABLE);
    }

    /*---------------- favorite locations functions ----------------------------*/
    // Adding new favorite location
    public boolean addFavorite(String town, String id, String date, String[] max, String[] min, String[] icon) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FAV_NAME, town);
        values.put(KEY_FAV_ID, id);
        values.put(KEY_FAV_DATE, date);

        for (int i = 0; i < 6; i++) {
            values.put("fav_max" + i, max[i]);
            values.put("fav_min" + i, min[i]);
            values.put("fav_icon" + i, icon[i]);
        }

        long ret = db.insert(TABLE_FAVS, null, values);
        db.close();

        return ret == -1;
    }

    // Updating a favorite location
    public int updateFav(String town, String id, String date, String[] max, String[] min, String[] icon) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FAV_NAME, town);
        values.put(KEY_FAV_ID, id);
        values.put(KEY_FAV_DATE, date);

        for (int i = 0; i < 6; i++) {
            values.put("fav_max" + i, max[i]);
            values.put("fav_min" + i, min[i]);
            values.put("fav_icon" + i, icon[i]);
        }

        db.insert(TABLE_FAVS, null, values);
        return db.update(TABLE_FAVS, values, KEY_FAV_NAME + " = ?", new String[]{town});
    }

    // Deleting a favorite location
    public void deleteFavorite(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVS, KEY_FAV_NAME + " = ?", new String[]{name});
        db.close();
    }

    // Getting a favorite location
    public Favorite getFavorite(String name) {
        String[] max, min, icon;
        Favorite fav = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVS, new String[]{
                KEY_FAV_NAME, KEY_FAV_ID, KEY_FAV_DATE,
                KEY_MAX0, KEY_MIN0, KEY_ICON0,
                KEY_MAX1, KEY_MIN1, KEY_ICON1,
                KEY_MAX2, KEY_MIN2, KEY_ICON2,
                KEY_MAX3, KEY_MIN3, KEY_ICON3,
                KEY_MAX4, KEY_MIN4, KEY_ICON4,
                KEY_MAX5, KEY_MIN5, KEY_ICON5},
                KEY_FAV_NAME + "=?", new String[]{name}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            max = new String[6];
            min = new String[6];
            icon = new String[6];
            fav = new Favorite();

            fav.setName(cursor.getString(0));
            fav.setId(cursor.getString(1));
            fav.setDate(cursor.getString(2));

            for (int i = 0; i < 6; i++) {
                max[i] = cursor.getString(3 * (i + 1));
                min[i] = cursor.getString(3 * (i + 1) + 1);
                icon[i] = cursor.getString(3 * (i + 1) + 2);
            }

            fav.setMax(max);
            fav.setMin(min);
            fav.setIcon(icon);

            cursor.close();
        }
        db.close();

        return fav;
    }

    // Vazoume ola ta favorite locations ston adapter
    public void getAllFavs(FavoriteAdapter favoriteAdapter) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT " + KEY_FAV_NAME + " FROM " + TABLE_FAVS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                favoriteAdapter.add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
    }

    public void printAllFavs() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_FAVS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Log.e("Favorite", "Name " + cursor.getString(0) + " Id " + cursor.getString(1) + " Date " + cursor.getString(2));
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
    }
}
