package com.example.root.umbrellapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class SingleLocationActivity extends HelperActivity {
    String townName, day;
    String[] icons, maxTemps, minTemps, textDays;
    DBHandler db;
    ProgressDialog dialog;
    TextView townText, maxTemp, minTemp;
    ImageView wheatherIcon;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.helperOnCreate(R.layout.activity_single_location, true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = new DBHandler(getApplicationContext());
        dialog = new ProgressDialog(this);

        // Check the today's day and set the texts that we are going to use for the other days
        Calendar cal = Calendar.getInstance();
        int cnt = 0, today = cal.get(Calendar.DAY_OF_WEEK) - 1;
        day = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DATE);
        String[] weekDays = {"SUN", "MON", "TUE", "WEN", "THU", "FRI", "SAT"};
        textDays = new String[5];

        for (int i = 0; i < 5 && (today + i + 1) < 7; i++)
            textDays[cnt++] = weekDays[today + i + 1];

        for (int i = 0; i < today - 1; i++)
            textDays[cnt++] = weekDays[i];

        // Get the intent with the corresponding data
        Intent inte = getIntent();
        if (inte.hasExtra("favorite")) {
            String name = inte.getStringExtra("name");
            Favorite fav = db.getFavorite(name);

            // If the latest date of the location is today then just show the info,
            // else update the info by downloading the latest forecast
            if (day.equals(fav.getDate())) {
                townName = fav.getName();
                icons = fav.getIcon();
                maxTemps = fav.getMax();
                minTemps = fav.getMin();
                setLayouts();
            } else
                startWeatherTask("q=" + name.replace(" ", "%20"));
        }
        else {
            townName = inte.getStringExtra("townName");
            icons = inte.getStringArrayExtra("icons");
            maxTemps = inte.getStringArrayExtra("maxTemps");
            minTemps = inte.getStringArrayExtra("minTemps");
            setLayouts();
        }
    }

    private void setLayouts() {
        townText = (TextView) findViewById(R.id.townText);
        wheatherIcon = (ImageView) findViewById(R.id.wheatherIcon);
        maxTemp = (TextView) findViewById(R.id.maxTemp);
        minTemp = (TextView) findViewById(R.id.minTemp);

        // Set the fonts
        font = Typeface.createFromAsset(getAssets(), "Biko_Regular.otf");
        ((TextView) findViewById(R.id.todayText)).setTypeface(font);
        townText.setTypeface(font);
        maxTemp.setTypeface(font);
        minTemp.setTypeface(font);

        // Set the X image listener to close the drawer
        ImageView heart = (ImageView) findViewById(R.id.heart);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newFav = db.addFavorite(townName, "", day, maxTemps, minTemps, icons);

                if (!newFav) {
                    addFavoriteToAdapter(townName);
                    Toast.makeText(getApplicationContext(), R.string.fav_added, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), R.string.fav_added_already, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set the info for today
        townText.setText(townName);
        wheatherIcon.setImageResource(getDrawableByName(icons[0]));
        maxTemp.setText(Html.fromHtml(maxTemps[0] + " <sup>o</sup>C"));
        minTemp.setText(Html.fromHtml(minTemps[0] + " <sup>o</sup>C"));

        int[] textID = {
                R.id.nextDay1,
                R.id.nextDay2,
                R.id.nextDay3,
                R.id.nextDay4,
                R.id.nextDay5
        };

        int[] imageID = {
                R.id.nextIcon1,
                R.id.nextIcon2,
                R.id.nextIcon3,
                R.id.nextIcon4,
                R.id.nextIcon5
        };

        int[] maxTempID = {
                R.id.nextMaxTemp1,
                R.id.nextMaxTemp2,
                R.id.nextMaxTemp3,
                R.id.nextMaxTemp4,
                R.id.nextMaxTemp5
        };

        int[] minTempID = {
                R.id.nextMinTemp1,
                R.id.nextMinTemp2,
                R.id.nextMinTemp3,
                R.id.nextMinTemp4,
                R.id.nextMinTemp5
        };

        // Set the weather for the layouts for the rest of the 5 days
        for (int i = 0; i < 5; i++) {
            TextView nextDayView = (TextView) findViewById(textID[i]);
            ImageView nextIcon = (ImageView) findViewById(imageID[i]);
            TextView maxTempView = (TextView) findViewById(maxTempID[i]);
            TextView minTempView = (TextView) findViewById(minTempID[i]);

            nextDayView.setText(textDays[i]);
            nextDayView.setTypeface(font);

            nextIcon.setImageResource(getDrawableByName(icons[i + 1]));

            maxTempView.setText(Html.fromHtml(maxTemps[i + 1] + " <sup>o</sup>C"));
            maxTempView.setTypeface(font);

            minTempView.setText(Html.fromHtml(minTemps[i + 1] + " <sup>o</sup>C"));
            minTempView.setTypeface(font);
        }
    }

    // Get the resource id for an icon by its string name
    private int getDrawableByName(String name) {
        int id = getResources().getIdentifier("ic_" + name, "drawable", "com.example.root.umbrellapp");

        if (id == 0)
            return R.drawable.ic_nwa;

        return id;
    }

    private void startWeatherTask(String url) {
        showDialogBox(getString(R.string.getting_forecast), dialog);
        HttpGetWheather task = new HttpGetWheather() {
            @Override
            public void onResponseReceived(int error, String townName, String[] micons, String[] mmaxTemps, String[] mminTemps) {
                if (error > 0) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    icons = micons;
                    maxTemps = mmaxTemps;
                    minTemps = mminTemps;
                    db.updateFav(townName, "", day, maxTemps, minTemps, icons);
                    setLayouts();
                }
                safeDismiss(dialog);
            }
        };

        task.execute("HttpGetWheather", url);
    }
}
