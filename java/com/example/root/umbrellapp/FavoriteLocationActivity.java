package com.example.root.umbrellapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class FavoriteLocationActivity extends Activity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_location);

        FavoriteAdapter adapter = new FavoriteAdapter(getApplicationContext());
        adapter.add("Athens, GR");
        adapter.add("Milan, IT");
        adapter.add("Budhapest, HU");
        adapter.add("Kaplony, RO");
        adapter.add("Gyzi, GR");

        ListView list = (ListView)findViewById(R.id.favList);
        list.setFooterDividersEnabled(true);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
//        Intent intent = new Intent(getApplicationContext(), DisplayMed.class);
//        intent.putExtra("barcode", (String) mAdapter.getItem(position));
//        intent.putExtra("name", name);
//        startActivity(intent);
        Toast.makeText(getApplicationContext(), "lelouas", Toast.LENGTH_SHORT).show();
    }
}
