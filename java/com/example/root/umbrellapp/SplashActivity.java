package com.example.root.umbrellapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        DBHandler db = new DBHandler(this);
        db.printAllFavs();

        // Set the fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "Biko_Regular.otf");
        ((TextView) findViewById(R.id.umbrellaText)).setTypeface(font);

        new CountDownTimer(3000, 3000) {

            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(), InsertLocationActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}