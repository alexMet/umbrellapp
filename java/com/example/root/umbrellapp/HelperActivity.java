package com.example.root.umbrellapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/*
 *  Einai h klassh pou kanei extend to AppCompatActivity kai to xrhshmopoioume
 *  se ka8e activity ws voithikh gia elaxistopoihsh kwdika
 *  ylopoiei epishs to plaino menu kai synarthseis pou xrhshmopoioume
 *  se diafora activities.
 */
public class HelperActivity extends AppCompatActivity {
    // Query me location --> q=Psarra
    // Query me coordinates --> lat=35&lon=139
    public static String server = "http://api.openweathermap.org/data/2.5/forecast?mode=json&appid=cb89a2d010e5f7e225c403c1f7b1335f&";
    // O xronos se ms (10 sec) meta apo ton opoio exoume connection timeout an argei h syndesh h o server
    public static int timeoutTime = 10000;
    public int myMenu;

    public void setMenu(int myMenu) {
        this.myMenu = myMenu;
    }
    
// /*
// super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_location_insert);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//  */
//    public void helperOnCreate(int activityXML, int title, boolean has_arrow) {
//        setContentView(activityXML);
//
//        Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar);
//        mToolBar.setTitle(title);
//        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
//        setSupportActionBar(mToolBar);
//
//        try {
//            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//            // Vazoume mLeftDragger gia na anoigei to drawer apo aristera
//            // an 8eloume na anoigei apo de3ia vazoume mRightDragger
//            Field mDragger = drawer.getClass().getDeclaredField("mLeftDragger");
//            mDragger.setAccessible(true);
//
//            ViewDragHelper draggerObj = (ViewDragHelper) mDragger.get(drawer);
//
//            Field mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
//            mEdgeSize.setAccessible(true);
//
//            // Orizoume thn timh pou mas voleuei apo pou na 3ekinaei to drawer an syroume to daxtylo mas
//            // sthn o8onh, mporoume na orisoume opoiadhpote timh p.x. mEdgeSize.setInt(draggerObj, 150); gia 150dp
//            int edge = 0;
//            edge = mEdgeSize.getInt(draggerObj);
//            mEdgeSize.setInt(draggerObj, edge * 2);
//
//            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                    this, drawer, (has_arrow) ? mToolBar : null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//            drawer.setDrawerListener(toggle);
//            toggle.setDrawerIndicatorEnabled(!has_arrow);
//            toggle.syncState();
//
//            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//            navigationView.setNavigationItemSelectedListener(this);
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        // gia na phgainei pisw me to velaki prepei na mpei edw meta to toggle.serDrawerIndicatorEnabled()
//        // giati alliws to kanei overide, to parapanw xreiazetai gia na mas deixnei to hamburger icon
//        if (has_arrow) {
//            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onBackPressed();
//                }
//            });
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(this.myMenu, menu);
//        return true;
//    }
//
//    // Kanoume overide to onBackPressed ka8ws 8eloume otan einai anoixto to drawer na kleinei me
//    // to pathma tou 'pisw' kai an den einai anoixto na kaleite h super
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        if (drawer.isDrawerOpen(GravityCompat.START))
//            drawer.closeDrawer(GravityCompat.START);
//        else
//            super.onBackPressed();
//    }
//
//    // Elegxoume an exei path8ei to hamburger icon kai anoigoume to drawer
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == android.R.id.home) {
//            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//            drawer.openDrawer(GravityCompat.START);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//   /*
//    *   If set, and the activity being launched is already running in the current task, then instead of
//    *   launching a new instance of that activity, all of the other activities on top of it will be closed and
//    *   this Intent will be delivered to the (now on top) old activity as a new Intent.
//    *       showItemIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//    *   If set, the activity will not be launched if it is already running at the top of the history stack.
//    *       showItemIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//    */
//
//    // Edw elegxoume poio stoixeio ths listas tou drawer exei path8ei kai kaloume to activity ths antistoixhs o8onhs
//    // an apo opoiadhpote o8onh kalesoume thn idia o8onh, den ginetai tipota. An h o8onh apo thn opoia pathsame thn lista
//    // den einai h TwoButtons tote kanoume finish to sygekrimeno activity. An apo opoiadhpote o8onh kalesoume thn TwoButtons
//    // tote vazoume ta 2 flags: FLAG_ACTIVITY_CLEAR_TOP, FLAG_ACTIVITY_SINGLE_TOP opou parapanw e3hgoume ti kanoun
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        String class_name = this.getClass().getSimpleName();
//        boolean started = false;
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    // Synarthsh pou koitaei an o xrhsths einai syndedemenos sto internet epishs exoume kai
//    // timeout sto http connection (10 sec se ola ektos apo to blue red list pou einai 30 sec kai apo
//    // to inputter pou pairnoume ta dedomena tou eof pou einai 30 sec)
//    // http://stackoverflow.com/questions/1443166/android-how-to-check-if-the-server-is-available
//    public static boolean isOnline(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (connectivityManager != null) {
//            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
//
//            if (netInfo != null && netInfo.isConnected()
//                    && netInfo.isConnectedOrConnecting()
//                    && netInfo.isAvailable()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    // Synarthsh pou allazei ta xrwmata sta koumpia otan ta patame pairnei ws parametrous
//    // to koumpi pou path8hke, to drawable tou koumpiou kai to xrwma tou keimenou mesa sto koumpi
//    // to koumpi pou 3epath8hke kai ta parapanw dedomena tou
//    public static void changeButtonsLayout(Button pressed, Button unpressed, int presDraw, int unpresDraw, int presColor, int unpresColor) {
//        pressed.setTextColor(presColor);
//        pressed.setBackgroundResource(presDraw);
//        unpressed.setTextColor(unpresColor);
//        unpressed.setBackgroundResource(unpresDraw);
//    }
//
//    // Synarthsh pou ftiaxnei to dialog kai to deixnei
//    public static void showDialogBox(String message, ProgressDialog dialog) {
//        dialog.setMessage(message);
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//    }
//
//    // Synarthsh pou katastrefei to dialog opws prepei
//    public static void safeDismiss(ProgressDialog dialog) {
//        try {
//            if (dialog != null && dialog.isShowing())
//                dialog.dismiss();
//
//        } catch (final Exception e) {
//            Log.e("Dialog Eskase", "MalformedURLException");
//            e.printStackTrace();
//        }
//    }
//
//    // Synarthsh pou diavazei ta dedomena pou mas stelnei o server
//    public static String readStream(InputStream in) {
//        BufferedReader reader = null;
//        StringBuffer data = new StringBuffer("");
//
//        try {
//            reader = new BufferedReader(new InputStreamReader(in));
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                data.append(line);
//            }
//        } catch (IOException e) {
//            Log.e("ReadStream", "IOException");
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return data.toString();
//    }
//
}