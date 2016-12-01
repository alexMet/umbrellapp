package com.example.root.umbrellapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.root.umbrellapp.HelperActivity.isOnline;
import static com.example.root.umbrellapp.HelperActivity.safeDismiss;
import static com.example.root.umbrellapp.HelperActivity.showDialogBox;

public class InsertLocationActivity extends HelperActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient mGoogleApiClient;
    public AutoCompleteTextView locationEdit;
    public ArrayAdapter<String> locationEditAdapter;
    public List<String> suggest;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.helperOnCreate(R.layout.activity_location_insert, false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dialog = new ProgressDialog(this);
        suggest = new ArrayList<String>();
        locationEdit = (AutoCompleteTextView) findViewById(R.id.typeLoc);
        locationEdit.addTextChangedListener(new TextWatcher(){

            public void afterTextChanged(Editable editable) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString().trim();

                if (newText.length() > 2)
                    townNamesTask(newText);
            }
        });

        Button getLocationBtn = (Button) findViewById(R.id.buttonLoc);

        // Setting the font of the texts
        Typeface font = Typeface.createFromAsset(getAssets(), "Biko_Regular.otf");
        ((TextView) findViewById(R.id.giveLocText)).setTypeface(font);
        ((TextView) findViewById(R.id.firstText)).setTypeface(font);
        locationEdit.setTypeface(font);
        getLocationBtn.setTypeface(font);

        if (checkPlayServices())
            buildGoogleApiClient();

        locationEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (locationEdit.getRight() - locationEdit.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        String townName = locationEdit.getText().toString().trim();

                        if (townName.isEmpty())
                            Toast.makeText(InsertLocationActivity.this, R.string.ins_empty, Toast.LENGTH_SHORT).show();
                        else {
                            if (isOnline(getApplicationContext()))
                                startWeatherTask("q=" + townName.replace(" ", "%20"));
                            else
                                Toast.makeText(InsertLocationActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }

                return false;
            }
        });

        getLocationBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isOnline(getApplicationContext()))
                    weatherByCoordTask();
                else
                    Toast.makeText(InsertLocationActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void weatherByCoordTask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(InsertLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            String url = "lat=" + mLastLocation.getLatitude() + "&lon=" + mLastLocation.getLongitude();
            startWeatherTask(url);
        } else
            Toast.makeText(getApplicationContext(), R.string.no_location, Toast.LENGTH_LONG).show();
    }

    private void startWeatherTask(String url) {
        showDialogBox(getString(R.string.getting_forecast), dialog);
        HttpGetWheather task = new HttpGetWheather() {
            @Override
            public void onResponseReceived(int error, String townName, String[] icons, String[] maxTemps, String[] minTemps) {
                if (error > 0)
                    Toast.makeText(getApplicationContext(), R.string.oups_fail, Toast.LENGTH_LONG).show();
                else {
                    Intent intent = new Intent(getApplicationContext(), SingleLocationActivity.class);
                    intent.putExtra("townName", townName);
                    intent.putExtra("icons", icons);
                    intent.putExtra("maxTemps", maxTemps);
                    intent.putExtra("minTemps", minTemps);
                    startActivity(intent);
                }
                safeDismiss(dialog);
            }
        };

        task.execute("HttpGetWheather", url);
    }

    // Get the town names and add them to the autocomplete adapter
    private void townNamesTask(String halfName) {
        String url = "q=" + halfName.replace(" ", "%20");

        final HttpGetNames task = new HttpGetNames() {
            @Override
            public void onResponseReceived(int error, ArrayList<String> townNames) {
                if (error < 0) {
                    suggest = townNames;
                    locationEditAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdown_item, suggest);
                    locationEdit.setAdapter(locationEditAdapter);
                    locationEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (isOnline(getApplicationContext()))
                                startWeatherTask("q=" + ((String) parent.getItemAtPosition(position)).replace(" ", "%20"));
                            else
                                Toast.makeText(InsertLocationActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        }
                    });
                    locationEditAdapter.notifyDataSetChanged();
                }
            }
        };

        task.execute("HttpGetTownNames", url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    weatherByCoordTask();
                else
                    Toast.makeText(getApplicationContext(), R.string.we_need_perm, Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else
                Toast.makeText(getApplicationContext(), R.string.unable_to_get_location, Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFavoritesList();
        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("InsertLocation", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) { }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
}