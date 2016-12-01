package com.example.root.umbrellapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.CalendarContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

/*
 *  Parent class that extends activity and is responsible for setting up the drawer
 *  Also stores globaly used functions
 */
public class HelperActivity extends Activity {
    public static int timeoutTime = 10000;
    private DBHandler db;
    private FavoriteAdapter adapter;

    public void helperOnCreate(int activityXML, final boolean fromSingle) {
        setContentView(activityXML);
        db = new DBHandler(getApplicationContext());

        try {
            // mLeftDragger for left to right drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            Field mDragger = drawer.getClass().getDeclaredField("mLeftDragger");
            mDragger.setAccessible(true);

            ViewDragHelper draggerObj = (ViewDragHelper) mDragger.get(drawer);
            Field mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);

            // Set the edge where the drawer is triggered we can use mEdgeSize.setInt(draggerObj, 150); for 150dp
            int edge = 0;
            edge = mEdgeSize.getInt(draggerObj);
            mEdgeSize.setInt(draggerObj, edge * 2);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            // Set the fonts
            Typeface font = Typeface.createFromAsset(getAssets(), "Biko_Regular.otf");
            ((TextView) findViewById(R.id.favoritesText)).setTypeface(font);

            Button addFav = (Button) findViewById(R.id.typeFavorite);
            addFav.setTypeface(font);
            addFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fromSingle)
                        finish();
                    else {
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });

            // Set the hamburger icon listener to open the drawer
            ImageView hamBur = (ImageView) findViewById(R.id.menu);
            hamBur.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });

            // Set the X image listener to close the drawer
            ImageView X = (ImageView) findViewById(R.id.x);
            X.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            });

            CoordinatorLayout coord = (CoordinatorLayout) findViewById(R.id.coordLayout);
            adapter = new FavoriteAdapter(getApplicationContext(), coord);
            db.getAllFavs(adapter);
            ListView list = (ListView)findViewById(R.id.favList);
            list.setFooterDividersEnabled(true);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), SingleLocationActivity.class);
                    intent.putExtra("favorite", "true");
                    intent.putExtra("name", (String) parent.getItemAtPosition(position));
                    startActivity(intent);

                    if (fromSingle)
                        finish();
                }
            });
            list.setAdapter(adapter);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // When on back pressed is called and the drawer is open then we simply close the drawer
    // else we call the super.onBackPressed()
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    protected void addFavoriteToAdapter(String name) {
        adapter.add(name);
    }

    protected void refreshFavoritesList() {
        adapter.clear();
        db.getAllFavs(adapter);
    }

    // Function to check if the user has connection to the internet
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    // Function to create the dialog and set the proper message
    public static void showDialogBox(String message, ProgressDialog dialog) {
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    // Function to destroy the dialog
    public static void safeDismiss(ProgressDialog dialog) {
        try {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

        } catch (final Exception e) {
            Log.e("Dialog Eskase", "MalformedURLException");
            e.printStackTrace();
        }
    }

    // Function to read the output returned by the server
    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer("");

        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            Log.e("ReadStream", "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data.toString();
    }

    // Interfaces used by Async Tasks to get the response
    public interface HttpData {
        void onResponseReceived(int error, String townName, String[] icons, String[] maxTemps, String[] minTemps);
    }

    public interface HttpIntString {
        void onResponseReceived(int error, ArrayList<String> townNames);
    }
}