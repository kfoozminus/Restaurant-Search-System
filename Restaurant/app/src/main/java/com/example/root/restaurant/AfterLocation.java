package com.example.root.restaurant;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AfterLocation extends AppCompatActivity {

    Location mLastLocation;
    Button entireDatabase;
    String latitude, longitude;
    String logged = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_location);

        Bundle locData = getIntent().getExtras();
        if(locData == null) {
            return ;
        }

        String latitude = locData.getString("latitude");
        String longitude = locData.getString("longitude");
        logged = "0";

    }

    public void onLogin(View v) {
        Intent i = new Intent(AfterLocation.this, Login.class);
        startActivityForResult(i, 1);
        Log.i("jennysMsg", "login after location" + logged);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                logged = data.getStringExtra("logged");
            }
        }
    }

    public void onClickShowEntireDatabase(View v) {

        Intent i = new Intent(AfterLocation.this, ShowEntireDatabase.class);
        i.putExtra("logged", logged);
        startActivity(i);
    }

    public void onSugRes(View v) {
        Intent i = new Intent(AfterLocation.this, SuggestedRestaurantList.class);
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        i.putExtra("logged", logged);
        startActivity(i);
    }
}
