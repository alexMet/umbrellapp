package com.example.root.umbrellapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new CountDownTimer(10000, 10000) {

            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(), LocationInsertActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}