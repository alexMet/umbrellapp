package com.example.root.umbrellapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_location);

        TextView townText = (TextView) findViewById(R.id.townText);
        ImageView wheatherIcon = (ImageView) findViewById(R.id.wheatherIcon);
        TextView maxTemp = (TextView) findViewById(R.id.maxTemp);
        TextView minTemp = (TextView) findViewById(R.id.minTemp);

        townText.setText("Athens, GR");
        wheatherIcon.setImageResource(R.drawable.ic_2);
        maxTemp.setText("14");
        minTemp.setText("11");
    }

}
