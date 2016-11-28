package com.example.root.umbrellapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LocationInsertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_insert);

        EditText locationEdit = (EditText) findViewById(R.id.typeLoc);
        Button getLocationBtn = (Button) findViewById(R.id.buttonLoc);

        getLocationBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "locatzis", Toast.LENGTH_SHORT).show();
            }
        });
    }
}